package com.bamboo.tool.components.api.ui.component;

import com.intellij.util.ui.EditableModel;

import javax.swing.table.DefaultTableModel;

public class DescFrameWorkTableModel extends DefaultTableModel implements EditableModel {
    @Override
    public void addRow() {

    }

    @Override
    public void exchangeRows(int oldIndex, int newIndex) {
    }

    @Override
    public boolean canExchangeRows(int oldIndex, int newIndex) {
        return true;
    }
}
