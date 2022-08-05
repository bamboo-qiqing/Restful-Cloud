package com.bamboo.tool.entity;

import com.alibaba.fastjson.JSONObject;
import com.bamboo.tool.factory.FrameworkExecute;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.util.PsiUtil;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private String remark = StringUtils.EMPTY;
    private String methodName = StringUtils.EMPTY;
    private  String projectId;
    private  String classId;
    private  int accessLevel;

    private List<String> methodUrl = new ArrayList<>();
    private List<String> requestMethods = new ArrayList<>();
    private MethodReturnType returnType = new MethodReturnType();
    private List<MethodParam> methodParams = new ArrayList<>();
    private List<BambooDesc> descs = new ArrayList<>();
    private Map<String, AnnotationInfo> annotationInfoMap;
    private JavaDocComment javaDocComment;

    public void buildMethodParams(PsiParameterList psiParameterList) {
        int parametersCount = psiParameterList.getParametersCount();
        if (parametersCount > 0) {
            for (int i = 0; i < parametersCount; i++) {
                PsiParameter parameter = psiParameterList.getParameter(i);
                MethodParam methodParam = new MethodParam();
                assert parameter != null;
                methodParam.buildMethodParam(parameter, i);
                methodParams.add(methodParam);
            }
        }
    }

    public BambooMethod buildMethod(PsiMethod method) {
        this.setMethodName(method.getName());
        this.setAccessLevel(PsiUtil.getAccessLevel(method.getModifierList()));
        //构建方法注解信息
        FrameworkExecute.buildMethodAnnotationInfo(method, this);
        // 构建返回类型
        this.getReturnType().buildReturnType(method.getReturnType());
        //构建方法参数
        this.buildMethodParams(method.getParameterList());
        //构建注释
        this.setDescription(FrameworkExecute.getMethodDescription(method));
        // 构建java注释对象
        this.setJavaDocComment(JavaDocComment.buildJavaDocComment(method.getDocComment()));

        return this;
    }


    public String toInsertSql(String projectId, String classId) {
        return new StringBuilder()
                .append("insert into bamboo_method (id, project_id, method_name, class_id,description,method_params,return_type,method_url,annotation_attributes,request_methods,java_doc_comment) VALUES(")
                .append("'").append(UUID.randomUUID()).append("',")
                .append("'").append(projectId).append("',")
                .append("'").append(methodName).append("',")
                .append("'").append(classId).append("',")
                .append("'").append(description).append("',")
                .append("'").append(JSONObject.toJSON(methodParams)).append("',")
                .append("'").append(JSONObject.toJSON(returnType)).append("',")
                .append("'").append(JSONObject.toJSON(methodUrl)).append("',")
                .append("'").append(JSONObject.toJSON(annotationInfoMap)).append("',")
                .append("'").append(JSONObject.toJSON(requestMethods)).append("',")
                .append("'").append(javaDocComment == null ? "" : JSONObject.toJSON(javaDocComment)) + "'"
                + ");";
    }
}
