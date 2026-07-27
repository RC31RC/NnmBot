package com.nnm.bot.admin.repository;

import com.nnm.bot.admin.domain.SysMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SysMenuRepository extends JpaRepository<SysMenu, Long> {

    Optional<SysMenu> findByName(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByParentId(Long parentId);

    List<SysMenu> findAllByOrderBySortOrderAscIdAsc();

    List<SysMenu> findAllByEnabledTrueOrderBySortOrderAscIdAsc();
}
