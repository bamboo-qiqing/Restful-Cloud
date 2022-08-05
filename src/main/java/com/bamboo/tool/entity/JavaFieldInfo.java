package com.bamboo.tool.entity;

import lombok.Data;

import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/8/5 15:35
 * Description
 */
@Data
public class JavaFieldInfo {
    /**
     * 属性名
     */
    private String fieldName;
    /**
     * 属性类型
     */
    private String fieldType;
    /**
     * javadoc
     */
    private JavaDocComment javaDocComment;
    /**
     * 下级属性集合
     */
    private List<JavaFieldInfo> children;

}
