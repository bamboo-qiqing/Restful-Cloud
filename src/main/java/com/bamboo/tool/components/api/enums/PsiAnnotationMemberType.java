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
    PsiReferenceExpression("PsiReferenceExpression",""),
    PsiLiteralExpression("PsiLiteralExpression","String"),
    PsiArrayInitializerMemberValue("PsiArrayInitializerMemberValue",""),
    PsiClassObjectAccessExpression("PsiClassObjectAccessExpression","");

    private String code;
    private String name;

    PsiAnnotationMemberType(String code,String name) {
        this.code = code;
        this.name = name;
    }


}
