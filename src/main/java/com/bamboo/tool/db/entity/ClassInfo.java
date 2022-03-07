package com.bamboo.tool.db.entity;

import lombok.Data;

@Data
public class ClassInfo {
    private String projectName;
    private String modelName;
    private String className;
    private String frameworkName;
    private String soaType;
    private String annotationValue;
    private String annotationUseType;
    private String classId;
}
