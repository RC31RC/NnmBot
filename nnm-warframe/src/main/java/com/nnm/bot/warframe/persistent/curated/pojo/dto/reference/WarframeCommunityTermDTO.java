package com.nnm.bot.warframe.persistent.curated.pojo.dto.reference;

import lombok.Getter;
import lombok.AllArgsConstructor;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class WarframeCommunityTermDTO implements WarframeCommunityRowDTO {
    private final Long id;
    private final String recordKey;
    private final Instant updatedAt;
    private final long version;
    private final String termGroup;
    private final String englishText;
    private final String chineseText;
}
