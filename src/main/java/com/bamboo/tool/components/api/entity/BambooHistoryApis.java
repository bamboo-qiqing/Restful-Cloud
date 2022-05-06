package com.bamboo.tool.components.api.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class BambooHistoryApis {

    private String id;
    private String projectName;
    private String projectPath;
    private String className;
    private String classPath;
    private String methodName;
    private String methodReturn;
    private String queryCount;

    public String toSql() {
        return "INSERT INTO bamboo_history_apis (id, project_name, project_path, class_name, class_path, method_name,method_return,query_count) VALUES ('"
                + UUID.randomUUID() + "', '"
                + projectName + "','"
                + projectPath + "','"
                + className + "','"
                + classPath + "','"
                + methodName + "','"
                + methodReturn + "','"
                + 1 + "');";
    }
}
