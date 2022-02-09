package com.bamboo.tool.components.api.enums;

import com.bamboo.tool.components.api.factory.ClassAnnotationProcess;
import com.bamboo.tool.components.api.framework.odianyun.annotations.SoaMethodClient;
import com.bamboo.tool.components.api.framework.odianyun.annotations.SoaServiceClient;
import com.bamboo.tool.components.api.framework.odianyun.annotations.SoaServiceRegister;
import com.bamboo.tool.components.api.framework.spring.annotations.Controller;
import com.bamboo.tool.components.api.framework.spring.annotations.RequestMapping;
import com.bamboo.tool.components.api.framework.spring.annotations.RestController;
import lombok.Getter;

/**
 * Create by GuoQing
 * Date 2022/1/21 12:22
 * Description  class注解类型枚举
 */
@Getter
public enum ClassAnnotationType {
    REST_CONTROLLER("org.springframework.web.bind.annotation.RestController", new RestController()),
    CONTROLLER("org.springframework.stereotype.Controller", new Controller()),
    REQUEST_MAPPING("org.springframework.web.bind.annotation.RequestMapping", new RequestMapping()),
    JAVAX_PATH("javax.ws.rs.Path", null),


    SOA_SERVICE_REGISTER("com.odianyun.soa.annotation.SoaServiceRegister", new SoaServiceRegister()),
    SOA_SERVICE_CLIENT("com.odianyun.soa.client.annotation.SoaServiceClient", new SoaServiceClient());
    private String classPath;
    private ClassAnnotationProcess classAnnotationProcess;


    ClassAnnotationType(String classPath, ClassAnnotationProcess classAnnotationProcess) {
        this.classPath = classPath;
        this.classAnnotationProcess = classAnnotationProcess;

    }
}
