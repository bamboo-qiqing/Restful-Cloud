package com.bamboo.tool.components.api.configurable;

import com.intellij.ide.actions.searcheverywhere.PersistentSearchEverywhereContributorFilter;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFiltersAction;
import com.intellij.ide.util.ElementsChooser;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ShortcutSet;
import com.intellij.openapi.util.NlsActions;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/**
 * Create by GuoQing
 * Date 2022/2/24 13:52
 * Description
 */
public class ApiSearchEverywhereFiltersAction  extends SearchEverywhereFiltersAction {
    public ApiSearchEverywhereFiltersAction(@NotNull PersistentSearchEverywhereContributorFilter filter, @NotNull Runnable rebuildRunnable) {
        super(filter, rebuildRunnable);
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return super.isSelected(e);
    }

    @Override
    public PersistentSearchEverywhereContributorFilter getFilter() {
        return super.getFilter();
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    protected boolean isActive() {
        return super.isActive();
    }

    @Override
    protected ElementsChooser<?> createChooser() {
        return super.createChooser();
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        super.setSelected(e, state);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
    }

    @Override
    public @NotNull
    @NonNls String getDimensionServiceKey() {
        return super.getDimensionServiceKey();
    }

    @Override
    public boolean displayTextInToolbar() {
        return super.displayTextInToolbar();
    }

    @Override
    public boolean useSmallerFontForTextInToolbar() {
        return super.useSmallerFontForTextInToolbar();
    }

    @Override
    public void beforeActionPerformedUpdate(@NotNull AnActionEvent e) {
        super.beforeActionPerformedUpdate(e);
    }

    @Override
    protected void setShortcutSet(@NotNull ShortcutSet shortcutSet) {
        super.setShortcutSet(shortcutSet);
    }

    @Override
    public void setDefaultIcon(boolean isDefaultIconSet) {
        super.setDefaultIcon(isDefaultIconSet);
    }

    @Override
    public boolean isDefaultIcon() {
        return super.isDefaultIcon();
    }

    @Override
    public void setInjectedContext(boolean worksInInjected) {
        super.setInjectedContext(worksInInjected);
    }

    @Override
    public boolean isInInjectedContext() {
        return super.isInInjectedContext();
    }




    @Override
    public void addTextOverride(@NotNull String place, @NotNull String text) {
        super.addTextOverride(place, text);
    }

    @Override
    public void addTextOverride(@NotNull String place, @NotNull Supplier<String> text) {
        super.addTextOverride(place, text);
    }

    @Override
    public void copyActionTextOverride(@NotNull String fromPlace, @NotNull String toPlace, String id) {
        super.copyActionTextOverride(fromPlace, toPlace, id);
    }

    @Override
    public void applyTextOverride(@NotNull AnActionEvent event) {
        super.applyTextOverride(event);
    }

    @Override
    public void applyTextOverride(@NotNull String place, @NotNull Presentation presentation) {
        super.applyTextOverride(place, presentation);
    }

    @Override
    protected void copyActionTextOverrides(AnAction targetAction) {
        super.copyActionTextOverrides(targetAction);
    }



    @Override
    public @NotNull List<Supplier<String>> getSynonyms() {
        return super.getSynonyms();
    }

    @Override
    public @Nls String toString() {
        return super.toString();
    }

    @Override
    public @Nullable
    @NlsActions.ActionText String getTemplateText() {
        return super.getTemplateText();
    }
}
