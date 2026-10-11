package com.nnm.bot.warframe.repository;

import java.util.Optional;
import com.nnm.bot.warframe.model.entity.WarframeGroupWatchRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WarframeGroupWatchRuleRepository
        extends JpaRepository<WarframeGroupWatchRule, Long> {

    List<WarframeGroupWatchRule> findAllByEnabledTrue();

    List<WarframeGroupWatchRule> findAllByGroupIdAndEnabledTrue(Long groupId);

    List<WarframeGroupWatchRule> findAllByGroupIdAndMemberIdAndEnabledTrue(
            Long groupId,
            Long memberId);

    Optional<WarframeGroupWatchRule>
    findFirstByGroupIdAndMemberIdAndWatchTypeAndWatchKeyAndWatchValue(
            Long groupId, Long memberId, String watchType, String watchKey, String watchValue);

    List<WarframeGroupWatchRule>
    findAllByGroupIdAndMemberIdAndWatchTypeAndWatchValueAndEnabledTrue(
            Long groupId, Long memberId, String watchType, String watchValue);

    List<WarframeGroupWatchRule>
    findAllByGroupIdAndMemberIdAndWatchTypeAndWatchKeyAndWatchValueAndEnabledTrue(
            Long groupId, Long memberId, String watchType, String watchKey, String watchValue);

    @Modifying
    @Query("""
            update WarframeGroupWatchRule rule
               set rule.lastMatchKey = :matchKey,
                   rule.updatedAt = CURRENT_TIMESTAMP
             where rule.id = :ruleId
               and (rule.lastMatchKey is null or rule.lastMatchKey <> :matchKey)
            """)
    int updateLastMatchKeyIfChanged(
            @Param("ruleId")
            long ruleId,
            @Param("matchKey")
            String matchKey);
}
