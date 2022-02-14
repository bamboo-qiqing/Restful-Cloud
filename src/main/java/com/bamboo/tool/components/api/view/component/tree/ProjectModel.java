package com.bamboo.tool.components.api.view.component.tree;

import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Create by GuoQing
 * Date 2022/2/14 13:11
 * Description
 */
public class ProjectModel extends BaseNode<String> {
    public ProjectModel(@NotNull String projectName) {
        super(projectName);
    }

    @Override
    public @Nullable
    Icon getIcon(boolean selected) {
        return AllIcons.Nodes.Project;
    }
}
