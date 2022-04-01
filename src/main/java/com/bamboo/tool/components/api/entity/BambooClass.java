package com.bamboo.tool.components.api.entity;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class BambooClass {
    private String id;
    private String className = StringUtils.EMPTY;
    private String moduleName = StringUtils.EMPTY;
    private String description = StringUtils.EMPTY;
    private String classPath = StringUtils.EMPTY;
    private String poolUrl;
    private List<String> classUrl = new ArrayList<>();
    private String projectId;
    private List<BambooMethod> methods = new ArrayList<>();
    private AnnotationInfoSetting setting;
    private List<BambooDesc> descs = new ArrayList<>();


    public String toSql(Integer projectInfoId) {
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO bamboo_class (id,class_name, model_name, description, class_path, project_id,setting_id) VALUES (");
        sql.append("'").append(id).append("',");
        sql.append("'").append(className).append("',");
        sql.append("'").append(moduleName).append("',");
        sql.append("'").append(description).append("',");
        sql.append("'").append(classPath).append("',");
        sql.append("'").append(projectInfoId).append("',");
        sql.append("'").append(setting.getId()).append("'");
        sql.append(");");
        return sql.toString();
    }
}
