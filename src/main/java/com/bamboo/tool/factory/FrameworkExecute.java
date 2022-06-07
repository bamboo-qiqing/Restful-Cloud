package com.bamboo.tool.factory;

import cn.hutool.core.collection.CollectionUtil;
import com.bamboo.tool.config.model.PsiClassCache;
import com.bamboo.tool.db.service.BambooService;
import com.bamboo.tool.entity.*;
import com.bamboo.tool.enums.AnnotationScope;
import com.bamboo.tool.enums.MethodScope;
import com.bamboo.tool.util.PsiAnnotationMemberUtil;
import com.bamboo.tool.util.PsiUtils;
import com.bamboo.tool.util.RequestMethodUtil;
import com.bamboo.tool.util.StringUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.util.PsiUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Create by GuoQing
 * Date 2022/1/21 14:25
 * Description
 */
public class FrameworkExecute {


    /**
     * 构建当前项目 api集合
     */
    public static List<BambooClass> buildApiMethod(Project project) {

        List<AnnotationInfoSetting> annotationInfoSettings = BambooService.selectAllAnnotationInfoSetting();
        Map<String, AnnotationInfoSetting> infoSettingClassMap = annotationInfoSettings.stream().filter(e -> e.getEffect().contains("attribute")).filter(e -> AnnotationScope.CLASS.getCode().equals(e.getScope().getCode())).collect(Collectors.toMap(AnnotationInfoSetting::getAnnotationPath, e -> e));
        Map<String, AnnotationInfoSetting> infoSettingMethodMap = annotationInfoSettings.stream().filter(e -> e.getEffect().contains("attribute")).filter(e -> AnnotationScope.METHOD.getCode().equals(e.getScope().getCode())).collect(Collectors.toMap(AnnotationInfoSetting::getAnnotationPath, e -> e));
        List<BambooClass> bambooClasses = new ArrayList<>();
        List<PsiClassCache> caches = PsiUtils.getALLPsiClass(project, annotationInfoSettings);
        caches.forEach(cache -> {
            PsiClass psiClass = cache.getPsiClass();
            AnnotationInfoSetting info = cache.getInfo();
            Module module = ModuleUtil.findModuleForPsiElement(psiClass);
            if (module != null) {
                BambooClass bambooClass = new BambooClass();
                bambooClass.setClassName(psiClass.getName());
                bambooClass.setClassPath(Objects.requireNonNull(PsiUtil.getVirtualFile(psiClass)).getPath());
                bambooClass.setModuleName(module.getName());
                bambooClass.setSetting(info);
                String classDescription = FrameworkExecute.getClassDescription(psiClass);

                if (StringUtil.isNotBlank(classDescription)) {
                    BambooDesc bambooDesc = new BambooDesc();
                    bambooDesc.setDescribe(classDescription);
                    bambooDesc.setFramewordCode("javadoc");
                    bambooClass.getDescs().add(bambooDesc);
                }
                PsiAnnotation[] annotations = psiClass.getAnnotations();
                if (annotations.length > 0) {
                    buildAnnotations(bambooClass, null, infoSettingClassMap, annotations, info);
                }
                PsiMethod[] methods = psiClass.getMethods();
                if (methods.length > 0) {
                    Map<String, AnnotationMethodScope> methodScopes = info.getMethodScopes().stream().collect(Collectors.toMap(AnnotationMethodScope::getMethodScope, methodScope -> methodScope));
                    Arrays.stream(methods).forEach(method -> {
                        if (CollectionUtil.isNotEmpty(info.getMethodScopes())) {
                            boolean satisfyScope = methodLevel(methodScopes, method);
                            if (satisfyScope) {
                                BambooMethod bambooMethod = new BambooMethod();
                                bambooMethod.setMethodName(method.getName());
                                bambooMethod.setAccessLevel(PsiUtil.getAccessLevel(method.getModifierList()));

                                AnnotationMethodScope annotationMethodScope = methodScopes.get(MethodScope.ANNOTATION.getCode());
                                if (annotationMethodScope != null) {
                                    PsiAnnotation[] methodAnnotations = method.getAnnotations();
                                    if (methodAnnotations.length > 0) {
                                        boolean annotations1 = buildAnnotations(null, bambooMethod, infoSettingMethodMap, methodAnnotations, info);
                                        if (annotations1) {
                                            bambooMethod.getReturnType().buildReturnType(Objects.requireNonNull(method.getReturnType()));
                                            String methodDescription = FrameworkExecute.getMethodDescription(method);
                                            if (StringUtil.isNotBlank(methodDescription)) {
                                                BambooDesc bambooDesc = new BambooDesc();
                                                bambooDesc.setDescribe(methodDescription);
                                                bambooDesc.setFramewordCode("javadoc");
                                                bambooMethod.getDescs().add(bambooDesc);
                                            }
                                            bambooMethod.buildMethodParams(method.getParameterList());
                                            bambooClass.getMethods().add(bambooMethod);
                                        }

                                    }
                                } else {
                                    if (info.getFramework().getName().equals("o_dian_yun")) {
                                        bambooMethod.getReturnType().buildReturnType(Objects.requireNonNull(method.getReturnType()));
                                        String methodDescription = FrameworkExecute.getMethodDescription(method);
                                        if (StringUtil.isNotBlank(methodDescription)) {
                                            BambooDesc bambooDesc = new BambooDesc();
                                            bambooDesc.setDescribe(methodDescription);
                                            bambooDesc.setFramewordCode("javadoc");
                                            bambooMethod.getDescs().add(bambooDesc);
                                        }
                                        bambooMethod.buildMethodParams(method.getParameterList());
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

    /**
     * 判断方法范围
     */
    private static boolean methodLevel(Map<String, AnnotationMethodScope> methodScopes, PsiMethod method) {

        int accessLevel = PsiUtil.getAccessLevel(method.getModifierList());
        AnnotationMethodScope privateScope = methodScopes.get(MethodScope.PRIVATE.getCode());
        if (privateScope != null) {
            return accessLevel == PsiUtil.ACCESS_LEVEL_PRIVATE;
        }
        AnnotationMethodScope publicScope = methodScopes.get(MethodScope.PUBLIC.getCode());
        if (publicScope != null) {
            return accessLevel == PsiUtil.ACCESS_LEVEL_PUBLIC;
        }
        return true;
    }

    /**
     * 构建注释
     */
    private static boolean buildAnnotations(BambooClass bambooClass, BambooMethod bambooMethod, Map<String, AnnotationInfoSetting> infoSettingMap, PsiAnnotation[] annotations, AnnotationInfoSetting info) {

        AtomicBoolean results = new AtomicBoolean(false);
        Arrays.stream(annotations).forEach(annotation -> {

            AnnotationInfoSetting annotationInfoSetting = infoSettingMap.get(Objects.requireNonNull(annotation.getNameReferenceElement()).getCanonicalText());
            if (!Objects.isNull(annotationInfoSetting)) {
                results.set(true);
                String requestMethod = RequestMethodUtil.REQUEST_METHOD.get(annotationInfoSetting.getAnnotationPath());
                if (StringUtil.isNotEmpty(requestMethod)) {
                    bambooMethod.getRequestMethods().add(requestMethod);
                }
                Map<String, AnnotationParam> params = annotationInfoSetting.getParams().stream().collect(Collectors.toMap(AnnotationParam::getName, param -> param));
                if (CollectionUtil.isNotEmpty(params)) {
                    PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();
                    if (attributes.length > 0) {
                        buildAttributes(bambooClass, bambooMethod, info, annotationInfoSetting, params, attributes);
                    } else {
                        if (StringUtil.isNotBlank(requestMethod)) {
                            bambooMethod.getMethodUrl().add("");
                        }
                    }

                }
            }
        });
        return results.get();
    }

    private static void buildAttributes(BambooClass bambooClass, BambooMethod bambooMethod, AnnotationInfoSetting info, AnnotationInfoSetting annotationInfoSetting, Map<String, AnnotationParam> params, PsiNameValuePair[] attributes) {
        for (PsiNameValuePair attribute : attributes) {
            PsiAnnotationMemberValue value = attribute.getValue();
            String name = StringUtil.isEmpty(attribute.getName()) ? "null" : attribute.getName();
            AnnotationParam annotationParam = params.get(name);
            if (!Objects.isNull(annotationParam)) {
                List<String> values = PsiAnnotationMemberUtil.getValue(value);
                String type = annotationParam.getType();
                if (bambooMethod != null) {
                    buildAttributesMethod(bambooMethod, annotationInfoSetting, values, type);
                }
                if (bambooClass != null) {
                    buildAttributesClass(bambooClass, info, annotationInfoSetting, values, type);
                }

            }
        }
    }

    private static void buildAttributesClass(BambooClass bambooClass, AnnotationInfoSetting info, AnnotationInfoSetting annotationInfoSetting, List<String> values, String type) {
        if ("poolUrl".equals(type)) {
            if (info.getFramework().getName().equals("o_dian_yun")) {
                if (info.getSoaType().equals("client") && CollectionUtil.isNotEmpty(values)) {
                    bambooClass.setPoolUrl(values.get(0) + "/cloud");
                }
            } else {
                bambooClass.setPoolUrl(values.get(0));
            }
        } else if ("classUrl".equals(type)) {
            if (info.getFramework().getName().equals("o_dian_yun")) {
                if (info.getSoaType().equals("service") && CollectionUtil.isNotEmpty(values)) {
                    values = values.stream().map(StringUtil::lowerFirst).collect(Collectors.toList());
                }
                if (info.getSoaType().equals("client") && CollectionUtil.isNotEmpty(values)) {
                    values = values.stream().map(e -> {
                        String[] split = e.split("\\.");
                        return StringUtil.lowerFirst(split[split.length - 1]);
                    }).collect(Collectors.toList());
                }
            }
            bambooClass.setClassUrl(values);
        } else if ("consumes".equals(type)) {
            bambooClass.setConsumes(values.toString());
        } else if ("params".equals(type)) {
            bambooClass.setParams(values.toString());
        } else if ("headers".equals(type)) {
            bambooClass.setHeaders(values.toString());
        } else if ("produces".equals(type)) {
            bambooClass.setProduces(values.toString());
        } else if ("desc".equals(type)) {
            BambooDesc bambooDesc = new BambooDesc();
            String frameworkName = annotationInfoSetting.getFramework().getName();
            bambooDesc.setFramewordCode(frameworkName);
            bambooDesc.setDescribe(values.get(0));
            if (bambooClass != null && StringUtil.isNotBlank(values.get(0))) {
                bambooClass.getDescs().add(bambooDesc);
            }

        }
    }

    private static void buildAttributesMethod(BambooMethod bambooMethod, AnnotationInfoSetting annotationInfoSetting, List<String> values, String type) {
        if ("methodUrl".equals(type)) {
            bambooMethod.setMethodUrl(values);
        } else if ("requestMethod".equals(type)) {
            bambooMethod.getRequestMethods().addAll(values);
        } else if ("consumes".equals(type)) {
            bambooMethod.setConsumes(values.toString());
        } else if ("params".equals(type)) {
            bambooMethod.setParams(values.toString());
        } else if ("headers".equals(type)) {
            bambooMethod.setHeaders(values.toString());
        } else if ("produces".equals(type)) {
            bambooMethod.setProduces(values.toString());
        } else if ("desc".equals(type)) {
            BambooDesc bambooDesc = new BambooDesc();
            String frameworkName = annotationInfoSetting.getFramework().getName();
            bambooDesc.setFramewordCode(frameworkName);
            bambooDesc.setDescribe(values.get(0));
            if (bambooMethod != null && StringUtil.isNotBlank(values.get(0))) {
                bambooMethod.getDescs().add(bambooDesc);
            }
        }
    }

    public static String getMethodDescription(PsiMethod psiMethod) {
        PsiDocComment docComment = psiMethod.getDocComment();
        StringBuilder commentStringBuilder = new StringBuilder();
        if (docComment != null) {
            PsiElement[] descriptionElements = docComment.getDescriptionElements();
            for (PsiElement descriptionElement : descriptionElements) {
                if (descriptionElement instanceof PsiDocToken) {
                    commentStringBuilder.append(StringUtil.replace(descriptionElement.getText(), "'", "`")).append("\n");
                }
            }
        }
        return commentStringBuilder.toString().trim();
    }

    public static String getClassDescription(PsiClass psiClass) {
        PsiDocComment docComment = psiClass.getDocComment();
        StringBuilder commentStringBuilder = new StringBuilder();
        if (docComment != null) {
            PsiElement[] descriptionElements = docComment.getDescriptionElements();
            for (PsiElement descriptionElement : descriptionElements) {
                if (descriptionElement instanceof PsiDocToken) {
                    commentStringBuilder.append(StringUtil.replace(descriptionElement.getText(), "'", "`")).append("\n");
                }
            }
        }
        return commentStringBuilder.toString().trim();
    }
}
