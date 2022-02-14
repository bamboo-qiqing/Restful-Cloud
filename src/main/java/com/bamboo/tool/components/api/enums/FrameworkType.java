package com.bamboo.tool.components.api.enums;

import lombok.Getter;

/**
 * Create by GuoQing
 * Date 2022/1/21 14:19
 * Description
 */
@Getter
public enum FrameworkType {
    SPRING("SPRING"), JAX_RS("JAX_RS"), O_DIAN_YUN("O_DIAN_YUN"), SWAGGER("swagger");

    private String code;

    FrameworkType(String code) {
        this.code = code;
    }
}
