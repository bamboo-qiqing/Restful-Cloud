package com.bamboo.tool.components.api.framework.spring.annotations;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.entity.ApiMethod;
import com.bamboo.tool.components.api.enums.ClassAnnotationType;
import com.bamboo.tool.components.api.enums.FrameworkType;
import com.bamboo.tool.components.api.enums.MethodAnnotationType;
import com.bamboo.tool.components.api.enums.RequestMethod;
import com.bamboo.tool.components.api.factory.ClassAnnotationProcess;
import com.bamboo.tool.components.api.factory.MethodAnnotationProcess;
import com.bamboo.tool.util.StringUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiNameValuePair;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class RequestMapping implements ClassAnnotationProcess, MethodAnnotationProcess {

    @Override
    public void buildClass(ApiClass apiMethod, PsiAnnotation annotation) {
        PsiNameValuePair[] pairs = annotation.getParameterList().getAttributes();
        if (pairs == null && pairs.length < 1) {
            return;
        }

        for (PsiNameValuePair pair : pairs) {
            String attributeName = pair.getName();
            // value和path 属性获取
            if (Objects.equals(attributeName, "value")) {
                String text = pair.getValue().getText();
                if (StringUtil.isNotBlank(text)) {
                    apiMethod.getClassUrls().addAll(StringUtil.getAttributes(text,pair));
                }
            }
            if (Objects.equals(attributeName, "path")) {
                String text = pair.getValue().getText();
                if (StringUtil.isNotBlank(text)) {
                    apiMethod.getClassUrls().addAll(StringUtil.getAttributes(text,pair));
                }
            }
            if ( StringUtil.isBlank(attributeName)) {
                String text = pair.getText();
                if (StringUtil.isNotBlank(text)) {
                    apiMethod.getClassUrls().addAll(StringUtil.getAttributes(text,pair));
                }
            }

        }
        if(CollectionUtil.isNotEmpty(apiMethod.getClassUrls())){
            List<String> classUrls = apiMethod.getClassUrls()
                    .parallelStream()
                    .map(e -> CharSequenceUtil.addPrefixIfNot(e, "/"))
                    .collect(Collectors.toList());
            apiMethod.setClassUrls(classUrls);
        }
    }

    @Override
    public ClassAnnotationType getClassAnnotationType() {
        return ClassAnnotationType.REQUEST_MAPPING;
    }

    @Override
    public void buildMethod(ApiMethod apiMethod, PsiAnnotation annotation) {
        PsiNameValuePair[] pairs = annotation.getParameterList().getAttributes();
        if (pairs == null && pairs.length < 1) {
            return;
        }
        for (PsiNameValuePair pair : pairs) {
            String attributeName = pair.getName();
            // url
            if (Objects.equals(attributeName, "value") || Objects.equals(attributeName, "path")||StringUtil.isBlank(attributeName)) {
                String text = pair.getText();
                if (StringUtil.isNotBlank(text)) {
                    apiMethod.getMethodUrls().addAll(StringUtil.getAttributes(text,pair));
                }
            }
            // method
            if (Objects.equals(attributeName, "method")) {
                String text = pair.getText();
                if (StringUtil.isNotBlank(text)) {
                    apiMethod.getMethodTypes().addAll(StringUtil.getRequestMethod(text));
                }
            }
            //ContentType
            if (Objects.equals(attributeName, "produces") || Objects.equals(attributeName, "consumes")) {
                String text = pair.getText();
                if (StringUtil.isNotBlank(text)) {
                    apiMethod.getContentTypes().addAll(StringUtil.getAttributes(text,pair));
                }
            }
            //headers
            if (Objects.equals(attributeName, "headers")) {
                String text = pair.getText();
                if (StringUtil.isNotBlank(text)) {
                    apiMethod.getHeaders().addAll(StringUtil.getAttributes(text,pair));
                }
            }
        }
        if (CollectionUtil.isEmpty(apiMethod.getMethodTypes())) {
            apiMethod.getMethodTypes().add(RequestMethod.ALL.getCode());
        }
        if(CollectionUtil.isNotEmpty(apiMethod.getMethodUrls())){
            List<String> classUrls = apiMethod.getMethodUrls()
                    .parallelStream()
                    .map(e -> CharSequenceUtil.addPrefixIfNot(e, "/"))
                    .collect(Collectors.toList());
            apiMethod.setMethodUrls(classUrls);
        }
    }

    @Override
    public MethodAnnotationType getMethodAnnotationType() {
        return MethodAnnotationType.REQUEST_MAPPING;
    }

    @Override
    public String getClassShortName() {
        return "RequestMapping";
    }

    @Override
    public FrameworkType getFrameworkType() {
        return FrameworkType.SPRING;
    }

}