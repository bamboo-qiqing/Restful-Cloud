package com.bamboo.tool.components.api.framework.swagger.annotations;

import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.entity.ApiMethod;
import com.bamboo.tool.components.api.enums.FrameworkType;
import com.bamboo.tool.components.api.enums.MethodAnnotationType;
import com.bamboo.tool.components.api.factory.MethodAnnotationProcess;
import com.intellij.psi.PsiAnnotation;

/**
 * Create by GuoQing
 * Date 2022/2/14 17:31
 * Description
 */
public class ApiOperation implements MethodAnnotationProcess {
    @Override
    public void buildMethod(ApiMethod apiMethod, PsiAnnotation psiAnnotation, ApiClass apiClass) {

    }

    @Override
    public MethodAnnotationType getMethodAnnotationType() {
//        return MethodAnnotationType.API_OPERATION;
        return null;
    }

    @Override
    public String getClassShortName() {
        return "ApiOperation";
    }

    @Override
    public FrameworkType getFrameworkType() {
        return FrameworkType.SWAGGER;
    }
}
