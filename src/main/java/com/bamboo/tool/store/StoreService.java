package com.bamboo.tool.store;

import com.bamboo.tool.entity.AnnotationInfoSetting;

import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/7/25 14:39
 * Description
 */
public interface StoreService {
    /**
     * 获取所有注解信息
     *
     * @return
     */
    List<AnnotationInfoSetting> getAllAnnotation();
}
