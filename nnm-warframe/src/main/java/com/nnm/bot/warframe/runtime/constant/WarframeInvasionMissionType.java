package com.nnm.bot.warframe.runtime.constant;

/** oracle 的任务简称对应官方已入库的任务类型代码；未知值不强行翻译。 */
public enum WarframeInvasionMissionType {
    ASSASSINATION("Assassination", "MT_ASSASSINATION"), CAPTURE("Capture", "MT_CAPTURE"),
    DEFENSE("Defense", "MT_DEFENSE"), EXTERMINATE("Exterminate", "MT_EXTERMINATION"),
    MOBILE_DEFENSE("MobileDefense", "MT_MOBILE_DEFENSE"), SABOTAGE("Sabotage", "MT_SABOTAGE"), SPY("Spy", "MT_INTEL"),
    SURVIVAL("Survival", "MT_SURVIVAL"), RESCUE("Rescue", "MT_RESCUE");

    private final String upstream;
    private final String official;

    WarframeInvasionMissionType(String upstream, String official) {
        this.upstream = upstream;
        this.official = official;
    }

    /** @param value oracle 返回的任务代码；@return 官方任务类型代码，未知值原样保留。 */
    public static String officialCode(String value) {
        for (WarframeInvasionMissionType mission : values())
            if (mission.upstream.equals(value))
                return mission.official;
        return value;
    }
}
