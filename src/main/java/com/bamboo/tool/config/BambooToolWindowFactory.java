package com.bamboo.tool.config;

import com.bamboo.tool.components.api.view.CurrentApisNavToolWindow;
import com.bamboo.tool.components.api.view.OtherApisNavToolWindow;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Create by GuoQing
 * Date 2022/1/27 16:26
 * Description
 */
public class BambooToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        CurrentApisNavToolWindow currentApisNavToolWindow = new CurrentApisNavToolWindow(project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        currentApisNavToolWindow.getComponent().add(currentApisNavToolWindow.getContent());
        Content currentApis = contentFactory.createContent(currentApisNavToolWindow, "Current Project", true);
        toolWindow.getContentManager().addContent(currentApis);

        OtherApisNavToolWindow otherApisNavToolWindow = new OtherApisNavToolWindow(project);
        otherApisNavToolWindow.getComponent().add(otherApisNavToolWindow.getContent());
        Content otherApis = contentFactory.createContent(otherApisNavToolWindow, "Other Project", true);
        toolWindow.getContentManager().addContent(otherApis);
    }
}
