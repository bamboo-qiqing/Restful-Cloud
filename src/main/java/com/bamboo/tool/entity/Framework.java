package com.bamboo.tool.entity;

import lombok.Data;

/**
 * Create by GuoQing
 * Date 2022/2/18 16:47
 * Description
 */
@Data
public class Framework {
    private Integer id;
    private String name;
    private String describe;

    public Object[] getStrings() {
        Object[] data = { name,describe, id};
        return data;
    }

    @Override
    public String toString() {
        return name;
    }
}
