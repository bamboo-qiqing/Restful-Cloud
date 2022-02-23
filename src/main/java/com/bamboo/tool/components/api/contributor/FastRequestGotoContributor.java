package com.bamboo.tool.components.api.contributor;

import com.intellij.ide.actions.searcheverywhere.AbstractGotoSEContributor;
import com.intellij.ide.actions.searcheverywhere.ContributorSearchResult;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/2/15 9:53
 * Description
 */
public class FastRequestGotoContributor extends AbstractGotoSEContributor {

    private Project myProject;
    private RequestMappingModel requestMappingModel;

    protected FastRequestGotoContributor(@NotNull AnActionEvent event) {
        super(event);
        myProject = event.getProject();
        requestMappingModel = new RequestMappingModel(myProject, ExtensionPointName.<ChooseByNameContributor>create("com.bamboo.bamboo-tool.requestMnappingContributor").getExtensionList());
    }

    @Override
    public @NotNull
    @Nls String getFullGroupName() {
        return super.getFullGroupName();
    }

    @Override
    public @NotNull List<AnAction> getActions(@NotNull Runnable onChanged) {
        return super.getActions(onChanged);
    }

    @Override
    public @NotNull ContributorSearchResult<Object> search(@NotNull String pattern, @NotNull ProgressIndicator progressIndicator, int elementsLimit) {
        return super.search(pattern, progressIndicator, elementsLimit);
    }

    @Override
    public @NotNull List<Object> search(@NotNull String pattern, @NotNull ProgressIndicator progressIndicator) {
        return super.search(pattern, progressIndicator);
    }

    @Override
    public boolean isEmptyPatternSupported() {
        return super.isEmptyPatternSupported();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    protected @NotNull
    FilteringGotoByModel<?> createModel(@NotNull Project project) {
        return requestMappingModel;
    }

    @Override
    public @NotNull
    @Nls
    String getGroupName() {
        return "Bamboo Apis";
    }

    @Override
    public int getSortWeight() {
        return 1000;
    }

    @Override
    public @Nullable
    @Nls
    String getAdvertisement() {
        return "url to search";
    }

    static class Factory implements SearchEverywhereContributorFactory<Object> {

        @Override
        public @NotNull
        SearchEverywhereContributor<Object> createContributor(@NotNull AnActionEvent initEvent) {
            return new FastRequestGotoContributor(initEvent);
        }


    }
}
