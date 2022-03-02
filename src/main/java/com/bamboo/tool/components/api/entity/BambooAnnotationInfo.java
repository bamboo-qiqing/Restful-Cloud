package com.bamboo.tool.components.api.entity;

import lombok.Data;

@Data
public class BambooAnnotationInfo {
    private String id;
    private String value;
    private AnnotationInfoSetting annotationInfoSetting;
    private Integer classId;
    private AnnotationParam param;
}
