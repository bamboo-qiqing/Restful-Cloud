package com.bamboo.tool.entity;

import com.intellij.psi.PsiType;
import lombok.Data;

import java.util.UUID;

@Data
public class MethodReturnType {

    private String returnType;
    private String returnTypePath;

    public void buildReturnType(PsiType psiType) {
        this.returnType = psiType.getPresentableText();
        this.returnTypePath = psiType.getCanonicalText();
    }


}
