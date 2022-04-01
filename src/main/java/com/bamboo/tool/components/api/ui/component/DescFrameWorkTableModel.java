package com.bamboo.tool.components.api.ui.component;

import com.bamboo.tool.components.api.entity.DescFramework;
import com.intellij.util.ui.EditableModel;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class DescFrameWorkTableModel<T> extends DefaultTableModel implements EditableModel {

    private List<T> descFrameworks;

    public DescFrameWorkTableModel(List<T> descFrameworks) {
        this.descFrameworks = descFrameworks;
    }

    @Override
    public void addRow() {

    }

    @Override
    public void exchangeRows(int oldIndex, int newIndex) {
        super.moveRow(oldIndex, oldIndex, newIndex);
        T remove = this.descFrameworks.remove(oldIndex);
        this.descFrameworks.add(newIndex, remove);
    }

    @Override
    public boolean canExchangeRows(int oldIndex, int newIndex) {
        return true;
    }

    public List<T> getDescFrameworks() {
        return descFrameworks;
    }

    public void removeAll() {
        final int rowCount = super.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            super.removeRow(i);
        }
    }

    public boolean isModified() {
        for (int i = 0; i < descFrameworks.size(); i++) {
            DescFramework descFramework = (DescFramework) descFrameworks.get(i);
            if (descFramework.getSequence() != i) {
                return true;
            }
        }
        return false;
    }

}
