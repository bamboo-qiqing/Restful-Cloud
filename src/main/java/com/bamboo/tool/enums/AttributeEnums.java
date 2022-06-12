package com.bamboo.tool.enums;

import lombok.Getter;

@Getter
public enum AttributeEnums {

    POOL_URL("poolUrl", "项目url"),
    CLASS_URL("classUrl", "类url"),
    METHOD_URL("methodUrl", "方法url"),
    consumes("consumes", "consumes"),
    params("params", "params"),
    headers("headers", "headers"),
    produces("produces", "produces"),
    description("desc", "描述"),
    ;

    private final String code;
    private final String desc;

    AttributeEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AttributeEnums getAttributeEnums(String code) {
        for (AttributeEnums value : AttributeEnums.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
