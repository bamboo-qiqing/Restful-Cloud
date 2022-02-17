package com.bamboo.tool.components.api.enums;

import lombok.Getter;

/**
 * Create by GuoQing
 * Date 2022/2/16 13:07
 * Description
 */
@Getter
public enum AnnotationScope {
    CLASS("Class"), METHOD("Method");

    private String code;

    AnnotationScope(String code) {
        this.code = code;
    }
}
