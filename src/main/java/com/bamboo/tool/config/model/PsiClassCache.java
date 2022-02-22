package com.bamboo.tool.config.model;

import com.bamboo.tool.components.api.entity.AnnotationInfoSetting;
import com.intellij.psi.PsiClass;
import lombok.Data;

/**
 * Create by GuoQing
 * Date 2022/1/27 10:22
 * Description
 */
@Data
public class PsiClassCache {

    private PsiClass psiClass;
    private AnnotationInfoSetting info;

    public PsiClassCache(AnnotationInfoSetting info, PsiClass psiClass) {
        this.psiClass = psiClass;
        this.info = info;
    }

}
