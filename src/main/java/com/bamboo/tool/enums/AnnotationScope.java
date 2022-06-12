package com.bamboo.tool.enums;

import lombok.Getter;

/**
 * Create by GuoQing
 * Date 2022/2/16 13:07
 * Description
 */
@Getter
public enum AnnotationScope {
    CLASS("Class","类注解"), METHOD("Method","方法注解");

    private final String code;
    private final String desc;

    AnnotationScope(String code,String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AnnotationScope getAnnotationScope(String code) {
        for (AnnotationScope value : AnnotationScope.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
