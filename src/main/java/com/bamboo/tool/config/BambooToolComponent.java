package com.bamboo.tool.config;

import cn.hutool.core.collection.CollectionUtil;
import com.bamboo.tool.config.model.RestfulCloudConfig;
import com.bamboo.tool.entity.AnnotationInfoSetting;
import com.bamboo.tool.entity.AnnotationParam;
import com.bamboo.tool.enums.DefaultAnnotationEnum;
import com.bamboo.tool.enums.DefaultAnnotationParamEnum;
import com.bamboo.tool.enums.DefaultOtherParams;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by GuoQing
 * Date 2022/1/20 10:58
 * Description
 */
@State(name = "restfulCloudConfig", storages = {@Storage("restfulCloudConfig.xml")})
public class BambooToolComponent implements PersistentStateComponent<RestfulCloudConfig> {


    private RestfulCloudConfig toolConfig;

    @Override
    public @Nullable
    RestfulCloudConfig getState() {
        if (Objects.isNull(toolConfig)) {
            toolConfig = BambooToolComponent.initBambooToolConfig();
        }
        return toolConfig;
    }

    @Override
    public void loadState(@NotNull RestfulCloudConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }

    public static BambooToolComponent getInstance() {
        return ApplicationManager.getApplication().getService(BambooToolComponent.class);
    }

    public static RestfulCloudConfig initBambooToolConfig() {
        RestfulCloudConfig bambooToolConfig = new RestfulCloudConfig();
        bambooToolConfig.setIsShowDesc(true);
        bambooToolConfig.setProjectInfos(new ArrayList<>());
        Map<DefaultAnnotationEnum, List<DefaultAnnotationParamEnum>> listMap = Arrays.stream(DefaultAnnotationParamEnum.values()).collect(Collectors.groupingBy(DefaultAnnotationParamEnum::getAnnotationEnum, Collectors.toList()));
        List<AnnotationInfoSetting> annotations = Arrays.stream(DefaultAnnotationEnum.values()).map(annotationEnum -> {
            AnnotationInfoSetting annotationInfoSetting = new AnnotationInfoSetting();
            annotationInfoSetting.setAnnotationName(annotationEnum.getClassShortName());
            annotationInfoSetting.setAnnotationPath(annotationEnum.getClassPath());
            annotationInfoSetting.setEffect(annotationEnum.getEffect());
            annotationInfoSetting.setScope(annotationEnum.getScope());
            annotationInfoSetting.setSoaType(annotationEnum.getSoaType());
            annotationInfoSetting.setFramework(annotationEnum.getFrameWork());
            List<DefaultAnnotationParamEnum> defaultAnnotationParamEnums = listMap.get(annotationEnum);
            if (CollectionUtil.isNotEmpty(defaultAnnotationParamEnums)) {
                List<AnnotationParam> params = defaultAnnotationParamEnums.stream().map(list -> {
                    AnnotationParam annotationParam = new AnnotationParam();
                    annotationParam.setName(list.getName());
                    annotationParam.setType(list.getType());
                    annotationParam.setDescribe(list.getDesc());
                    return annotationParam;
                }).collect(Collectors.toList());
                annotationInfoSetting.setParams(params);
            }
            Map<String, List<String>> otherParams = annotationInfoSetting.getOtherParams();
            Arrays.stream(DefaultOtherParams.values()).filter(e -> e.getAnnotation().equals(annotationEnum)).forEach(e -> {
                List<String> strings = otherParams.get(e.getAttribute().getCode());
                if (CollectionUtil.isNotEmpty(strings)) {
                    strings.add(e.getRequestMethod().getCode());
                } else {
                    List<String> objects = new ArrayList<>();
                    objects.add(e.getRequestMethod().getCode());
                    otherParams.put(e.getAttribute().getCode(), objects);
                }
            });
            return annotationInfoSetting;
        }).collect(Collectors.toList());
        bambooToolConfig.setAnnotations(annotations);
        return bambooToolConfig;
    }


}
