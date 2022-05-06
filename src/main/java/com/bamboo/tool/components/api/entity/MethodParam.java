package com.bamboo.tool.components.api.entity;

import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import lombok.Data;

import java.util.UUID;

@Data
public class MethodParam {
    private String id;
    private String paramType;
    private String paramTypePath;
    private String paramName;
    private Integer paramIndex;
    private String methodId;

    public void buildMethodParam(PsiParameter parameter, int paramIndex) {
        final PsiType psiType = parameter.getType();
        this.paramType = psiType.getPresentableText();
        this.paramTypePath = psiType.getCanonicalText();
        this.paramName = parameter.getName();
        this.paramIndex = paramIndex;
    }

    public String toSql(String methodId) {
        return "INSERT INTO bamboo_method_param (id, param_type, param_type_path, param_name, param_index, method_id) VALUES ('"
                + UUID.randomUUID() + "', '"
                + paramType + "', '"
                + paramTypePath + "', '"
                + paramName + "',"
                + paramIndex + ", '"
                + methodId + "');";
    }
}
