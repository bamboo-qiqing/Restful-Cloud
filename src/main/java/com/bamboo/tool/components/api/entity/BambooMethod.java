package com.bamboo.tool.components.api.entity;

import com.alibaba.fastjson.annotation.JSONField;
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
    private String description = StringUtils.EMPTY;
    private String methodName = StringUtils.EMPTY;
    private String url = StringUtils.EMPTY;
    @JSONField(serialize = false)
    private PsiMethod psiMethod;
    List<BambooAnnotationInfo> annotationInfos;
}
