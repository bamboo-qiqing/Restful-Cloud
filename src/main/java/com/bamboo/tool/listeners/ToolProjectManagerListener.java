package com.bamboo.tool.listeners;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.factory.FrameworkExecute;
import com.bamboo.tool.config.BambooToolComponent;
import com.bamboo.tool.config.model.BambooToolConfig;
import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.util.FileUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        String apiUrlFileName = String.format("%s-api.json", projectName);
        String apiUrlFilePath = String.format("%s%s%s", state.getProjectSavePath(), projectName + "\\", apiUrlFileName);
        ProjectInfo projectInfo = state.getProjectInfo();
        projectInfo.setProjectName(projectName);
        projectInfo.setProjectPath(projectPath);
        projectInfo.setApiUrlFilePath(apiUrlFilePath);
        projectInfo.setApiUrlFileName(apiUrlFileName);
        ApplicationManager.getApplication().runWriteAction(()->{
            List<ApiClass> apiClasses = FrameworkExecute.buildApiMethod(project);
            FileWriter writer = null;
            try {
                String formatStr = JSON.toJSONString(apiClasses, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                        SerializerFeature.WriteDateUseDateFormat);
                File touch = FileUtil.touch(state.getProjectInfo().getApiUrlFilePath());
                writer = new FileWriter(touch);
                writer.write(formatStr);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

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
        BambooToolConfig state = BambooToolComponent.getInstance().getState();


        ProjectManagerListener.super.projectClosingBeforeSave(project);
    }
}
