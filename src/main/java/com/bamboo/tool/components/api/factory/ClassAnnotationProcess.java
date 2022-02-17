package com.bamboo.tool.components.api.factory;

import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.enums.AnnotationType;
import com.bamboo.tool.components.api.enums.FrameworkType;
import com.intellij.psi.PsiAnnotation;

public interface ClassAnnotationProcess {

    void buildClass(ApiClass apiClass, PsiAnnotation psiAnnotation);

    AnnotationType getClassAnnotationType();

    String getClassShortName();

    FrameworkType getFrameworkType();
}
