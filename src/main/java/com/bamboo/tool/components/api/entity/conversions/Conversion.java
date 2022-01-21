package com.bamboo.tool.components.api.entity.conversions;

/**
 * Create by GuoQing
 * Date 2022/1/21 13:42
 * Description
 */
public interface Conversion<T, V> {
    public V conversion(T t);
}
