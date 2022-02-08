package com.bamboo.tool.components.api.view.component.tree;

import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ClassNode extends BaseNode<String> {
    public ClassNode(String className) {
        super(className);
    }

    @Override
    public @Nullable
    Icon getIcon(boolean selected) {
        return AllIcons.Nodes.Class;
    }
}
