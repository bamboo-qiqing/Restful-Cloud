package com.bamboo.tool.components.api.factory;

import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.enums.ClassAnnotationType;
import com.bamboo.tool.components.api.enums.FrameworkType;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;

public interface ClassAnnotationProcess {

    void buildClass(ApiClass apiClass, PsiAnnotation psiAnnotation);

    ClassAnnotationType getClassAnnotationType();

    String getClassShortName();

    FrameworkType getFrameworkType();
}
