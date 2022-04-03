package com.bamboo.tool.components.api.view.component.actions;

import com.bamboo.tool.components.api.contributor.BambooApiGotoContributor;
import com.intellij.ide.actions.SearchEverywhereBaseAction;
import com.intellij.ide.actions.searcheverywhere.SymbolSearchEverywhereContributor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

public class RestfulCloudSearchAction extends SearchEverywhereBaseAction implements DumbAware {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        boolean dumb = DumbService.isDumb(project);
        if (!dumb || new SymbolSearchEverywhereContributor(e).isDumbAware()) {
            String tabID = Registry.is("search.everywhere.group.contributors.by.type")
                    ? "SearchEverywhere.Project"
                    : BambooApiGotoContributor.class.getSimpleName();
            showInSearchEverywherePopup(tabID, e, true, true);
        }
    }
}
