package com.bamboo.tool.config.model;

import com.bamboo.tool.entity.AnnotationInfoSetting;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import lombok.Data;

import java.util.Collection;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/1/27 10:22
 * Description
 */
@Data
public class PsiClassCache {

    private AnnotationInfoSetting setting;
    private Collection<PsiAnnotation> annotations;

    public PsiClassCache(AnnotationInfoSetting setting, Collection<PsiAnnotation> annotations) {
        this.setting = setting;
        this.annotations = annotations;
    }

}
