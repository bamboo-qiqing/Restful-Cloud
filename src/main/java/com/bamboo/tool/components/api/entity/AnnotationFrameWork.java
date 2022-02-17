package com.bamboo.tool.components.api.entity;

import lombok.Data;

/**
 * Create by GuoQing
 * Date 2022/2/16 12:58
 * Description
 */
@Data
public class AnnotationFrameWork {

    private String frameWorkName;
    private String frameWorkDesc;

    public AnnotationFrameWork(String frameWorkName) {
        this.frameWorkName = frameWorkName;
    }
}
