package com.bamboo.tool.view;

import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.configurable.BambooApiFilterConfiguration;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.db.service.BambooService;
import com.bamboo.tool.entity.BambooClass;
import com.bamboo.tool.enums.RequestMethod;
import com.bamboo.tool.factory.FrameworkExecute;
import com.bamboo.tool.util.PsiUtils;
import com.bamboo.tool.view.component.actions.RefreshApiAction;
import com.bamboo.tool.view.component.actions.RenameDescAction;
import com.bamboo.tool.view.component.actions.SearchApiAction;
import com.bamboo.tool.view.component.tree.*;
import com.intellij.ide.CommonActionsManager;
import com.intellij.ide.actions.searcheverywhere.PersistentSearchEverywhereContributorFilter;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFiltersAction;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.ActionManagerImpl;
import com.intellij.openapi.actionSystem.impl.MenuItemPresentationFactory;
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
import java.util.Arrays;
import java.util.List;

public class CurrentApisNavTool extends SimpleToolWindowPanel implements Disposable {
    private Project myProject;
    private JPanel mainJPanel;
    private JTextField textField1;
    private ApiTree apiTree;
    private List<BambooClass> allApiList;
    private PersistentSearchEverywhereContributorFilter requestTypeFiler;

    public CurrentApisNavTool(Project project) {
        super(true, false);
        this.myProject = project;
        mainJPanel = new JPanel();
        textField1 = new JTextField();
        mainJPanel.setLayout(new BorderLayout());
        this.setContent(mainJPanel);
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

        PopupHandler popupHandler = new PopupHandler() {
            @Override
            public void invokePopup(Component comp, int x, int y) {

                if (comp instanceof ApiTree) {

                    DefaultActionGroup group = new DefaultActionGroup();
                    ApiTree source = (ApiTree) comp;
                    Object lastPathComponent = source.getSelectionPath().getLastPathComponent();
                    if (lastPathComponent instanceof MethodNode) {
                        group.add(new RenameDescAction(source));
                    }
                    if (lastPathComponent instanceof ClassNode) {
                        group.add(new RenameDescAction(source));
                    }
                    if (lastPathComponent instanceof ModuleNode) {
//                        group.add(new RenameDescAction(source));
                    }
                    if (lastPathComponent instanceof ProjectNode) {
//                        group.add(new RenameDescAction(source));
                    }
                    ActionPopupMenu contextMenu = ((ActionManagerImpl) ActionManager.getInstance()).createActionPopupMenu(ActionPlaces.POPUP, new DefaultActionGroup(group), new MenuItemPresentationFactory(true));
                    contextMenu.getComponent().show(comp, x, y);
                }

            }
        };
        apiTree.addMouseListener(popupHandler);
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
        scrollPane.add(textField1);
        mainJPanel.add(scrollPane);
        mainJPanel.add(scrollPane);
        renderData();
    }

    @Override
    public void dispose() {

    }

    private void renderData() {
        DumbService.getInstance(myProject).smartInvokeLater(() -> rendingTree());
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

    private void refresh() {
        this.rendingTree();
    }

    private void rendingTree() {
        Task.Backgroundable task = new Task.Backgroundable(myProject, "Restful cloud...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(false);
                ApplicationManager.getApplication().runReadAction(() -> {
                    allApiList = FrameworkExecute.buildApiMethod(myProject);
                    indicator.setText("Rendering");

                    ProjectInfo currentProject = BambooService.queryProject(myProject.getBasePath(), myProject.getName());
                    BambooService.saveClass(allApiList, currentProject);
                    List<BambooApiMethod> allApi = BambooService.getAllApi(currentProject.getId().toString(), null, myProject, null, false);
                    RootNode root = new RootNode("apis(" + allApi.size() + ")");
                    apiTree.setModel(new DefaultTreeModel(root));
                    PsiUtils.convertToRoot(root, allApi, requestTypeFiler.getSelectedElements());
                    NotificationGroupManager.getInstance().getNotificationGroup("toolWindowNotificationGroup").createNotification("Reload apis complete", MessageType.INFO).notify(myProject);
                });

            }
        };
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));
    }

    private void initActionBar() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new RefreshApiAction(this::renderData));
        group.add(new SearchApiAction());
        BambooApiFilterConfiguration instance = BambooApiFilterConfiguration.getInstance(myProject);
        requestTypeFiler = new PersistentSearchEverywhereContributorFilter(Arrays.asList(RequestMethod.values()), instance, (a) -> a.toString(), (e) -> null);
        group.add(new SearchEverywhereFiltersAction(requestTypeFiler, this::refresh));
        group.add(CommonActionsManager.getInstance().createExpandAllAction(apiTree, apiTree));
        group.add(CommonActionsManager.getInstance().createCollapseAllAction(apiTree, apiTree));
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLWINDOW_CONTENT, group, true);
        actionToolbar.setTargetComponent(mainJPanel);
        JComponent toolbarComponent = actionToolbar.getComponent();
        Border border = IdeBorderFactory.createBorder(SideBorder.BOTTOM);
        actionToolbar.getComponent().setBorder(border);
        setToolbar(toolbarComponent);

    }


}
