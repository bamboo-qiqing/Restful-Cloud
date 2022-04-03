package com.bamboo.tool.components.api.entity;

import com.intellij.psi.PsiType;
import lombok.Data;

import java.util.UUID;

@Data
public class MethodReturnType {
    private String id;
    private String returnType;
    private String returnTypePath;

    public void buildReturnType(PsiType psiType) {
        this.returnType = psiType.getPresentableText();
        this.returnTypePath = psiType.getCanonicalText();
    }

    public String toSql(String methodId) {
        return "INSERT INTO bamboo_method_return_type (id, return_type, return_type_path, method_id) VALUES ('" + UUID.randomUUID() + "', '" + returnType + "', '" + returnTypePath + "', '" + methodId + "');";
    }
}
