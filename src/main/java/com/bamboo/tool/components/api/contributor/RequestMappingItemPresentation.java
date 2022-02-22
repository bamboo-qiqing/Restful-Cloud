package com.bamboo.tool.components.api.contributor;


import com.intellij.icons.AllIcons;
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

    private RequestMappingNavigationItem item;

    public RequestMappingItemPresentation(RequestMappingNavigationItem requestMappingNavigationItem) {
        this.item = requestMappingNavigationItem;
    }

    @Override
    public @NlsSafe
    @Nullable
    String getPresentableText() {
        return item.getUrlPath();
    }

    @Override
    public @NlsSafe
    @Nullable
    String getLocationString() {
        return item.getProjectName() + "." + item.getClassName() + ".java[" + item.getRequestMethod() + "]";
    }

    @Override
    public @Nullable
    Icon getIcon(boolean b) {
//        if (item.getTypes().contains(FrameworkType.O_DIAN_YUN.getCode())) {
//            if (item.getTypes().contains(InterfaceType.SERVICE.getCode())) {
//                return PluginIcons.SOA_SERVICE;
//            }
//            if (item.getTypes().contains(InterfaceType.CLIENT.getCode())) {
//                return PluginIcons.SOA_CLIENT;
//            }
//        }
//        if (item.getTypes().contains(InterfaceType.SERVICE.getCode())) {
//            return AllIcons.Nodes.Static;
//        }
//        if (item.getTypes().contains(InterfaceType.CLIENT.getCode())) {
//            return AllIcons.Nodes.Class;
//        }
        return null;
    }
}
