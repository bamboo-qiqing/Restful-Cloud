package com.bamboo.tool.components.api.enums;

import com.bamboo.tool.components.api.factory.MethodAnnotationProcess;
import com.bamboo.tool.components.api.framework.odianyun.annotations.SoaMethodRegister;
import com.bamboo.tool.components.api.framework.spring.annotations.*;
import com.bamboo.tool.components.api.framework.swagger.annotations.ApiOperation;
import lombok.Getter;

@Getter
public enum MethodAnnotationType {
//    API_OPERATION("io.swagger.annotations.ApiOperation", new ApiOperation()),
    GET_MAPPING("org.springframework.web.bind.annotation.GetMapping", new GetMapping()),
    POST_MAPPING("org.springframework.web.bind.annotation.PostMapping", new PostMapping()),
    REQUEST_MAPPING("org.springframework.web.bind.annotation.RequestMapping", new RequestMapping()),
    DELETE_MAPPING("org.springframework.web.bind.annotation.DeleteMapping", new DeleteMapping()),
    PUT_MAPPING("org.springframework.web.bind.annotation.PutMapping", new PutMapping()),
    PATCH_MAPPING("org.springframework.web.bind.annotation.PatchMapping", new PatchMapping()),
    SOA_METHOD_REGISTER("com.odianyun.soa.annotation.SoaMethodRegister", new SoaMethodRegister());


    private String code;
    private MethodAnnotationProcess methodAnnotationProcess;

    MethodAnnotationType(String code, MethodAnnotationProcess methodAnnotationProcess) {
        this.code = code;
        this.methodAnnotationProcess = methodAnnotationProcess;
    }

}
