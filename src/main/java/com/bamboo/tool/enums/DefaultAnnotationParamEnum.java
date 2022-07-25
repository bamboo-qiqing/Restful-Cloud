package com.bamboo.tool.enums;

import lombok.Getter;

/**
 * Create by GuoQing
 * Date 2022/7/25 17:03
 * Description 默认注解参数初始化数据枚举
 */
@Getter
public enum DefaultAnnotationParamEnum {


    RequestMapping_Class_value("value", "classUrl", "",DefaultAnnotationEnum.RequestMapping_Class),
    RequestMapping_Class_path("path", "classUrl", "",DefaultAnnotationEnum.RequestMapping_Class),
    RequestMapping_Class_null("null", "classUrl", "",DefaultAnnotationEnum.RequestMapping_Class),

    GetMapping_value("value", "methodUrl", "",DefaultAnnotationEnum.GetMapping),
    GetMapping_path("path", "methodUrl", "",DefaultAnnotationEnum.GetMapping),
    GetMapping_null("null", "methodUrl", "",DefaultAnnotationEnum.GetMapping),

    PostMapping_value("value", "methodUrl", "",DefaultAnnotationEnum.PostMapping),
    PostMapping_path("path", "methodUrl", "",DefaultAnnotationEnum.PostMapping),
    PostMapping_null("null", "methodUrl", "",DefaultAnnotationEnum.PostMapping),

    DeleteMapping_value("value", "methodUrl", "",DefaultAnnotationEnum.DeleteMapping),
    DeleteMapping_path("path", "methodUrl", "",DefaultAnnotationEnum.DeleteMapping),
    DeleteMapping_null("null", "methodUrl", "",DefaultAnnotationEnum.DeleteMapping),


    PutMapping_value("value", "methodUrl", "",DefaultAnnotationEnum.PutMapping),
    PutMapping_path("path", "methodUrl", "",DefaultAnnotationEnum.PutMapping),
    PutMapping_null("null", "methodUrl", "",DefaultAnnotationEnum.PutMapping),

    PatchMapping_value("value", "methodUrl", "",DefaultAnnotationEnum.PatchMapping),
    PatchMapping_path("path", "methodUrl", "",DefaultAnnotationEnum.PatchMapping),
    PPatchMapping_null("null", "methodUrl", "",DefaultAnnotationEnum.PatchMapping),

    RequestMapping_Method_value("value", "methodUrl", "",DefaultAnnotationEnum.RequestMapping_Method),
    RequestMapping_Method_path("path", "methodUrl", "",DefaultAnnotationEnum.RequestMapping_Method),
    RequestMapping_Method_null("null", "methodUrl", "",DefaultAnnotationEnum.RequestMapping_Method),
    RequestMapping_Method_method("method", "requestMethod", "",DefaultAnnotationEnum.RequestMapping_Method),

    Api_value("value", "desc", "",DefaultAnnotationEnum.Api),
    Api_description("desc", "methodUrl", "",DefaultAnnotationEnum.Api),
    Api_null("null", "desc", "",DefaultAnnotationEnum.Api),


    ApiOperation_value("value", "desc", "",DefaultAnnotationEnum.ApiOperation),
    ApiOperation_null("null", "desc", "",DefaultAnnotationEnum.ApiOperation),


    FeignClient_name("name", "poolUrl", "",DefaultAnnotationEnum.FeignClient),
    FeignClient_path("path", "classUrl", "",DefaultAnnotationEnum.FeignClient),
    ;


    private final String name;
    private final String type;
    private final String desc;
    private final DefaultAnnotationEnum annotationEnum;
    DefaultAnnotationParamEnum(String name, String type, String desc,DefaultAnnotationEnum annotationEnum) {
        this.name = name;
        this.type = type;
        this.desc = desc;
        this.annotationEnum = annotationEnum;
    }
}
