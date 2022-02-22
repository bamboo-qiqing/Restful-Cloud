package com.bamboo.tool.components.api.entity;

import com.bamboo.tool.components.api.enums.AnnotationScope;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/2/16 12:48
 * Description
 */
@Data
public class AnnotationInfoSetting {
    private Integer id;
    private String annotationName;
    private String annotationPath;
    private Framework framework;
    private AnnotationScope scope;
    private String soaType;
    private String effect;
    private List<AnnotationParam> params = new ArrayList<>();
    List<AnnotationMethodScope> methodScopes = new ArrayList<>();

}
