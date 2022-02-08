package com.bamboo.tool.components.api.framework.spring.annotations;

import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.enums.ClassAnnotationType;
import com.bamboo.tool.components.api.enums.FrameworkType;
import com.bamboo.tool.components.api.factory.ClassAnnotationProcess;
import com.intellij.psi.PsiAnnotation;
import lombok.Data;

@Data
public class Controller implements ClassAnnotationProcess {


    @Override
    public void buildClass(ApiClass apiMethod, PsiAnnotation psiAnnotation) {

    }

    @Override
    public ClassAnnotationType getClassAnnotationType() {
        return ClassAnnotationType.CONTROLLER;
    }

    @Override
    public String getClassShortName() {
        return "Controller";
    }

    @Override
    public FrameworkType getFrameworkType() {
        return FrameworkType.SPRING;
    }

}
