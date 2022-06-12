package com.bamboo.tool.view.component.table;

import com.bamboo.tool.db.service.BambooService;
import com.bamboo.tool.entity.Framework;
import com.intellij.util.ui.EditableModel;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class AbstractTableModel<T> extends DefaultTableModel implements EditableModel {
    private List<T> data;
    private Object[] newData;

    public AbstractTableModel(List<T> data, Object[] newData) {
        this.data = data;
        this.newData = newData;
    }

    @Override
    public void addRow() {
        Framework framework = BambooService.addFramework(String.valueOf(newData[0]), String.valueOf(newData[1]));
        this.data.add((T) framework);
        super.addRow(newData);
    }

    public void setDataList(List<T> data) {
        this.data = data;
    }

    @Override
    public void exchangeRows(int oldIndex, int newIndex) {
        super.moveRow(oldIndex, oldIndex, newIndex);
        T remove = this.data.remove(oldIndex);
        this.data.add(newIndex, remove);
    }

    @Override
    public boolean canExchangeRows(int oldIndex, int newIndex) {
        return true;
    }


    public void removeAll() {
        final int rowCount = super.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            super.removeRow(0);
        }
    }

}
