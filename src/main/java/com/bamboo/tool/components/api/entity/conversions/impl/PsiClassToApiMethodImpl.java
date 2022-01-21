package com.bamboo.tool.components.api.entity.conversions.impl;

import cn.hutool.core.util.StrUtil;
import com.bamboo.tool.components.api.entity.ApiMethod;
import com.bamboo.tool.components.api.entity.conversions.Conversion;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Create by GuoQing
 * Date 2022/1/21 13:44
 * Description
 */
public class PsiClassToApiMethodImpl implements Conversion<PsiMethod, ApiMethod> {
    @Override
    public ApiMethod conversion(PsiMethod psiMethod) {
        if (Objects.isNull(psiMethod)) {
            return null;
        }
        ApiMethod apiService = new ApiMethod();

        psiMethod.getModifierList();
//        Module module = ModuleUtil.findModuleForPsiElement(psiClass);
//        if (Objects.isNull(module)) {
//            return null;
//        }
//        //模块名
//        String moduleName = module.getName();
//        //class文件名
//        String className = psiClass.getName();
//        //包名
//        String packageName = getPackageName(psiClass);

        return apiService;
    }

    private static String getPackageName(@NotNull PsiClass psiClass) {
        String defaultPackageName = "";
        String qualifiedName = psiClass.getQualifiedName();
        if (StrUtil.isEmpty(qualifiedName)) {
            return defaultPackageName;
        }

        String fileName = psiClass.getName();
        if (StrUtil.isEmpty(fileName)) {
            return defaultPackageName;
        }

        if (!qualifiedName.equals(fileName)) {
            return qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
        }
        return defaultPackageName;
    }


}
