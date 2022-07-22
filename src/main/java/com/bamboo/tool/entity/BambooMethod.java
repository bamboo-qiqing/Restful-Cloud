package com.bamboo.tool.entity;

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
    private String projectId;
    private String classId;
    private int accessLevel;

    private List<String> methodUrl = new ArrayList<>();
    private List<String> requestMethods = new ArrayList<>();
    private MethodReturnType returnType = new MethodReturnType();
    private String returnTypeStr;
    private String methodParamsStr;
    private List<MethodParam> methodParams = new ArrayList<>();
    private List<BambooDesc> descs = new ArrayList<>();
    private Boolean isExist = false;
    private Map<String, AnnotationInfo> annotationInfoMap;

    public void buildMethodParams(PsiParameterList psiParameterList) {
        int parametersCount = psiParameterList.getParametersCount();
        if (parametersCount > 0) {
            for (int i = 0; i < parametersCount; i++) {
                PsiParameter parameter = psiParameterList.getParameter(i);
                final MethodParam methodParam = new MethodParam();
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
        FrameworkExecute.buildClassAnnotationInfo(method, this);
        // 构建返回类型
        this.getReturnType().buildReturnType(method.getReturnType());
        //构建方法参数
        this.buildMethodParams(method.getParameterList());
        return this;
    }


    public String toInsertSql(String projectId) {
        return "insert into bamboo_method (id, project_id, method_name, class_id,description,method_params,return_type) VALUES(" + "'" + id + "'," + "'" + projectId + "'," + "'" + methodName + "'," + "'" + classId + "'," + "'" + description + "'," + "'" + methodParamsStr + "'," + "'" + returnTypeStr + "'" + ");";
    }

    public String toUpdateSql() {
        return "UPDATE bamboo_method SET description   = '" + description + "' WHERE id = '" + id + "';";
    }

    public String toDeleteSql() {
        return "delete from bamboo_method where id='" + id + "';";
    }
}
