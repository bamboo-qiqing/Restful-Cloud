package com.bamboo.tool.view.component.table;

import com.intellij.util.ui.EditableModel;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class AnnotationTableModel<T> extends DefaultTableModel implements EditableModel {
    private List<T> frameworks;

    public AnnotationTableModel(List<T> frameworks) {
        this.frameworks = frameworks;
    }

    @Override
    public void addRow() {

    }

    public void setDataList(List<T> frameworks) {
        this.frameworks = frameworks;
    }

    @Override
    public void exchangeRows(int oldIndex, int newIndex) {
        super.moveRow(oldIndex, oldIndex, newIndex);
//        T remove = this.frameworks.remove(oldIndex);
//        this.frameworks.add(newIndex, remove);
    }

    @Override
    public boolean canExchangeRows(int oldIndex, int newIndex) {
        return true;
    }

    public List<T> getFrameworks() {
        return frameworks;
    }

    public void removeAll() {
        int rowCount = super.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            super.removeRow(0);
        }
    }
}
