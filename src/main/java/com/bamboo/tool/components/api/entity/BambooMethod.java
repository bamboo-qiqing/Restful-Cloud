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
public class BambooMethod {
    private String id;
    private String description = StringUtils.EMPTY;
    private String methodName = StringUtils.EMPTY;
    private String url = StringUtils.EMPTY;
    private PsiMethod psiMethod;
    List<BambooAnnotationInfo> annotationInfos;
    List<BambooApiModel> apis=new ArrayList<>();
}
