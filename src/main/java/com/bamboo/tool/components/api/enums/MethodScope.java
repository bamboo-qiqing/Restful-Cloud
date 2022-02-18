package com.bamboo.tool.components.api.enums;

import lombok.Getter;

/**
 * Create by GuoQing
 * Date 2022/2/18 15:21
 * Description
 */
@Getter
public enum MethodScope {
    All("all"), PUBLIC("public"), PRIVATE("private"), ANNOTATION("annotation");

    private String code;

    MethodScope(String code) {
        this.code = code;
    }

    public static MethodScope getMethodScope(String code) {
        for (MethodScope value : MethodScope.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
