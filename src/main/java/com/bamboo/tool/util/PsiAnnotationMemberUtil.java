package com.bamboo.tool.util;

import com.intellij.psi.*;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

public class PsiAnnotationMemberUtil {


    @SneakyThrows
    public static List<String> getValue(PsiAnnotationMemberValue value) {
        List<String> values = new ArrayList<>();
        final String name = value.getClass().getName();
        if (name.contains("PsiArrayInitializerMemberValue")) {
            final PsiArrayInitializerMemberValue arrayInitializerMemberValue = (PsiArrayInitializerMemberValue) value;
            final PsiAnnotationMemberValue[] initializers = arrayInitializerMemberValue.getInitializers();
            for (PsiAnnotationMemberValue initializer : initializers) {
                oneExpression(initializer, values);
            }
        }
        oneExpression(value, values);


        return values;
    }

    private static void oneExpression(PsiAnnotationMemberValue value, List<String> values) {
        final String name = value.getClass().getName();
        if (name.contains("PsiLiteralExpression")) {
            PsiLiteralExpression literal = (PsiLiteralExpression) value;
            values.add(literal.getValue().toString());
        }
        if (name.contains("PsiClassObjectAccessExpression")) {
            PsiClassObjectAccessExpression classObject = (PsiClassObjectAccessExpression) value;
            values.add(classObject.getOperand().getType().getPresentableText());
        }
        if (name.contains("PsiReferenceExpression")) {
            PsiReferenceExpression reference = (PsiReferenceExpression) value;
            values.add(reference.getReferenceName());
        }
    }
}
