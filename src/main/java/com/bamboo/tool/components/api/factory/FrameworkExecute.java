package com.bamboo.tool.components.api.factory;

import cn.hutool.core.util.StrUtil;
import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.entity.ApiMethod;
import com.bamboo.tool.components.api.enums.ClassAnnotationType;
import com.bamboo.tool.components.api.enums.MethodAnnotationType;
import com.bamboo.tool.util.PsiUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiMethod;
import com.intellij.util.SlowOperations;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by GuoQing
 * Date 2022/1/21 14:25
 * Description
 */
public class FrameworkExecute {
    public static Map<String, ClassAnnotationProcess> classAnnotationProcessMap = new HashMap();
    public static Map<String, MethodAnnotationProcess> methodAnnotationProcessMap = new HashMap();

    static {
        classAnnotationProcessMap = Arrays.stream(ClassAnnotationType.values())
                .filter(e -> e.getClassAnnotationProcess() != null)
                .collect(Collectors.toMap(e -> e.getClassPath(), e -> e.getClassAnnotationProcess()));
        methodAnnotationProcessMap = Arrays.stream(MethodAnnotationType.values())
                .filter(e -> e.getMethodAnnotationProcess() != null)
                .collect(Collectors.toMap(e -> e.getCode(), e -> e.getMethodAnnotationProcess()));
    }

    /**
     * 构建当前项目 api集合
     *
     * @param project
     * @return
     */
    public static List<ApiClass> buildApiMethod(Project project) {
        List<ApiClass> apiClasses = new ArrayList<>();
        Collection<PsiClass> allPsiClass = PsiUtil.getALLPsiClass(project);
        allPsiClass.stream().forEach(psiClass -> {
            Module module = ModuleUtil.findModuleForPsiElement(psiClass);
            if (module != null) {
                // 构建apiclass
                ApiClass apiClass = new ApiClass();
                apiClass.setModuleName(module.getName());
                apiClass.setClassName(psiClass.getName());

                String packageName = getPackageName(psiClass);
                apiClass.setPackageName(packageName);

                PsiAnnotation[] classAnnotations = psiClass.getAnnotations();
                for (PsiAnnotation classAnnotation : classAnnotations) {
                    PsiJavaCodeReferenceElement referenceElement = classAnnotation.getNameReferenceElement();
                    ClassAnnotationProcess classAnnotationProcess = classAnnotationProcessMap.get(classAnnotation.getQualifiedName());
                    if (!Objects.isNull(classAnnotationProcess) && classAnnotationProcess.getClassShortName().equals(referenceElement.getReferenceName())) {
                        classAnnotationProcess.buildClass(apiClass, classAnnotation);
                    }
                }

                // 构建ApiMethod
                PsiMethod[] methods = psiClass.getMethods();
                List<ApiMethod> apiMethods = new ArrayList<>();
                for (PsiMethod method : methods) {
                    ApiMethod apiMethod = buildMethod(classAnnotations, method);
                    if (apiMethod != null) {
                        apiMethods.add(apiMethod);
                    }
                }

                apiClass.setMethods(apiMethods);
                apiClasses.add(apiClass);
            }

        });
        return apiClasses;
    }

    private static ApiMethod buildMethod(PsiAnnotation[] classAnnotations, PsiMethod method) {
        ApiMethod apiMethod = new ApiMethod();
        PsiAnnotation[] methodAnnotations = method.getAnnotations();
        if (classAnnotations == null || classAnnotations.length < 1) {
            return null;
        }
        apiMethod.setMethodName(method.getName());
        for (PsiAnnotation methodAnnotation : methodAnnotations) {
            PsiJavaCodeReferenceElement referenceElement = methodAnnotation.getNameReferenceElement();
            String qualifiedName = methodAnnotation.getQualifiedName();
            MethodAnnotationProcess methodAnnotationProcess = methodAnnotationProcessMap.get(qualifiedName);
            if (!Objects.isNull(methodAnnotationProcess) && methodAnnotationProcess.getClassShortName().equals(referenceElement.getReferenceName())) {
                methodAnnotationProcess.buildMethod(apiMethod, methodAnnotation);
            }
        }
        return apiMethod;
    }

    private static String getPackageName(@NotNull PsiClass psiClass) {
        String defaultPackageName = "";
        String qualifiedName = psiClass.getQualifiedName();
        if (StrUtil.isEmpty(qualifiedName)) {
            return defaultPackageName;
        }

        String fileName = psiClass.getName();
        if (StrUtil.isEmpty(fileName)) {
            return defaultPackageName;
        }

        if (!qualifiedName.equals(fileName)) {
            return qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
        }
        return defaultPackageName;
    }

}
