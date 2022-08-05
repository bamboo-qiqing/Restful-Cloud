package com.bamboo.tool.entity;

import com.bamboo.tool.util.PsiUtils;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MethodReturnType {

    private String returnType;
    private String returnTypePath;
    private List<JavaFieldInfo> fields;

    public void buildReturnType(PsiType psiType) {
        this.returnType = psiType.getPresentableText();
        this.returnTypePath = psiType.getCanonicalText();
        fields = PsiUtils.getClassField(psiType);

    }


}
