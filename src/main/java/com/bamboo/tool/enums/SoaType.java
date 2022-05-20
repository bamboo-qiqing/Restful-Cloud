package com.bamboo.tool.enums;

/**
 * Create by GuoQing
 * Date 2022/2/16 14:18
 * Description
 */
public enum SoaType {
    SERVICE("service"),
    CLIENT("client"),;

    private final String code;

    SoaType(String code) {
        this.code = code;
    }
}
