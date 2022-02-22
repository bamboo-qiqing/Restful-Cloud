package com.bamboo.tool.components.api.factory;

import cn.hutool.core.collection.CollectionUtil;
import com.bamboo.tool.components.api.entity.*;
import com.bamboo.tool.components.api.enums.AnnotationScope;
import com.bamboo.tool.components.api.enums.MethodScope;
import com.bamboo.tool.config.model.PsiClassCache;
import com.bamboo.tool.db.service.AnnotationInfoSettingService;
import com.bamboo.tool.util.PsiAnnotationMemberUtil;
import com.bamboo.tool.util.PsiUtils;
import com.bamboo.tool.util.StringUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;

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
    public static List<BambooClass> buildApiMethod(Project project) {
        AnnotationInfoSettingService annotationInfoSettingService = ApplicationManager.getApplication().getService(AnnotationInfoSettingService.class);
        List<AnnotationInfoSetting> annotationInfoSettings = annotationInfoSettingService.selectAll();
        Map<String, AnnotationInfoSetting> infoSettingClassMap = annotationInfoSettings.stream().filter(e -> e.getEffect().contains("attribute")).filter(e -> AnnotationScope.CLASS.getCode().equals(e.getScope().getCode())).collect(Collectors.toMap(AnnotationInfoSetting::getAnnotationPath, e -> e));
        Map<String, AnnotationInfoSetting> infoSettingMethodMap = annotationInfoSettings.stream().filter(e -> e.getEffect().contains("attribute")).filter(e -> AnnotationScope.METHOD.getCode().equals(e.getScope().getCode())).collect(Collectors.toMap(AnnotationInfoSetting::getAnnotationPath, e -> e));
        List<BambooClass> bambooClasses = new ArrayList<>();
        final List<PsiClassCache> caches = PsiUtils.getALLPsiClass(project, annotationInfoSettings);
        caches.forEach(cache -> {
            final PsiClass psiClass = cache.getPsiClass();
            final AnnotationInfoSetting info = cache.getInfo();
            Module module = ModuleUtil.findModuleForPsiElement(psiClass);
            if (module != null) {
                BambooClass bambooClass = new BambooClass();
                bambooClass.setQualifiedName(psiClass.getQualifiedName());
                bambooClass.setClassName(psiClass.getName());
                bambooClass.setClassPath(PsiUtil.getVirtualFile(psiClass).getPath());
                bambooClass.setModuleName(module.getName());
                PsiAnnotation[] annotations = psiClass.getAnnotations();
                if (annotations.length > 0) {
                    List<BambooAnnotationInfo> classAnnotations = buildAnnotations(infoSettingClassMap, annotations);
                    bambooClass.setAnnotations(classAnnotations);
                }
                final PsiMethod[] methods = psiClass.getMethods();
                if (methods.length > 0) {
                    Map<String, AnnotationMethodScope> methodScopes = info.getMethodScopes().stream().collect(Collectors.toMap(AnnotationMethodScope::getMethodScope, methodScope -> methodScope));
                    Arrays.stream(methods).forEach(method -> {
                        if (CollectionUtil.isNotEmpty(info.getMethodScopes())) {
                            boolean satisfyScope = methodLevel(methodScopes, method);
                            if (satisfyScope) {
                                BambooMethod bambooMethod = new BambooMethod();
                                bambooMethod.setMethodName(method.getName());
                                bambooMethod.setPsiMethod(method);
                                final AnnotationMethodScope annotationMethodScope = methodScopes.get(MethodScope.ANNOTATION.getCode());
                                if (annotationMethodScope != null) {
                                    final PsiAnnotation[] methodAnnotations = method.getAnnotations();
                                    if (methodAnnotations.length > 0) {
                                        List<BambooAnnotationInfo> bambooAnnotationInfos = buildAnnotations(infoSettingMethodMap, methodAnnotations);
                                        if (bambooAnnotationInfos.size() > 0) {
                                            bambooMethod.setAnnotationInfos(bambooAnnotationInfos);
                                            bambooClass.getMethods().add(bambooMethod);
                                        }
                                    }
                                }else{
                                    bambooClass.getMethods().add(bambooMethod);
                                }

                            }
                        }

                    });
                }
//                final Map<String, AnnotationMethodScope> methodScopes = info.getMethodScopes().stream().collect(Collectors.toMap(AnnotationMethodScope::getMethodScope, methodScope -> methodScope));
//                Arrays.stream(psiClass.getMethods()).forEach(method -> {
//                    final int accessLevel = PsiUtil.getAccessLevel(method.getModifierList());
//                    final AnnotationMethodScope annotationMethodScope = methodScopes.get(MethodScope.ANNOTATION);
//                    if(annotationMethodScope!=null){
//                        final PsiAnnotation[] methodAnnotations = method.getAnnotations();
//                        for (PsiAnnotation methodAnnotation :methodAnnotations) {
//
//                        }
//                    }
//                });
//                Arrays.stream(psiClass.getMethods()).filter(e->{e})
//                List<PsiMethod> methodList;
//                if (classPaths.contains(AnnotationType.SOA_SERVICE_CLIENT)) {
//                    methodList = Arrays.stream(psiClass.getMethods()).filter(e -> PsiUtil.getAccessLevel(e.getModifierList()) == PsiUtil.ACCESS_LEVEL_PUBLIC).collect(Collectors.toList());
//                } else {
//                    methodList = Arrays.asList(psiClass.getMethods());
//                }
//                List<ApiMethod> apiMethods = methodList.stream().parallel().map(e -> buildMethod(e, BambooClass, classPaths)).filter(e -> e != null).collect(Collectors.toList());
//                BambooClass.setMethods(apiMethods);
                bambooClasses.add(bambooClass);
            }
        });
        return bambooClasses;
    }

    private static boolean methodLevel(Map<String, AnnotationMethodScope> methodScopes, PsiMethod method) {

        final int accessLevel = PsiUtil.getAccessLevel(method.getModifierList());
        final AnnotationMethodScope privateScope = methodScopes.get(MethodScope.PRIVATE);
        if (privateScope != null) {
            if (accessLevel == PsiUtil.ACCESS_LEVEL_PRIVATE) {
                return true;
            } else {
                return false;
            }
        }
        final AnnotationMethodScope publicScope = methodScopes.get(MethodScope.PUBLIC);
        if (publicScope != null) {
            if (accessLevel == PsiUtil.ACCESS_LEVEL_PUBLIC) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    private static List<BambooAnnotationInfo> buildAnnotations(Map<String, AnnotationInfoSetting> infoSettingMap, PsiAnnotation[] annotations) {
        final List<BambooAnnotationInfo> annotationInfos = new ArrayList<>();
        Arrays.stream(annotations).forEach(annotation -> {
            AnnotationInfoSetting annotationInfoSetting = infoSettingMap.get(annotation.getQualifiedName());
            if (!Objects.isNull(annotationInfoSetting)) {
                Map<String, AnnotationParam> params = annotationInfoSetting.getParams().stream().collect(Collectors.toMap(AnnotationParam::getName, param -> param));
                if (CollectionUtil.isNotEmpty(params)) {
                    PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();
                    for (PsiNameValuePair attribute : attributes) {
                        PsiAnnotationMemberValue value = attribute.getValue();
                        String name = attribute.getName();
                        if(StringUtil.isEmpty(name)){
                            name="null";
                        }
                        AnnotationParam annotationParam = params.get(name);
                        if (!Objects.isNull(annotationParam)) {
                            final List<String> values = PsiAnnotationMemberUtil.getValue(annotationParam.getType(), value);
                            values.stream().forEach(a->{
                                BambooAnnotationInfo bambooAnnotationInfo = new BambooAnnotationInfo();
                                bambooAnnotationInfo.setValue(a);
                                bambooAnnotationInfo.setAnnotationInfoSetting(annotationInfoSetting);
                                bambooAnnotationInfo.setParam(annotationParam);
                                annotationInfos.add(bambooAnnotationInfo);
                            });

                        }
                    }
                }
            }
        });
        return annotationInfos;
    }

//    private static ApiMethod buildMethod(PsiMethod method, BambooClass psiClass, List<String> classPaths) {
//        ApiMethod apiMethod = new ApiMethod();
//        apiMethod.setMethodName(method.getName());
//        apiMethod.setPsiMethod(method);
//
//        boolean isSoa = true;
//        PsiAnnotation[] methodAnnotations = method.getAnnotations();
//        if (methodAnnotations != null || methodAnnotations.length > 0) {
//            for (PsiAnnotation methodAnnotation : methodAnnotations) {
//                PsiJavaCodeReferenceElement referenceElement = methodAnnotation.getNameReferenceElement();
//                String qualifiedName = referenceElement.getQualifiedName();
//                MethodAnnotationProcess methodAnnotationProcess = methodAnnotationProcessMap.get(qualifiedName);
//                if (!Objects.isNull(methodAnnotationProcess) && methodAnnotationProcess.getClassShortName().equals(referenceElement.getReferenceName())) {
//                    methodAnnotationProcess.buildMethod(apiMethod, methodAnnotation, psiClass);
//                    isSoa = false;
//                }
//            }
//        }
//        if (isSoa) {
//            if (classPaths.contains(AnnotationType.SOA_SERVICE_CLIENT.getClassPath())) {
//                apiMethod.getMethodUrls().add(method.getName());
//                apiMethod.getTypes().add(FrameworkType.O_DIAN_YUN.getCode());
//                apiMethod.getTypes().add(InterfaceType.CLIENT.getCode());
//                apiMethod.getMethodTypes().add(RequestMethod.ALL.getCode());
//            }
//            if (classPaths.contains(AnnotationType.SOA_SERVICE_REGISTER.getClassPath())) {
//                apiMethod.getTypes().add(FrameworkType.O_DIAN_YUN.getCode());
//                apiMethod.getTypes().add(InterfaceType.SERVICE.getCode());
//                apiMethod.getMethodUrls().add(method.getName());
//                apiMethod.getMethodTypes().add(RequestMethod.ALL.getCode());
//            }
//        }
//        if (CollectionUtil.isEmpty(apiMethod.getMethodUrls())) {
//            return null;
//        }
//        List<String> classUrls = apiMethod.getMethodUrls().parallelStream().map(e -> CharSequenceUtil.addPrefixIfNot(e, "/")).collect(Collectors.toList());
//        apiMethod.setMethodUrls(classUrls);
//        psiClass.getClassUrls().parallelStream().forEach(e -> {
//            List<String> urls = apiMethod.getMethodUrls().parallelStream().map(a -> e + a).collect(Collectors.toList());
//            apiMethod.getUrls().addAll(urls);
//        });
//        return apiMethod;
//    }


}
