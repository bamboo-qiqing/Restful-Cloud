package com.bamboo.tool.components.api.framework.odianyun.annotations;

import cn.hutool.core.text.CharSequenceUtil;
import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.enums.ClassAnnotationType;
import com.bamboo.tool.components.api.enums.FrameworkType;
import com.bamboo.tool.components.api.enums.InterfaceType;
import com.bamboo.tool.components.api.factory.ClassAnnotationProcess;
import com.bamboo.tool.util.StringUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiNameValuePair;

import java.util.Objects;

/**
 * Create by GuoQing
 * Date 2022/2/9 9:54
 * Description
 */
public class SoaServiceClient implements ClassAnnotationProcess {
    @Override
    public void buildClass(ApiClass apiClass, PsiAnnotation psiAnnotation) {
        apiClass.getTypes().add(FrameworkType.O_DIAN_YUN.getCode());
        apiClass.getTypes().add(InterfaceType.CLIENT.getCode());
        PsiNameValuePair[] pairs = psiAnnotation.getParameterList().getAttributes();
        String poolName = "";
        String interfaceName = "";
        for (PsiNameValuePair pair : pairs) {
            String attributeName = pair.getName();
            if (Objects.equals(attributeName, "name")) {
                PsiLiteralExpression value = (PsiLiteralExpression) pair.getValue();
                poolName = value.getValue().toString();
            }
            if (Objects.equals(attributeName, "interfaceName")) {
                PsiLiteralExpression value = (PsiLiteralExpression) pair.getValue();
                interfaceName = value.getValue().toString();
            }
        }
        String url = "";
        if (StringUtil.isNotEmpty(poolName)) {
            url = url + CharSequenceUtil.addPrefixIfNot(poolName, "/")+"/cloud";
            apiClass.setServiceName(poolName);
        }
        if (StringUtil.isNotEmpty(interfaceName)) {
            String[] split = interfaceName.split("\\.");
            url = url + CharSequenceUtil.addPrefixIfNot(StringUtil.lowerFirst(split[split.length - 1]), "/");
        }
        apiClass.getClassUrls().add(url);
    }

    @Override
    public ClassAnnotationType getClassAnnotationType() {
        return ClassAnnotationType.SOA_SERVICE_CLIENT;
    }

    @Override
    public String getClassShortName() {
        return "SoaServiceClient";
    }

    @Override
    public FrameworkType getFrameworkType() {
        return FrameworkType.O_DIAN_YUN;
    }
}
