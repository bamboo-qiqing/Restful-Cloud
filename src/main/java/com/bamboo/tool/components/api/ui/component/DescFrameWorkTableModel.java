package com.bamboo.tool.components.api.ui.component;

import com.intellij.util.ui.EditableModel;

import javax.swing.table.DefaultTableModel;

public class DescFrameWorkTableModel extends DefaultTableModel implements EditableModel {

    private Boolean isModified = false;

    @Override
    public void addRow() {

    }

    @Override
    public void exchangeRows(int oldIndex, int newIndex) {
        super.moveRow(oldIndex, oldIndex, newIndex);
        this.isModified = true;
    }

    @Override
    public boolean canExchangeRows(int oldIndex, int newIndex) {
        return true;
    }

    public Boolean getModified() {
        return isModified;
    }

    public void setModified(Boolean modified) {
        isModified = modified;
    }


    public void removeAll() {
        final int rowCount = super.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            super.removeRow(i);
        }
    }

}
