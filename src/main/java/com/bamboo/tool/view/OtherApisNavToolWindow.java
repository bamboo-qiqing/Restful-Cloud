package com.bamboo.tool.view;

import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.configurable.BambooApiFilterConfiguration;
import com.bamboo.tool.configurable.BambooSoaFilterConfiguration;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.db.service.BambooService;
import com.bamboo.tool.enums.RequestMethod;
import com.bamboo.tool.enums.SoaType;
import com.bamboo.tool.util.PsiUtils;
import com.bamboo.tool.view.component.actions.RefreshApiAction;
import com.bamboo.tool.view.component.actions.SearchEverywhereFiltersAction;
import com.bamboo.tool.view.component.tree.ApiTree;
import com.bamboo.tool.view.component.tree.BaseNode;
import com.bamboo.tool.view.component.tree.MethodNode;
import com.bamboo.tool.view.component.tree.RootNode;
import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.searcheverywhere.PersistentSearchEverywhereContributorFilter;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
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
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/2/14 10:58
 * Description
 */
public class OtherApisNavToolWindow extends SimpleToolWindowPanel implements Disposable {
    private final Project myProject;
    private final JPanel panel;
    private final ApiTree apiTree;
    private PersistentSearchEverywhereContributorFilter requestTypeFiler;
    private PersistentSearchEverywhereContributorFilter soaTypeFiler;

    public OtherApisNavToolWindow(Project project) {
        super(false, false);
        this.myProject = project;
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        this.setContent(panel);
        setLayout(new BorderLayout());

        initActionBar();
        apiTree = new ApiTree();
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

        Disposer.register(myProject, this);
        renderData();
    }

    @Override
    public void dispose() {

    }

    private void renderData() {

        DumbService.getInstance(myProject).smartInvokeLater(() -> rendingTree());
    }

    private void rendingTree() {
        Task.Backgroundable task = new Task.Backgroundable(myProject, "Bamboo apis...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(false);
                ProjectInfo currentProject = BambooService.queryProject(myProject.getBasePath(), myProject.getName());
                List<BambooApiMethod> allApi = BambooService.getAllApi(null, currentProject.getId().toString(), myProject, null, false, soaTypeFiler.getSelectedElements());

                RootNode rootNode = PsiUtils.convertOtherToRoot(allApi, requestTypeFiler.getSelectedElements());
                apiTree.setModel(new DefaultTreeModel(rootNode));
                NotificationGroupManager.getInstance().getNotificationGroup("toolWindowNotificationGroup").createNotification("Reload apis complete", MessageType.INFO).notify(myProject);
            }
        };
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));
    }

    /**
     * Initialize operation bar
     */
    private void initActionBar() {
        DefaultActionGroup group = new DefaultActionGroup();
        BambooApiFilterConfiguration instance = BambooApiFilterConfiguration.getInstance(myProject);
        requestTypeFiler = new PersistentSearchEverywhereContributorFilter(Arrays.asList(RequestMethod.values()), instance, (a) -> a.toString(), (e) -> null);
        group.add(new SearchEverywhereFiltersAction(requestTypeFiler, this::renderData, "请求类型过滤器", "请求类型过滤器", AllIcons.General.Filter));

        BambooSoaFilterConfiguration soaFilterConfiguration = BambooSoaFilterConfiguration.getInstance(myProject);
        soaTypeFiler = new PersistentSearchEverywhereContributorFilter(Arrays.asList(SoaType.values()), soaFilterConfiguration, (a) -> {
            SoaType soaType = (SoaType) a;
            return soaType.getDesc();
        }, (e) -> {
            SoaType soaType = (SoaType) e;
            return soaType.getIcon();
        });
        group.add(new com.bamboo.tool.view.component.actions.SearchEverywhereFiltersAction(soaTypeFiler, this::renderData, "Soa类型过滤器", "Soa类型过滤器", PluginIcons.FILERSOA));

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLWINDOW_CONTENT, group, false);
        actionToolbar.setTargetComponent(panel);
        JComponent toolbarComponent = actionToolbar.getComponent();
        Border border = IdeBorderFactory.createBorder(SideBorder.BOTTOM);
        actionToolbar.getComponent().setBorder(border);
        setToolbar(toolbarComponent);
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
        BambooApiMethod source = methodNode.getSource();
        source.navigate(true);
    }
}
