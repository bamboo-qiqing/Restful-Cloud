package com.bamboo.tool.view.component.actions;

import com.bamboo.tool.configurable.BambooApiFilterConfiguration;
import com.bamboo.tool.enums.RequestMethod;
import com.intellij.ide.actions.searcheverywhere.PersistentSearchEverywhereContributorFilter;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFiltersAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SearchApiFiltersAction extends SearchEverywhereFiltersAction {


    public SearchApiFiltersAction(@NotNull Project  project, @NotNull Runnable rebuildRunnable) {
        super(new PersistentSearchEverywhereContributorFilter(Arrays.asList(RequestMethod.values()), BambooApiFilterConfiguration.getInstance(project), (a) -> a.toString(), (e) -> null), rebuildRunnable);

    }
}
