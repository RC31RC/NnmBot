package com.nnm.bot.admin.repository;

import com.nnm.bot.admin.domain.SysUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface SysUserRepository extends JpaRepository<SysUser, Long> {

    @EntityGraph(attributePaths = {"roles", "roles.permissions", "roles.menus"})
    Optional<SysUser> findByUsername(String username);

    @EntityGraph(attributePaths = {"roles", "roles.permissions", "roles.menus"})
    Optional<SysUser> findByUsernameIgnoreCase(String username);

    @EntityGraph(attributePaths = {"roles", "roles.permissions", "roles.menus"})
    @Query("select user from SysUser user where user.id = :id")
    Optional<SysUser> findByIdWithAuthorities(@Param("id") Long id);

    boolean existsByIdAndEnabledTrue(Long id);

    boolean existsByUsernameIgnoreCase(String username);

    @EntityGraph(attributePaths = "roles")
    List<SysUser> findAllByRoles_Id(Long roleId);

    @EntityGraph(attributePaths = "roles")
    Page<SysUser> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    @EntityGraph(attributePaths = "roles")
    Page<SysUser> findAllBy(Pageable pageable);
}
