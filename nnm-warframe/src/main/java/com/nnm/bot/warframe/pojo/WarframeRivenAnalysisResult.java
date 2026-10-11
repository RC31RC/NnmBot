package com.nnm.bot.warframe.pojo;

import java.util.List;

/** 紫卡结构化分析结果；不确定字段必须显式返回，禁止用 OCR 原文伪造结论。 */
public record WarframeRivenAnalysisResult(
        String rawText,
        List<String> rawLines,
        String weaponName,
        String weaponType,
        List<String> weaponCandidates,
        boolean weaponConfirmed,
        Integer rank,
        Integer masteryRank,
        List<Attribute> attributes,
        boolean negativeAny,
        List<Trend> trends,
        List<LineAnalysis> lineAnalyses,
        List<String> missingFields
) {

    public WarframeRivenAnalysisResult {
        rawText = rawText == null ? "" : rawText;
        rawLines = rawLines == null ? List.of() : List.copyOf(rawLines);
        weaponName = weaponName == null ? "" : weaponName;
        weaponType = weaponType == null ? "" : weaponType;
        weaponCandidates = weaponCandidates == null ? List.of() : List.copyOf(weaponCandidates);
        attributes = attributes == null ? List.of() : List.copyOf(attributes);
        trends = trends == null ? List.of() : List.copyOf(trends);
        lineAnalyses = lineAnalyses == null ? List.of() : List.copyOf(lineAnalyses);
        missingFields = missingFields == null ? List.of() : List.copyOf(missingFields);
    }

    public boolean complete() {
        return missingFields.isEmpty();
    }

    public record Attribute(
            String code,
            String displayName,
            Double value,
            boolean negative,
            boolean compound,
            String sourceLine,
            String valueStatus) {
        public Attribute {
            sourceLine = sourceLine == null ? "" : sourceLine;
            valueStatus = valueStatus == null ? "UNKNOWN" : valueStatus;
        }
    }

    public record LineAnalysis(
            String sourceLine,
            String kind,
            List<String> attributeCodes,
            String valueStatus) {
        public LineAnalysis {
            sourceLine = sourceLine == null ? "" : sourceLine;
            kind = kind == null ? "UNKNOWN" : kind;
            attributeCodes = attributeCodes == null ? List.of() : List.copyOf(attributeCodes);
            valueStatus = valueStatus == null ? "UNKNOWN" : valueStatus;
        }
    }

    public record Trend(
            String displayName,
            String weaponType,
            Double coefficient) {
    }
}
