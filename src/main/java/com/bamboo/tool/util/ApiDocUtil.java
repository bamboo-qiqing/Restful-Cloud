package com.bamboo.tool.util;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;

public class ApiDocUtil {

    public static String getMethodJavaDoc(PsiMethod psiMethod) {
        PsiDocComment docComment = psiMethod.getDocComment();
        StringBuilder commentStringBuilder = new StringBuilder();
        if (docComment != null) {
            PsiElement[] descriptionElements = docComment.getDescriptionElements();
            for (PsiElement descriptionElement : descriptionElements) {
                if (descriptionElement instanceof PsiDocToken) {
                    commentStringBuilder.append(descriptionElement.getText());
                }
            }
        }
        return commentStringBuilder.toString().trim();
    }


    public static String getMethodSwaggerDoc(PsiMethod psiMethod) {
        PsiAnnotation annotation = psiMethod.getAnnotation("io.swagger.annotations.ApiOperation");
        if (annotation != null) {
            PsiAnnotationMemberValue descValue = annotation.findDeclaredAttributeValue("value");
            if (descValue != null) {
                return descValue.getText().replace("\"", "");
            }
        }
        return null;
    }



}
