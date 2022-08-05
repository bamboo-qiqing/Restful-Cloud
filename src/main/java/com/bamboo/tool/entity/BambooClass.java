package com.bamboo.tool.entity;

import com.alibaba.fastjson.JSONObject;
import com.bamboo.tool.enums.MethodScope;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiUtil;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Data
@ToString
public class BambooClass {
    private String id;
    private String className = StringUtils.EMPTY;
    private String moduleName = StringUtils.EMPTY;
    private String description = StringUtils.EMPTY;
    private String classPath = StringUtils.EMPTY;
    private String poolUrl = StringUtils.EMPTY;
    private List<String> classUrl = new ArrayList<>();
    private List<BambooMethod> methods = new ArrayList<>();
    private AnnotationInfoSetting setting;
    private List<BambooDesc> descs = new ArrayList<>();
    private Map<String, AnnotationInfo> annotationInfoMap;
    private JavaDocComment javaDocComment;

    public void buildMethods(PsiMethod[] methods, List<String> scanMethods) {
        if (methods.length > 0) {
            // 构建所有方法
            List<BambooMethod> methodList = Arrays.stream(methods).map(method -> new BambooMethod().buildMethod(method)).filter(a -> {
                long count = a.getAnnotationInfoMap().values().stream().map(f -> f.getAnnotationName()).filter(f -> scanMethods.contains(f)).count();
                if (count < 1) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());
            this.setMethods(methodList);
        }
    }

    public String toInsertSql(Integer projectInfoId, String id) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO bamboo_class (id,class_name, model_name, description, class_path, project_id,setting_id,annotation_attributes,java_doc_comment) VALUES (");
        sql.append("'").append(id).append("',");
        sql.append("'").append(className).append("',");
        sql.append("'").append(moduleName).append("',");
        sql.append("'").append(JSONObject.toJSONString(descs)).append("',");
        sql.append("'").append(classPath).append("',");
        sql.append("'").append(projectInfoId).append("',");
        sql.append("'").append(setting.getId()).append("',");
        sql.append("'").append(JSONObject.toJSON(annotationInfoMap)).append("',");
        sql.append("'").append(javaDocComment == null ? "" : JSONObject.toJSON(javaDocComment)).append("'");
        sql.append(");");
        return sql.toString();
    }

}
