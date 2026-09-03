package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.domain.entity.WarframeOfficialWeapon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarframeOfficialWeaponRepository
        extends JpaRepository<WarframeOfficialWeapon, Long> {
}
