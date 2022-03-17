package com.bamboo.tool.listeners;

import cn.hutool.core.io.FileUtil;
import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.db.service.BambooService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

/**
 * Create by GuoQing
 * Date 2022/1/19 14:56
 * Description  项目启动关闭监听
 */
public class ToolProjectManagerListener implements ProjectManagerListener {

    @Override
    public void projectOpened(@NotNull Project project) {
        String db = FileUtil.getUserHomePath() + "\\bambooTool\\bambooApi.db";
        final boolean exist = FileUtil.exist(db);
        if (!exist) {
            FileUtil.touch(db);
        }
        BambooService.initTable();
        ProjectInfo currentProject = BambooService.queryProject(project.getBasePath(), project.getName());
        if (currentProject == null) {
            currentProject = new ProjectInfo();
            currentProject.setProjectPath(project.getBasePath());
            currentProject.setProjectName(project.getName());
        }
        BambooService.saveProject(currentProject);

    }

    @Override
    public void projectClosing(@NotNull Project project) {

    }
}
