package com.bamboo.tool.entity;

import com.alibaba.fastjson.JSONObject;
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
    private String settingId;
    private List<BambooDesc> descs = new ArrayList<>();
    private String consumes = StringUtils.EMPTY;
    private String params = StringUtils.EMPTY;
    private String headers = StringUtils.EMPTY;
    private String produces = StringUtils.EMPTY;
    private Boolean isExist = false;

    public String toInsertSql(Integer projectInfoId) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO bamboo_class (id,class_name, model_name, description, class_path, project_id,setting_id) VALUES (");
        sql.append("'").append(id).append("',");
        sql.append("'").append(className).append("',");
        sql.append("'").append(moduleName).append("',");
        sql.append("'").append(JSONObject.toJSONString(descs)).append("',");
        sql.append("'").append(classPath).append("',");
        sql.append("'").append(projectInfoId).append("',");
        sql.append("'").append(setting.getId()).append("'");
        sql.append(");");
        return sql.toString();
    }

    public String toUpdateSql() {
        return "UPDATE bamboo_class SET description = '" + description+ "' WHERE id = '" + id + "';";
    }

    public String toDeleteSql() {
        return "delete from bamboo_class where id='" + id + "';";
    }

    public String toDeleteMethodSql() {
        return "delete from bamboo_method where class_id='" + id + "';";
    }
}
