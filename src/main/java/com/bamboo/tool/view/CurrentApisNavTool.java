package com.bamboo.tool.view;

import com.bamboo.tool.config.BambooApisComponent;
import com.bamboo.tool.config.BambooApisComponentConfig;
import com.bamboo.tool.config.BambooToolComponent;
import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.config.model.RestfulCloudConfig;
import com.bamboo.tool.configurable.BambooApiFilterConfiguration;
import com.bamboo.tool.configurable.BambooSoaFilterConfiguration;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.db.service.BambooService;
import com.bamboo.tool.entity.AnnotationInfoSetting;
import com.bamboo.tool.entity.BambooClass;
import com.bamboo.tool.enums.RequestMethod;
import com.bamboo.tool.enums.SoaType;
import com.bamboo.tool.factory.FrameworkExecute;
import com.bamboo.tool.store.StoreService;
import com.bamboo.tool.util.PsiUtils;
import com.bamboo.tool.view.component.actions.*;
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
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.*;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.power.doc.model.ApiConfig;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

        apiTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (!apiTree.isEnabled()) {
                    return;
                }
//                if (SwingUtilities.isLeftMouseButton(event)) {
//                    if (event.getClickCount() == 2 && getDoubleClickListener() != null) {
//                        getDoubleClickListener().accept(node);
//                    }
//                } else if (SwingUtilities.isRightMouseButton(event)) {
//                    showPopupMenu(event.getX(), event.getY(), getPopupMenu(event, node));
//                }
                if (SwingUtilities.isRightMouseButton(event)) {
                    showPopupMenu(event.getX(), event.getY(), getPopupMenu(event));
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
        scrollPane.add(textField1);
        mainJPanel.add(scrollPane);
        mainJPanel.add(scrollPane);
        renderData();
    }

    public JPopupMenu getPopupMenu(@NotNull MouseEvent event) {
        List<JMenuItem> items = new ArrayList<>();
        JBPopupMenu menu = new JBPopupMenu();

        JMenu jMenu = new JMenu("导出");
        jMenu.setIcon(AllIcons.ToolbarDecorator.Export);
        JMenuItem exportHtml = new JMenuItem("导出为Html");
        exportHtml.addActionListener(actionEvent -> {
            ApiConfig apiConfig = new ApiConfig();
        });
        jMenu.add(exportHtml);
        menu.add(jMenu);
        return menu;
    }

    @Override
    public void dispose() {

    }

    /**
     * 显示右键菜单
     */
    protected void showPopupMenu(int x, int y, @Nullable JPopupMenu menu) {
        if (menu == null) {
            return;
        }
        TreePath path = apiTree.getPathForLocation(x, y);
        apiTree.setSelectionPath(path);
        Rectangle rectangle = apiTree.getUI().getPathBounds(apiTree, path);
        if (rectangle != null && rectangle.contains(x, y)) {
            menu.show(apiTree, x, rectangle.y + rectangle.height);
        }
    }

    private void renderData() {
        RestfulCloudConfig state = BambooToolComponent.getInstance().getState();
        List<ProjectInfo> projectInfos = state.getProjectInfos();
        long count = projectInfos.stream().filter(e -> e.getProjectName().equals(myProject.getName())).filter(e -> e.getProjectPath().equals(myProject.getBasePath())).count();
        if (count < 1) {
            ProjectInfo projectInfo = new ProjectInfo();
            projectInfo.setProjectPath(myProject.getBasePath());
            projectInfo.setProjectName(myProject.getName());
            projectInfos.add(projectInfo);
        }
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
//                setToolTipText(((MethodNode) target).getToolTipText());
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
                    StoreService storeService = BambooApisComponent.getStoreService();
                    allApiList = FrameworkExecute.buildApiMethod(myProject);


                    indicator.setText("Rendering");
                    storeService.saveData(allApiList, myProject);
                    List<BambooApiMethod> apis = new ArrayList<>();
                    allApiList.parallelStream().forEach(bambooClass -> {
                        bambooClass.getMethods().parallelStream().forEach(method -> {
                            List<BambooApiMethod> list = method.getMethodUrl().stream().map(e -> {
                                BambooApiMethod bambooApiMethod = new BambooApiMethod();
                                bambooApiMethod.setUrl(e);
                                bambooApiMethod.setProject(myProject);
                                bambooApiMethod.setMethodName(method.getMethodName());
                                bambooApiMethod.setClassPath(bambooClass.getClassPath());
                                bambooApiMethod.setProjectName(myProject.getName());
                                bambooApiMethod.setRequestMethods(method.getRequestMethods());
                                bambooApiMethod.setMethodReturn(method.getReturnType().getReturnTypePath());
                                bambooApiMethod.setModelName(bambooClass.getModuleName());
                                bambooApiMethod.setProjectPath(myProject.getBasePath());
                                bambooApiMethod.setClassName(bambooClass.getClassName());
                                bambooApiMethod.setMethodParams(method.getMethodParams());
                                AnnotationInfoSetting setting = bambooClass.getSetting();
                                String soaTypeStr = setting.getSoaType();
                                SoaType soaType = SoaType.getSoaType(soaTypeStr);
                                bambooApiMethod.setSoaType(soaType);
                                return bambooApiMethod;
                            }).collect(Collectors.toList());
                            apis.addAll(list);
                        });
                    });
                    RootNode rootNode = PsiUtils.convertToRoot(apis, requestTypeFiler.getSelectedElements());
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
