package com.nnm.bot.warframe.persistent.official.pojo.dto.record;

import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.MaintenanceType;
import java.time.LocalDateTime;

/** 单表闭合投影；读取名称索引时不加载原始 JSON 或受管实体。 */
public interface OfficialSummaryDTO {
    /**
     * 读取本地记录主键。
     *
     * @return 本地记录主键。
     */
    Long getId();

    /**
     * 读取记录维护状态。
     *
     * @return 记录维护状态。
     */
    MaintenanceType getMaintenanceType();

    /**
     * 读取稳定业务键。
     *
     * @return 稳定业务键。
     */
    String getBkey();

    /**
     * 读取官方原始名称。
     *
     * @return 官方原始名称。
     */
    String getOfficialName();

    /**
     * 读取中文名称。
     *
     * @return 中文名称。
     */
    String getLocalizedName();

    /**
     * 读取上游父项名称。
     *
     * @return 上游父项名称。
     */
    String getParentName();

    /**
     * 读取游戏物品分类。
     *
     * @return 游戏物品分类。
     */
    String getProductCategory();

    /**
     * 读取是否从资料库隐藏。
     *
     * @return 是否从资料库隐藏。
     */
    Boolean getExcludeFromCodex();

    /**
     * 读取说明文本。
     *
     * @return 说明文本。
     */
    String getDescription();

    /**
     * 读取详细说明。
     *
     * @return 详细说明。
     */
    String getLongDescription();

    /**
     * 读取百科页面标识。
     *
     * @return 百科页面标识。
     */
    String getWikiSlug();

    /**
     * 读取市场物品地址标识。
     *
     * @return 市场物品地址标识。
     */
    String getMarketSlug();

    /**
     * 判断当前功能或记录是否已经启用。
     *
     * @return 满足当前功能或记录是否已经启用时为真，否则为假。
     */
    boolean isEnabled();

    /**
     * 读取创建时间。
     *
     * @return 创建时间。
     */
    LocalDateTime getCreatedAt();

    /**
     * 读取更新时间。
     *
     * @return 更新时间。
     */
    LocalDateTime getUpdatedAt();

    /**
     * 将当前只读投影转换为非受管的数据对象。
     *
     * @return 游戏资料官方记录对象。
     */
    default WarframeOfficialRecordDTO toData() {
        WarframeOfficialRecordDTO data = new WarframeOfficialRecordDTO();
        data.setId(getId());
        data.setMaintenanceType(getMaintenanceType());
        data.setBkey(getBkey());
        data.setOfficialName(getOfficialName());
        data.setLocalizedName(getLocalizedName());
        data.setParentName(getParentName());
        data.setProductCategory(getProductCategory());
        data.setExcludeFromCodex(getExcludeFromCodex());
        data.setDescription(getDescription());
        data.setLongDescription(getLongDescription());
        data.setWikiSlug(getWikiSlug());
        data.setMarketSlug(getMarketSlug());
        data.setEnabled(isEnabled());
        data.setCreatedAt(getCreatedAt());
        data.setUpdatedAt(getUpdatedAt());
        return data;
    }
}
