package com.bamboo.tool.view.component.actions;


import com.intellij.ide.actions.searcheverywhere.PersistentSearchEverywhereContributorFilter;
import com.intellij.ide.util.ElementsChooser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SearchEverywhereFiltersAction<T> extends ShowFilterAction {
     PersistentSearchEverywhereContributorFilter<T> filter;
     Runnable rebuildRunnable;

    public SearchEverywhereFiltersAction(PersistentSearchEverywhereContributorFilter<T> filter,
                                          Runnable rebuildRunnable, String text, String desc, Icon icon) {
        super(text,desc,icon);
        this.filter = filter;
        this.rebuildRunnable = rebuildRunnable;

    }

    public PersistentSearchEverywhereContributorFilter<T> getFilter() {
        return filter;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    protected boolean isActive() {
        return filter.getAllElements().size() != filter.getSelectedElements().size();
    }

    @Override
    protected ElementsChooser<?> createChooser() {
        return createChooser(filter, rebuildRunnable);
    }

    private static <T> ElementsChooser<T> createChooser(@NotNull PersistentSearchEverywhereContributorFilter<T> filter, @NotNull Runnable rebuildRunnable) {
        ElementsChooser<T> res = new ElementsChooser<>(filter.getAllElements(), false) {
            @Override
            protected String getItemText(@NotNull T value) {
                return filter.getElementText(value);
            }

            @Nullable
            @Override
            protected Icon getItemIcon(@NotNull T value) {
                return filter.getElementIcon(value);
            }
        };
        res.markElements(filter.getSelectedElements());
        ElementsChooser.ElementsMarkListener<T> listener = (element, isMarked) -> {
            filter.setSelected(element, isMarked);
            rebuildRunnable.run();
        };
        res.addElementsMarkListener(listener);

        return res;
    }
}
