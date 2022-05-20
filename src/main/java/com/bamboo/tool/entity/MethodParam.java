package com.bamboo.tool.entity;

import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import lombok.Data;

import java.util.UUID;

@Data
public class MethodParam {

    private String paramType;
    private String paramTypePath;
    private String paramName;
    private Integer paramIndex;


    public void buildMethodParam(PsiParameter parameter, int paramIndex) {
        final PsiType psiType = parameter.getType();
        this.paramType = psiType.getPresentableText();
        this.paramTypePath = psiType.getCanonicalText();
        this.paramName = parameter.getName();
        this.paramIndex = paramIndex;
    }
}
