package com.bamboo.tool.listeners;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.json.JSONUtil;
import com.bamboo.tool.config.BambooToolComponent;
import com.bamboo.tool.config.model.BambooToolConfig;
import com.bamboo.tool.config.model.ProjectInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Create by GuoQing
 * Date 2022/1/19 14:56
 * Description  项目启动关闭监听
 */
public class BambooProjectManagerListener implements ProjectManagerListener {

    @Override
    public void projectOpened(@NotNull Project project) {
        BambooToolConfig state = BambooToolComponent.getInstance().getState();
        String projectSavePath = state.getProjectSavePath();
        String filePath = projectSavePath + "projects.json";
        boolean exist = FileUtil.exist(filePath);
        File file = null;
        if (!exist) {
            file = FileUtil.touch(filePath);
        } else {
            file = FileUtil.file(filePath);
        }
        FileReader fileReader = FileReader.create(file);
        String result = fileReader.readString();
        List<ProjectInfo> projectInfos = JSONUtil.toList(result, ProjectInfo.class);
//        ProjectInfo currentProjectInfo = state.getCurrentProjectInfo(project.getName(), project.getBasePath());
//        if(Objects.isNull(currentProjectInfo)){
//            ProjectInfo projectInfo = new ProjectInfo();
//            projects.put(project.getName()+"-"+project.getBasePath(),projectInfo);
//            String userLocalPath = System.getProperty("user.home");
//            projectInfo.setProjectName(project.getName());
//            projectInfo.setProjectPath(project.getBasePath());
//        }
//
//        String userLocalPath = System.getProperty("user.home");
        ProjectManagerListener.super.projectOpened(project);
    }

    @Override
    public void projectClosed(@NotNull Project project) {
        System.out.printf("111111111111");
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
}
