package com.bamboo.tool.entity;

import com.bamboo.tool.enums.AnnotationScope;
import com.bamboo.tool.enums.SoaType;
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
    private SoaType soaType;
    private String effect;
    private List<AnnotationParam> params = new ArrayList<>();
    List<AnnotationMethodScope> methodScopes = new ArrayList<>();

    public Object[] getStrings() {
        Object[] data = {annotationPath,
                scope.getDesc(),
                soaType == null ? "无" : soaType.getDesc(),
                effect.contains("scann")?"是":"否",effect.contains("attribute")?"是":"否", annotationName, id};
        return data;
    }

    @Override
    public String toString() {
        return annotationName+"("+scope.getDesc()+")";
    }
}
