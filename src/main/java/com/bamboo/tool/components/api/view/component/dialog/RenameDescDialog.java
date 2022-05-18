package com.bamboo.tool.components.api.view.component.dialog;

import com.bamboo.tool.components.api.view.component.actions.RenameDescAction;
import com.bamboo.tool.components.api.view.component.tree.*;

import javax.swing.*;
import java.awt.event.*;

public class RenameDescDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField renameDescTextField;
    private ApiTree apiTree;

    public RenameDescDialog(ApiTree apiTree) {
        this.apiTree = apiTree;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // 点击 X 时调用 onCancel()
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // 遇到 ESCAPE 时调用 onCancel()
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        Object lastPathComponent = apiTree.getSelectionPath().getLastPathComponent();
        if (lastPathComponent instanceof MethodNode) {

        }
        if (lastPathComponent instanceof ClassNode) {

        }
        if (lastPathComponent instanceof ModuleNode) {

        }
        if (lastPathComponent instanceof ProjectNode) {

        }
        dispose();
    }

    private void onCancel() {
        // 必要时在此处添加您的代码
        dispose();
    }


}
