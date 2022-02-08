package com.bamboo.tool.components.api.framework.odianyun.annotations;

import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.enums.ClassAnnotationType;
import com.bamboo.tool.components.api.enums.FrameworkType;
import com.bamboo.tool.components.api.factory.ClassAnnotationProcess;
import com.bamboo.tool.util.StringUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiNameValuePair;

import java.util.Objects;

public class SoaServiceRegister implements ClassAnnotationProcess {
    @Override
    public void buildClass(ApiClass apiClass, PsiAnnotation psiAnnotation) {
        PsiNameValuePair[] pairs = psiAnnotation.getParameterList().getAttributes();
        for (PsiNameValuePair pair : pairs) {
            String attributeName = pair.getName();
            if(Objects.equals(attributeName, "interfaceClass")){
                String text = pair.getValue().getText();
                if(text.contains(".class")){
                    text = text.replaceAll(".class", "");
                }
                apiClass.getClassUrls().add("/"+apiClass.getModuleName()+"/cloud/"+text);
            }
        }
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
