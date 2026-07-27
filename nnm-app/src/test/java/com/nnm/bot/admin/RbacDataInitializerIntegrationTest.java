package com.nnm.bot.admin;

import com.nnm.bot.admin.config.AdminBootstrapProperties;
import com.nnm.bot.admin.config.RbacDataInitializer;
import com.nnm.bot.admin.domain.SysMenu;
import com.nnm.bot.admin.domain.SysRole;
import com.nnm.bot.admin.repository.SysMenuRepository;
import com.nnm.bot.admin.repository.SysRoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:nnm-rbac-upgrade;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.default_schema=PUBLIC",
        "spring.flyway.enabled=false",
        "nnm.admin.bootstrap.enabled=false",
        "nnm.warframe.polling.enabled=false",
        "shiro.ws.server.enable=false",
        "shiro.ws.client.enable=false"
})
class RbacDataInitializerIntegrationTest {

    private static final List<String> ROOT_MENU_NAMES = List.of(
            "Dashboard",
            "System",
            "Template",
            "Widgets",
            "Examples",
            "Article",
            "Result",
            "Exception",
            "Safeguard",
            "Document",
            "LiteVersion",
            "OldVersion",
            "ChangeLog"
    );

    @Autowired
    private RbacDataInitializer initializer;

    @Autowired
    private AdminBootstrapProperties bootstrapProperties;

    @Autowired
    private SysMenuRepository menuRepository;

    @Autowired
    private SysRoleRepository roleRepository;

    @Test
    @Transactional
    void addsMissingMenusToAnExistingRbacDatabase() throws Exception {
        SysMenu dashboard = saveLegacyMenu(null, "Dashboard", "/dashboard", 100);
        saveLegacyMenu(dashboard.getId(), "Console", "console", 101);
        SysMenu system = saveLegacyMenu(null, "System", "/system", 200);
        saveLegacyMenu(system.getId(), "User", "user", 201);
        saveLegacyMenu(system.getId(), "Role", "role", 202);
        saveLegacyMenu(system.getId(), "Menus", "menu", 203);
        saveLegacyMenu(system.getId(), "OperationLog", "audit", 204);
        saveLegacyMenu(system.getId(), "OnlineSession", "session", 205);
        saveLegacyMenu(system.getId(), "Nested", "nested", 206);
        Long existingDashboardId = dashboard.getId();

        bootstrapProperties.setEnabled(true);
        bootstrapProperties.setUsername("admin");
        bootstrapProperties.setPassword("admin123");
        initializer.run(null);

        List<SysMenu> menus = menuRepository.findAllByOrderBySortOrderAscIdAsc();
        assertThat(menus.stream()
                .filter(menu -> menu.getParentId() == null)
                .map(SysMenu::getName)
                .toList()).containsExactlyElementsOf(ROOT_MENU_NAMES);
        assertThat(menuRepository.findByName("Dashboard").orElseThrow().getId())
                .isEqualTo(existingDashboardId);
        assertThat(menus)
                .extracting(SysMenu::getName)
                .contains(
                        "Ecommerce",
                        "PermissionButtonAuth",
                        "ArticleDetail",
                        "Exception500",
                        "SafeguardServer"
                );

        SysRole superAdmin = roleRepository.findByCode("R_SUPER").orElseThrow();
        assertThat(superAdmin.getMenus()).hasSameSizeAs(menus);
        assertThat(superAdmin.getPermissions())
                .extracting(permission -> permission.getCode())
                .contains("system:user:view", "system:menu:delete", "add", "manage");
    }

    private SysMenu saveLegacyMenu(Long parentId, String name, String path, int sortOrder) {
        SysMenu menu = new SysMenu();
        menu.setParentId(parentId);
        menu.setName(name);
        menu.setPath(path);
        menu.setComponent("/legacy/component");
        menu.setTitle("legacy." + name);
        menu.setSortOrder(sortOrder);
        menu.setVisible(true);
        menu.setEnabled(true);
        return menuRepository.save(menu);
    }
}
