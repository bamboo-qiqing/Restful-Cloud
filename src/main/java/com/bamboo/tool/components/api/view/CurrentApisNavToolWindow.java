package com.bamboo.tool.components.api.view;

import com.bamboo.tool.components.api.entity.BambooClass;
import com.bamboo.tool.components.api.factory.FrameworkExecute;
import com.bamboo.tool.components.api.view.component.tree.ApiTree;
import com.bamboo.tool.components.api.view.component.tree.BaseNode;
import com.bamboo.tool.components.api.view.component.tree.MethodNode;
import com.bamboo.tool.components.api.view.component.tree.RootNode;
import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.db.service.BambooService;
import com.bamboo.tool.util.PsiUtils;
import com.intellij.icons.AllIcons;
import com.intellij.ide.CommonActionsManager;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.*;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CurrentApisNavToolWindow extends SimpleToolWindowPanel implements Disposable {
    private final Project myProject;
    private final JPanel panel;
    private final ApiTree apiTree;
    private List<BambooClass> allApiList;

    public CurrentApisNavToolWindow(Project project) {
        super(false, false);
        this.myProject = project;
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        this.setContent(panel);
        allApiList = new ArrayList<>();
        setLayout(new BorderLayout());
        apiTree = new ApiTree();
        initActionBar();


        Disposer.register(myProject, this);
        apiTree.setCellRenderer(new MyCellRenderer());
        apiTree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    navigateToMethod();
                }
            }
        });
        apiTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2 && event.getButton() == MouseEvent.BUTTON1) {
                    navigateToMethod();
                }
            }
        });
        TreeSpeedSearch treeSpeedSearch = new TreeSpeedSearch(apiTree, path -> {
            BaseNode node = (BaseNode) path.getLastPathComponent();
            Object object = node.getUserObject();
            return object.toString();
        }, true);
        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(treeSpeedSearch.getComponent());
        panel.add(scrollPane);
        renderData(myProject);
    }

    @Override
    public void dispose() {

    }

    private void renderData(Project project) {
        DumbService.getInstance(project).smartInvokeLater(() -> rendingTree(project));
    }


    private static class MyCellRenderer extends ColoredTreeCellRenderer {

        @Override
        public void customizeCellRenderer(@NotNull JTree tree, Object target, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            BaseNode node = null;
            if (target instanceof BaseNode) {
                node = (BaseNode<?>) target;
            }
            if (node == null) {
                return;
            }

            append(node.toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            setIcon(node.getIcon(true));
            if (target instanceof MethodNode) {
                setToolTipText(((MethodNode) target).getToolTipText());
            }

            SpeedSearchUtil.applySpeedSearchHighlighting(this, this, false, true);
        }
    }

    private void navigateToMethod() {
        if (!apiTree.isEnabled()) {
            return;
        }
        Object component = apiTree.getLastSelectedPathComponent();
        if (!(component instanceof MethodNode)) {
            return;
        }
        MethodNode methodNode = (MethodNode) component;
        final BambooApiMethod source = methodNode.getSource();

        source.navigate(true);
    }


    private void rendingTree(Project project) {
        Task.Backgroundable task = new Task.Backgroundable(myProject, "Restful cloud...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(false);
                ApplicationManager.getApplication().runReadAction(() -> {
                    allApiList = FrameworkExecute.buildApiMethod(myProject);
                    indicator.setText("Rendering");
                    RootNode root = new RootNode("apis("+allApiList.size()+")");
                    apiTree.setModel(new DefaultTreeModel(root));
                    ProjectInfo currentProject = BambooService.queryProject(project.getBasePath(), project.getName());
                    BambooService.saveClass(allApiList, currentProject);
                    final List<BambooApiMethod> allApi = BambooService.getAllApi(currentProject.getId().toString(), null, project,null);
                    PsiUtils.convertToRoot(root, allApi);
                    NotificationGroupManager.getInstance().getNotificationGroup("toolWindowNotificationGroup").createNotification("Reload apis complete", MessageType.INFO).notify(myProject);
                });

            }
        };
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));
    }

    private void initActionBar() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new RefreshApiAction());
        group.add(CommonActionsManager.getInstance().createExpandAllAction(apiTree, apiTree));
        group.add(CommonActionsManager.getInstance().createCollapseAllAction(apiTree, apiTree));
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLWINDOW_CONTENT, group, true);
        actionToolbar.setTargetComponent(panel);
        JComponent toolbarComponent = actionToolbar.getComponent();
        Border border = IdeBorderFactory.createBorder(SideBorder.BOTTOM);
        actionToolbar.getComponent().setBorder(border);
        setToolbar(toolbarComponent);
    }

    private final class RefreshApiAction extends AnAction {
        public RefreshApiAction() {
            super("Refresh", "Refresh", AllIcons.Actions.Refresh);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            renderData(myProject);
        }
    }


}
