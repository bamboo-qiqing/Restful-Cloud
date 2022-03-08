package com.bamboo.tool.components.api.view.action;

import com.bamboo.tool.components.api.contributor.BambooApiGotoContributor;
import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.SearchEverywhereBaseAction;
import com.intellij.ide.actions.searcheverywhere.SymbolSearchEverywhereContributor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ShortcutSet;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

/**
 * Create by GuoQing
 * Date 2022/2/14 17:57
 * Description
 */
public class GotoRequestAction extends SearchEverywhereBaseAction implements DumbAware {

    public GotoRequestAction() {
        Presentation templatePresentation = getTemplatePresentation();
        templatePresentation.setIcon(AllIcons.Actions.Find);
        templatePresentation.setText("搜索 Api");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project == null) return;

        boolean dumb = DumbService.isDumb(project);
        if (!dumb || new SymbolSearchEverywhereContributor(anActionEvent).isDumbAware()) {
            String tabID = Registry.is("search.everywhere.group.contributors.by.type")
                    ? "SearchEverywhere.Project"
                    : BambooApiGotoContributor.class.getSimpleName();
            showInSearchEverywherePopup(tabID, anActionEvent, true, true);
        }
    }

    @Override
    protected void setShortcutSet(@NotNull ShortcutSet shortcutSet) {
        super.setShortcutSet(shortcutSet);
    }
}
