package com.bamboo.tool.components.api.view.component.actions;


import com.bamboo.tool.components.api.view.component.dialog.RenameDescDialog;
import com.bamboo.tool.components.api.view.component.tree.ApiTree;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class RenameDescAction extends AnAction {

    private ApiTree apiTree;

    public RenameDescAction(ApiTree apiTree) {
        super("Rename");
        this.apiTree=apiTree;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        RenameDescDialog dialog = new RenameDescDialog(apiTree);
        dialog.setTitle("重命名类描述");
        dialog.setLocationRelativeTo(null);
        dialog.pack();
        dialog.setVisible(true);
    }

}
