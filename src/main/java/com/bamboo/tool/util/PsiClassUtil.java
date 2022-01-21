package com.bamboo.tool.util;

import com.bamboo.tool.components.api.enums.ClassAnnotationTypeEnum;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.AllClassesSearch;
import com.intellij.util.Query;

import java.util.Arrays;
import java.util.Collection;

public class PsiClassUtil {

    public static final Collection<PsiClass> getALLPsiClass(Project project) {
        Query<PsiClass> query = AllClassesSearch.search(ProjectScope.getContentScope(project), project);
        Collection<PsiClass> controller = query.filtering(e -> isContainsAnnotation(e)).allowParallelProcessing().findAll();
        return controller;
    }


    private static boolean isContainsAnnotation(PsiClass psiClass) {
        return Arrays.asList(ClassAnnotationTypeEnum.values()).stream().filter(a -> psiClass.getAnnotation(a.getClassPath()) != null).count() > 1;
    }
}
