package com.bamboo.tool.components.api.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class ApiClass {
    private String className = StringUtils.EMPTY;
    private String moduleName = StringUtils.EMPTY;
    private String serviceName = StringUtils.EMPTY;
    private List<String> classUrls = new ArrayList<>();
    private String description = StringUtils.EMPTY;
    private List<String> types = new ArrayList<>();
    private List<ApiMethod> methods;
    private String classPath= StringUtils.EMPTY;
}
