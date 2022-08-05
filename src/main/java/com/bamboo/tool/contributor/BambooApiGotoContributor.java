package com.bamboo.tool.contributor;

import com.bamboo.tool.config.BambooApisComponent;
import com.bamboo.tool.config.BambooToolComponent;
import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.configurable.BambooApiFilterConfiguration;
import com.bamboo.tool.configurable.ProjectFileterConfiguration;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.enums.RequestMethod;
import com.bamboo.tool.view.component.actions.SearchEverywhereFiltersAction;
import com.bamboo.tool.view.component.renderer.SearchEverywherePsiRenderer;
import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.searcheverywhere.AbstractGotoSEContributor;
import com.intellij.ide.actions.searcheverywhere.PersistentSearchEverywhereContributorFilter;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.TextWithIcon;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/2/15 9:53
 * Description
 */
public class BambooApiGotoContributor extends AbstractGotoSEContributor {

    private Project myProject;

    private PersistentSearchEverywhereContributorFilter<RequestMethod> requestTypeFilter;
    private PersistentSearchEverywhereContributorFilter<ProjectInfo> projectFilter;


    protected BambooApiGotoContributor(@NotNull AnActionEvent event) {
        super(event);
        myProject = event.getProject();
        List<ProjectInfo> projectInfos = BambooApisComponent.getStoreService().getAllProjectInfos();
        BambooApiFilterConfiguration instance = BambooApiFilterConfiguration.getInstance(myProject);
        ProjectFileterConfiguration projectInstance = ProjectFileterConfiguration.getInstance(myProject);

        requestTypeFilter = new PersistentSearchEverywhereContributorFilter(List.of(RequestMethod.values()), instance, (a) -> a.toString(), (e) -> null);
        projectFilter = new PersistentSearchEverywhereContributorFilter(projectInfos, projectInstance, (a) -> {
            ProjectInfo projectInfo = (ProjectInfo) a;
            return projectInfo.getProjectName();
        }, (e) -> null);
    }

    @Override
    protected @NotNull
    FilteringGotoByModel<RequestMethod> createModel(@NotNull Project project) {
        RequestMappingModel requestMappingModel = new RequestMappingModel(myProject, ExtensionPointName.<ChooseByNameContributor>create("com.bamboo.requestMappingContributor").getExtensionList());
        requestMappingModel.setFilterItems(requestTypeFilter.getSelectedElements());
        requestMappingModel.setProjectInfos(projectFilter.getSelectedElements());
        return requestMappingModel;
    }

    @Override
    public @NotNull ListCellRenderer<Object> getElementsRenderer() {
        return new SearchEverywherePsiRenderer(this){

            protected DefaultListCellRenderer getRightCellRenderer(final Object value) {
                if(value instanceof BambooApiMethod){
                    BambooApiMethod item = (BambooApiMethod) value;

                    return new DefaultListCellRenderer(){
                        @Override
                        public Icon getIcon() {
                            return AllIcons.Nodes.Module;
                        }

                        @Override
                        public String getText() {
                            return item.getProjectName();
                        }
                    };
                }
                return super.getRightCellRenderer(value);
            }


            protected @Nullable TextWithIcon getItemLocation(Object value) {
                if(value instanceof BambooApiMethod){
                    BambooApiMethod item = (BambooApiMethod) value;

                    return new TextWithIcon(item.getProjectName(), AllIcons.Nodes.Module);
                }
                return super.getItemLocation(value);
            }
        };
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

        List<AnAction> actions = new ArrayList<>();
        actions.add(new SearchEverywhereFiltersAction(projectFilter, onChanged, "项目过滤器", "项目过滤器", AllIcons.Nodes.Module));
        actions.add(new SearchEverywhereFiltersAction(requestTypeFilter, onChanged, "请求类型过滤器", "请求类型过滤器", AllIcons.General.Filter));
        return actions;
    }


    @Override
    public @Nullable
    @Nls
    String getAdvertisement() {
        return "ctrl+alt+q";
    }

    static class Factory implements SearchEverywhereContributorFactory<Object> {

        @Override
        public @NotNull
        SearchEverywhereContributor<Object> createContributor(@NotNull AnActionEvent initEvent) {
            return new BambooApiGotoContributor(initEvent);
        }


    }
}
