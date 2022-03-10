package com.bamboo.tool.listeners;

import cn.hutool.core.io.FileUtil;
import com.bamboo.tool.config.BambooToolComponent;
import com.bamboo.tool.config.model.BambooToolConfig;
import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.db.service.BambooService;
import com.bamboo.tool.util.StringUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

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
        if(!exist){
            FileUtil.touch(db);
        }
        BambooToolConfig state = BambooToolComponent.getInstance().getState();
        ProjectInfo projectInfo = null;
        if (StringUtil.isNotEmpty(state.getProjectId())) {
            ProjectInfo currentProject = BambooService.queryProject(project.getBasePath(),project.getName());
            if (currentProject != null) {
                projectInfo = currentProject;
            } else {
                String projectId = UUID.randomUUID().toString();
                projectInfo = new ProjectInfo();
                projectInfo.setProjectId(projectId);
            }
        } else {
            String projectId = UUID.randomUUID().toString();
            projectInfo = new ProjectInfo();
            projectInfo.setProjectId(projectId);
        }
        projectInfo.setProjectPath(project.getBasePath());
        projectInfo.setProjectName(project.getName());
        projectInfo = BambooService.saveProject(projectInfo);
        state.setProjectInfo(projectInfo);
        state.setProjectId(projectInfo.getProjectId());

        BambooService.initTable();
    }

    @Override
    public void projectClosing(@NotNull Project project) {

    }
}
