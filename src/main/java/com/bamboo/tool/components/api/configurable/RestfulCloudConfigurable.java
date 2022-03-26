package com.bamboo.tool.components.api.configurable;

import com.bamboo.tool.components.api.ui.component.DescFrameWorkTableModel;
import com.bamboo.tool.db.service.BambooService;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class RestfulCloudConfigurable implements Configurable {
    private JPanel mainPanel;
    private JPanel initDataSettingPanel;
    private JTextField initDataUrlField;
    private JLabel initDataUrlLable;
    private JPanel incPanel;
    private JPanel descriptionPanel;
    private JCheckBox descriptionBox;
    private JPanel descFrameWorkPanel;

    private JLabel descFrameWorkLable;
    private @NotNull JPanel descFrameWorkPane;
    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return null;
    }

    @Override
    public @Nullable
    JComponent createComponent() {
        initDataUrlLable.setText("初始化数据地址");
        initDataUrlField.setText(BambooService.initDataUrl);
        initDataSettingPanel.setBorder(IdeBorderFactory.createTitledBorder("初始化数据配置", true));
        incPanel.setBorder(IdeBorderFactory.createTitledBorder("接口描述配置", true));
        descriptionBox.setText("是否开启接口描述");
        descriptionBox.setSelected(true);
        descFrameWorkPanel.setBorder(IdeBorderFactory.createTitledBorder("接口描述框架设置", true));
        descFrameWorkLable.setText("该配置用于设置左侧工具栏显示接口，启用接口描述后支持的框架优先级配置");
        JBTable  descFrameWorkTable = new JBTable();
        DescFrameWorkTableModel defaultTableModel = new DescFrameWorkTableModel();
        defaultTableModel.addColumn("框架");
        String[] one = {"javadoc"};
        String[] two = {"odianyun"};
        String[] tw1 = {"odianyun"};
        String[] tw2 = {"odianyun"};
        defaultTableModel.addRow(one);
        defaultTableModel.addRow(two);
        defaultTableModel.addRow(tw1);
        defaultTableModel.addRow(tw2);
        defaultTableModel.addRow(tw2);
        defaultTableModel.addRow(tw2);
        descFrameWorkTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        descFrameWorkTable.setModel(defaultTableModel);
        descFrameWorkTable.setBorder(null);
        final ToolbarDecorator decorator = ToolbarDecorator.createDecorator(descFrameWorkTable);
        decorator.disableAddAction();
        decorator.disableRemoveAction();

        descFrameWorkPane.add(decorator.createPanel(), BorderLayout.CENTER);

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
