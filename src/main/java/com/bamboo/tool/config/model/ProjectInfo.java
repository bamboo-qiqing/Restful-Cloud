package com.bamboo.tool.config.model;

import lombok.Data;

/**
 * Create by GuoQing
 * Date 2022/1/20 11:40
 * Description
 */
@Data
public class ProjectInfo {
    private Integer id;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 项目地址
     */
    private String projectPath;

    private Boolean isShowDesc;
}
