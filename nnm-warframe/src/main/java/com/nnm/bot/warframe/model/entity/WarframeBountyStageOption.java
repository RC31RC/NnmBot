package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class WarframeBountyStageOption {
    @Column(name = "stage_number", nullable = false)
    private int stageNumber;
    @Column(name = "option_number", nullable = false)
    private int optionNumber;
    @Column(name = "job_reference", nullable = false, length = 500)
    private String jobReference;
}
