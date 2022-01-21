package com.bamboo.tool.config.model;

import cn.hutool.core.io.FileUtil;
import com.bamboo.tool.components.api.entity.ApiMethod;
import lombok.Data;

import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/1/20 11:04
 * Description
 */
@Data
public class BambooToolConfig {

    private String projectSavePath ;

    private ProjectInfo projectInfo;

    private List<ApiMethod> apiMethods;
}
