package com.bamboo.tool.config.model;

import com.bamboo.tool.entity.AnnotationInfoSetting;
import com.bamboo.tool.entity.Framework;
import lombok.Data;

import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/1/20 11:04
 * Description
 */
@Data
public class RestfulCloudConfig {

    /**
     * 是否显示注释，默认显示
     */
    private Boolean isShowDesc;
    /**
     * 注解集合
     */
    private List<AnnotationInfoSetting> annotations;
    /**
     * 项目集合
     */
    private List<ProjectInfo> projectInfos;

}
