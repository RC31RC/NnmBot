package com.nnm.bot.warframe.runtime.pojo.vo.riven;

import cn.hutool.core.collection.CollUtil;
import java.util.List;
import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.WarframeRivenAttributeDTO;

/**
 * 紫卡结构化分析结果；不确定字段必须显式返回。
 *
 * @param rawText 原始 OCR 或命令文本
 * @param rawLines 原始逐行证据
 * @param weaponName 已确认的武器名称，未确认时为空
 * @param weaponType 来源提供的武器类别，不从名称推断
 * @param weaponCandidates 名称歧义候选
 * @param weaponConfirmed 是否已命中明确武器记录
 * @param rank 卡片升级等级，与段位需求独立
 * @param masteryRank 紫卡使用段位需求，缺失时为 null
 * @param attributes 已识别属性及 OCR 数值
 * @param negativeAny 是否包含任意负面的筛选条件
 * @param trends 已采纳的属性参考基准，不是最终数值评级
 * @param lineAnalyses 每行属性识别证据
 * @param missingFields 待确认字段编码
 * @param referenceRanges 按参考项目规则计算的范围，不表示现行版本合法性或价值评级
 */
public record WarframeRivenAnalysisVO(
        String rawText,
        List<String> rawLines,
        String weaponName,
        String weaponType,
        List<String> weaponCandidates,
        boolean weaponConfirmed,
        Integer rank,
        Integer masteryRank,
        List<AttributeVO> attributes,
        boolean negativeAny,
        List<TrendVO> trends,
        List<LineAnalysisVO> lineAnalyses,
        List<String> missingFields,
        List<ReferenceRangeVO> referenceRanges
) {

    /**
     * 防御性复制结果，避免异步渲染过程中被外部修改。
     *
     * @param rawText 原始文本。
     * @param rawLines 原始行集合。
     * @param weaponName 武器名称。
     * @param weaponType 武器类型。
     * @param weaponCandidates 武器候选集合。
     * @param weaponConfirmed 武器已确认。
     * @param rank 等级。
     * @param masteryRank 段位等级。
     * @param attributes 属性集合。
     * @param negativeAny 负面任意。
     * @param trends 趋势集合。
     * @param lineAnalyses 行分析结果集合。
     * @param missingFields 缺失字段集合。
     * @param referenceRanges 引用范围集合。
     */
    public WarframeRivenAnalysisVO {
        rawText = rawText == null ? "" : rawText;
        rawLines = rawLines == null ? List.of() : List.copyOf(rawLines);
        weaponName = weaponName == null ? "" : weaponName;
        weaponType = weaponType == null ? "" : weaponType;
        weaponCandidates = weaponCandidates == null ? List.of() : List.copyOf(weaponCandidates);
        attributes = attributes == null ? List.of() : List.copyOf(attributes);
        trends = trends == null ? List.of() : List.copyOf(trends);
        lineAnalyses = lineAnalyses == null ? List.of() : List.copyOf(lineAnalyses);
        missingFields = missingFields == null ? List.of() : List.copyOf(missingFields);
        referenceRanges = referenceRanges == null ? List.of() : List.copyOf(referenceRanges);
    }

    /**
     * 保留未计算区间的调用签名，历史结果不能被默认描述成已验证。
     *
     * @param rawText 原始文本。
     * @param rawLines 原始行集合。
     * @param weaponName 武器名称。
     * @param weaponType 武器类型。
     * @param weaponCandidates 武器候选集合。
     * @param weaponConfirmed 武器已确认。
     * @param rank 等级。
     * @param masteryRank 段位等级。
     * @param attributes 属性集合。
     * @param negativeAny 负面任意。
     * @param trends 趋势集合。
     * @param lineAnalyses 行分析结果集合。
     * @param missingFields 缺失字段集合。
     */
    public WarframeRivenAnalysisVO(
            String rawText, List<String> rawLines, String weaponName, String weaponType,
            List<String> weaponCandidates, boolean weaponConfirmed, Integer rank, Integer masteryRank,
            List<AttributeVO> attributes, boolean negativeAny, List<TrendVO> trends,
            List<LineAnalysisVO> lineAnalyses, List<String> missingFields) {
        this(rawText, rawLines, weaponName, weaponType, weaponCandidates, weaponConfirmed,
                rank, masteryRank, attributes, negativeAny, trends, lineAnalyses, missingFields, List.of());
    }

    /**
     * 只表示必需字段齐全，不代表 OCR 数值合法性已经验证。
     *
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    public boolean complete() {
        return CollUtil.isEmpty(missingFields);
    }

    /**
     * @param code 正式编码，组合别名用逗号连接
     * @param displayName 正式显示名称或组合别名
     * @param value 原始数学数值，未知为 null
     * @param negative 依据来源定义和明确符号识别的负面效果
     * @param compound 是否是来源定义的组合属性
     * @param sourceLine 原始证据行
     * @param valueStatus OCR_ONLY 表示仅识别数值，UNKNOWN 表示未确定数值
     * @param observedUnit 数值片段中的 percent / seconds / scalar / multiplier，不从词条猜测
     */
    public record AttributeVO(
            String code,
            String displayName,
            Double value,
            boolean negative,
            boolean compound,
            String sourceLine,
            String valueStatus,
            String observedUnit) {
        /**
         * 缺证据或状态时采用空证据与未知值状态。
         *
         * @param code 当前数据集或业务对象的稳定代码。
         * @param displayName 展示名称。
         * @param value 待处理的字段值。
         * @param negative 负面。
         * @param compound 组合。
         * @param sourceLine 来源行。
         * @param valueStatus 值状态。
         * @param observedUnit 实际观测单位。
         */
        public AttributeVO {
            sourceLine = sourceLine == null ? "" : sourceLine;
            valueStatus = valueStatus == null ? "UNKNOWN" : valueStatus;
            observedUnit = observedUnit == null ? "unknown" : observedUnit;
        }

        /**
         * 旧调用没有单位证据，不据此计算参考范围。
         *
         * @param code 当前数据集或业务对象的稳定代码。
         * @param displayName 展示名称。
         * @param value 待处理的字段值。
         * @param negative 负面。
         * @param compound 组合。
         * @param sourceLine 来源行。
         * @param valueStatus 值状态。
         */
        public AttributeVO(String code, String displayName, Double value, boolean negative,
                boolean compound, String sourceLine, String valueStatus) {
            this(code, displayName, value, negative, compound, sourceLine, valueStatus, "unknown");
        }
    }

    /**
     * @param sourceLine 原始证据行
     * @param kind 行分析类型，如 ATTRIBUTE
     * @param attributeCodes 本行明确识别的编码
     * @param valueStatus PARTIAL 表示至少一个属性数值未确定
     */
    public record LineAnalysisVO(
            String sourceLine,
            String kind,
            List<String> attributeCodes,
            String valueStatus) {
        /**
         * 行证据不可变，空值保持为未知状态。
         *
         * @param sourceLine 来源行。
         * @param kind 字段在当前记录中的实际类型。
         * @param attributeCodes 属性代码集合。
         * @param valueStatus 值状态。
         */
        public LineAnalysisVO {
            sourceLine = sourceLine == null ? "" : sourceLine;
            kind = kind == null ? "UNKNOWN" : kind;
            attributeCodes = attributeCodes == null ? List.of() : List.copyOf(attributeCodes);
            valueStatus = valueStatus == null ? "UNKNOWN" : valueStatus;
        }
    }

    /**
     * @param displayName 参考属性名称
     * @param weaponType 已确认的基准类别，未知为空
     * @param parameters 官方原始参数，语义随每项保留，不冒充卡面基准
     */
    public record TrendVO(
            String displayName,
            String weaponType,
            List<WarframeRivenAttributeDTO.ParameterDTO> parameters) {
    }

    /**
     * @param code 对应的单一正式属性编码
     * @param displayName 属性显示名
     * @param units 已采纳定义的单位
     * @param lower 数学意义的下限，未知为 null
     * @param upper 数学意义的上限，未知为 null
     * @param deviationPercent 相对区间中值的幅度偏差，不是品质等级
     * @param status IN_RANGE / OUT_OF_RANGE / UNAVAILABLE，仅与本参考区间比较
     * @param reason 未计算原因编码
     * @param ruleVersion 明确的参考规则版本，不冒充游戏当前版本
     * @param weaponKey 完整武器路径，用于确认武器类别
     * @param weaponSource 武器类别所取完整记录的来源码
     * @param baselineSource 属性基准的来源码
     */
    public record ReferenceRangeVO(
            String code, String displayName, String units, Double lower, Double upper,
            Double deviationPercent, String status, String reason, String ruleVersion,
            String weaponKey, String weaponSource, String baselineSource) {
    }
}
