package com.bamboo.tool.components.api.entity;

import com.bamboo.tool.components.api.enums.AnnotationScope;
import lombok.Data;

/**
 * Create by GuoQing
 * Date 2022/2/16 12:48
 * Description
 */
@Data
public class AnnotationInfo {
    private String annotationName;
    private String annotationPath;
    private AnnotationFrameWork frameWorkType;
    private AnnotationScope scope;
}
