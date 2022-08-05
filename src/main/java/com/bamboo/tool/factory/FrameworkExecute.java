package com.bamboo.tool.factory;

import cn.hutool.core.collection.CollectionUtil;
import com.bamboo.tool.config.BambooApisComponent;
import com.bamboo.tool.config.model.PsiClassCache;
import com.bamboo.tool.entity.*;
import com.bamboo.tool.enums.AnnotationScope;
import com.bamboo.tool.enums.AttributeEnums;
import com.bamboo.tool.enums.RequestMethod;
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
        List<AnnotationInfoSetting> annotationInfoSettings = BambooApisComponent.getStoreService().getAllAnnotation();
        List<String> scanMethods = annotationInfoSettings.parallelStream().filter(e -> e.getEffect().contains("attribute")).filter(e -> AnnotationScope.METHOD.getCode().equals(e.getScope())).map(e -> e.getAnnotationName()).collect(Collectors.toList());
        List<AnnotationInfoSetting> attributeClass = annotationInfoSettings.parallelStream().filter(e -> e.getEffect().contains("attribute")).filter(e -> AnnotationScope.CLASS.getCode().equals(e.getScope())).collect(Collectors.toList());
        Map<String, AnnotationInfoSetting> attributeMethod = annotationInfoSettings.parallelStream().filter(e -> e.getEffect().contains("attribute")).filter(e -> AnnotationScope.METHOD.getCode().equals(e.getScope())).collect(Collectors.toMap(e -> e.getAnnotationName(), e -> e));

        List<BambooClass> bambooClasses = new ArrayList<>();
        List<PsiClassCache> caches = PsiUtils.getALLPsiClass(project, annotationInfoSettings);
        caches.forEach(cache -> {
            AnnotationInfoSetting info = cache.getSetting();
            cache.getAnnotations().stream().forEach(e -> {
                PsiClass psiClass = (PsiClass) e.getParent().getParent();
                Module module = ModuleUtil.findModuleForPsiElement(psiClass);
                if (module != null) {
                    BambooClass bambooClass = new BambooClass();
                    bambooClass.setClassName(psiClass.getName());
                    bambooClass.setClassPath(Objects.requireNonNull(PsiUtil.getVirtualFile(psiClass)).getPath());
                    bambooClass.setModuleName(module.getName());
                    bambooClass.setSetting(info);
                    String classDescription = FrameworkExecute.getClassDescription(psiClass);
                    bambooClass.setDescription(classDescription);
                    bambooClass.setJavaDocComment(JavaDocComment.buildJavaDocComment(psiClass.getDocComment()));
                    // 构建注解信息
                    buildClassAnnotationInfo(psiClass, bambooClass);
                    bambooClass.buildMethods(psiClass.getMethods(), scanMethods);
                    attributeClass.parallelStream().forEach(setting -> {
                        Map<String, AnnotationInfo> annotationInfoMap = bambooClass.getAnnotationInfoMap();
                        AnnotationInfo annotationInfo = annotationInfoMap.get(setting.getAnnotationName());
                        if (!Objects.isNull(annotationInfo) && CollectionUtil.isNotEmpty(annotationInfo.getAnnotationAttributs())) {
                            Map<String, List<String>> annotationAttributs = annotationInfo.getAnnotationAttributs();
                            List<AnnotationParam> params = setting.getParams();
                            params.parallelStream().forEach(param -> {
                                List<String> strings = annotationAttributs.get(param.getName());
                                if (AttributeEnums.POOL_URL.getCode().equals(param.getType()) && CollectionUtil.isNotEmpty(strings)) {
                                    if (StringUtil.isNotEmpty(strings.get(0))) {
                                        boolean startWith = StringUtil.startWith(strings.get(0), "/");
                                        if (!startWith) {
                                            bambooClass.setPoolUrl("/" + strings.get(0));
                                        } else {
                                            bambooClass.setPoolUrl(strings.get(0));
                                        }
                                    }
                                }

                                if (AttributeEnums.CLASS_URL.getCode().equals(param.getType()) && CollectionUtil.isNotEmpty(strings)) {
                                    List<String> classUrls = strings.stream().map(classUrl -> {
                                        if (StringUtil.isNotEmpty(classUrl)) {
                                            boolean classStartWith = StringUtil.startWith(classUrl, "/");
                                            if (!classStartWith) {
                                                classUrl = "/" + classUrl;
                                            }
                                        }
                                        return classUrl;
                                    }).collect(Collectors.toList());
                                    bambooClass.getClassUrl().addAll(classUrls);
                                }
                            });
                        }
                    });
                    bambooClass.getMethods().parallelStream().forEach(method -> {
                        Map<String, AnnotationInfo> annotationInfoMap = method.getAnnotationInfoMap();
                        annotationInfoMap.values().stream().filter(annotationInfo -> annotationInfo.getAnnotationName() != null).forEach(s -> {
                            AnnotationInfoSetting annotationInfoSetting = attributeMethod.get(s.getAnnotationName());
                            if (!Objects.isNull(annotationInfoSetting)) {
                                Map<String, List<String>> annotationAttributs = s.getAnnotationAttributs();
                                List<AnnotationParam> params = annotationInfoSetting.getParams();
                                params.forEach(param -> {
                                    List<String> strings = annotationAttributs.get(param.getName());
                                    if (CollectionUtil.isNotEmpty(strings)) {
                                        if (AttributeEnums.METHOD_URL.getCode().equals(param.getType())) {
                                            List<String> urls = new ArrayList<>();
                                            strings.stream().forEach(methodUrl -> {
                                                boolean methodStartWith = StringUtil.startWith(methodUrl, "/");
                                                if (!methodStartWith) {
                                                    methodUrl = "/" + methodUrl;
                                                }
                                                if (CollectionUtil.isNotEmpty(bambooClass.getClassUrl())) {
                                                    for (String classUrl : bambooClass.getClassUrl()) {
                                                        urls.add(bambooClass.getPoolUrl() + classUrl + methodUrl);
                                                    }
                                                }
                                            });
                                            method.getMethodUrl().addAll(urls);
                                        } else if (AttributeEnums.REQUEST_METHOD.getCode().equals(param.getType())) {
                                            method.getRequestMethods().addAll(strings);
                                        }
                                    }
                                });
                                Map<String, List<String>> otherParams = annotationInfoSetting.getOtherParams();
                                otherParams.forEach((key, value) -> {
                                    List<String> requestMethods = otherParams.get(AttributeEnums.REQUEST_METHOD.getCode());
                                    if (CollectionUtil.isNotEmpty(requestMethods)) {
                                        method.getRequestMethods().addAll(requestMethods);
                                    }
                                    List<String> methodUrls = otherParams.get(AttributeEnums.METHOD_URL.getCode());
                                    if (CollectionUtil.isNotEmpty(methodUrls)) {
                                        method.getMethodUrl().addAll(methodUrls);
                                    }
                                });
                            }
                        });
                        if (CollectionUtil.isEmpty(method.getRequestMethods())) {
                            method.getRequestMethods().add(RequestMethod.ALL.getCode());
                        }
                    });
                    bambooClasses.add(bambooClass);
                }
            });

        });
        return bambooClasses;
    }

    public static void buildClassAnnotationInfo(PsiClass psiClass, BambooClass bambooClass) {
        Map<String, AnnotationInfo> stringAnnotationInfoMap = builAnnotationInfo(psiClass.getAnnotations());
        bambooClass.setAnnotationInfoMap(stringAnnotationInfoMap);
    }

    public static void buildMethodAnnotationInfo(PsiMethod psiMethod, BambooMethod method) {
        Map<String, AnnotationInfo> stringAnnotationInfoMap = builAnnotationInfo(psiMethod.getAnnotations());
        method.setAnnotationInfoMap(stringAnnotationInfoMap);
    }

    public static Map<String, AnnotationInfo> builAnnotationInfo(PsiAnnotation[] annotations) {
        Map<String, AnnotationInfo> annotationInfoMap = Arrays.stream(annotations).map(annotation -> {
            AnnotationInfo annotationInfo = new AnnotationInfo();
            annotationInfo.setAnnotationName(annotation.getNameReferenceElement().getReferenceName());
            Map<String, List<String>> stringListMap = buildAttributes(annotation.getParameterList().getAttributes());
            annotationInfo.setAnnotationAttributs(stringListMap);
            return annotationInfo;
        }).collect(Collectors.toMap(AnnotationInfo::getAnnotationName, a -> a));
        return annotationInfoMap;
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
                        Map<String, List<String>> stringListMap = buildAttributes(attributes);
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

    public static Map<String, List<String>> buildAttributes(PsiNameValuePair[] attributes) {
        Map<String, List<String>> attributesMap = new HashMap<>();
        for (PsiNameValuePair attribute : attributes) {
            PsiAnnotationMemberValue value = attribute.getValue();
            String name = StringUtil.isEmpty(attribute.getName()) ? "null" : attribute.getName();
            attributesMap.put(name, PsiAnnotationMemberUtil.getValue(value));
        }
        return attributesMap;
//        for (PsiNameValuePair attribute : attributes) {
//            PsiAnnotationMemberValue value = attribute.getValue();
//            String name = StringUtil.isEmpty(attribute.getName()) ? "null" : attribute.getName();
//            AnnotationParam annotationParam = params.get(name);
//            if (!Objects.isNull(annotationParam)) {
//                List<String> values = PsiAnnotationMemberUtil.getValue(value);
//                String type = annotationParam.getType();
//                if (bambooMethod != null) {
//                    buildAttributesMethod(bambooMethod, annotationInfoSetting, values, type);
//                }
//                if (bambooClass != null) {
//                    buildAttributesClass(bambooClass, info, annotationInfoSetting, values, type);
//                }
//
//            }
//        }
    }

//    private static void buildAttributesClass(BambooClass bambooClass, AnnotationInfoSetting info, AnnotationInfoSetting annotationInfoSetting, List<String> values, String type) {
//        if ("poolUrl".equals(type)) {
//            if (info.getFramework().getName().equals("o_dian_yun")) {
//                if (info.getSoaType().getCode().equals("soa_client") && CollectionUtil.isNotEmpty(values)) {
//                    bambooClass.setPoolUrl(values.get(0) + "/cloud");
//                }
//            } else {
//                bambooClass.setPoolUrl(values.get(0));
//            }
//        } else if ("classUrl".equals(type)) {
//            if (info.getFramework().getName().equals("o_dian_yun")) {
//                if (info.getSoaType().getCode().equals("soa_service") && CollectionUtil.isNotEmpty(values)) {
//                    values = values.stream().map(StringUtil::lowerFirst).collect(Collectors.toList());
//                }
//                if (info.getSoaType().getCode().equals("soa_client") && CollectionUtil.isNotEmpty(values)) {
//                    values = values.stream().map(e -> {
//                        String[] split = e.split("\\.");
//                        return StringUtil.lowerFirst(split[split.length - 1]);
//                    }).collect(Collectors.toList());
//                }
//            }
//            bambooClass.setClassUrl(values);
//        } else if ("consumes".equals(type)) {
//            bambooClass.setConsumes(values.toString());
//        } else if ("params".equals(type)) {
//            bambooClass.setParams(values.toString());
//        } else if ("headers".equals(type)) {
//            bambooClass.setHeaders(values.toString());
//        } else if ("produces".equals(type)) {
//            bambooClass.setProduces(values.toString());
//        } else if ("desc".equals(type)) {
//            BambooDesc bambooDesc = new BambooDesc();
//            String frameworkName = annotationInfoSetting.getFramework().getName();
//            bambooDesc.setFramewordCode(frameworkName);
//            bambooDesc.setDescribe(values.get(0));
//            if (bambooClass != null && StringUtil.isNotBlank(values.get(0))) {
//                bambooClass.getDescs().add(bambooDesc);
//            }
//
//        }
//    }    private static void buildAttributesClass(BambooClass bambooClass, AnnotationInfoSetting info, AnnotationInfoSetting annotationInfoSetting, List<String> values, String type) {
//        if ("poolUrl".equals(type)) {
//            if (info.getFramework().getName().equals("o_dian_yun")) {
//                if (info.getSoaType().getCode().equals("soa_client") && CollectionUtil.isNotEmpty(values)) {
//                    bambooClass.setPoolUrl(values.get(0) + "/cloud");
//                }
//            } else {
//                bambooClass.setPoolUrl(values.get(0));
//            }
//        } else if ("classUrl".equals(type)) {
//            if (info.getFramework().getName().equals("o_dian_yun")) {
//                if (info.getSoaType().getCode().equals("soa_service") && CollectionUtil.isNotEmpty(values)) {
//                    values = values.stream().map(StringUtil::lowerFirst).collect(Collectors.toList());
//                }
//                if (info.getSoaType().getCode().equals("soa_client") && CollectionUtil.isNotEmpty(values)) {
//                    values = values.stream().map(e -> {
//                        String[] split = e.split("\\.");
//                        return StringUtil.lowerFirst(split[split.length - 1]);
//                    }).collect(Collectors.toList());
//                }
//            }
//            bambooClass.setClassUrl(values);
//        } else if ("consumes".equals(type)) {
//            bambooClass.setConsumes(values.toString());
//        } else if ("params".equals(type)) {
//            bambooClass.setParams(values.toString());
//        } else if ("headers".equals(type)) {
//            bambooClass.setHeaders(values.toString());
//        } else if ("produces".equals(type)) {
//            bambooClass.setProduces(values.toString());
//        } else if ("desc".equals(type)) {
//            BambooDesc bambooDesc = new BambooDesc();
//            String frameworkName = annotationInfoSetting.getFramework().getName();
//            bambooDesc.setFramewordCode(frameworkName);
//            bambooDesc.setDescribe(values.get(0));
//            if (bambooClass != null && StringUtil.isNotBlank(values.get(0))) {
//                bambooClass.getDescs().add(bambooDesc);
//            }
//
//        }
//    }

    private static void buildAttributesMethod(BambooMethod bambooMethod, AnnotationInfoSetting annotationInfoSetting, List<String> values, String type) {
        if ("methodUrl".equals(type)) {
            bambooMethod.setMethodUrl(values);
        } else if ("requestMethod".equals(type)) {
            bambooMethod.getRequestMethods().addAll(values);
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
