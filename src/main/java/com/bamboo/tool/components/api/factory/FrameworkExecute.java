package com.bamboo.tool.components.api.factory;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.bamboo.tool.components.api.entity.*;
import com.bamboo.tool.components.api.enums.AnnotationScope;
import com.bamboo.tool.components.api.enums.MethodScope;
import com.bamboo.tool.config.model.PsiClassCache;
import com.bamboo.tool.db.service.BambooService;
import com.bamboo.tool.util.PsiAnnotationMemberUtil;
import com.bamboo.tool.util.PsiUtils;
import com.bamboo.tool.util.StringUtil;
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

        List<AnnotationInfoSetting> annotationInfoSettings = BambooService.selectAllAnnotationInfoSetting();
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
                bambooClass.setClassName(psiClass.getName());
                bambooClass.setClassPath(PsiUtil.getVirtualFile(psiClass).getPath());
                bambooClass.setModuleName(module.getName());
                bambooClass.setSetting(info);
                PsiAnnotation[] annotations = psiClass.getAnnotations();
                if (annotations.length > 0) {
                    buildAnnotations(bambooClass, null, infoSettingClassMap, annotations, info);
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
                                final AnnotationMethodScope annotationMethodScope = methodScopes.get(MethodScope.ANNOTATION.getCode());
                                if (annotationMethodScope != null) {
                                    final PsiAnnotation[] methodAnnotations = method.getAnnotations();
                                    if (methodAnnotations.length > 0) {
                                        buildAnnotations(null, bambooMethod, infoSettingMethodMap, methodAnnotations, info);
                                        bambooClass.getMethods().add(bambooMethod);
                                    }
                                } else {
                                    if (info.getFramework().getName().equals("o_dian_yun")) {
                                        if (info.getSoaType().equals("service")) {
                                            bambooMethod.getMethodUrl().add(method.getName());
                                        }
                                        if (info.getSoaType().equals("client")) {
                                            bambooMethod.getMethodUrl().add(method.getName());
                                        }
                                    }
                                    bambooClass.getMethods().add(bambooMethod);
                                }
                            }
                        }
                    });
                }
                bambooClasses.add(bambooClass);
            }
        });
        return bambooClasses;
    }

    private static boolean methodLevel(Map<String, AnnotationMethodScope> methodScopes, PsiMethod method) {

        final int accessLevel = PsiUtil.getAccessLevel(method.getModifierList());
        final AnnotationMethodScope privateScope = methodScopes.get(MethodScope.PRIVATE.getCode());
        if (privateScope != null) {
            if (accessLevel == PsiUtil.ACCESS_LEVEL_PRIVATE) {
                return true;
            } else {
                return false;
            }
        }
        final AnnotationMethodScope publicScope = methodScopes.get(MethodScope.PUBLIC.getCode());
        if (publicScope != null) {
            if (accessLevel == PsiUtil.ACCESS_LEVEL_PUBLIC) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    private static void buildAnnotations(BambooClass bambooClass, BambooMethod bambooMethod, Map<String, AnnotationInfoSetting> infoSettingMap, PsiAnnotation[] annotations, AnnotationInfoSetting info) {

        Arrays.stream(annotations).forEach(annotation -> {
            Arrays.stream(annotation.getChildren()).collect(Collectors.toMap(e->e.getNode().getElementType().getDebugName(),e->e));
            AnnotationInfoSetting annotationInfoSetting = infoSettingMap.get(annotation.getNameReferenceElement().getCanonicalText());
            if (!Objects.isNull(annotationInfoSetting)) {
                Map<String, AnnotationParam> params = annotationInfoSetting.getParams().stream().collect(Collectors.toMap(AnnotationParam::getName, param -> param));
                if (CollectionUtil.isNotEmpty(params)) {
                    PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();
                    for (PsiNameValuePair attribute : attributes) {
                        PsiAnnotationMemberValue value = attribute.getValue();
                        String name = attribute.getName();
                        if (StringUtil.isEmpty(name)) {
                            name = "null";
                        }
                        AnnotationParam annotationParam = params.get(name);
                        if (!Objects.isNull(annotationParam)) {
                            List<String> values = PsiAnnotationMemberUtil.getValue(value);
                            final String type = annotationParam.getType();
                            if ("poolUrl".equals(type)) {
                                if (info.getFramework().getName().equals("o_dian_yun")) {

                                    if (info.getSoaType().equals("client") && CollectionUtil.isNotEmpty(values)) {
                                        bambooClass.setPoolUrl(values.get(0)+"/cloud");
                                    }
                                }else{
                                    bambooClass.setPoolUrl(values.get(0));
                                }

                            }
                            if ("classUrl".equals(type)) {
                                if (info.getFramework().getName().equals("o_dian_yun")) {
                                    if (info.getSoaType().equals("service") && CollectionUtil.isNotEmpty(values)) {
                                        values = values.stream().map(e -> StringUtil.lowerFirst(e)).collect(Collectors.toList());
                                    }
                                    if (info.getSoaType().equals("client") && CollectionUtil.isNotEmpty(values)) {
                                        values = values.stream().map(e -> {
                                            String[] split = e.split("\\.");
                                            return StringUtil.lowerFirst(split[split.length-1]);
                                        }).collect(Collectors.toList());
                                    }
                                }
                                bambooClass.setClassUrl(values);
                            }
                            if ("methodUrl".equals(type)) {
                                bambooMethod.setMethodUrl(values);
                            }
                            if ("requestMethod".equals(type)) {
                                bambooMethod.setRequestMethods(values);
                            }
                        }
                    }
                }
            }
        });
    }
}
