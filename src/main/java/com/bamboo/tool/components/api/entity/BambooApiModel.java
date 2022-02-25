package com.bamboo.tool.components.api.entity;

import com.intellij.psi.PsiMethod;
import lombok.Data;

/**
 * Create by GuoQing
 * Date 2022/2/25 12:26
 * Description
 */
@Data
public class BambooApiModel {
    private Integer id;
    private Integer methodId;
    private String url;
    private String requestType;
    private String desc;
    private PsiMethod psiMethod;

}
