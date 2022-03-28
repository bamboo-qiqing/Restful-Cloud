package com.bamboo.tool.components.api.view.component.tree;

import com.intellij.icons.AllIcons;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Map;

@Getter
public class ClassNode extends DefaultMutableTreeNode {
    private String className;
    private Boolean isShowDesc;
    private Map<String,String> descMap;

    public ClassNode(String className, Boolean isShowDesc, Map<String,String> descMap) {
        this.className=className;
        this.isShowDesc=isShowDesc;
        this.descMap=descMap;
    }


    public @Nullable
    Icon getIcon(boolean selected) {
        return AllIcons.Nodes.Class;
    }

    @Override
    public String toString() {
        return isShowDesc?;
    }
}
