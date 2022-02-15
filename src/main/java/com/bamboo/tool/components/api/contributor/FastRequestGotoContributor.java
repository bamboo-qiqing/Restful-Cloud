package com.bamboo.tool.components.api.contributor;

import com.intellij.ide.actions.searcheverywhere.AbstractGotoSEContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        requestMappingModel = new RequestMappingModel(myProject, ExtensionPointName.<ChooseByNameContributor>create("com.bamboo.bamboo-tool.requestMappingContributor").getExtensionList());
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
