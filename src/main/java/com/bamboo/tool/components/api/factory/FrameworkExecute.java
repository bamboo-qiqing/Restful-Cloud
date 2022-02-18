package com.bamboo.tool.components.api.factory;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.bamboo.tool.components.api.entity.AnnotationFrameWork;
import com.bamboo.tool.components.api.entity.AnnotationInfo;
import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.entity.ApiMethod;
import com.bamboo.tool.components.api.enums.*;
import com.bamboo.tool.config.model.PsiClassCache;
import com.bamboo.tool.db.service.AnnotationInfoService;
import com.bamboo.tool.db.service.ApiMethodService;
import com.bamboo.tool.util.PsiUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by GuoQing
 * Date 2022/1/21 14:25
 * Description
 */
public class FrameworkExecute {


    /**
     * 构建当前项目 api集合
     *
     * @param project
     * @return
     */
    public static List<ApiClass> buildApiMethod(Project project) {
        AnnotationInfoService annotationInfoService = ApplicationManager.getApplication().getService(AnnotationInfoService.class);
        List<AnnotationInfo> annotationInfos = annotationInfoService.selectAll();
        List<AnnotationInfo> attribute = annotationInfos.stream().filter(e -> e.getEffect().contains("attribute")).collect(Collectors.toList());
        List<ApiClass> apiClasses = new ArrayList<>();
        Collection<PsiClass> allPsiClass = PsiUtils.getALLPsiClass(project);
        allPsiClass.forEach(psiClass -> {
            Module module = ModuleUtil.findModuleForPsiElement(psiClass);
            if (module != null) {
                ApiClass apiClass = new ApiClass();
                apiClass.setClassName(psiClass.getName());
                apiClass.setClassPath(PsiUtil.getVirtualFile(psiClass).getPath());
                apiClass.setModuleName(module.getName());
                psiClass.getAnnotations();

                List<PsiMethod> methodList;
                if (classPaths.contains(AnnotationType.SOA_SERVICE_CLIENT)) {
                    methodList = Arrays.stream(psiClass.getMethods()).filter(e -> PsiUtil.getAccessLevel(e.getModifierList()) == PsiUtil.ACCESS_LEVEL_PUBLIC).collect(Collectors.toList());
                } else {
                    methodList = Arrays.asList(psiClass.getMethods());
                }
                List<ApiMethod> apiMethods = methodList.stream().parallel().map(e -> buildMethod(e, apiClass, classPaths)).filter(e -> e != null).collect(Collectors.toList());
                apiClass.setMethods(apiMethods);
                apiClasses.add(apiClass);
            }
        });
        return apiClasses;
    }

    private static ApiMethod buildMethod(PsiMethod method, ApiClass psiClass, List<String> classPaths) {
        ApiMethod apiMethod = new ApiMethod();
        apiMethod.setMethodName(method.getName());
        apiMethod.setPsiMethod(method);

        boolean isSoa = true;
        PsiAnnotation[] methodAnnotations = method.getAnnotations();
        if (methodAnnotations != null || methodAnnotations.length > 0) {
            for (PsiAnnotation methodAnnotation : methodAnnotations) {
                PsiJavaCodeReferenceElement referenceElement = methodAnnotation.getNameReferenceElement();
                String qualifiedName = referenceElement.getQualifiedName();
                MethodAnnotationProcess methodAnnotationProcess = methodAnnotationProcessMap.get(qualifiedName);
                if (!Objects.isNull(methodAnnotationProcess) && methodAnnotationProcess.getClassShortName().equals(referenceElement.getReferenceName())) {
                    methodAnnotationProcess.buildMethod(apiMethod, methodAnnotation, psiClass);
                    isSoa = false;
                }
            }
        }
        if (isSoa) {
            if (classPaths.contains(AnnotationType.SOA_SERVICE_CLIENT.getClassPath())) {
                apiMethod.getMethodUrls().add(method.getName());
                apiMethod.getTypes().add(FrameworkType.O_DIAN_YUN.getCode());
                apiMethod.getTypes().add(InterfaceType.CLIENT.getCode());
                apiMethod.getMethodTypes().add(RequestMethod.ALL.getCode());
            }
            if (classPaths.contains(AnnotationType.SOA_SERVICE_REGISTER.getClassPath())) {
                apiMethod.getTypes().add(FrameworkType.O_DIAN_YUN.getCode());
                apiMethod.getTypes().add(InterfaceType.SERVICE.getCode());
                apiMethod.getMethodUrls().add(method.getName());
                apiMethod.getMethodTypes().add(RequestMethod.ALL.getCode());
            }
        }
        if (CollectionUtil.isEmpty(apiMethod.getMethodUrls())) {
            return null;
        }
        List<String> classUrls = apiMethod.getMethodUrls().parallelStream().map(e -> CharSequenceUtil.addPrefixIfNot(e, "/")).collect(Collectors.toList());
        apiMethod.setMethodUrls(classUrls);
        psiClass.getClassUrls().parallelStream().forEach(e -> {
            List<String> urls = apiMethod.getMethodUrls().parallelStream().map(a -> e + a).collect(Collectors.toList());
            apiMethod.getUrls().addAll(urls);
        });
        return apiMethod;
    }



}
