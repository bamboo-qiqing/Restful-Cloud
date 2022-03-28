package com.bamboo.tool.components.api.configurable;

import com.bamboo.tool.components.api.entity.DescFramework;
import com.bamboo.tool.components.api.ui.component.DescFrameWorkTableModel;
import com.bamboo.tool.config.BambooToolComponent;
import com.bamboo.tool.config.model.BambooToolConfig;
import com.bamboo.tool.db.service.BambooService;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

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
    private JBTable descFrameWorkTable;
    private DescFrameWorkTableModel defaultTableModel;
    private @NotNull
    JPanel descFrameWorkPane;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "RestfulCloud";
    }

    @Override
    public @Nullable
    JComponent createComponent() {
        final BambooToolConfig state = BambooToolComponent.getInstance().getState();
        initDataUrlLable.setText("初始化数据地址");
        initDataUrlField.setText(BambooService.initDataUrl);
        initDataSettingPanel.setBorder(IdeBorderFactory.createTitledBorder("初始化数据配置", true));
        incPanel.setBorder(IdeBorderFactory.createTitledBorder("接口描述配置", true));
        descriptionBox.setText("是否开启接口描述");
        descriptionBox.setSelected(state.getIsShowDesc());
        descFrameWorkPanel.setBorder(IdeBorderFactory.createTitledBorder("接口描述框架设置", true));
        descFrameWorkLable.setText("该配置用于设置左侧工具栏显示接口，启用接口描述后支持的框架优先级配置");
        descFrameWorkTable = new JBTable();
        defaultTableModel = new DescFrameWorkTableModel();
        defaultTableModel.addColumn("框架");
        final List<DescFramework> descFrameworks = BambooService.selectAllDescFramework();
        for (DescFramework descFramework : descFrameworks) {
            defaultTableModel.addRow(descFramework.getStrings());
        }
        descFrameWorkTable.setModel(defaultTableModel);
        final ToolbarDecorator decorator = ToolbarDecorator.createDecorator(descFrameWorkTable);
        decorator.disableAddAction();
        decorator.disableRemoveAction();
        descFrameWorkPane.add(decorator.createPanel(), BorderLayout.CENTER);
        descFrameWorkTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        descFrameWorkTable.setBorder(null);
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        if (defaultTableModel.getModified()) {
            return true;
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }
}
