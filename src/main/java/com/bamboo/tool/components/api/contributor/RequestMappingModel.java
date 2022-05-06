package com.bamboo.tool.components.api.contributor;

import com.bamboo.tool.components.api.enums.RequestMethod;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
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
public class RequestMappingModel extends FilteringGotoByModel<RequestMethod> {

    protected RequestMappingModel(@NotNull Project project, @NotNull List<ChooseByNameContributor> contributors) {
        super(project, contributors);
    }

    @Override
    protected @Nullable
    RequestMethod filterValueFor(NavigationItem navigationItem) {

        return null;
    }



    @Override
    protected boolean acceptItem(NavigationItem item) {
         BambooApiMethod api = (BambooApiMethod) item;
        final Collection<RequestMethod> filterItems = this.getFilterItems();
        long count = filterItems.parallelStream().filter(e -> api.getRequestMethods().contains(e.getCode())).count();
        return count>0;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence)
    String getPromptText() {
        return "Enter mapping url";
    }

    @Override
    public @NotNull
     String getNotInMessage() {
        return "Mapping not found";
    }

    @Override
    public @NotNull
     String getNotFoundMessage() {
        return null;
    }

    @Override
    public @Nullable String getCheckBoxName() {
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
    protected synchronized @Nullable
    Collection<RequestMethod> getFilterItems() {
        return super.getFilterItems();
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
        return true;
    }

    @Override
    public @NotNull
    ChooseByNameItemProvider getItemProvider(@Nullable PsiElement context) {
        return new RequestMappingItemProvider(this);
    }

}
