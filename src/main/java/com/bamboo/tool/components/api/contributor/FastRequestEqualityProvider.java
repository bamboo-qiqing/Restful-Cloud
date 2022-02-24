package com.bamboo.tool.components.api.contributor;

import com.intellij.ide.actions.searcheverywhere.AbstractEqualityProvider;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import org.jetbrains.annotations.NotNull;

/**
 * Create by GuoQing
 * Date 2022/2/15 11:07
 * Description
 */
public class FastRequestEqualityProvider extends AbstractEqualityProvider {
    @Override
    protected boolean areEqual(@NotNull SearchEverywhereFoundElementInfo newItem, @NotNull SearchEverywhereFoundElementInfo alreadyFoundItem) {
        Object newItemElement = newItem.getElement();
        Object alreadyFoundItemElement = alreadyFoundItem.getElement();
        String newItemElementString = getString(newItemElement);
        String alreadyFoundItemElementString = getString(alreadyFoundItemElement);
        return newItemElementString != null && newItemElementString.equals(alreadyFoundItemElementString);
//        return Objects.equals(newItem,alreadyFoundItem);
    }

    private static String getString(Object item) {

        if (item == null) return null;

        if (item instanceof RequestMappingNavigationItem) {
            return ApplicationManager.getApplication().runReadAction((Computable<String>) item::toString);
        }
        return null;
    }
}
