package com.bamboo.tool.components.api.enums;

import com.bamboo.tool.components.api.factory.MethodAnnotationProcess;
import com.bamboo.tool.components.api.framework.spring.annotations.*;
import lombok.Getter;

@Getter
public enum MethodAnnotationType {

    GET_MAPPING("org.springframework.web.bind.annotation.GetMapping", new GetMapping()),
    POST_MAPPING("org.springframework.web.bind.annotation.PostMapping", new PostMapping()),
    REQUEST_MAPPING("org.springframework.web.bind.annotation.RequestMapping", new RequestMapping()),
    DELETE_MAPPING("org.springframework.web.bind.annotation.DeleteMapping", new DeleteMapping()),
    PUT_MAPPING("org.springframework.web.bind.annotation.PutMapping", new PutMapping()),
    PATCH_MAPPING("org.springframework.web.bind.annotation.PatchMapping", new PatchMapping());

    private String code;
    private MethodAnnotationProcess methodAnnotationProcess;

    MethodAnnotationType(String code, MethodAnnotationProcess methodAnnotationProcess) {
        this.code = code;
        this.methodAnnotationProcess = methodAnnotationProcess;
    }

}
