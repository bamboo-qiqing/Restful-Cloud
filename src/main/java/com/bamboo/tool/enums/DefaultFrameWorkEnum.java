package com.bamboo.tool.enums;

import lombok.Getter;

/**
 * Create by GuoQing
 * Date 2022/7/25 12:34
 * Description 默认框架配置
 */
@Getter
public enum DefaultFrameWorkEnum {
    SPRING("Spring", "Spring"),
    SWAGGER("swagger", "swagger"),
    SPRING_CLOUD("spring_cloud", "spring cloud"), ;

    private final String code;
    private final String name;

    DefaultFrameWorkEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
