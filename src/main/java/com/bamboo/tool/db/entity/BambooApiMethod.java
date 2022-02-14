package com.bamboo.tool.db.entity;

import lombok.Data;

/**
 * Create by GuoQing
 * Date 2022/2/14 11:50
 * Description
 */
@Data
public class BambooApiMethod {
    private String projectName;
    private String projectPath;
    private String id;
    private String projectId;
    private String description;
    private String methodName;
    private String methodType;
    private String contentType;
    private String header;
    private String params;
    private String url;
    private String modelName;
    private String className;
    private String classDesc;
    private String types;
    private String serviceName;
}
