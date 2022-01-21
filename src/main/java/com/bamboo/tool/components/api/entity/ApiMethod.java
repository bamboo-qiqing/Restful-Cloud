package com.bamboo.tool.components.api.entity;

import lombok.Data;

/**
 * Create by GuoQing
 * Date 2022/1/20 14:46
 * Description
 */
@Data
public class ApiMethod {
    /**
     * url地址
     */
    private String url;
    /**
     * 描述
     */
    private String description;
    /**
     * 方法名称
     */
    private String methodName;
    /**
     * 方法类型
     */
    private String methodType;
    /**
     * 文件名
     */
    private String className;
    /**
     * 模块名
     */
    private String moduleName;
    /**
     * 包名
     */
    private String packageName;
}
