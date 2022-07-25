package com.bamboo.tool.config;

import com.bamboo.tool.entity.BambooClass;
import lombok.Data;

import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/7/25 10:55
 * Description
 */
@Data
public class BambooApisComponentConfig {
    List<BambooClass> bambooClasses;
}
