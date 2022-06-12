package com.bamboo.tool.entity;

import com.bamboo.tool.enums.AttributeEnums;
import com.bamboo.tool.util.StringUtil;
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


    public Object[] getStrings() {
        Object[] data = {name, StringUtil.isNotBlank(type)?AttributeEnums.getAttributeEnums(type).getDesc():"",attribute, describe,annotationInfoId,id};
        return data;
    }
}
