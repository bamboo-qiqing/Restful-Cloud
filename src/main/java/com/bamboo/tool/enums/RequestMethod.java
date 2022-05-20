package com.bamboo.tool.enums;

import lombok.Getter;


/**
 * request类型
 */
@Getter
public enum RequestMethod {
    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    DELETE("DELETE"),
    PUT("PUT"),
    PATCH("PATCH"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE"),
    ALL("ALL");

    private final String code;

    RequestMethod(String code) {
        this.code = code;
    }
}
