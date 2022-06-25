package com.bamboo.tool.view.component.actions;


import com.bamboo.tool.util.Function;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;




public class RefreshApiAction extends AnAction {
    private Function method;

    public RefreshApiAction(Function method) {
        super("Refresh", "Refresh", AllIcons.Actions.Refresh);
        this.method = method;

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        method.apply();
    }
}
