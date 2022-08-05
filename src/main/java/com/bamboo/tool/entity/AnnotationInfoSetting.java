package com.bamboo.tool.entity;

import b.C.S;
import com.bamboo.tool.enums.DefaultFrameWorkEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String scope;
    private String soaType;
    private String effect;
    private List<AnnotationParam> params = new ArrayList<>();
    Map<String, List<String>> otherParams = new HashMap<>();
//    public Object[] getStrings() {
//        Object[] data = {annotationPath,
//                scope.getDesc(),
//                soaType == null ? "无" : soaType.getDesc(),
//                effect.contains("scann")?"是":"否",effect.contains("attribute")?"是":"否", annotationName, id};
//        return data;
//    }
//
//    @Override
//    public String toString() {
//        return annotationName+"("+scope.getDesc()+")";
//    }
}
