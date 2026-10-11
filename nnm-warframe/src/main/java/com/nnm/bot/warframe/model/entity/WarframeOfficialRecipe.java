package com.nnm.bot.warframe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** PublicExport 蓝图和配方结构化字段。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_recipe")
public class WarframeOfficialRecipe {

    @Id
    @Column(name = "official_record_id")
    private Long officialRecordId;

    @Column(name = "result_type", length = 500)
    private String resultType;
    @Column(name = "build_price")
    private Integer buildPrice;
    @Column(name = "build_time")
    private Integer buildTime;
    @Column(name = "consume_on_use")
    private Boolean consumeOnUse;
    @Column(name = "skip_build_time_price")
    private Integer skipBuildTimePrice;
    private Integer num;
    @Column(name = "prime_selling_price")
    private Integer primeSellingPrice;
    @Column(name = "always_available")
    private Boolean alwaysAvailable;
    @Column(name = "ingredients_json", columnDefinition = "TEXT")
    private String ingredientsJson;
    @Column(name = "secret_ingredients_json", columnDefinition = "TEXT")
    private String secretIngredientsJson;
}
