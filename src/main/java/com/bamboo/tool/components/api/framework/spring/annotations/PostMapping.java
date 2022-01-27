package com.bamboo.tool.components.api.framework.spring.annotations;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.entity.ApiMethod;
import com.bamboo.tool.components.api.enums.FrameworkType;
import com.bamboo.tool.components.api.enums.MethodAnnotationType;
import com.bamboo.tool.components.api.enums.RequestMethod;
import com.bamboo.tool.components.api.factory.MethodAnnotationProcess;
import com.bamboo.tool.util.StringUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiNameValuePair;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PostMapping implements MethodAnnotationProcess {

    @Override
    public void buildMethod(ApiMethod apiMethod, PsiAnnotation annotation, ApiClass apiClass) {
        apiMethod.getMethodTypes().add(RequestMethod.POST.getCode());

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
            //params
            if (Objects.equals(attributeName, "params")) {
                String text = pair.getText();
                if (StringUtil.isNotBlank(text)) {
                    apiMethod.getHeaders().addAll(StringUtil.getAttributes(text,pair));
                }
            }
        }
        if(CollectionUtil.isNotEmpty(apiMethod.getMethodUrls())){
            List<String> classUrls = apiMethod.getMethodUrls()
                    .parallelStream()
                    .map(e -> CharSequenceUtil.addPrefixIfNot(e, "/"))
                    .collect(Collectors.toList());
            apiMethod.setMethodUrls(classUrls);
        }
        apiClass.getClassUrls().parallelStream().forEach(e->{
            List<String> urls = apiMethod.getMethodUrls().parallelStream().map(a -> e + a).collect(Collectors.toList());
            apiMethod.getUrls().addAll(urls);
        });
    }

    @Override
    public MethodAnnotationType getMethodAnnotationType() {
        return MethodAnnotationType.POST_MAPPING;
    }

    @Override
    public String getClassShortName() {
        return "PostMapping";
    }

    @Override
    public FrameworkType getFrameworkType() {
        return FrameworkType.SPRING;
    }
}
