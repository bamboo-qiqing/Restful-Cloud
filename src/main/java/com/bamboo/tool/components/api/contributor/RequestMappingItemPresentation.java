package com.bamboo.tool.components.api.contributor;


import com.bamboo.tool.db.entity.BambooApiMethod;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.NlsSafe;
import icons.PluginIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Create by GuoQing
 * Date 2022/2/15 11:18
 * Description
 */
public class RequestMappingItemPresentation implements ItemPresentation {

    private BambooApiMethod item;

    public RequestMappingItemPresentation(BambooApiMethod bambooApiMethod) {
        this.item = bambooApiMethod;
    }

    @Override
    public @NlsSafe
    @Nullable
    String getPresentableText() {
        return item.getUrl();
    }

    @Override
    public @NlsSafe
    @Nullable
    String getLocationString() {
        return item.getProjectName() + "." + item.getClassName() + item.getRequestMethods();
    }

    @Override
    public @Nullable
    Icon getIcon(boolean b) {
        if (item.getFrameworkName().equals("o_dian_yun")||item.getFrameworkName().equals("spring_cloud")) {
            if (item.getSoaType().equals("service")) {
                return PluginIcons.SOA_SERVICE;
            }
            if (item.getSoaType().equals("client")) {
                return PluginIcons.SOA_CLIENT;
            }
        }
        if (item.getFrameworkName().equals("Spring")) {
            if (item.getSoaType().equals("service")) {
                return PluginIcons.SERVICE;
            }
            if (item.getSoaType().equals("client")) {
                return PluginIcons.CLIENT;
            }
        }
        return null;
    }
}
