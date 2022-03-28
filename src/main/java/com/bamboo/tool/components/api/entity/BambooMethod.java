package com.bamboo.tool.components.api.entity;

import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
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
    private int accessLevel;
    private String consumes = StringUtils.EMPTY;
    private String params = StringUtils.EMPTY;
    private String headers = StringUtils.EMPTY;
    private String produces = StringUtils.EMPTY;
    private List<String> methodUrl = new ArrayList<>();
    private List<String> requestMethods = new ArrayList<>();
    private MethodReturnType returnType = new MethodReturnType();
    private List<MethodParam> methodParams = new ArrayList<>();
    private List<BambooDesc> descs=new ArrayList<>();

    public void buildMethodParams(PsiParameterList psiParameterList) {
        int parametersCount = psiParameterList.getParametersCount();
        if (parametersCount > 0) {
            for (int i = 0; i < parametersCount; i++) {
                PsiParameter parameter = psiParameterList.getParameter(i);
                final MethodParam methodParam = new MethodParam();
                methodParam.buildMethodParam(parameter, i);
                methodParams.add(methodParam);
            }
        }
    }
}
