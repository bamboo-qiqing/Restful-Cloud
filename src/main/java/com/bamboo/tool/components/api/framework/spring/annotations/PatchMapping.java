//package com.bamboo.tool.components.api.framework.spring.annotations;
//
//import com.bamboo.tool.components.api.entity.BambooClass;
//import com.bamboo.tool.components.api.entity.ApiMethod;
//import com.bamboo.tool.components.api.enums.FrameworkType;
//import com.bamboo.tool.components.api.enums.InterfaceType;
//import com.bamboo.tool.components.api.enums.MethodAnnotationType;
//import com.bamboo.tool.components.api.enums.RequestMethod;
//import com.bamboo.tool.components.api.factory.MethodAnnotationProcess;
//import com.bamboo.tool.util.PsiUtils;
//import com.intellij.psi.PsiAnnotation;
//
//public class PatchMapping implements MethodAnnotationProcess {
//
//    @Override
//    public void buildMethod(ApiMethod apiMethod, PsiAnnotation annotation, BambooClass BambooClass) {
//        apiMethod.getTypes().add(getFrameworkType().getCode());
//        apiMethod.getTypes().add(InterfaceType.SERVICE.getCode());
//        PsiUtils.buildValuePair(apiMethod, annotation, BambooClass,RequestMethod.PATCH.getCode());
//    }
//
//    @Override
//    public MethodAnnotationType getMethodAnnotationType() {
//        return MethodAnnotationType.PATCH_MAPPING;
//    }
//
//    @Override
//    public String getClassShortName() {
//        return "PatchMapping";
//    }
//
//    @Override
//    public FrameworkType getFrameworkType() {
//        return FrameworkType.SPRING;
//    }
//}
