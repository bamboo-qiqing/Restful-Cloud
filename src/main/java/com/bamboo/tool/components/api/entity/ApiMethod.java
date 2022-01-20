package com.bamboo.tool.components.api.entity;

import lombok.Data;

/**
 * Create by GuoQing
 * Date 2022/1/20 14:46
 * Description
 */
@Data
public class ApiMethod {
    private String url;
    private String description;
    private String name;
    private String methodType;
    private String className;
}
