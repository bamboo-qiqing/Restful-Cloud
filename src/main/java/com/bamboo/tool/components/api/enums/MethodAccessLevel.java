package com.bamboo.tool.components.api.enums;

/**
 * Create by GuoQing
 * Date 2022/3/14 16:40
 * Description
 */
public enum MethodAccessLevel {
    PUBLIC(4),
    PROTECTED(3),
    PACKAGE_LOCAL(2),
    PRIVATE(1);

    private int code;

    MethodAccessLevel(int code) {
        this.code = code;
    }
}
