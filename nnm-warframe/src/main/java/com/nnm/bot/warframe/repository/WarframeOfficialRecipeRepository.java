package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.model.entity.WarframeOfficialRecipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarframeOfficialRecipeRepository
        extends JpaRepository<WarframeOfficialRecipe, Long> {
}
