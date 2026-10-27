package com.nnm.bot.warframe.persistent.official.repository.snapshot;

import com.nnm.bot.warframe.persistent.official.pojo.entity.snapshot.WarframeOfficialFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarframeOfficialFileRepository
        extends JpaRepository<WarframeOfficialFile, String> {
}
