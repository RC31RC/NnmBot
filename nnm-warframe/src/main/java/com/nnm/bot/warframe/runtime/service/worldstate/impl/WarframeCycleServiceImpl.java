package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.CambionCycleDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.CetusCycleDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.DuviriCycleDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.EarthCycleDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.VallisCycleDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.ZarimanCycleDTO;
import com.nnm.bot.warframe.runtime.config.worldstate.WarframeCycleProperties;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeCycleService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

@Service
public class WarframeCycleServiceImpl implements WarframeCycleService {
    @Resource
    private WarframeCycleProperties properties;
    @Resource
    private Clock warframeClock;

    /**
     * 校验当前配置或输入是否满足业务约束。
     */
    @PostConstruct
    void validate() { properties.validate(); }

    /**
     * 解析引用或输入对应的实际业务对象。
     *
     * @param state 已经解析的当前世界状态。
     * @return 已经解析并校验的世界状态快照。
     */
    @Override
    public WorldStateDTO resolve(WorldStateDTO state) {
        if (state == null) return null;
        Instant now = warframeClock.instant();
        Instant anchor = state.syndicateMissions().stream()
                .filter(mission -> "CetusSyndicate".equals(mission.syndicate()))
                .map(WorldStateDTO.SyndicateMissionDTO::expiry)
                .filter(Objects::nonNull).max(Instant::compareTo).orElse(null);
        CetusCycleDTO cetus = state.cetusCycle();
        if (cetus == null || !valid(cetus.expiry(), now)) {
            cetus = WarframeCycleCalculator.cetus(now, anchor, properties.getCetusAnchorMaxAge());
        }
        CambionCycleDTO cambion = state.cambionCycle();
        if (cambion == null || !valid(cambion.expiry(), now)) {
            cambion = cetus == null ? null : new WorldStateDTO.CambionCycleDTO(
                    cetus.isDay() ? "fass" : "vome", cetus.expiry());
        }
        ZarimanCycleDTO zariman = state.zarimanCycle();
        if (zariman == null || !valid(zariman.expiry(), now)) {
            zariman = WarframeCycleCalculator.zariman(
                    now, anchor, properties.getZarimanBountyOffset(),
                    properties.getZarimanCorpusEpoch(), properties.getCetusAnchorMaxAge());
        }
        DuviriCycleDTO duviri = state.duviriCycle();
        if (duviri == null || !valid(duviri.expiry(), now)) {
            duviri = WarframeCycleCalculator.duviri(now, properties.getDuviriEpoch());
        }
        EarthCycleDTO earth = state.earthCycle();
        if (earth == null || !valid(earth.expiry(), now)) earth = WarframeCycleCalculator.earth(now);
        VallisCycleDTO vallis = state.vallisCycle();
        if (vallis == null || !valid(vallis.expiry(), now)) {
            vallis = WarframeCycleCalculator.vallis(now, properties.getVallisWarmEpoch());
        }
        return state.withLocalCycles(cetus, vallis, cambion, earth, duviri, zariman);
    }

    /**
     * 判断是否满足有效条件。
     *
     * @param expiry 到期。
     * @param now 本次操作使用的当前时间。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private boolean valid(Instant expiry, Instant now) {
        return expiry != null && expiry.isAfter(now);
    }
}
