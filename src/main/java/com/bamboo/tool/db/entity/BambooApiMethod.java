package com.bamboo.tool.db.entity;

import lombok.Data;

/**
 * Create by GuoQing
 * Date 2022/2/14 11:50
 * Description
 */
@Data
public class BambooApiMethod {
    private String url;
    private String requestMethods;
    private String methodName;
    private String methodDesc;
    private String className;
    private String modelName;
    private String projectName;
    private String soaType;
    private String frameworkName;

}
