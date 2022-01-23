package com.bamboo.tool.components.api.factory;

import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.enums.ClassAnnotationType;
import com.bamboo.tool.components.api.enums.FrameworkType;
import com.intellij.psi.PsiAnnotation;

public interface ClassAnnotationProcess {

    void buildClass(ApiClass apiMethod, PsiAnnotation psiAnnotation);

    ClassAnnotationType getClassAnnotationType();

    String getClassShortName();

    FrameworkType getFrameworkType();
}
