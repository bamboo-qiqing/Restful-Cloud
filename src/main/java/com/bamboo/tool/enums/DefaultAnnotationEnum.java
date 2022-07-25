package com.bamboo.tool.enums;

import lombok.Getter;

/**
 * Create by GuoQing
 * Date 2022/7/25 12:46
 * Description
 */
@Getter
public enum DefaultAnnotationEnum {
    //spring
    RestController("org.springframework.web.bind.annotation.RestController",
            "RestController", "Class", "service", "scann", DefaultFrameWorkEnum.SPRING),

    Controller("org.springframework.stereotype.Controller",
            "Controller", "Class", "service", "scann", DefaultFrameWorkEnum.SPRING),

    RequestMapping_Class("org.springframework.web.bind.annotation.RequestMapping",
            "RequestMapping", "Class", "service", "attribute", DefaultFrameWorkEnum.SPRING),

    GetMapping("org.springframework.web.bind.annotation.GetMapping,GetMapping",
            "GetMapping", "Method", "service", "scann,attribute", DefaultFrameWorkEnum.SPRING),

    PostMapping("org.springframework.web.bind.annotation.PostMapping,PostMapping",
            "PostMapping", "Method", "service", "scann,attribute", DefaultFrameWorkEnum.SPRING),

    DeleteMapping("org.springframework.web.bind.annotation.DeleteMapping",
            "DeleteMapping", "Method", "service", "scann,attribute", DefaultFrameWorkEnum.SPRING),

    PutMapping("org.springframework.web.bind.annotation.PutMapping",
            "PutMapping", "Method", "service", "scann,attribute", DefaultFrameWorkEnum.SPRING),

    PatchMapping("org.springframework.web.bind.annotation.PatchMapping",
            "PatchMapping", "Method", "service", "scann,attribute", DefaultFrameWorkEnum.SPRING),

    RequestMapping_Method("org.springframework.web.bind.annotation.RequestMapping",
            "RequestMapping", "Method", "service", "scann,attribute", DefaultFrameWorkEnum.SPRING),


    //swagger
    Api("io.swagger.annotations.Api",
            "Api", "Class", null, "attribute", DefaultFrameWorkEnum.SWAGGER),

    ApiOperation("io.swagger.annotations.ApiOperation",
            "ApiOperation", "Method", null, "attribute", DefaultFrameWorkEnum.SWAGGER),


    //Spring cloud
    FeignClient("org.springframework.cloud.openfeign.FeignClient",
            "FeignClient", "Class", "soa_client", "scann,attribute", DefaultFrameWorkEnum.SPRING_CLOUD),
    ;
    private final String classPath;
    private final String classShortName;
    private final String scope;
    private final String soaType;
    private final String effect;
    private final DefaultFrameWorkEnum frameWork;

    DefaultAnnotationEnum(String classPath, String classShortName, String scope, String soaType, String effect, DefaultFrameWorkEnum frameWork) {
        this.classPath = classPath;
        this.classShortName = classShortName;
        this.scope = scope;
        this.soaType = soaType;
        this.effect = effect;
        this.frameWork = frameWork;
    }


}
