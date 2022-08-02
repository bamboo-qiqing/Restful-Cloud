package com.bamboo.tool.configurable;

import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.enums.RequestMethod;
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

@State(name = "ProjectFileterConfiguration", storages = @Storage(StoragePathMacros.CACHE_FILE))
public class ProjectFileterConfiguration extends ChooseByNameFilterConfiguration<ProjectInfo> {
    public static ProjectFileterConfiguration getInstance(Project project) {
        return project.getService(ProjectFileterConfiguration.class);
    }


    @Override
    protected String nameForElement(ProjectInfo projectInfo) {
        return projectInfo.getProjectName();
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
