package com.bamboo.tool.components.api.framework.odianyun.annotations;

import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.entity.ApiMethod;
import com.bamboo.tool.components.api.enums.FrameworkType;
import com.bamboo.tool.components.api.enums.InterfaceType;
import com.bamboo.tool.components.api.enums.MethodAnnotationType;
import com.bamboo.tool.components.api.enums.RequestMethod;
import com.bamboo.tool.components.api.factory.MethodAnnotationProcess;
import com.bamboo.tool.util.StringUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SoaMethodRegister implements MethodAnnotationProcess {
    @Override
    public void buildMethod(ApiMethod apiMethod, PsiAnnotation annotation, ApiClass apiClass) {
        apiMethod.getTypes().add(getFrameworkType().getCode());
        apiMethod.getTypes().add(InterfaceType.SERVICE.getCode());
        apiMethod.getMethodTypes().add(RequestMethod.ALL.getCode());
        PsiNameValuePair[] pairs = annotation.getParameterList().getAttributes();
        if (pairs == null && pairs.length < 1) {
            return;
        }
        for (PsiNameValuePair pair : pairs) {
            String attributeName = pair.getName();
            if (Objects.equals(attributeName, "desc")) {
                apiMethod.setDescription(pair.getValue().getText());
            }
        }
        apiMethod.getMethodUrls().add(apiMethod.getMethodName());
    }

    @Override
    public MethodAnnotationType getMethodAnnotationType() {
        return MethodAnnotationType.SOA_METHOD_REGISTER;
    }

    @Override
    public String getClassShortName() {
        return "SoaMethodRegister";
    }

    @Override
    public FrameworkType getFrameworkType() {
        return FrameworkType.O_DIAN_YUN;
    }

}
