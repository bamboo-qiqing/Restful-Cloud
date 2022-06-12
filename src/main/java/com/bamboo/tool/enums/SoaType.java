package com.bamboo.tool.enums;

import lombok.Getter;

/**
 * Create by GuoQing
 * Date 2022/2/16 14:18
 * Description
 */
@Getter
public enum SoaType {
    SERVICE("service","服务端"),
    CLIENT("client","客户端"),
    NULL("","无"),
    ;
    private final String code;
    private final String desc;
    SoaType(String code,String desc) {

        this.code = code;
        this.desc = desc;
    }

    public static SoaType getSoaType(String code) {
        for (SoaType value : SoaType.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
