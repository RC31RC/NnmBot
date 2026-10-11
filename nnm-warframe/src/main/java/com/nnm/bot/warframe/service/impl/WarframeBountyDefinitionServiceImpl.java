package com.nnm.bot.warframe.service.impl;

import com.nnm.bot.warframe.application.WarframeCommunityDatasetProjectionMapper;
import com.nnm.bot.warframe.constant.WarframeCommunityRecordSource;
import com.nnm.bot.warframe.constant.WarframeCommunitySource;
import com.nnm.bot.warframe.model.entity.WarframeCommunityBountyDefinition;
import com.nnm.bot.warframe.pojo.WarframeBountyDefinitionDetails;
import com.nnm.bot.warframe.repository.WarframeBountyDefinitionRepository;
import com.nnm.bot.warframe.service.WarframeBountyDefinitionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Optional;

@Service
public class WarframeBountyDefinitionServiceImpl implements WarframeBountyDefinitionService {
    @Resource
    private WarframeBountyDefinitionRepository repository;
    @Resource
    private WarframeCommunityDatasetProjectionMapper mapper;

    @Override
    public Optional<WarframeBountyDefinitionDetails> find(String jobType) {
        // 本业务仅采纳 plus 定义；其他翻译来源的展示顺序待用户决定，不跨源拼阶段。
        return repository.findAllByEnabledTrueAndRecordKey(jobType).stream()
                .filter(value -> value.getRecordSource() == WarframeCommunityRecordSource.SELF_MAINTAINED
                        || WarframeCommunitySource.PLUS_BOUNTIES.code().equals(value.getSourceCode()))
                .min(Comparator.comparing(value ->
                        value.getRecordSource() != WarframeCommunityRecordSource.SELF_MAINTAINED))
                .map(this::details);
    }

    private WarframeBountyDefinitionDetails details(WarframeCommunityBountyDefinition value) {
        return (WarframeBountyDefinitionDetails) mapper.details(value);
    }
}
