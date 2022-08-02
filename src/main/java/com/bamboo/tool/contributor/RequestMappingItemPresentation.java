package com.bamboo.tool.contributor;


import com.bamboo.tool.db.entity.BambooApiMethod;
import com.intellij.navigation.ItemPresentation;
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
    public
    @Nullable
    String getPresentableText() {
        return item.getUrl();
    }

    @Override
    public
    @Nullable
    String getLocationString() {
        return item.getClassName() + item.getRequestMethods();
    }

    @Override
    public @Nullable
    Icon getIcon(boolean b) {
        return item.getSoaType().getIcon();
    }

}
