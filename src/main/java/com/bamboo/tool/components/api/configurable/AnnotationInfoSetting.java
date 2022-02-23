package com.bamboo.tool.components.api.configurable;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Create by GuoQing
 * Date 2022/2/23 10:59
 * Description
 */
public class AnnotationInfoSetting implements Configurable {
    private JPanel infoSetting;
    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return null;
    }

    @Override
    public @Nullable JComponent createComponent() {
       return infoSetting;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }
}
