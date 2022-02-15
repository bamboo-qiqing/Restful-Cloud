package com.bamboo.tool.components.api.contributor;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.pom.Navigatable;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.searches.ReferencesSearch;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

/**
 * Create by GuoQing
 * Date 2022/2/15 11:15
 * Description
 */
@Data
public class RequestMappingNavigationItem implements NavigationItem {
    private Navigatable navigationElement;
    private PsiElement psiElement;
    private String urlPath;
    private String className;
    private String classPath;
    private String requestMethod;
    private String types;
    private String modelName;
    private String projectName;
    public RequestMappingNavigationItem() {

    }

    @Override
    public @Nullable
    @NlsSafe String getName() {
        return this.requestMethod + " " + this.urlPath;
    }

    @Override
    public @Nullable
    ItemPresentation getPresentation() {

        return new RequestMappingItemPresentation(this);
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (navigationElement != null) {

            navigationElement.navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return this.navigationElement != null;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }


}
