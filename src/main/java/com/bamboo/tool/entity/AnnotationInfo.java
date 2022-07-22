package com.bamboo.tool.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Create by GuoQing
 * Date 2022/7/22 15:48
 * Description 注解信息
 */
@Data
public class AnnotationInfo {
    /**
     * 注解名称
     */
    private String annotationName;
    /**
     * 注解属性值
     */
    private Map<String,List<String>> annotationAttributs;

}
