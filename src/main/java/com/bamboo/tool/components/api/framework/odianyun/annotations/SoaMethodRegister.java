package com.bamboo.tool.components.api.framework.odianyun.annotations;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.entity.ApiMethod;
import com.bamboo.tool.components.api.enums.FrameworkType;
import com.bamboo.tool.components.api.enums.MethodAnnotationType;
import com.bamboo.tool.components.api.factory.MethodAnnotationProcess;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiNameValuePair;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SoaMethodRegister implements MethodAnnotationProcess {
    @Override
    public void buildMethod(ApiMethod apiMethod, PsiAnnotation annotation, ApiClass apiClass) {
        PsiNameValuePair[] pairs = annotation.getParameterList().getAttributes();
        if (pairs == null && pairs.length < 1) {
            return;
        }
        for (PsiNameValuePair pair : pairs) {
            String attributeName = pair.getName();
            if (Objects.equals(attributeName, "desc")) {
                apiMethod.setDescription(pair.getValue().getText());
            }
            apiMethod.getMethodUrls().add("/"+apiMethod.getMethodName());
        }
        if (CollectionUtil.isNotEmpty(apiMethod.getMethodUrls())) {
            List<String> classUrls = apiMethod.getMethodUrls().parallelStream().map(e -> CharSequenceUtil.addPrefixIfNot(e, "/")).collect(Collectors.toList());
            apiMethod.setMethodUrls(classUrls);
            apiClass.getClassUrls().parallelStream().forEach(e -> {
                List<String> urls = apiMethod.getMethodUrls().parallelStream().map(a -> e + a).collect(Collectors.toList());
                apiMethod.getUrls().addAll(urls);
            });
        }
    }

    @Override
    public MethodAnnotationType getMethodAnnotationType() {
        return MethodAnnotationType.SOA_METHOD_REGISTER;
    }

    @Override
    public String getClassShortName() {
        return "SoaMethodRegister";
    }

    @Override
    public FrameworkType getFrameworkType() {
        return FrameworkType.O_DIAN_YUN;
    }
}
