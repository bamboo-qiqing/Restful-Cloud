package com.bamboo.tool.entity;

import com.jetbrains.D.v.N.b.X.S;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/2/18 16:47
 * Description
 */
@Data
public class Framework {
    private String id;
    private String name;
    private String describe;
    private List<AnnotationInfoSetting> annotationInfoSettings=new ArrayList<>();

    public Object[] getStrings() {
        Object[] data = {name, describe, id};
        return data;
    }

    @Override
    public String toString() {
        return name;
    }
}
