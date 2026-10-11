package com.nnm.bot.warframe.service.impl;

import com.nnm.bot.warframe.application.WarframeCycleService;
import com.nnm.bot.warframe.config.properties.WarframeCycleProperties;
import com.nnm.bot.warframe.model.WorldState;
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

    @PostConstruct
    void validate() { properties.validate(); }

    @Override
    public WorldState resolve(WorldState state) {
        if (state == null) return null;
        Instant now = warframeClock.instant();
        Instant anchor = state.syndicateMissions().stream()
                .filter(mission -> "CetusSyndicate".equals(mission.syndicate()))
                .map(WorldState.SyndicateMission::expiry)
                .filter(Objects::nonNull).max(Instant::compareTo).orElse(null);
        WorldState.CetusCycle cetus = state.cetusCycle();
        if (cetus == null || !valid(cetus.expiry(), now)) {
            cetus = WarframeCycleCalculator.cetus(now, anchor, properties.getCetusAnchorMaxAge());
        }
        WorldState.CambionCycle cambion = state.cambionCycle();
        if (cambion == null || !valid(cambion.expiry(), now)) {
            cambion = cetus == null ? null : new WorldState.CambionCycle(
                    cetus.isDay() ? "fass" : "vome", cetus.expiry());
        }
        WorldState.ZarimanCycle zariman = state.zarimanCycle();
        if (zariman == null || !valid(zariman.expiry(), now)) {
            zariman = WarframeCycleCalculator.zariman(
                    now, anchor, properties.getZarimanBountyOffset(),
                    properties.getZarimanCorpusEpoch(), properties.getCetusAnchorMaxAge());
        }
        WorldState.DuviriCycle duviri = state.duviriCycle();
        if (duviri == null || !valid(duviri.expiry(), now)) {
            duviri = WarframeCycleCalculator.duviri(now, properties.getDuviriEpoch());
        }
        WorldState.EarthCycle earth = state.earthCycle();
        if (earth == null || !valid(earth.expiry(), now)) earth = WarframeCycleCalculator.earth(now);
        WorldState.VallisCycle vallis = state.vallisCycle();
        if (vallis == null || !valid(vallis.expiry(), now)) {
            vallis = WarframeCycleCalculator.vallis(now, properties.getVallisWarmEpoch());
        }
        return state.withLocalCycles(cetus, vallis, cambion, earth, duviri, zariman);
    }

    private boolean valid(Instant expiry, Instant now) {
        return expiry != null && expiry.isAfter(now);
    }
}
