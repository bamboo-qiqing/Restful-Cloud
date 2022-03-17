package com.bamboo.tool.components.api.configurable;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Create by GuoQing
 * Date 2022/2/16 12:36
 * Description
 */
public class BambooToolConfigurable implements Configurable, Configurable.Composite {
    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Bamboo Tool";
    }

    @Override
    public @Nullable
    JComponent createComponent() {
        return null;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }

    @Override
    public Configurable[] getConfigurables() {


        return new Configurable[0];
    }
}
