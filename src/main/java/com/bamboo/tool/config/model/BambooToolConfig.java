package com.bamboo.tool.config.model;

import com.bamboo.tool.components.api.entity.BambooMethod;
import lombok.Data;

import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/1/20 11:04
 * Description
 */
@Data
public class BambooToolConfig {

    private String projectId;

    private ProjectInfo projectInfo;

}
