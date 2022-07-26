package com.bamboo.tool.entity;

import lombok.Data;

import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/7/26 16:45
 * Description
 */
@Data
public class RestfulApi {
    private List<String> apis;
    private List<String> requestMethods;
}
