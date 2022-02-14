package com.bamboo.tool.components.api.framework.spring.annotations;

import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.entity.ApiMethod;
import com.bamboo.tool.components.api.enums.FrameworkType;
import com.bamboo.tool.components.api.enums.InterfaceType;
import com.bamboo.tool.components.api.enums.MethodAnnotationType;
import com.bamboo.tool.components.api.enums.RequestMethod;
import com.bamboo.tool.components.api.factory.MethodAnnotationProcess;
import com.bamboo.tool.util.PsiUtils;
import com.intellij.psi.PsiAnnotation;

public class DeleteMapping implements MethodAnnotationProcess {

    @Override
    public void buildMethod(ApiMethod apiMethod, PsiAnnotation annotation, ApiClass apiClass) {
        apiMethod.getTypes().add(getFrameworkType().getCode());
        apiMethod.getTypes().add(InterfaceType.SERVICE.getCode());
        PsiUtils.buildValuePair(apiMethod, annotation, apiClass,RequestMethod.DELETE.getCode());
    }

    @Override
    public MethodAnnotationType getMethodAnnotationType() {
        return MethodAnnotationType.DELETE_MAPPING;
    }

    @Override
    public String getClassShortName() {
        return "DeleteMapping";
    }

    @Override
    public FrameworkType getFrameworkType() {
        return FrameworkType.SPRING;
    }
}
