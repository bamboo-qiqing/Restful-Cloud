package com.bamboo.tool.components.api.frameworkType;

import com.bamboo.tool.components.api.entity.ApiMethod;
import com.bamboo.tool.util.PsiClassUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

import java.util.Collection;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/1/21 14:25
 * Description
 */
public class FrameworkExecute {
    public static List<ApiMethod> buildApiMethod(Project project) {
        Collection<PsiClass> allPsiClass = PsiClassUtil.getALLPsiClass(project);
        allPsiClass.parallelStream().forEach(psiClass->{

        });
        return null;
    }

}
