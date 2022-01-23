package com.bamboo.tool.components.api.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/1/20 14:46
 * Description
 */
@Data
public class ApiMethod {
    /**
     * 描述
     */
    private String description = StringUtils.EMPTY;
    /**
     * 方法名称
     */
    private String methodName = StringUtils.EMPTY;

    /**
     * 请求类型
     */
    private List<String> methodTypes;

    /**
     * 方法Url地址
     */
    private List<String> methodUrls = new ArrayList<>();

    /**
     * url地址
     */
    private String url = StringUtils.EMPTY;

    private List<String> contentTypes = new ArrayList<>();
    private List<String> headers = new ArrayList<>();
    private List<String> params = new ArrayList<>();
}
