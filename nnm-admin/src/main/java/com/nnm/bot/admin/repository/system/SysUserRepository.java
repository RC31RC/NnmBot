package com.nnm.bot.admin.repository.system;

import com.nnm.bot.admin.pojo.entity.system.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SysUserRepository extends JpaRepository<SysUser, Long> {

    @EntityGraph(SysUser.GRAPH_AUTHORITIES)
    Optional<SysUser> findByUsername(String username);

    @EntityGraph(SysUser.GRAPH_AUTHORITIES)
    Optional<SysUser> findByUsernameIgnoreCase(String username);

    @EntityGraph(SysUser.GRAPH_AUTHORITIES)
    Optional<SysUser> findWithAuthoritiesById(Long id);

    @EntityGraph(SysUser.GRAPH_ROLES)
    Optional<SysUser> findWithRolesById(Long id);

    boolean existsByIdAndEnabledTrue(Long id);

    boolean existsByUsernameIgnoreCase(String username);

    @EntityGraph(SysUser.GRAPH_ROLES)
    List<SysUser> findAllByRoles_Id(Long roleId);

    Page<IdOnly> findPageBy(Pageable pageable);

    Page<IdOnly> findPageByUsernameContainingIgnoreCase(String username, Pageable pageable);

    @EntityGraph(SysUser.GRAPH_ROLES)
    List<SysUser> findPageDetailsByIdIn(Collection<Long> ids);

    @EntityGraph(SysUser.GRAPH_ROLES)
    List<SysUser> findAllWithRolesByIdIn(Collection<Long> ids);

    interface IdOnly {

        Long getId();
    }
}
