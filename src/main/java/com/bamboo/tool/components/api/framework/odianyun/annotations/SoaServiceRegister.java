//package com.bamboo.tool.components.api.framework.odianyun.annotations;
//
//import cn.hutool.core.text.CharSequenceUtil;
//import com.bamboo.tool.components.api.entity.BambooClass;
//import com.bamboo.tool.components.api.enums.AnnotationType;
//import com.bamboo.tool.components.api.enums.FrameworkType;
//import com.bamboo.tool.components.api.enums.InterfaceType;
//import com.bamboo.tool.components.api.factory.ClassAnnotationProcess;
//import com.bamboo.tool.util.StringUtil;
//import com.intellij.psi.PsiAnnotation;
//import com.intellij.psi.PsiNameValuePair;
//
//import java.util.Objects;
//
//public class SoaServiceRegister implements ClassAnnotationProcess {
//    @Override
//    public void buildClass(BambooClass BambooClass, PsiAnnotation psiAnnotation) {
//        BambooClass.getTypes().add(FrameworkType.O_DIAN_YUN.getCode());
//        BambooClass.getTypes().add(InterfaceType.SERVICE.getCode());
//        PsiNameValuePair[] pairs = psiAnnotation.getParameterList().getAttributes();
//        for (PsiNameValuePair pair : pairs) {
//            String attributeName = pair.getName();
//            if (Objects.equals(attributeName, "interfaceClass")) {
//                String text = pair.getValue().getText();
//                if (text.contains(".class")) {
//                    text = text.replaceAll(".class", "");
//                }
//                BambooClass.setServiceName(text);
//                BambooClass.getClassUrls().add(CharSequenceUtil.addPrefixIfNot(StringUtil.lowerFirst(text), "/"));
//                continue;
//            }
//            if (Objects.equals(attributeName, "desc")) {
//                BambooClass.setDescription(pair.getValue().getText());
//            }
//        }
//    }
//
//    @Override
//    public AnnotationType getClassAnnotationType() {
//        return AnnotationType.SOA_SERVICE_REGISTER;
//    }
//
//    @Override
//    public String getClassShortName() {
//        return "SoaServiceRegister";
//    }
//
//    @Override
//    public FrameworkType getFrameworkType() {
//        return FrameworkType.O_DIAN_YUN;
//    }
//}
