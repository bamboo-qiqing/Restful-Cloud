package com.bamboo.tool.components.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellij.psi.PsiMethod;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/1/20 14:46
 * Description
 */
@Data
@ToString
public class BambooMethod {
    private String id;
    private String description = StringUtils.EMPTY;
    private String methodName = StringUtils.EMPTY;
    private String projectId;
    private String classId;
    private String returnType;
    private String consumes= StringUtils.EMPTY;
    private String params= StringUtils.EMPTY;
    private String headers= StringUtils.EMPTY;
    private String produces= StringUtils.EMPTY;
    private List<String> methodUrl = new ArrayList<>();
    private List<String> requestMethods = new ArrayList<>();
}
