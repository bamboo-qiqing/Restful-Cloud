package com.bamboo.tool.entity;

import lombok.Data;

/**
 * Create by GuoQing
 * Date 2022/3/26 14:18
 * Description
 */
@Data
public class DescFramework {
    private Integer id;
    private String framework;
    private String frameworkCode;
    private String describe;
    private Integer sequence;

    public Object[] getStrings() {
        Object[] descFramework = {framework, frameworkCode, describe, sequence, id};
        return descFramework;
    }
}
