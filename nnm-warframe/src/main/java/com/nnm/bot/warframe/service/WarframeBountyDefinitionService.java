package com.nnm.bot.warframe.service;

import com.nnm.bot.warframe.pojo.WarframeBountyDefinitionDetails;

import java.util.Optional;

public interface WarframeBountyDefinitionService {
    Optional<WarframeBountyDefinitionDetails> find(String jobType);
}
