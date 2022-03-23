package com.bamboo.tool.components.api.configurable;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Create by GuoQing
 * Date 2022/3/23 20:26
 * Description
 */
public class RestfulCloudConfigurable implements Configurable {
    private JTable frameworkTable;
    private JPanel mainPanel;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Restful Cloud";
    }


    @Override
    public @Nullable
    JComponent createComponent() {
        initTable();
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }

    private void initTable() {
        final DefaultTableModel defaultTableModel = new DefaultTableModel();
        defaultTableModel.addColumn("framework");
        defaultTableModel.addColumn("description");
        frameworkTable.setModel(defaultTableModel);
    }
}
