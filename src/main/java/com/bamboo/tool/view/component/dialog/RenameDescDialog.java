package com.bamboo.tool.view.component.dialog;

import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.db.service.BambooService;
import com.bamboo.tool.entity.NoteData;
import com.bamboo.tool.util.StringUtil;
import com.bamboo.tool.view.component.tree.*;

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
        String text = renameDescTextField.getText();
        if (StringUtil.isNotBlank(text)) {
            if (lastPathComponent instanceof MethodNode) {
                MethodNode methodNode = (MethodNode) lastPathComponent;
//                BambooApiMethod source = methodNode.getSource();
//                String methodId = source.getMethodId();
//                BambooService.saveMethodRemark(text,methodId);
            }
            if (lastPathComponent instanceof ClassNode) {
                ClassNode classNode = (ClassNode) lastPathComponent;
//                NoteData source = classNode.getSource();
//                String classId = source.getClassId();
//                BambooService.saveClassRemark(text,classId);
            }
        }
        dispose();
    }

    private void onCancel() {
        // 必要时在此处添加您的代码
        dispose();
    }


}
