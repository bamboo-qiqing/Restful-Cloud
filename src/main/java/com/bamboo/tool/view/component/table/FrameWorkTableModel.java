package com.bamboo.tool.view.component.table;

import com.bamboo.tool.entity.DescFramework;
import com.intellij.util.ui.EditableModel;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class FrameWorkTableModel <T> extends DefaultTableModel implements EditableModel {
    private List<T> frameworks;

    public FrameWorkTableModel( List<T> frameworks) {
        this.frameworks = frameworks;
    }

    @Override
    public void addRow() {

    }

    public void setDataList(List<T> descFrameworks) {
        this.frameworks = descFrameworks;
    }

    @Override
    public void exchangeRows(int oldIndex, int newIndex) {
        super.moveRow(oldIndex, oldIndex, newIndex);
        T remove = this.frameworks.remove(oldIndex);
        this.frameworks.add(newIndex, remove);
    }

    @Override
    public boolean canExchangeRows(int oldIndex, int newIndex) {
        return true;
    }

    public List<T> getFrameworks() {
        return frameworks;
    }

    public void removeAll() {
        final int rowCount = super.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            super.removeRow(i);
        }
    }

    public boolean isModified() {
        for (int i = 0; i < frameworks.size(); i++) {
            DescFramework descFramework = (DescFramework) frameworks.get(i);
            if (descFramework.getSequence() != i) {
                return true;
            }
        }
        return false;
    }
}
