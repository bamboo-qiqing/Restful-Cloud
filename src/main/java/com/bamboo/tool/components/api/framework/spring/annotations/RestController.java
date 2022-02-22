//package com.bamboo.tool.components.api.framework.spring.annotations;
//
//import com.bamboo.tool.components.api.entity.BambooClass;
//import com.bamboo.tool.components.api.enums.AnnotationType;
//import com.bamboo.tool.components.api.enums.FrameworkType;
//import com.bamboo.tool.components.api.enums.InterfaceType;
//import com.bamboo.tool.components.api.factory.ClassAnnotationProcess;
//import com.intellij.psi.PsiAnnotation;
//import lombok.Data;
//
//@Data
//public class RestController implements ClassAnnotationProcess {
//    @Override
//    public void buildClass(BambooClass apiMethod, PsiAnnotation psiAnnotation) {
//        apiMethod.getTypes().add(getFrameworkType().getCode());
//        apiMethod.getTypes().add(InterfaceType.SERVICE.getCode());
//    }
//
//    @Override
//    public AnnotationType getClassAnnotationType() {
//        return AnnotationType.REST_CONTROLLER;
//    }
//
//    @Override
//    public String getClassShortName() {
//        return "RestController";
//    }
//
//    @Override
//    public FrameworkType getFrameworkType() {
//        return FrameworkType.SPRING;
//    }
//
//
//}
