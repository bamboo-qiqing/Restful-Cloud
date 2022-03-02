package com.bamboo.tool.components.api.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class BambooClass {
    private String id;
    private String className = StringUtils.EMPTY;
    private String moduleName = StringUtils.EMPTY;
    private String description = StringUtils.EMPTY;
    private String classPath = StringUtils.EMPTY;
    private String qualifiedName = StringUtils.EMPTY;
    private String projectId;
    private List<BambooMethod> methods = new ArrayList<>();
    private List<BambooAnnotationInfo> annotations = new ArrayList<>();
}
