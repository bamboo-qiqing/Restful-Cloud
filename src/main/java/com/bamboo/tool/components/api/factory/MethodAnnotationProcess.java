package com.bamboo.tool.components.api.factory;

import com.bamboo.tool.components.api.entity.ApiMethod;
import com.bamboo.tool.components.api.enums.FrameworkType;
import com.bamboo.tool.components.api.enums.MethodAnnotationType;
import com.intellij.psi.PsiAnnotation;

public interface MethodAnnotationProcess {

    void buildMethod(ApiMethod apiMethod, PsiAnnotation psiAnnotation);

    MethodAnnotationType getMethodAnnotationType();

    String getClassShortName();

    FrameworkType getFrameworkType();
}
