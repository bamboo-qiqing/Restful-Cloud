package com.bamboo.tool.entity;

import cn.hutool.core.collection.CollectionUtil;
import com.bamboo.tool.factory.FrameworkExecute;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class MethodParam {
    /**
     * 参数类型
     */
    private String paramType;
    /**
     * 参数对象路径地址
     */
    private String paramTypePath;
    /**
     * 参数名称
     */
    private String paramName;
    /**
     * 参数注解信息
     */
    private Map<String, AnnotationInfo> annotationInfoMap = new HashMap<>();
    /**
     * 参数顺序号
     */
    private Integer paramIndex;

    /**
     * 构建参数方法对象
     *
     * @param parameter  参数对象
     * @param paramIndex 参数下标
     */
    public void buildMethodParam(PsiParameter parameter, int paramIndex) {
        final PsiType psiType = parameter.getType();
        this.paramType = psiType.getPresentableText();
        this.paramTypePath = psiType.getCanonicalText();
        this.paramName = parameter.getName();
        this.paramIndex = paramIndex;
        PsiAnnotation[] annotations = parameter.getAnnotations();
        if (annotations.length > 0) {
            annotationInfoMap = FrameworkExecute.builAnnotationInfo(annotations);
        }
    }
}
