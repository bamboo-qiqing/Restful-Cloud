package com.bamboo.tool.configurable;

import com.bamboo.tool.db.service.BambooService;
import com.bamboo.tool.entity.AnnotationInfoSetting;
import com.bamboo.tool.entity.AnnotationParam;
import com.bamboo.tool.entity.Framework;
import com.bamboo.tool.enums.AnnotationScope;
import com.bamboo.tool.enums.AttributeEnums;
import com.bamboo.tool.enums.SoaType;
import com.bamboo.tool.view.component.table.AbstractTableModel;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.ComboBoxCellEditor;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomFrameworkConfigurable implements Configurable {
    private JPanel mainJPanel;
    private JPanel frameworkTableJPanel;

    private JComboBox frameworksBox;
    private JComboBox annotationComboBox;

    private JPanel annotationTablePanel;
    private JPanel annotationParamJPanel;

    private AbstractTableModel defaultTableModel;
    private AbstractTableModel annotationTableModel;
    private AbstractTableModel annotationParamsTableModel;
    List<Framework> frameworks;
    List<AnnotationInfoSetting> annotationInfoSettings;
    List<AnnotationParam> annotationParams;
    List<String> scopeAll;
    List<String> soaTypeAll;
    List<String> attributeAll;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "自定义框架配置";
    }

    @Override
    public @Nullable
    JComponent createComponent() {
        scopeAll = Arrays.stream(AnnotationScope.values()).map(e -> e.getDesc()).collect(Collectors.toList());
        soaTypeAll = Arrays.stream(SoaType.values()).map(e -> e.getDesc()).collect(Collectors.toList());
        attributeAll = Arrays.stream(AttributeEnums.values()).map(e -> e.getDesc()).collect(Collectors.toList());

        frameworks = BambooService.selectAllFramework();
        defaultTableModel = new AbstractTableModel(frameworks, new Object[]{"框架","描述"});
        defaultTableModel.addColumn("框架");
        defaultTableModel.addColumn("描述");
        JBTable frameWorkTable = new JBTable();
        frameworks.forEach(descFramework -> {
            defaultTableModel.addRow(descFramework.getStrings());
            frameworksBox.addItem(descFramework);
        });

        frameWorkTable.setModel(defaultTableModel);
        frameWorkTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(frameWorkTable);
        frameworkTableJPanel.setPreferredSize(new Dimension(-1, 260));
        frameworkTableJPanel.add(decorator.createPanel(), BorderLayout.CENTER);
        frameworksBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Framework selectedItem = (Framework) frameworksBox.getSelectedItem();
                List<AnnotationInfoSetting> annotationInfoSettings = BambooService.selectAllAnnotationInfoSetting(selectedItem.getId().toString());
                this.annotationInfoSettings = annotationInfoSettings;
                annotationTableModel.removeAll();
                annotationComboBox.removeAllItems();
                annotationInfoSettings.forEach(annotationInfoSetting -> {
                    annotationTableModel.addRow(annotationInfoSetting.getStrings());
                    String effect = annotationInfoSetting.getEffect();
                    if(effect.contains("attribute")){
                        annotationComboBox.addItem(annotationInfoSetting);
                    }

                });
            }
        });


        Framework selectedItem = (Framework) frameworksBox.getSelectedItem();
        annotationInfoSettings = BambooService.selectAllAnnotationInfoSetting(selectedItem.getId().toString());
        JBTable annotationsTable = new JBTable();
        annotationTableModel = new AbstractTableModel(annotationInfoSettings,null);
        annotationTableModel.addColumn("全路径类名");
        annotationTableModel.addColumn("注解生效范围");
        annotationTableModel.addColumn("SOA类型");
        annotationTableModel.addColumn("是否扫描");
        annotationTableModel.addColumn("获取属性");

        annotationInfoSettings.forEach(annotationInfoSetting -> {
            annotationTableModel.addRow(annotationInfoSetting.getStrings());
            String effect = annotationInfoSetting.getEffect();
            if(effect.contains("attribute")){
                annotationComboBox.addItem(annotationInfoSetting);
            }

        });
        annotationsTable.setModel(annotationTableModel);
        TableColumn typeTableColumn = annotationsTable.getColumn("注解生效范围");
        TableColumn soaTypeTableColumn = annotationsTable.getColumn("SOA类型");
        TableColumn isScanTableColumn = annotationsTable.getColumn("是否扫描");
        TableColumn getPropertyTableColumn = annotationsTable.getColumn("获取属性");
        typeTableColumn.setCellEditor(new ComboBoxCellEditor() {
            @Override
            protected List<String> getComboBoxItems() {
                return scopeAll;
            }
        });
        soaTypeTableColumn.setCellEditor(new ComboBoxCellEditor() {
            @Override
            protected List<String> getComboBoxItems() {
                return soaTypeAll;
            }
        });
        isScanTableColumn.setCellEditor(new ComboBoxCellEditor(){
            @Override
            protected List<String> getComboBoxItems() {
                return Arrays.asList("是","否");
            }
        });
        getPropertyTableColumn.setCellEditor(new ComboBoxCellEditor(){
            @Override
            protected List<String> getComboBoxItems() {
                return Arrays.asList("是","否");
            }
        });

        annotationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ToolbarDecorator annotationDecorator = ToolbarDecorator.createDecorator(annotationsTable);
        annotationTablePanel.setPreferredSize(new Dimension(-1, 260));
        annotationTablePanel.add(annotationDecorator.createPanel(), BorderLayout.CENTER);


        annotationComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                AnnotationInfoSetting selectedComboBox = (AnnotationInfoSetting) annotationComboBox.getSelectedItem();
                annotationParams = BambooService.selectAllAnnotationParam(selectedComboBox.getId());
                annotationParamsTableModel.removeAll();
                annotationParams.forEach(annotationParam -> {
                    annotationParamsTableModel.addRow(annotationParam.getStrings());
                });
            }

        });

        AnnotationInfoSetting annotationSelectedItem = (AnnotationInfoSetting) annotationComboBox.getSelectedItem();
        annotationParams = BambooService.selectAllAnnotationParam(annotationSelectedItem.getId());
        JBTable annotationParamsTable = new JBTable();
        annotationParamsTableModel = new AbstractTableModel(annotationParams,null);
        annotationParamsTableModel.addColumn("属性名");
        annotationParamsTableModel.addColumn("类型");
        annotationParamsTableModel.addColumn("描述");



        annotationParams.forEach(annotationParam -> {
            annotationParamsTableModel.addRow(annotationParam.getStrings());
        });
        annotationParamsTable.setModel(annotationParamsTableModel);
        TableColumn annotationTypeTableColumn = annotationParamsTable.getColumn("类型");
        annotationTypeTableColumn.setCellEditor(new ComboBoxCellEditor(){
            @Override
            protected List<String> getComboBoxItems() {
                return attributeAll;
            }
        });
        annotationParamsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ToolbarDecorator annotationParamsDecorator = ToolbarDecorator.createDecorator(annotationParamsTable);
        annotationParamJPanel.setPreferredSize(new Dimension(-1, 260));
        annotationParamJPanel.add(annotationParamsDecorator.createPanel(), BorderLayout.CENTER);


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
