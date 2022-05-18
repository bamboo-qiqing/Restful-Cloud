package com.bamboo.tool.config;

import com.bamboo.tool.components.api.view.CurrentApisNavToolWindow;
import com.bamboo.tool.components.api.view.OtherApisNavToolWindow;
import com.bamboo.tool.components.api.view.HistoryApisNavToolWindow;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import org.jetbrains.annotations.NotNull;

/**
 * Create by GuoQing
 * Date 2022/1/27 16:26
 * Description
 */
public class BambooToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        CurrentApisNavToolWindow currentApisNavToolWindow = new CurrentApisNavToolWindow(project);
        currentApisNavToolWindow.getComponent().add(currentApisNavToolWindow.getContent());
        Content currentApis = contentFactory.createContent(currentApisNavToolWindow, "Current", true);
        toolWindow.getContentManager().addContent(currentApis);

        HistoryApisNavToolWindow historyApisNavToolWindow = new HistoryApisNavToolWindow(project);
        historyApisNavToolWindow.getComponent().add(historyApisNavToolWindow.getContent());

        Content historyApis = contentFactory.createContent(historyApisNavToolWindow, "History", true);
        toolWindow.getContentManager().addContent(historyApis);
        toolWindow.addContentManagerListener(new ContentManagerListener(){
            @Override
            public void selectionChanged(@NotNull ContentManagerEvent event) {
                final Content content = event.getContent().getManager().getContent(1);
                if(content.getComponent() instanceof  HistoryApisNavToolWindow){
                    HistoryApisNavToolWindow component = (HistoryApisNavToolWindow)content.getComponent();
                    component.renderData(project);
                }
                ContentManagerListener.super.selectionChanged(event);
            }
        });

        OtherApisNavToolWindow otherApisNavToolWindow = new OtherApisNavToolWindow(project);
        otherApisNavToolWindow.getComponent().add(otherApisNavToolWindow.getContent());
        Content otherApis = contentFactory.createContent(otherApisNavToolWindow, "Other", true);
        toolWindow.getContentManager().addContent(otherApis);
    }
}
