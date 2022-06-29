package com.bamboo.tool.config;

import com.bamboo.tool.db.service.BambooService;
import com.bamboo.tool.util.I18nUtil;
import com.bamboo.tool.view.CurrentApisNavTool;

import com.bamboo.tool.view.OtherApisNavToolWindow;
import com.bamboo.tool.view.HistoryApisNavToolWindow;
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
        BambooService.initTable(project);


        CurrentApisNavTool currentApisNavTool = new CurrentApisNavTool(project);
        currentApisNavTool.getComponent().add(currentApisNavTool.getContent());
        Content currentApisNavToolApis = contentFactory.createContent(currentApisNavTool, I18nUtil.getString("panel.current"), true);
        toolWindow.getContentManager().addContent(currentApisNavToolApis);



        HistoryApisNavToolWindow historyApisNavToolWindow = new HistoryApisNavToolWindow(project);
        historyApisNavToolWindow.getComponent().add(historyApisNavToolWindow.getContent());

        Content historyApis = contentFactory.createContent(historyApisNavToolWindow, I18nUtil.getString("panel.history"), true);
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
        Content otherApis = contentFactory.createContent(otherApisNavToolWindow, I18nUtil.getString("panel.other"), true);
        toolWindow.getContentManager().addContent(otherApis);
    }
}
