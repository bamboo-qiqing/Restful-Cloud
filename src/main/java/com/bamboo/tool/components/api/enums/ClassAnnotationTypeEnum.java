package com.bamboo.tool.components.api.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/1/21 12:22
 * Description  class注解类型枚举
 */
@Getter
public enum ClassAnnotationTypeEnum {
    REST_CONTROLLER("org.springframework.web.bind.annotation.RestController", FrameworkTypeEnum.SPRING),
    CONTROLLER("org.springframework.stereotype.Controller", FrameworkTypeEnum.SPRING),
    REQUEST_MAPPING("org.springframework.web.bind.annotation.RequestMapping", FrameworkTypeEnum.SPRING),
    JAVAX_PATH("javax.ws.rs.Path", FrameworkTypeEnum.JAX_RS);
    /**
     * class 文件路径
     */
    private String classPath;
    /**
     * 框架类型，属于哪个框架
     */
    private FrameworkTypeEnum frameworkType;

    ClassAnnotationTypeEnum(String classPath, FrameworkTypeEnum frameworkType) {
        this.classPath = classPath;
        this.frameworkType = frameworkType;
    }

    /**
     * 根据框架类型获取所有类注解枚举
     *
     * @param frameworkType 框架类型
     * @return 类注解类型集合
     */
    public static List<ClassAnnotationTypeEnum> getClassAnnotationTypes(FrameworkTypeEnum frameworkType) {
        List<ClassAnnotationTypeEnum> classAnnotationTypeEnums = new ArrayList<>();
        ClassAnnotationTypeEnum[] values = ClassAnnotationTypeEnum.values();
        for (ClassAnnotationTypeEnum value : values) {
            if (frameworkType.equals(value.getFrameworkType())) {
                classAnnotationTypeEnums.add(value);
            }
        }
        return classAnnotationTypeEnums;
    }
}
