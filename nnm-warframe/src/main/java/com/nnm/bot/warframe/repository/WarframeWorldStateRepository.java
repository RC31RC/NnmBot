package com.nnm.bot.warframe.repository;

import com.nnm.bot.warframe.model.entity.WarframeWorldStateRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WarframeWorldStateRepository<T extends WarframeWorldStateRecord>
        extends JpaRepository<T, Long> {
    List<T> findAllByEnabledTrue();
}
