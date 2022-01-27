package com.bamboo.tool.components.api.factory;

import cn.hutool.core.collection.CollectionUtil;
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
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.AllClassesSearch;
import com.intellij.util.Query;
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
        classAnnotationProcessMap = Arrays.stream(ClassAnnotationType.values()).filter(e -> e.getClassAnnotationProcess() != null).collect(Collectors.toMap(e -> e.getClassAnnotationProcess().getClassShortName(), e -> e.getClassAnnotationProcess()));
        methodAnnotationProcessMap = Arrays.stream(MethodAnnotationType.values()).filter(e -> e.getMethodAnnotationProcess() != null).collect(Collectors.toMap(e -> e.getCode(), e -> e.getMethodAnnotationProcess()));
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
        allPsiClass.forEach(psiClass -> {
            // 构建apiclass
            PsiAnnotation[] classAnnotations = psiClass.getAnnotations();
            if (classAnnotations.length > 0) {
                ApiClass apiClass = new ApiClass();
                apiClass.setClassName(psiClass.getName());
                String packageName = getPackageName(psiClass);
                apiClass.setPackageName(packageName);
                for (PsiAnnotation classAnnotation : classAnnotations) {
                    PsiJavaCodeReferenceElement referenceElement = classAnnotation.getNameReferenceElement();
                    String referenceName = referenceElement.getReferenceName();
                    ClassAnnotationProcess classAnnotationProcess = classAnnotationProcessMap.get(referenceName);
                    if (!Objects.isNull(classAnnotationProcess)) {
                        classAnnotationProcess.buildClass(apiClass, classAnnotation);

                    }
                }
                // 构建ApiMethod
                PsiMethod[] methods = psiClass.getMethods();
                List<ApiMethod> apiMethods = Arrays.stream(methods).parallel().map(e -> buildMethod(e,apiClass)).filter(e -> e != null).collect(Collectors.toList());
                apiClass.setMethods(apiMethods);
                apiClasses.add(apiClass);
            }

        });
        return apiClasses;
    }

    private static ApiMethod buildMethod(PsiMethod method,ApiClass psiClass) {
        ApiMethod apiMethod = new ApiMethod();
        PsiAnnotation[] methodAnnotations = method.getAnnotations();
        if (methodAnnotations == null || methodAnnotations.length < 1) {
            return null;
        }
        apiMethod.setMethodName(method.getName());
        for (PsiAnnotation methodAnnotation : methodAnnotations) {
            PsiJavaCodeReferenceElement referenceElement = methodAnnotation.getNameReferenceElement();
            String qualifiedName = referenceElement.getQualifiedName();
            MethodAnnotationProcess methodAnnotationProcess = methodAnnotationProcessMap.get(qualifiedName);
            if (!Objects.isNull(methodAnnotationProcess) && methodAnnotationProcess.getClassShortName().equals(referenceElement.getReferenceName())) {
                methodAnnotationProcess.buildMethod(apiMethod, methodAnnotation,psiClass);
            }
        }
        if (CollectionUtil.isEmpty(apiMethod.getMethodUrls())) {
            return null;
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
