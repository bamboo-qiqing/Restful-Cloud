package com.bamboo.tool.view;

import com.bamboo.tool.config.BambooApisComponent;
import com.bamboo.tool.config.BambooApisComponentConfig;
import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.configurable.BambooApiFilterConfiguration;
import com.bamboo.tool.configurable.BambooSoaFilterConfiguration;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.db.service.BambooService;
import com.bamboo.tool.entity.BambooClass;
import com.bamboo.tool.enums.RequestMethod;
import com.bamboo.tool.enums.SoaType;
import com.bamboo.tool.factory.FrameworkExecute;
import com.bamboo.tool.util.PsiUtils;
import com.bamboo.tool.view.component.actions.RefreshApiAction;
import com.bamboo.tool.view.component.actions.RenameDescAction;
import com.bamboo.tool.view.component.actions.SearchApiAction;
import com.bamboo.tool.view.component.actions.SearchEverywhereFiltersAction;
import com.bamboo.tool.view.component.notificationGroup.ToolWindowNotificationGroup;
import com.bamboo.tool.view.component.tree.*;
import com.intellij.icons.AllIcons;
import com.intellij.ide.CommonActionsManager;
import com.intellij.ide.actions.searcheverywhere.PersistentSearchEverywhereContributorFilter;
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
    private PersistentSearchEverywhereContributorFilter soaTypeFiler;

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

        Disposer.register(myProject, this);

        initActionBar();
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
                    BambooApisComponentConfig state = BambooApisComponent.getInstance(myProject).getState();

                    state.setBambooClasses(allApiList);
                    ProjectInfo currentProject = BambooService.queryProject(myProject.getBasePath(), myProject.getName());
                    BambooService.saveClass(allApiList, currentProject);
                    List<BambooApiMethod> allApi = BambooService.getAllApi(currentProject.getId().toString(), null, myProject, null, false, soaTypeFiler.getSelectedElements());

                    RootNode rootNode = PsiUtils.convertToRoot(allApi, requestTypeFiler.getSelectedElements());
                    apiTree.setModel(new DefaultTreeModel(rootNode));
                    ToolWindowNotificationGroup.NOTIFICATION_GROUP.createNotification("Reload apis complete", MessageType.INFO).notify(myProject);
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
        BambooSoaFilterConfiguration soaFilterConfiguration = BambooSoaFilterConfiguration.getInstance(myProject);
        this.requestTypeFiler = new PersistentSearchEverywhereContributorFilter(Arrays.asList(RequestMethod.values()), instance, (a) -> a.toString(), (e) -> null);
        this.soaTypeFiler = new PersistentSearchEverywhereContributorFilter(Arrays.asList(SoaType.values()), soaFilterConfiguration, (a) -> {
            SoaType soaType = (SoaType) a;
            return soaType.getDesc();
        }, (e) -> {
            SoaType soaType = (SoaType) e;
            return soaType.getIcon();
        });
        group.add(new SearchEverywhereFiltersAction(this.requestTypeFiler, this::refresh, "请求类型过滤器", "请求类型过滤器", AllIcons.General.Filter));
        group.add(new SearchEverywhereFiltersAction(this.soaTypeFiler, this::refresh, "Soa类型过滤器", "Soa类型过滤器", PluginIcons.FILERSOA));
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
