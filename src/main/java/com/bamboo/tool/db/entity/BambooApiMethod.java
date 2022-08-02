package com.bamboo.tool.db.entity;

import com.bamboo.tool.contributor.RequestMappingItemPresentation;
import com.bamboo.tool.db.service.BambooService;
import com.bamboo.tool.entity.DescFramework;
import com.bamboo.tool.entity.MethodParam;
import com.bamboo.tool.enums.SoaType;
import com.bamboo.tool.util.PsiUtils;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by GuoQing
 * Date 2022/2/14 11:50
 * Description
 */
@Data
public class BambooApiMethod implements NavigationItem {
    private Navigatable navigationElement;
    private String url;
    private List<String> requestMethods;
    private String methodName;
    private String methodReturn;
    private String className;
    private String classPath;
    private String classId;
    private Map<String, String> classDescHashMap = new HashMap<>();
    private Map<String, String> methodDescHashMap = new HashMap<>();
    private String modelName;
    private String projectName;
    private SoaType soaType;
    private String frameworkName;
    private String projectPath;
    private List<MethodParam> methodParams ;
    private Integer queryCount;
    private String methodId;
    private Project project;
    private Boolean IsShowDesc;
    private List<DescFramework> descFrameworks;

    @Override
    public @Nullable  String getName() {
        return this.requestMethods + " " + this.url;
    }

    @Override
    public @Nullable
    ItemPresentation getPresentation() {
        return new RequestMappingItemPresentation(this);
    }

    @Override
    public void navigate(boolean requestFocus) {
        VirtualFile file = LocalFileSystem.getInstance().refreshAndFindFileByPath(classPath);

        ApplicationManager.getApplication().runReadAction(() -> {
            if (file != null && file.isValid()) {
                PsiUtils.openFile(file, project, methodName, methodReturn,methodParams);
            }
            if (navigationElement != null) {
                navigationElement.navigate(requestFocus);
            }
        });
    }

    @Override
    public boolean canNavigate() {
        return this.navigationElement != null;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }

}
