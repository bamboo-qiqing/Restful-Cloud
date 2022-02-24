package com.bamboo.tool.components.api.contributor;

import com.bamboo.tool.components.api.view.action.RequestMappingItemProvider;
import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/2/15 10:38
 * Description
 */
public class RequestMappingModel extends FilteringGotoByModel<FileType> {

    protected RequestMappingModel(@NotNull Project project, @NotNull List<ChooseByNameContributor> contributors) {
        super(project, contributors);
    }

    @Override
    protected @Nullable
    FileType filterValueFor(NavigationItem navigationItem) {

        return null;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence)
    String getPromptText() {
        return "Enter mapping url";
    }

    @Override
    public @NotNull
    @NlsContexts.Label String getNotInMessage() {
        return "Mapping not found";
    }

    @Override
    public @NotNull
    @NlsContexts.Label String getNotFoundMessage() {
        return null;
    }

    @Override
    public @Nullable
    @NlsContexts.Label String getCheckBoxName() {
        return null;
    }

    @Override
    public boolean loadInitialCheckBoxState() {
        return false;
    }

    @Override
    public void saveInitialCheckBoxState(boolean b) {

    }

    @Override
    public String[] getSeparators() {
        return new String[0];
    }

    @Override
    public @Nullable
    String getFullName(@NotNull Object element) {
        return getElementName(element);
    }

    @Override
    public boolean willOpenEditor() {
        return false;
    }

    @Override
    protected synchronized @Nullable
    Collection<FileType> getFilterItems() {
        return super.getFilterItems();
    }
    @Override
    public @NotNull
    ChooseByNameItemProvider getItemProvider(@Nullable PsiElement context) {
        return new RequestMappingItemProvider(this);
    }

}
