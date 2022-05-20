package com.bamboo.tool.entity;

import lombok.Data;

/**
 * Create by GuoQing
 * Date 2022/2/18 16:37
 * Description
 */
@Data
public class AnnotationParam {
    private Integer id;
    private String name;
    private String type;
    private String describe;
    private String attribute;
    private Integer annotationInfoId;
}
