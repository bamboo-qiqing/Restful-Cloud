package com.bamboo.tool.components.api.entity;

import lombok.Data;

@Data
public class BambooAnnotationInfo {
    private String id;
    private String value;
    private String settingId;
    private String associatedId;
    private String projectId;
    private AnnotationInfoSetting annotationInfoSetting;
    private AnnotationParam param;
}
