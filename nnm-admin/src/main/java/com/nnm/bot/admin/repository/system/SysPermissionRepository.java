package com.nnm.bot.admin.repository.system;

import com.nnm.bot.admin.pojo.entity.system.SysPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** 查询角色绑定的稳定权限码集合。 */
public interface SysPermissionRepository extends JpaRepository<SysPermission, Long> {

    Optional<SysPermission> findByCode(String code);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByMenuId(Long menuId);

    List<SysPermission> findAllByMenuIdOrderByCodeAsc(Long menuId);

    List<SysPermission> findAllByOrderByCodeAsc();
}
