package com.bamboo.tool.components.api.entity;

import com.google.protobuf.Api;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class ApiClass {
    private String className = StringUtils.EMPTY;
    private String moduleName = StringUtils.EMPTY;
    private String packageName = StringUtils.EMPTY;
    private List<String> classUrls = new ArrayList<>();
    private String description = StringUtils.EMPTY;
    private List<ApiMethod> methods;
}
