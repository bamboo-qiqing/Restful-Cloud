package com.bamboo.tool.configurable;

import com.bamboo.tool.db.service.BambooService;
import com.bamboo.tool.entity.DescFramework;
import com.bamboo.tool.view.component.table.DescFrameWorkTableModel;
import com.bamboo.tool.view.component.table.FrameWorkTableModel;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CustomFrameworkConfigurable implements Configurable {
    private JPanel mainJPanel;
    private JPanel frameworkTableJPanel;
    private FrameWorkTableModel defaultTableModel;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "自定义框架配置";
    }

    @Override
    public @Nullable
    JComponent createComponent() {

        defaultTableModel = new FrameWorkTableModel(new ArrayList());
        defaultTableModel.addColumn("框架");
        defaultTableModel.addColumn("描述");
        JBTable frameWorkTable = new JBTable();

        BambooService.selectAllDescFramework().forEach(descFramework -> {
            defaultTableModel.addRow(descFramework.getStrings());
        });

//
//        for (DescFramework descFramework : descFrameworks) {
//            defaultTableModel.addRow(descFramework.getStrings());
//        }
        frameWorkTable.setModel(defaultTableModel);
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(frameWorkTable);
        frameworkTableJPanel.add(decorator.createPanel(), BorderLayout.CENTER);
        frameWorkTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        frameWorkTable.setBorder(null);
        return mainJPanel;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }
}
