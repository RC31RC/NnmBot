package com.nnm.bot.admin.repository.system;

import com.nnm.bot.admin.pojo.entity.system.SysRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SysRoleRepository extends JpaRepository<SysRole, Long> {

    Optional<SysRole> findByCode(String code);

    @EntityGraph(attributePaths = {"menus", "permissions"})
    Optional<SysRole> findWithGrantsById(Long id);

    boolean existsByCodeIgnoreCase(String code);

    Page<SysRole> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
            String name,
            String code,
            Pageable pageable
    );
}
