package com.bamboo.tool.components.api.view.component.tree;

import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ProjectNode extends BaseNode<String> {
    public ProjectNode(String projectName) {
        super(projectName);
    }

    @Override
    public @Nullable
    Icon getIcon(boolean selected) {
        return AllIcons.Nodes.Module;
    }
}
