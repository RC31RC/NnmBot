package com.nnm.bot.admin.repository.system;

import com.nnm.bot.admin.pojo.entity.system.SysUserIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface SysUserIdentityRepository extends JpaRepository<SysUserIdentity, Long> {

    Optional<SysUserIdentity> findByPlatformAndExternalUserId(String platform,
                                                                String externalUserId);

    Optional<SysUserIdentity> findFirstByPlatformInAndExternalUserIdOrderByIdAsc(
            Collection<String> platforms, String externalUserId);
}
