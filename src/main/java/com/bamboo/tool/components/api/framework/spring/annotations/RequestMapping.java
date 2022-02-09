package com.bamboo.tool.components.api.framework.spring.annotations;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.entity.ApiMethod;
import com.bamboo.tool.components.api.enums.*;
import com.bamboo.tool.components.api.factory.ClassAnnotationProcess;
import com.bamboo.tool.components.api.factory.MethodAnnotationProcess;
import com.bamboo.tool.util.PsiUtil;
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
        apiMethod.getTypes().add(getFrameworkType().getCode());
        apiMethod.getTypes().add(InterfaceType.SERVICE.getCode());
        PsiNameValuePair[] pairs = annotation.getParameterList().getAttributes();
        if (pairs == null && pairs.length < 1) {
            return;
        }

        for (PsiNameValuePair pair : pairs) {
            String attributeName = pair.getName();
            if (Objects.equals(attributeName, "value")) {
                String text = pair.getValue().getText();
                if (StringUtil.isNotBlank(text)) {
                    apiMethod.getClassUrls().addAll(StringUtil.getAttributes(text, pair));
                }
            }
            if (Objects.equals(attributeName, "path")) {
                String text = pair.getValue().getText();
                if (StringUtil.isNotBlank(text)) {
                    apiMethod.getClassUrls().addAll(StringUtil.getAttributes(text, pair));
                }
            }
            if (StringUtil.isBlank(attributeName)) {
                String text = pair.getText();
                if (StringUtil.isNotBlank(text)) {
                    apiMethod.getClassUrls().addAll(StringUtil.getAttributes(text, pair));
                }
            }

        }
        if (CollectionUtil.isNotEmpty(apiMethod.getClassUrls())) {
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
    public void buildMethod(ApiMethod apiMethod, PsiAnnotation annotation, ApiClass apiClass) {
        apiMethod.getTypes().add(getFrameworkType().getCode());
        apiMethod.getTypes().add(InterfaceType.SERVICE.getCode());
        PsiUtil.buildValuePair(apiMethod, annotation, apiClass,null);
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
