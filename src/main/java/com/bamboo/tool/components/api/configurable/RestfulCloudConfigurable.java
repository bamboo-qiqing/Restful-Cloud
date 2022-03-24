package com.bamboo.tool.components.api.configurable;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Create by GuoQing
 * Date 2022/3/23 20:26
 * Description
 */
public class RestfulCloudConfigurable implements Configurable {

    private JPanel mainPanel;
    private JTextField textField1;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Restful Cloud";
    }


    @Override
    public @Nullable
    JComponent createComponent() {
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
    }
}
