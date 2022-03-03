package com.bamboo.tool.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Create by GuoQing
 * Date 2022/3/3 10:53
 * Description
 */
@Data
@AllArgsConstructor
public class BambooModel {

    private String projectId;
    private String modelName;
    private String className;
    private String classDescription;
    private String classAnnotationValue;
    private String classAnnotationSoaType;
    private String classAnnotationPath;
    private String classAnnotationShortName;
    private String classAnnotationFrameworkName;
    private String methodName;
    private String methodDescription;
}
