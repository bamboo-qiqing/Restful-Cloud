package com.bamboo.tool.components.api.contributor;

import com.bamboo.tool.components.api.configurable.BambooApiFilterConfiguration;
import com.bamboo.tool.components.api.enums.RequestMethod;
import com.intellij.ide.actions.searcheverywhere.*;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/2/15 9:53
 * Description
 */
public class BambooApiGotoContributor extends AbstractGotoSEContributor {

    private final Project myProject;

    private final PersistentSearchEverywhereContributorFilter<RequestMethod> myFilter;

    protected BambooApiGotoContributor(@NotNull AnActionEvent event) {
        super(event);
        myProject = event.getProject();
        BambooApiFilterConfiguration instance = BambooApiFilterConfiguration.getInstance(myProject);
        myFilter = new PersistentSearchEverywhereContributorFilter(Arrays.asList(RequestMethod.values()), instance, (a) -> a.toString(), (e) -> null);
    }

    @Override
    protected @NotNull
    FilteringGotoByModel<RequestMethod> createModel(@NotNull Project project) {
        RequestMappingModel requestMappingModel = new RequestMappingModel(myProject, ExtensionPointName.<ChooseByNameContributor>create("com.bamboo.requestMappingContributor").getExtensionList());
        requestMappingModel.setFilterItems(myFilter.getSelectedElements());
        return requestMappingModel;
    }

    @Override
    public @NotNull
    @Nls
    String getGroupName() {
        return "Restful Cloud";
    }

    @Override
    public int getSortWeight() {
        return 1000;
    }

    @Override
    public @NotNull
    List<AnAction> getActions(@NotNull Runnable onChanged) {
        BambooApiFilterConfiguration instance = BambooApiFilterConfiguration.getInstance(myProject);
        List<AnAction> actions = new ArrayList<>();
        actions.add(new SearchEverywhereFiltersAction(new PersistentSearchEverywhereContributorFilter(Arrays.asList(RequestMethod.values()), instance, (a) -> a.toString(), (e) -> null), onChanged));
        return actions;
    }


    @Override
    public @Nullable
    @Nls
    String getAdvertisement() {
        return "alt+q";
    }

    static class Factory implements SearchEverywhereContributorFactory<Object> {

        @Override
        public @NotNull
        SearchEverywhereContributor<Object> createContributor(@NotNull AnActionEvent initEvent) {
            return new BambooApiGotoContributor(initEvent);
        }


    }
}
