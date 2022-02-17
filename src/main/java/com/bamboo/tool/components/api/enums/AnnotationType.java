package com.bamboo.tool.components.api.enums;

import com.bamboo.tool.components.api.entity.AnnotationFrameWork;
import lombok.Getter;

/**
 * Create by GuoQing
 * Date 2022/1/21 12:22
 * Description  class注解类型枚举
 */
@Getter
public enum AnnotationType {
    REST_CONTROLLER("org.springframework.web.bind.annotation.RestController", "RestController"
            , new AnnotationFrameWork("Spring"),AnnotationScope.CLASS,SoaType.SERVICE),
    CONTROLLER("org.springframework.stereotype.Controller", "Controller",
            new AnnotationFrameWork("Spring"),AnnotationScope.CLASS,SoaType.SERVICE),
    REQUEST_MAPPING_CLASS("org.springframework.web.bind.annotation.RequestMapping", "RequestMapping",
            new AnnotationFrameWork("Spring"),AnnotationScope.CLASS,SoaType.SERVICE),

    GET_MAPPING("org.springframework.web.bind.annotation.GetMapping", "GetMapping",
            new AnnotationFrameWork("Spring"),AnnotationScope.METHOD,SoaType.SERVICE),
    POST_MAPPING("org.springframework.web.bind.annotation.PostMapping", "PostMapping",
            new AnnotationFrameWork("Spring"),AnnotationScope.METHOD,SoaType.SERVICE),
    REQUEST_MAPPING_METHOD("org.springframework.web.bind.annotation.RequestMapping", "RequestMapping",
            new AnnotationFrameWork("Spring"),AnnotationScope.METHOD,SoaType.SERVICE),
    DELETE_MAPPING("org.springframework.web.bind.annotation.DeleteMapping", "DeleteMapping",
            new AnnotationFrameWork("Spring"),AnnotationScope.METHOD,SoaType.SERVICE),
    PUT_MAPPING("org.springframework.web.bind.annotation.PutMapping", "PutMapping",
            new AnnotationFrameWork("Spring"),AnnotationScope.METHOD,SoaType.SERVICE),
    PATCH_MAPPING("org.springframework.web.bind.annotation.PatchMapping", "PatchMapping",
            new AnnotationFrameWork("Spring"),AnnotationScope.METHOD,SoaType.SERVICE),

    SOA_METHOD_REGISTER("com.odianyun.soa.annotation.SoaMethodRegister", "SoaMethodRegister",
            new AnnotationFrameWork("ouDianYun"),AnnotationScope.CLASS,SoaType.SERVICE),
    SOA_SERVICE_CLIENT("com.odianyun.soa.client.annotation.SoaServiceClient", "SoaServiceRegister",
            new AnnotationFrameWork("ouDianYun"),AnnotationScope.CLASS,SoaType.CLIENT),
    SOA_SERVICE_REGISTER("com.odianyun.soa.annotation.SoaServiceRegister", "SoaServiceRegister",
            new AnnotationFrameWork("ouDianYun"),AnnotationScope.METHOD,SoaType.SERVICE);

    private String classPath;
    private String classShortName;
    private AnnotationFrameWork annotationFrameWork;
    private AnnotationScope scope;
    private SoaType soaType;

    AnnotationType(String classPath,
                   String classShortName,
                   AnnotationFrameWork annotationFrameWork,
                   AnnotationScope scope,
                   SoaType soaType){
        this.classPath = classPath;
        this.classShortName = classShortName;
        this.annotationFrameWork = annotationFrameWork;
        this.scope = scope;
        this.soaType = soaType;
    }
}
