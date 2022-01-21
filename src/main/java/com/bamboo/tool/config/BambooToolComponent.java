package com.bamboo.tool.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.bamboo.tool.config.model.BambooToolConfig;
import com.bamboo.tool.config.model.ProjectInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Create by GuoQing
 * Date 2022/1/20 10:58
 * Description url工具配置类
 */
@State(name = "bambooTool", storages = {@Storage("bambooTool.xml")})
public class BambooToolComponent implements PersistentStateComponent<BambooToolConfig> {


    private BambooToolConfig toolConfig;

    @Override
    public @Nullable
    BambooToolConfig getState() {
        if (Objects.isNull(toolConfig)) {
            toolConfig = new BambooToolConfig();
        }
        if(toolConfig.getApiMethods()==null){
            toolConfig.setApiMethods(new ArrayList<>());
        }
        if(toolConfig.getProjectInfo()==null){
            toolConfig.setProjectInfo(new ProjectInfo());
        }
        if(StrUtil.isEmpty(toolConfig.getProjectSavePath())){
            toolConfig.setProjectSavePath(FileUtil.getUserHomePath() + "\\bambooTool\\");
        }

        return toolConfig;
    }

    @Override
    public void loadState(@NotNull BambooToolConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }


    public static BambooToolComponent getInstance() {
        return ApplicationManager.getApplication().getService(BambooToolComponent.class);
    }

}
