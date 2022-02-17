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
    PsiReferenceExpression("CLIENT", null),
    PsiLiteralExpression("SERVICE", null),
    PsiArrayInitializerMemberValue("", null),
    PsiClassObjectAccessExpression("Class", null);

    private String code;
    private Method method;

    PsiAnnotationMemberType(String code, Method method) {
        this.code = code;
        this.method = method;
    }
}
