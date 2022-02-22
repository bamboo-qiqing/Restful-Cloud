package com.bamboo.tool.components.api.entity;

import lombok.Data;

@Data
public class BambooApiSetting {
    private Integer id;
    private Integer annotationInfoSettingId;
    private String classUrlMode;
    private String classUrlModeValue;
    private String methodUrlMode;
    private String methodUrlModeValue;
}
