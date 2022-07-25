package com.bamboo.tool.config;

import com.bamboo.tool.store.LocalStoreServiceImpl;
import com.bamboo.tool.store.StoreService;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Create by GuoQing
 * Date 2022/7/25 10:17
 * Description
 */
@State(name = "restfulCloud", storages = {@Storage("restfulCloudApis.xml")})
public class BambooApisComponent implements PersistentStateComponent<BambooApisComponentConfig> {

    BambooApisComponentConfig bambooApisComponentConfig;
    @Override
    public @Nullable
    BambooApisComponentConfig getState() {
        if (Objects.isNull(bambooApisComponentConfig)) {
            bambooApisComponentConfig = new BambooApisComponentConfig();
        }
        return bambooApisComponentConfig;
    }

    @Override
    public void loadState(@NotNull BambooApisComponentConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }

    public static BambooApisComponent getInstance(Project project) {
        return project.getService(BambooApisComponent.class);
    }
    public static StoreService getStoreService() {
        StoreService storeService=new LocalStoreServiceImpl();
        return storeService;
    }
}
