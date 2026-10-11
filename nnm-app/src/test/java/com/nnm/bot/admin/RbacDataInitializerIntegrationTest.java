package com.nnm.bot.admin;

import io.github.rdt.lock.DistributedLockManager;
import io.github.rdt.cache.CacheService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.nnm.bot.admin.config.properties.AdminBootstrapProperties;
import com.nnm.bot.admin.bootstrap.RbacDataInitializer;
import com.nnm.bot.admin.constant.DataScope;
import com.nnm.bot.admin.domain.SysRole;
import com.nnm.bot.admin.repository.SysMenuRepository;
import com.nnm.bot.admin.repository.SysRoleRepository;
import com.nnm.bot.admin.repository.SysUserRepository;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:nnm-rbac-bootstrap;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.default_schema=PUBLIC",
        "nnm.admin.bootstrap.enabled=false",
        "nnm.warframe.polling.enabled=false",
        "shiro.ws.server.enable=false",
        "shiro.ws.client.enable=false"
})
@MockitoBean(types = {DistributedLockManager.class, CacheService.class})
class RbacDataInitializerIntegrationTest {

    @Resource
    private RbacDataInitializer initializer;

    @Resource
    private AdminBootstrapProperties bootstrapProperties;

    @Resource
    private SysMenuRepository menuRepository;

    @Resource
    private SysRoleRepository roleRepository;

    @Resource
    private SysUserRepository userRepository;

    @Resource
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clearBootstrapData() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void createsOnlyTheMissingBootstrapAdministrator() throws Exception {
        SysRole superAdmin = new SysRole();
        superAdmin.setCode("R_SUPER");
        superAdmin.setName("超级管理员");
        superAdmin.setDescription("拥有全部管理权限");
        superAdmin.setEnabled(true);
        superAdmin.setAuthorityLevel(100);
        superAdmin.setDataScope(DataScope.ALL);
        roleRepository.saveAndFlush(superAdmin);

        bootstrapProperties.setEnabled(true);
        bootstrapProperties.setUsername("admin");
        bootstrapProperties.setPassword("admin123");
        initializer.run(null);

        var admin = userRepository.findByUsername("admin").orElseThrow();
        assertThat(admin.getRoles()).extracting(SysRole::getCode).containsExactly("R_SUPER");
        assertThat(passwordEncoder.matches("admin123", admin.getPassword())).isTrue();
        assertThat(menuRepository.count()).isZero();

        initializer.run(null);
        assertThat(userRepository.count()).isOne();
    }

    @Test
    void reportsMissingDatabaseSeedData() {
        bootstrapProperties.setEnabled(true);
        bootstrapProperties.setUsername("admin");
        bootstrapProperties.setPassword("admin123");

        assertThatThrownBy(() -> initializer.run(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("docs/sql/init.sql");
    }
}
