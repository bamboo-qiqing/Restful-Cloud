package com.bamboo.tool.util;

import com.bamboo.tool.components.api.enums.ClassAnnotationType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.AllClassesSearch;
import com.intellij.util.Query;

import java.util.*;

public class PsiUtil {
    static List<ClassAnnotationType> classAnnotationTypes = Arrays.asList(ClassAnnotationType.values());

    public static final Collection<PsiClass> getALLPsiClass(Project project) {
        Query<PsiClass> query = AllClassesSearch.search(ProjectScope.getContentScope(project), project);
        Collection<PsiClass> controller = query.filtering(e -> isContainsAnnotation(e)).allowParallelProcessing().findAll();
        return controller;
    }

    private static boolean isContainsAnnotation(PsiClass psiClass) {
        return classAnnotationTypes.parallelStream().filter(a -> psiClass.getAnnotation(a.getClassPath()) != null).count() > 1;
    }
}
