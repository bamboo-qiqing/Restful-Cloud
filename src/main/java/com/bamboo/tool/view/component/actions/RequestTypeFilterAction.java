package com.bamboo.tool.view.component.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class RequestTypeFilterAction extends AnAction {

    public RequestTypeFilterAction() {
        super("Filter", "Filter", AllIcons.General.Filter);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }


}
