package com.bamboo.tool.components.api.framework.odianyun.annotations;

import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.enums.ClassAnnotationType;
import com.bamboo.tool.components.api.enums.FrameworkType;
import com.bamboo.tool.components.api.factory.ClassAnnotationProcess;
import com.intellij.psi.PsiAnnotation;

public class SoaServiceRegister implements ClassAnnotationProcess {
    @Override
    public void buildClass(ApiClass apiClass, PsiAnnotation psiAnnotation) {
        System.out.printf("1");
    }

    @Override
    public ClassAnnotationType getClassAnnotationType() {
        return ClassAnnotationType.SOA_SERVICE_REGISTER;
    }

    @Override
    public String getClassShortName() {
        return "SoaServiceRegister";
    }

    @Override
    public FrameworkType getFrameworkType() {
        return FrameworkType.O_DIAN_YUN;
    }
}
