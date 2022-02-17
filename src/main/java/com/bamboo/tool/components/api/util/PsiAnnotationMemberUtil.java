package com.bamboo.tool.components.api.util;

import com.bamboo.tool.components.api.entity.AttributeInfo;
import com.bamboo.tool.components.api.enums.PsiAnnotationMemberType;
import com.intellij.psi.*;

/**
 * Create by GuoQing
 * Date 2022/2/16 10:18
 * Description
 */
public class PsiAnnotationMemberUtil {


    public static AttributeInfo getAnnotationAttributeValue(PsiNameValuePair pair) {
        PsiAnnotationMemberValue value = pair.getValue();
        String name = pair.getName();
        AttributeInfo attributeInfo = new AttributeInfo();
        attributeInfo.setName(name);
        if (value instanceof PsiClassObjectAccessExpression) {
            attributeInfo.setType(PsiAnnotationMemberType.PsiClassObjectAccessExpression);
        }

        if (value instanceof PsiLiteralExpression) {
            attributeInfo.setType(PsiAnnotationMemberType.PsiLiteralExpression);
        }
        if (value instanceof PsiArrayInitializerMemberValue) {
            attributeInfo.setType(PsiAnnotationMemberType.PsiArrayInitializerMemberValue);
            PsiAnnotationMemberValue[] values = ((PsiArrayInitializerMemberValue) value).getInitializers();
            for (PsiAnnotationMemberValue memberValue : values) {
                System.out.printf("1");
            }
        }

        return null;
    }
}
