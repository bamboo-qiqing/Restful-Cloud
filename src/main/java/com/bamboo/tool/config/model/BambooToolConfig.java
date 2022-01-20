package com.bamboo.tool.config.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by GuoQing
 * Date 2022/1/20 11:04
 * Description
 */
@Data
public class BambooToolConfig {

    private String projectSavePath =  System.getProperty("user.home")+"\\bambooTool\\";
}
