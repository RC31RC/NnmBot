package com.nnm.bot.admin.config;

import com.nnm.bot.admin.domain.DataScope;
import com.nnm.bot.admin.domain.PermissionType;
import com.nnm.bot.admin.domain.SysMenu;
import com.nnm.bot.admin.domain.SysPermission;
import com.nnm.bot.admin.domain.SysRole;
import com.nnm.bot.admin.domain.SysUser;
import com.nnm.bot.admin.repository.SysMenuRepository;
import com.nnm.bot.admin.repository.SysPermissionRepository;
import com.nnm.bot.admin.repository.SysRoleRepository;
import com.nnm.bot.admin.repository.SysUserRepository;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class RbacDataInitializer implements ApplicationRunner {

    @Resource
    private SysUserRepository userRepository;
    @Resource
    private SysRoleRepository roleRepository;
    @Resource
    private SysPermissionRepository permissionRepository;
    @Resource
    private SysMenuRepository menuRepository;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private AdminBootstrapProperties bootstrapProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(ApplicationArguments args) {
        if (!bootstrapProperties.isEnabled()) {
            return;
        }
        bootstrapProperties.validateWhenEnabled();

        List<SysMenu> menus = createMenus();
        List<SysPermission> permissions = createPermissions(menus);

        SysRole superAdmin = role("R_SUPER", "超级管理员", "拥有全部管理权限", DataScope.ALL);
        superAdmin.getMenus().addAll(menus);
        superAdmin.getPermissions().addAll(permissions);
        roleRepository.save(superAdmin);

        SysRole admin = role("R_ADMIN", "管理员", "负责日常用户管理", DataScope.ALL);
        admin.getMenus().addAll(menus.stream()
                .filter(menu -> !SUPER_ADMIN_ONLY_MENUS.contains(menu.getName()))
                .toList());
        admin.getPermissions().addAll(permissions.stream()
                .filter(permission -> !permission.getCode().startsWith("system:role:"))
                .filter(permission -> !permission.getCode().startsWith("system:menu:"))
                .filter(permission -> !permission.getCode().startsWith("system:audit:"))
                .filter(permission -> !permission.getCode().startsWith("system:session:"))
                .toList());
        roleRepository.save(admin);

        role("R_USER", "只读用户", "仅访问基础功能", DataScope.SELF);

        String username = bootstrapProperties.getUsername();
        SysUser user = userRepository.findByUsername(username).orElseGet(SysUser::new);
        if (user.getId() == null) {
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(bootstrapProperties.getPassword()));
            user.setEnabled(true);
        }
        user.getRoles().add(superAdmin);
        userRepository.save(user);
    }

    private List<SysMenu> createMenus() {
        List<SysMenu> menus = new ArrayList<>();

        SysMenu dashboard = menu(null, "Dashboard", "/dashboard", "/index/index",
                "menus.dashboard.title", "ri:pie-chart-line", 10, false);
        menus.add(dashboard);
        SysMenu console = menu(dashboard.getId(), "Console", "console", "/dashboard/console",
                "menus.dashboard.console", "ri:home-smile-2-line", 11, false);
        console.setFixedTab(true);
        menus.add(console);
        SysMenu analysis = menu(dashboard.getId(), "Analysis", "analysis", "/dashboard/analysis",
                "menus.dashboard.analysis", "ri:align-item-bottom-line", 12, false);
        menus.add(analysis);
        menus.add(menu(dashboard.getId(), "Ecommerce", "ecommerce", "/dashboard/ecommerce",
                "menus.dashboard.ecommerce", "ri:bar-chart-box-line", 13, false));

        SysMenu system = menu(null, "System", "/system", "/index/index",
                "menus.system.title", "ri:user-3-line", 20, false);
        menus.add(system);
        SysMenu user = menu(system.getId(), "User", "user", "/system/user",
                "menus.system.user", "ri:user-line", 21, true);
        menus.add(user);
        SysMenu role = menu(system.getId(), "Role", "role", "/system/role",
                "menus.system.role", "ri:user-settings-line", 22, true);
        menus.add(role);
        SysMenu userCenter = menu(system.getId(), "UserCenter", "user-center", "/system/user-center",
                "menus.system.userCenter", "ri:user-line", 23, true);
        hide(userCenter);
        userCenter.setHideTab(true);
        menus.add(userCenter);
        SysMenu menu = menu(system.getId(), "Menus", "menu", "/system/menu",
                "menus.system.menu", "ri:menu-line", 24, true);
        menus.add(menu);
        SysMenu audit = menu(system.getId(), "OperationLog", "audit", "/system/audit",
                "menus.system.audit", "ri:file-list-3-line", 25, true);
        menus.add(audit);
        SysMenu session = menu(system.getId(), "OnlineSession", "session", "/system/session",
                "menus.system.session", "ri:computer-line", 26, true);
        menus.add(session);

        SysMenu nested = menu(system.getId(), "Nested", "nested", null,
                "menus.system.nested", "ri:menu-unfold-3-line", 27, true);
        menus.add(nested);
        menus.add(menu(nested.getId(), "NestedMenu1", "menu1", "/system/nested/menu1",
                "menus.system.menu1", "ri:align-justify", 271, true));
        SysMenu nestedMenu2 = menu(nested.getId(), "NestedMenu2", "menu2", null,
                "menus.system.menu2", "ri:align-justify", 272, true);
        menus.add(nestedMenu2);
        menus.add(menu(nestedMenu2.getId(), "NestedMenu2-1", "menu2-1", "/system/nested/menu2",
                "menus.system.menu21", "ri:align-justify", 2721, true));
        SysMenu nestedMenu3 = menu(nested.getId(), "NestedMenu3", "menu3", null,
                "menus.system.menu3", "ri:align-justify", 273, true);
        menus.add(nestedMenu3);
        menus.add(menu(nestedMenu3.getId(), "NestedMenu3-1", "menu3-1", "/system/nested/menu3",
                "menus.system.menu31", null, 2731, true));
        SysMenu nestedMenu32 = menu(nestedMenu3.getId(), "NestedMenu3-2", "menu3-2", null,
                "menus.system.menu32", null, 2732, true);
        menus.add(nestedMenu32);
        menus.add(menu(nestedMenu32.getId(), "NestedMenu3-2-1", "menu3-2-1",
                "/system/nested/menu3/menu3-2", "menus.system.menu321", null, 27321, true));

        menus.add(menu(null, "BotManagement", "/bot-management", "/bot/instance",
                "menus.bot.title", "ri:robot-2-line", 28, true));

        SysMenu template = menu(null, "Template", "/template", "/index/index",
                "menus.template.title", "ri:apps-2-line", 30, false);
        menus.add(template);
        menus.add(menu(template.getId(), "Cards", "cards", "/template/cards",
                "menus.template.cards", "ri:wallet-line", 31, false));
        menus.add(menu(template.getId(), "Banners", "banners", "/template/banners",
                "menus.template.banners", "ri:rectangle-line", 32, false));
        menus.add(menu(template.getId(), "Charts", "charts", "/template/charts",
                "menus.template.charts", "ri:bar-chart-box-line", 33, false));
        menus.add(menu(template.getId(), "Map", "map", "/template/map",
                "menus.template.map", "ri:map-pin-line", 34, true));
        menus.add(menu(template.getId(), "Chat", "chat", "/template/chat",
                "menus.template.chat", "ri:message-3-line", 35, true));
        menus.add(menu(template.getId(), "Calendar", "calendar", "/template/calendar",
                "menus.template.calendar", "ri:calendar-2-line", 36, true));
        SysMenu pricing = menu(template.getId(), "Pricing", "pricing", "/template/pricing",
                "menus.template.pricing", "ri:money-cny-box-line", 37, true);
        pricing.setFullPage(true);
        menus.add(pricing);

        SysMenu widgets = menu(null, "Widgets", "/widgets", "/index/index",
                "menus.widgets.title", "ri:apps-2-add-line", 40, false);
        menus.add(widgets);
        menus.add(menu(widgets.getId(), "Icon", "icon", "/widgets/icon",
                "menus.widgets.icon", "ri:palette-line", 41, true));
        menus.add(menu(widgets.getId(), "ImageCrop", "image-crop", "/widgets/image-crop",
                "menus.widgets.imageCrop", "ri:screenshot-line", 42, true));
        menus.add(menu(widgets.getId(), "Excel", "excel", "/widgets/excel",
                "menus.widgets.excel", "ri:download-2-line", 43, true));
        menus.add(menu(widgets.getId(), "Video", "video", "/widgets/video",
                "menus.widgets.video", "ri:vidicon-line", 44, true));
        menus.add(menu(widgets.getId(), "CountTo", "count-to", "/widgets/count-to",
                "menus.widgets.countTo", "ri:anthropic-line", 45, false));
        menus.add(menu(widgets.getId(), "WangEditor", "wang-editor", "/widgets/wang-editor",
                "menus.widgets.wangEditor", "ri:t-box-line", 46, true));
        menus.add(menu(widgets.getId(), "Watermark", "watermark", "/widgets/watermark",
                "menus.widgets.watermark", "ri:water-flash-line", 47, true));
        menus.add(menu(widgets.getId(), "ContextMenu", "context-menu", "/widgets/context-menu",
                "menus.widgets.contextMenu", "ri:menu-2-line", 48, true));
        menus.add(menu(widgets.getId(), "Qrcode", "qrcode", "/widgets/qrcode",
                "menus.widgets.qrcode", "ri:qr-code-line", 49, true));
        menus.add(menu(widgets.getId(), "Drag", "drag", "/widgets/drag",
                "menus.widgets.drag", "ri:drag-move-fill", 50, true));
        menus.add(menu(widgets.getId(), "TextScroll", "text-scroll", "/widgets/text-scroll",
                "menus.widgets.textScroll", "ri:input-method-line", 51, true));
        SysMenu fireworks = menu(widgets.getId(), "Fireworks", "fireworks", "/widgets/fireworks",
                "menus.widgets.fireworks", "ri:magic-line", 52, true);
        fireworks.setTextBadge("Hot");
        menus.add(fireworks);
        SysMenu elementUi = menu(widgets.getId(), "ElementUI", "/outside/iframe/elementui", null,
                "menus.widgets.elementUI", "ri:apps-2-line", 53, false);
        elementUi.setLink("https://element-plus.org/zh-CN/component/overview.html");
        elementUi.setIframe(true);
        menus.add(elementUi);

        SysMenu examples = menu(null, "Examples", "/examples", "/index/index",
                "menus.examples.title", "ri:sparkling-line", 60, false);
        menus.add(examples);
        SysMenu permissionExamples = menu(examples.getId(), "Permission", "permission", null,
                "menus.examples.permission.title", "ri:fingerprint-line", 61, false);
        menus.add(permissionExamples);
        menus.add(menu(permissionExamples.getId(), "PermissionSwitchRole", "switch-role",
                "/examples/permission/switch-role", "menus.examples.permission.switchRole",
                "ri:contacts-line", 611, true));
        SysMenu buttonAuth = menu(permissionExamples.getId(), "PermissionButtonAuth", "button-auth",
                "/examples/permission/button-auth", "menus.examples.permission.buttonAuth",
                "ri:mouse-line", 612, true);
        menus.add(buttonAuth);
        menus.add(menu(permissionExamples.getId(), "PermissionPageVisibility", "page-visibility",
                "/examples/permission/page-visibility", "menus.examples.permission.pageVisibility",
                "ri:user-3-line", 613, true));
        menus.add(menu(examples.getId(), "Tabs", "tabs", "/examples/tabs",
                "menus.examples.tabs", "ri:price-tag-line", 62, false));
        menus.add(menu(examples.getId(), "TablesBasic", "tables/basic", "/examples/tables/basic",
                "menus.examples.tablesBasic", "ri:layout-grid-line", 63, true));
        menus.add(menu(examples.getId(), "Tables", "tables", "/examples/tables",
                "menus.examples.tables", "ri:table-3", 64, true));
        menus.add(menu(examples.getId(), "Forms", "forms", "/examples/forms",
                "menus.examples.forms", "ri:table-view", 65, true));
        menus.add(menu(examples.getId(), "SearchBar", "form/search-bar", "/examples/forms/search-bar",
                "menus.examples.searchBar", "ri:table-line", 66, true));
        menus.add(menu(examples.getId(), "TablesTree", "tables/tree", "/examples/tables/tree",
                "menus.examples.tablesTree", "ri:layout-2-line", 67, true));
        menus.add(menu(examples.getId(), "SocketChat", "socket-chat", "/examples/socket-chat",
                "menus.examples.socketChat", "ri:shake-hands-line", 68, true));

        SysMenu article = menu(null, "Article", "/article", "/index/index",
                "menus.article.title", "ri:book-2-line", 70, false);
        menus.add(article);
        SysMenu articleList = menu(article.getId(), "ArticleList", "article-list", "/article/list",
                "menus.article.articleList", "ri:article-line", 71, true);
        menus.add(articleList);
        SysMenu articleDetail = menu(article.getId(), "ArticleDetail", "detail/:id", "/article/detail",
                "menus.article.articleDetail", null, 72, true);
        hide(articleDetail);
        articleDetail.setActivePath("/article/article-list");
        menus.add(articleDetail);
        menus.add(menu(article.getId(), "ArticleComment", "comment", "/article/comment",
                "menus.article.comment", "ri:mail-line", 73, true));
        menus.add(menu(article.getId(), "ArticlePublish", "publish", "/article/publish",
                "menus.article.articlePublish", "ri:telegram-2-line", 74, true));

        SysMenu result = menu(null, "Result", "/result", "/index/index",
                "menus.result.title", "ri:checkbox-circle-line", 80, false);
        menus.add(result);
        menus.add(menu(result.getId(), "ResultSuccess", "success", "/result/success",
                "menus.result.success", "ri:checkbox-circle-line", 81, true));
        menus.add(menu(result.getId(), "ResultFail", "fail", "/result/fail",
                "menus.result.fail", "ri:close-circle-line", 82, true));

        SysMenu exception = menu(null, "Exception", "/exception", "/index/index",
                "menus.exception.title", "ri:error-warning-line", 90, false);
        menus.add(exception);
        menus.add(fullPage(menu(exception.getId(), "Exception403", "403", "/exception/403",
                "menus.exception.forbidden", null, 91, true)));
        menus.add(fullPage(menu(exception.getId(), "Exception404", "404", "/exception/404",
                "menus.exception.notFound", null, 92, true)));
        menus.add(fullPage(menu(exception.getId(), "Exception500", "500", "/exception/500",
                "menus.exception.serverError", null, 93, true)));

        SysMenu safeguard = menu(null, "Safeguard", "/safeguard", "/index/index",
                "menus.safeguard.title", "ri:shield-check-line", 100, false);
        menus.add(safeguard);
        menus.add(menu(safeguard.getId(), "SafeguardServer", "server", "/safeguard/server",
                "menus.safeguard.server", "ri:hard-drive-3-line", 101, true));

        menus.add(externalMenu("Document", "menus.help.document", "ri:bill-line",
                "https://www.artd.pro/docs/zh/", 110));
        menus.add(externalMenu("LiteVersion", "menus.help.liteVersion", "ri:bus-2-line",
                "https://www.artd.pro/docs/zh/guide/lite-version.html", 111));
        menus.add(externalMenu("OldVersion", "menus.help.oldVersion", "ri:subway-line",
                "https://www.artd.pro/v2/", 112));
        menus.add(menu(null, "ChangeLog", "/change/log", "/change/log",
                "menus.plan.log", "ri:gamepad-line", 113, false));

        return List.copyOf(menus);
    }

    private List<SysPermission> createPermissions(List<SysMenu> menus) {
        SysMenu userMenu = findMenu(menus, "User");
        SysMenu roleMenu = findMenu(menus, "Role");
        SysMenu menuMenu = findMenu(menus, "Menus");
        SysMenu auditMenu = findMenu(menus, "OperationLog");
        SysMenu sessionMenu = findMenu(menus, "OnlineSession");
        SysMenu botManagementMenu = findMenu(menus, "BotManagement");
        SysMenu buttonAuthMenu = findMenu(menus, "PermissionButtonAuth");

        return List.of(
                permission("system:user:view", "查看用户", userMenu),
                permission("system:user:create", "创建用户", userMenu),
                permission("system:user:update", "修改用户", userMenu),
                permission("system:user:disable", "禁用用户", userMenu),
                permission("system:role:view", "查看角色", roleMenu),
                permission("system:role:create", "创建角色", roleMenu),
                permission("system:role:update", "修改角色", roleMenu),
                permission("system:role:delete", "删除角色", roleMenu),
                permission("system:role:assign", "分配权限", roleMenu),
                permission("system:menu:view", "查看菜单", menuMenu),
                permission("system:menu:create", "创建菜单", menuMenu),
                permission("system:menu:update", "修改菜单", menuMenu),
                permission("system:menu:delete", "删除菜单", menuMenu),
                permission("system:audit:view", "查看操作日志", auditMenu),
                permission("system:session:view", "查看在线会话", sessionMenu),
                permission("system:session:kickout", "强制下线", sessionMenu),
                permission("bot:instance:view", "查看机器人实例", botManagementMenu),
                permission("bot:instance:create", "创建机器人实例", botManagementMenu),
                permission("bot:instance:update", "修改机器人实例", botManagementMenu),
                permission("bot:instance:delete", "删除机器人实例", botManagementMenu),
                permission("bot:instance:probe", "获取 OneBot 登录账号", botManagementMenu),
                permission("add", "新增", buttonAuthMenu),
                permission("edit", "编辑", buttonAuthMenu),
                permission("delete", "删除", buttonAuthMenu),
                permission("export", "导出", buttonAuthMenu),
                permission("view", "查看", buttonAuthMenu),
                permission("publish", "发布", buttonAuthMenu),
                permission("config", "配置", buttonAuthMenu),
                permission("manage", "管理", buttonAuthMenu)
        );
    }

    private SysRole role(String code, String name, String description, DataScope dataScope) {
        SysRole role = roleRepository.findByCode(code).orElseGet(SysRole::new);
        role.setCode(code);
        role.setName(name);
        role.setDescription(description);
        role.setEnabled(true);
        role.setDataScope(dataScope);
        return roleRepository.save(role);
    }

    private SysMenu menu(
            Long parentId,
            String name,
            String path,
            String component,
            String title,
            String icon,
            int sortOrder,
            boolean keepAlive
    ) {
        SysMenu menu = menuRepository.findByName(name).orElseGet(SysMenu::new);
        menu.setParentId(parentId);
        menu.setName(name);
        menu.setPath(path);
        menu.setComponent(component);
        menu.setTitle(title);
        menu.setIcon(icon);
        menu.setSortOrder(sortOrder);
        menu.setKeepAlive(keepAlive);
        menu.setFixedTab(false);
        menu.setHideTab(false);
        menu.setFullPage(false);
        menu.setIframe(false);
        menu.setLink(null);
        menu.setActivePath(null);
        menu.setTextBadge(null);
        menu.setVisible(true);
        menu.setEnabled(true);
        return menuRepository.save(menu);
    }

    private SysMenu externalMenu(String name, String title, String icon, String link, int sortOrder) {
        SysMenu menu = menu(null, name, "", null, title, icon, sortOrder, false);
        menu.setLink(link);
        return menu;
    }

    private SysMenu fullPage(SysMenu menu) {
        menu.setHideTab(true);
        menu.setFullPage(true);
        return menu;
    }

    private void hide(SysMenu menu) {
        menu.setVisible(false);
    }

    private SysPermission permission(String code, String name, SysMenu menu) {
        SysPermission permission = permissionRepository.findByCode(code).orElseGet(SysPermission::new);
        permission.setCode(code);
        permission.setName(name);
        permission.setType(PermissionType.BUTTON);
        permission.setMenuId(menu.getId());
        permission.setEnabled(true);
        return permissionRepository.save(permission);
    }

    private SysMenu findMenu(List<SysMenu> menus, String name) {
        return menus.stream()
                .filter(menu -> name.equals(menu.getName()))
                .findFirst()
                .orElseThrow();
    }

    private static final Set<String> SUPER_ADMIN_ONLY_MENUS = Set.of(
            "Role",
            "Menus",
            "OperationLog",
            "OnlineSession",
            "PermissionPageVisibility"
    );
}
