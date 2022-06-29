package com.bamboo.tool.configurable;

import com.bamboo.tool.enums.RequestMethod;
import com.bamboo.tool.enums.SoaType;
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

@State(name = "BambooSoaFilterConfiguration", storages = @Storage(StoragePathMacros.CACHE_FILE))
public class BambooSoaFilterConfiguration extends ChooseByNameFilterConfiguration<SoaType> {
    public static BambooSoaFilterConfiguration getInstance(Project project) {
        return project.getService(BambooSoaFilterConfiguration.class);
    }


    @Override
    protected String nameForElement(SoaType soaType) {
        return soaType.getCode();
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
