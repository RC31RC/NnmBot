package com.nnm.bot.warframe.persistent.curated.service.worldstate.impl;

import cn.hutool.core.text.CharSequenceUtil;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate.SteelPathRotationDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate.SteelPathRotationRowDTO;
import com.nnm.bot.warframe.persistent.curated.repository.worldstate.WarframeCommunitySteelPathRotationRepository;
import com.nnm.bot.warframe.persistent.curated.service.worldstate.WarframeSteelPathRotationService;
import jakarta.annotation.Resource;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import org.springframework.stereotype.Service;

/** 按数据库维护的周一 UTC 锚点及顺序轮换；资料不完整时不猜当前商品。 */
@Service
public class WarframeSteelPathRotationServiceImpl implements WarframeSteelPathRotationService {
    private static final int ROTATION_WEEKS = 8;
    private static final int WEEK_DAYS = 7;
    @Resource
    private WarframeCommunitySteelPathRotationRepository repository;

    /** @param asOf 快照上游时间（无时间时为当前 UTC）；@return 已验证轮换，不完整时为空。 */
    @Override
    public SteelPathRotationDTO current(Instant asOf) {
        List<SteelPathRotationRowDTO> rows = repository.findAllProjectedByOrderByRotationIndexAsc();
        if (rows.size() != ROTATION_WEEKS || rows.getFirst().getAnchorWeek() == null)
            return null;
        LocalDate anchor = rows.getFirst().getAnchorWeek();
        for (int index = 0; index < rows.size(); index++) {
            SteelPathRotationRowDTO row = rows.get(index);
            if (row.getRotationIndex() == null || row.getRotationIndex() != index || !anchor.equals(row.getAnchorWeek())
                || row.getStoreItem() == null || CharSequenceUtil.isBlank(row.getStoreItem()))
                return null;
        }
        LocalDate monday = LocalDate.ofInstant(asOf == null ? Instant.now() : asOf, ZoneOffset.UTC)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        long weeks = ChronoUnit.WEEKS.between(anchor, monday);
        if (weeks < 0)
            return null;
        int index = Math.floorMod(weeks, rows.size());
        Instant activation = monday.atStartOfDay(ZoneOffset.UTC).toInstant();
        return new SteelPathRotationDTO(rows.get(index).getStoreItem(), index, activation,
            activation.plus(WEEK_DAYS, ChronoUnit.DAYS),
            rows.stream().map(SteelPathRotationRowDTO::getStoreItem).toList());
    }
}
