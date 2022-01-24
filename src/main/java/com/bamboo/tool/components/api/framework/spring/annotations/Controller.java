package com.bamboo.tool.components.api.framework.spring.annotations;

import cn.hutool.core.util.StrUtil;
import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.enums.ClassAnnotationType;
import com.bamboo.tool.components.api.enums.FrameworkType;
import com.bamboo.tool.components.api.factory.ClassAnnotationProcess;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import lombok.Data;

import java.util.Objects;

@Data
public class Controller implements ClassAnnotationProcess {


    @Override
    public void buildClass(ApiClass apiMethod, PsiAnnotation psiAnnotation) {
        PsiAnnotationMemberValue value = psiAnnotation.findDeclaredAttributeValue("value");
        if (Objects.isNull(value)) {
            return ;
        }
        if (StrUtil.isNotBlank(value.getText())) {
            apiMethod.getClassUrls().add(value.getText());
        }

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
