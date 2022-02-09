package com.bamboo.tool.components.api.enums;

import lombok.Getter;

/**
 * Create by GuoQing
 * Date 2022/2/9 10:04
 * Description
 */
@Getter
public enum InterfaceType {

    CLIENT("CLIENT"), SERVICE("SERVICE");

    private String code;

    InterfaceType(String code) {
        this.code = code;
    }
}
