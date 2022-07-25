package com.bamboo.tool.store;

import com.bamboo.tool.config.BambooToolComponent;
import com.bamboo.tool.entity.AnnotationInfoSetting;

import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/7/25 14:42
 * Description
 */
public class LocalStoreServiceImpl implements StoreService {
    @Override
    public List<AnnotationInfoSetting> getAllAnnotation() {
        return BambooToolComponent.getInstance().getState().getAnnotations();
    }
}
