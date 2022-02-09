package com.bamboo.tool.components.api.framework.spring.annotations;

import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.enums.ClassAnnotationType;
import com.bamboo.tool.components.api.enums.FrameworkType;
import com.bamboo.tool.components.api.enums.InterfaceType;
import com.bamboo.tool.components.api.factory.ClassAnnotationProcess;
import com.intellij.psi.PsiAnnotation;
import lombok.Data;

@Data
public class RestController implements ClassAnnotationProcess {
    @Override
    public void buildClass(ApiClass apiMethod, PsiAnnotation psiAnnotation) {
        apiMethod.getTypes().add(getFrameworkType().getCode());
        apiMethod.getTypes().add(InterfaceType.SERVICE.getCode());
    }

    @Override
    public ClassAnnotationType getClassAnnotationType() {
        return ClassAnnotationType.REST_CONTROLLER;
    }

    @Override
    public String getClassShortName() {
        return "RestController";
    }

    @Override
    public FrameworkType getFrameworkType() {
        return FrameworkType.SPRING;
    }


}
