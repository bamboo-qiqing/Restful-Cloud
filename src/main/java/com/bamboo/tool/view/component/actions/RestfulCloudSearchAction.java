package com.bamboo.tool.view.component.actions;

import com.bamboo.tool.contributor.BambooApiGotoContributor;
import com.intellij.icons.AllIcons;
import com.intellij.ide.IdeEventQueue;
import com.intellij.ide.actions.SearchEverywhereBaseAction;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.SymbolSearchEverywhereContributor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

public class RestfulCloudSearchAction extends AnAction implements DumbAware {


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        String tabID = Registry.is("search.everywhere.group.contributors.by.type")
                ? "SearchEverywhere.Project"
                : BambooApiGotoContributor.class.getSimpleName();
        SearchEverywhereManager seManager = SearchEverywhereManager.getInstance(project);
        IdeEventQueue.getInstance().getPopupManager().closeAllPopups(false);

        seManager.show(tabID, "", e);
    }
}
