package com.bamboo.tool.view.component.actions;


import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.db.service.BambooService;
import com.bamboo.tool.view.component.dialog.RenameDescDialog;
import com.bamboo.tool.view.component.tree.ApiTree;
import com.bamboo.tool.view.component.tree.ClassNode;
import com.bamboo.tool.view.component.tree.MethodNode;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class RenameDescAction extends AnAction {

    private ApiTree apiTree;

    public RenameDescAction(ApiTree apiTree) {
        super("重命名");
        this.apiTree = apiTree;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Object lastPathComponent = apiTree.getSelectionPath().getLastPathComponent();
        RenameDescDialog dialog = new RenameDescDialog(apiTree);
        if (lastPathComponent instanceof MethodNode) {

            dialog.setTitle("重命名接口描述");
        }
        if (lastPathComponent instanceof ClassNode) {
            dialog.setTitle("重命名类描述");
        }

        dialog.setLocationRelativeTo(null);
        dialog.pack();
        dialog.setVisible(true);
    }

}
