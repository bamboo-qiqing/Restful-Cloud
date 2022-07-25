package com.bamboo.tool.configurable;

import com.bamboo.tool.entity.DescFramework;
import com.bamboo.tool.util.I18nUtil;
import com.bamboo.tool.view.component.table.DescFrameWorkTableModel;
import com.bamboo.tool.db.service.BambooService;
import com.intellij.openapi.options.Configurable;
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
    private DescFrameWorkTableModel defaultTableModel;
    private @NotNull
    JPanel descFrameWorkPane;
    List<DescFramework> descFrameworks;

    @Override
    public  String getDisplayName() {
        return "RestfulCloud";
    }

    @Override
    public @Nullable
    JComponent createComponent() {
//        boolean isShowDesc = BambooService.selectIsShowDesc();
        initDataUrlLable.setText(I18nUtil.getString("setting.init.dataAddress"));
        initDataUrlField.setText(BambooService.initDataUrl);
        initDataSettingPanel.setBorder(IdeBorderFactory.createTitledBorder(I18nUtil.getString("setting.init.dataConfiguration"), true));
        incPanel.setBorder(IdeBorderFactory.createTitledBorder(I18nUtil.getString("setting.interfaceDescConfig"), true));
        descriptionBox.setText(I18nUtil.getString("setting.isOpenInterfaceDesc"));
//        descriptionBox.setSelected(isShowDesc);
        descFrameWorkPanel.setBorder(IdeBorderFactory.createTitledBorder(I18nUtil.getString("setting.interfaceDescFrameworkSettings"), true));
        descFrameWorkLable.setText(I18nUtil.getString("setting.descFrameWorkLable"));
        JBTable descFrameWorkTable = new JBTable();

        descFrameworks = BambooService.selectAllDescFramework();
        defaultTableModel = new DescFrameWorkTableModel(descFrameworks);
        defaultTableModel.addColumn(I18nUtil.getString("setting.defaultTableModel.framework"));

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
        if (defaultTableModel.isModified()) {
            return true;
        }
        boolean isShowDesc = BambooService.selectIsShowDesc();
        boolean selected = descriptionBox.isSelected();
        if (isShowDesc != selected) {
            return true;
        }

        return false;
    }

    @Override
    public void apply()  {
        BambooService.updateDescFrameworkSequence(descFrameworks);
        BambooService.updateIsShowDesc(descriptionBox.isSelected());
        defaultTableModel.setDataList( BambooService.selectAllDescFramework());

    }
}
