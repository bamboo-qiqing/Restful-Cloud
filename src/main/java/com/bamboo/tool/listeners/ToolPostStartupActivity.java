package com.bamboo.tool.listeners;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.factory.FrameworkExecute;
import com.bamboo.tool.config.BambooToolComponent;
import com.bamboo.tool.config.model.BambooToolConfig;
import com.bamboo.tool.util.FileUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/1/24 10:49
 * Description
 */
public class ToolPostStartupActivity implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {

        ApplicationManager.getApplication().runReadAction(()->{
            BambooToolConfig state = BambooToolComponent.getInstance().getState();
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
    }
}
