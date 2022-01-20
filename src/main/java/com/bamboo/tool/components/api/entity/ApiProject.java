package com.bamboo.tool.components.api.entity;

import lombok.Data;

import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/1/20 14:40
 * Description
 */
@Data
public class ApiProject {
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 项目地址
     */
    private String projectPath;
    /**
     *  api 集合
     */
    private List<ApiMethod> apiMethods;

}
