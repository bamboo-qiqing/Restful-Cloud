package com.bamboo.tool.components.api.enums;

import lombok.Getter;

import java.lang.reflect.Method;

/**
 * Create by GuoQing
 * Date 2022/2/16 10:23
 * Description
 */
@Getter
public enum PsiAnnotationMemberType {
    PsiReferenceExpression("PsiReferenceExpression"),
    PsiLiteralExpression("PsiLiteralExpression"),
    PsiArrayInitializerMemberValue("PsiArrayInitializerMemberValue"),
    PsiClassObjectAccessExpression("PsiClassObjectAccessExpression");

    private String code;

    PsiAnnotationMemberType(String code) {
        this.code = code;
    }
}
