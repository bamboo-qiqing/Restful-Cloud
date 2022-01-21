package com.bamboo.tool.listeners;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.bamboo.tool.config.BambooToolComponent;
import com.bamboo.tool.config.model.BambooToolConfig;
import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.util.FileUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Create by GuoQing
 * Date 2022/1/19 14:56
 * Description  项目启动关闭监听
 */
public class ToolProjectManagerListener implements ProjectManagerListener {

    @Override
    public void projectOpened(@NotNull Project project) {
        BambooToolConfig state = BambooToolComponent.getInstance().getState();
        String projectName = project.getName();
        String projectPath = project.getBasePath();
        String apiUrlFileName = String.format("%sApi.json", projectName);
        String apiUrlFilePath = String.format("%s%s%s%s", state.getProjectSavePath(), projectName + "\\", apiUrlFileName);
        ProjectInfo projectInfo = state.getProjectInfo();
        projectInfo.setProjectName(projectName);
        projectInfo.setProjectPath(projectPath);
        projectInfo.setProjectPath(apiUrlFilePath);
        ProjectManagerListener.super.projectOpened(project);
    }



    @Override
    public void projectClosed(@NotNull Project project) {
        ProjectManagerListener.super.projectClosed(project);
    }

    @Override
    public void projectClosing(@NotNull Project project) {
        ProjectManagerListener.super.projectClosing(project);
    }

    @Override
    public void projectClosingBeforeSave(@NotNull Project project) {
        ProjectManagerListener.super.projectClosingBeforeSave(project);
    }

    private ProjectInfo loadProjectInfo(@NotNull Project project,  BambooToolConfig  config) {
        String projectName = project.getName();
        String projectPath = project.getBasePath();
        String projectSavePath = config.getProjectSavePath();
        String filePath = projectSavePath + "projects.json";
        String result = FileUtil.getFileReaderString(filePath);
        List<ProjectInfo> projectInfos;
        if (StrUtil.isNotBlank(result)) {
            projectInfos = JSONUtil.toList(result, ProjectInfo.class);
        } else {
            projectInfos = new ArrayList<>();
        }

        ProjectInfo projectInfo = projectInfos.stream()
                .filter(e -> e.getProjectPath().equals(project.getBasePath()))
                .filter(e -> e.getProjectName().equals(project.getName()))
                .findFirst().get();
        if (Objects.isNull(projectInfo)) {
            String apiUrlFileName = String.format("%sApi.json", projectName);
            String apiUrlFilePath = String.format("%s%s%s%s", projectSavePath, projectName + "\\", apiUrlFileName);
            projectInfo = new ProjectInfo();
            projectInfo.setProjectPath(projectPath);
            projectInfo.setProjectName(project.getName());
            projectInfo.setApiUrlFileName(apiUrlFileName);
            projectInfo.setApiUrlFilePath(apiUrlFilePath);
        }

        return projectInfo;
    }
}
