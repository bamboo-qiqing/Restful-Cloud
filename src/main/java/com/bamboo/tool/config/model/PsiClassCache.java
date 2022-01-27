package com.bamboo.tool.config.model;

import com.bamboo.tool.components.api.factory.ClassAnnotationProcess;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/1/27 10:22
 * Description
 */
@Data
public class PsiClassCache {

    private PsiClass psiClass;
    private List<ClassAnnotationProcessCache> processCaches = new ArrayList<>();

    public PsiClassCache(PsiClass psiClass, List<ClassAnnotationProcessCache> processCaches) {
        this.psiClass = psiClass;
        this.processCaches = processCaches;
    }
    @Data
    public static class ClassAnnotationProcessCache {
        private ClassAnnotationProcess classAnnotationProcesses;
        private PsiAnnotation psiAnnotation;

    }
}
