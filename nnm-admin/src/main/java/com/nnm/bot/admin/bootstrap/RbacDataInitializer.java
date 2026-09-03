package com.nnm.bot.admin.bootstrap;

import com.nnm.bot.admin.config.AdminBootstrapProperties;
import com.nnm.bot.admin.domain.SysRole;
import com.nnm.bot.admin.domain.SysUser;
import com.nnm.bot.admin.repository.SysRoleRepository;
import com.nnm.bot.admin.repository.SysUserRepository;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates only the first administrator account when explicitly enabled.
 * RBAC menus, permissions and roles are maintained by the database seed SQL.
 */
@Component
public class RbacDataInitializer implements ApplicationRunner {

    @Resource
    private SysUserRepository userRepository;
    @Resource
    private SysRoleRepository roleRepository;
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

        SysRole superAdmin = roleRepository.findByCode("R_SUPER")
                .orElseThrow(() -> new IllegalStateException(
                        "RBAC seed data is missing: execute docs/sql/01_init.sql first"));

        String username = bootstrapProperties.getUsername();
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(bootstrapProperties.getPassword()));
        user.setEnabled(true);
        user.getRoles().add(superAdmin);
        userRepository.save(user);
    }
}
