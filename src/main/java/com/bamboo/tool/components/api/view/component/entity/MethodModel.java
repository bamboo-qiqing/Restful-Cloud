package com.bamboo.tool.components.api.view.component.entity;

import com.intellij.psi.PsiMethod;
import lombok.Data;

/**
 * Create by GuoQing
 * Date 2022/2/14 13:58
 * Description
 */
@Data

public class MethodModel {
    private String url;
    private String desc;
    private String methodType;
    private PsiMethod psiMethod;

}
