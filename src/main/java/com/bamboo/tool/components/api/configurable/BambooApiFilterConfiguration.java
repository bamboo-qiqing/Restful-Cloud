package com.bamboo.tool.components.api.configurable;

import com.bamboo.tool.components.api.enums.RequestMethod;
import com.intellij.ide.util.gotoByName.ChooseByNameFilterConfiguration;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;

/**
 * Create by GuoQing
 * Date 2022/2/24 11:34
 * Description
 */

@State(name = "BambooApiFilterConfiguration", storages = @Storage(StoragePathMacros.CACHE_FILE))
public class BambooApiFilterConfiguration extends ChooseByNameFilterConfiguration<RequestMethod> {
    public static BambooApiFilterConfiguration getInstance(Project project) {
        return project.getService(BambooApiFilterConfiguration.class);
    }


    @Override
    protected String nameForElement(RequestMethod requestMethod) {
        return requestMethod.getCode();
    }

    @Override
    public void noStateLoaded() {
        super.noStateLoaded();
    }

    @Override
    public void initializeComponent() {
        super.initializeComponent();
    }
}
