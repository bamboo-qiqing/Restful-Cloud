package com.bamboo.tool.components.api.entity;

import com.intellij.psi.PsiMethod;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/1/20 14:46
 * Description
 */
@Data
public class ApiMethod {
    private String description = StringUtils.EMPTY;
    private String methodName = StringUtils.EMPTY;
    private List<String> methodTypes = new ArrayList<>();
    private List<String> methodUrls = new ArrayList<>();
    private List<String> contentTypes = new ArrayList<>();
    private List<String> headers = new ArrayList<>();
    private List<String> params = new ArrayList<>();
    private List<String> urls = new ArrayList<>();
    private PsiMethod psiMethod;
}
