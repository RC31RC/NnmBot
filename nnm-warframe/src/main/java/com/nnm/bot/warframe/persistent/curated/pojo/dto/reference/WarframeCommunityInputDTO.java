package com.nnm.bot.warframe.persistent.curated.pojo.dto.reference;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;

/** 社区人工资料的类型化字段合同；输入渠道不是记录来源类别。 */
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({@JsonSubTypes.Type(WarframeCommunityRecordDTO.AliasDTO.class),
    @JsonSubTypes.Type(WarframeCommunityRecordDTO.TermDTO.class),
    @JsonSubTypes.Type(WarframeCommunityRecordDTO.EssenceDTO.class),
    @JsonSubTypes.Type(WarframeCommunityRecordDTO.RivenAliasDTO.class),
    @JsonSubTypes.Type(WarframeCommunityRecordDTO.ReferenceMappingDTO.class),
    @JsonSubTypes.Type(WarframeCommunityRecordDTO.DescendiaRewardDTO.class)})
public sealed interface WarframeCommunityInputDTO permits WarframeCommunityRecordDTO.AliasDTO,
    WarframeCommunityRecordDTO.TermDTO, WarframeCommunityRecordDTO.EssenceDTO, WarframeCommunityRecordDTO.RivenAliasDTO,
    WarframeCommunityRecordDTO.ReferenceMappingDTO, WarframeCommunityRecordDTO.DescendiaRewardDTO {}
