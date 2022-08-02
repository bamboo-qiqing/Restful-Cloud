package com.bamboo.tool.enums;

import lombok.Getter;

/**
 * Create by GuoQing
 * Date 2022/7/28 17:01
 * Description
 */
@Getter
public enum DefaultOtherParams {
    GET_MAPPING_REQUEST_METHOD(AttributeEnums.REQUEST_METHOD, RequestMethod.GET, DefaultAnnotationEnum.GetMapping),
    POST_MAPPING_REQUEST_METHOD(AttributeEnums.REQUEST_METHOD, RequestMethod.POST, DefaultAnnotationEnum.PostMapping),
    DELETE_MAPPING_REQUEST_METHOD(AttributeEnums.REQUEST_METHOD, RequestMethod.DELETE, DefaultAnnotationEnum.DeleteMapping),
    PUT_MAPPING_REQUEST_METHOD(AttributeEnums.REQUEST_METHOD, RequestMethod.PUT, DefaultAnnotationEnum.PutMapping),
    PATCH_MAPPING_REQUEST_METHOD(AttributeEnums.REQUEST_METHOD, RequestMethod.PATCH, DefaultAnnotationEnum.PatchMapping),
    ;
    private final AttributeEnums attribute;
    private final RequestMethod requestMethod;
    private final DefaultAnnotationEnum annotation;

    DefaultOtherParams(AttributeEnums attribute, RequestMethod requestMethod, DefaultAnnotationEnum annotation) {
        this.attribute = attribute;
        this.requestMethod = requestMethod;
        this.annotation = annotation;
    }
}
