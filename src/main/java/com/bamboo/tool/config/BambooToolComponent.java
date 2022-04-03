package com.bamboo.tool.config;

import com.bamboo.tool.config.model.BambooToolConfig;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Create by GuoQing
 * Date 2022/1/20 10:58
 * Description
 */
@State(name = "bambooTool", storages = {@Storage("bambooTool.xml")})
public class BambooToolComponent implements PersistentStateComponent<BambooToolConfig>  {


    private BambooToolConfig toolConfig;

    @Override
    public @Nullable
    BambooToolConfig getState() {
        if (Objects.isNull(toolConfig)) {
            toolConfig = new BambooToolConfig();
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
