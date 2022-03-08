package com.bamboo.tool.components.api.view;

import com.bamboo.tool.components.api.view.component.tree.ApiTree;
import com.bamboo.tool.components.api.view.component.tree.BaseNode;
import com.bamboo.tool.components.api.view.component.tree.MethodNode;
import com.bamboo.tool.components.api.view.component.tree.RootNode;

import com.bamboo.tool.config.BambooToolComponent;
import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.db.service.BambooService;
import com.bamboo.tool.util.PsiUtils;
import com.intellij.icons.AllIcons;
import com.intellij.ide.CommonActionsManager;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
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
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/2/14 10:58
 * Description
 */
public class OtherApisNavToolWindow extends SimpleToolWindowPanel implements Disposable {


    private final Project myProject;
    private JPanel panel;
    private ApiTree apiTree;

    public OtherApisNavToolWindow(Project project) {
        super(false, false);
        this.myProject = project;
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        this.setContent(panel);
        setLayout(new BorderLayout());
        apiTree = new ApiTree();
        initActionBar();


        Disposer.register(myProject, this);
        apiTree.setCellRenderer(new MyCellRenderer());

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

    private void rendingTree(Project project) {
        Task.Backgroundable task = new Task.Backgroundable(myProject, "bamboo apis...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(false);
                ProjectInfo projectInfo = BambooToolComponent.getInstance().getState().getProjectInfo();
                final List<BambooApiMethod> allApi = BambooService.getAllApi(null, projectInfo.getProjectId(),project);
                RootNode root = new RootNode("apis");
                PsiUtils.convertOtherToRoot(root, allApi);
                apiTree.setModel(new DefaultTreeModel(root));
                NotificationGroupManager.getInstance().getNotificationGroup("toolWindowNotificationGroup").createNotification("Reload apis complete", MessageType.INFO).notify(myProject);
            }
        };
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));
    }

    private void initActionBar() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new RefreshApiAction());
        group.add(CommonActionsManager.getInstance().createExpandAllAction(apiTree, apiTree));
        group.add(CommonActionsManager.getInstance().createCollapseAllAction(apiTree, apiTree));

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLWINDOW_CONTENT, group, false);
        actionToolbar.setTargetComponent(panel);
        JComponent toolbarComponent = actionToolbar.getComponent();
        Border border = IdeBorderFactory.createBorder(SideBorder.BOTTOM);
        actionToolbar.getComponent().setBorder(border);
        setToolbar(toolbarComponent);
    }

    private final class RefreshApiAction extends AnAction {
        public RefreshApiAction() {
            super("refresh", "refresh", AllIcons.Actions.Refresh);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            renderData(myProject);
        }
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
}
