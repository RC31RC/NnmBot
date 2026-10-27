package com.nnm.bot.warframe.persistent.official.service.world.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.OFFICIAL_SOURCE_CODE;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialVendorDTO;
import com.nnm.bot.warframe.persistent.official.constant.WarframeCatalogConstants.VendorSection;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.WarframeVendorDetailsDTO.ItemDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.WarframeVendorDetailsDTO.VendorDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.world.WarframeVendorDefinitionVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.world.WarframeVendorDefinitionVO.*;
import com.nnm.bot.warframe.persistent.official.service.world.WarframeVendorDefinitionService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialSelectionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;

/** 静态商品定义与运行时库存分开；价格范围保留固定值和区间形态。 */
@Service
public class WarframeVendorDefinitionServiceImpl implements WarframeVendorDefinitionService {
    @Resource
    private WarframeOfficialSelectionService selection;

    @Resource
    private WarframeOfficialRecordReadService catalog;

    /**
     * 按查询条件筛选商人静态定义并返回分页结果。
     *
     * @param keyword 筛选使用的关键词。
     * @param section 分区。
     * @param page 从一开始的页码。
     * @return 商人静态定义的分页视图。
     */
    @Override
    public WarframeVendorDefinitionVO query(String keyword, VendorSection section, int page) {
        List<EntryVO> result = new ArrayList<>();
        List<String> vendors = new ArrayList<>();
        for (var root : catalog.records(WarframeOfficialDataDomain.VENDOR)) {
            OfficialVendorDTO value = (OfficialVendorDTO) root.data();
            VendorDTO vendor = new VendorDTO(bool(value.getIsDynamic()), bool(value.getIsOneBinPerCycle()), count(value.getNumItemsMinValue()),
                    count(value.getNumItemsMaxValue()), null, first(value.getRandomSeedType()), first(value.getRequiredGoalTag()));
            vendors.add(root.bkey());
            int order = 0;
            if (section == VendorSection.ITEMS) {
                for (OfficialVendorDTO.ItemsDTO child : values(value.getItems())){
                    OfficialVendorDTO.ItemsItemReferencesDTO reference = at(child.getItemReferences(), 0);
                    String itemName = reference == null ? "" : first(reference.getNameZh(), reference.getNameEn(), reference.getName());
                    if (!selection.matches(root, keyword) && !itemName.toLowerCase().contains(keyword.toLowerCase())) { order++; continue; }
                    List<PriceVO> prices = new ArrayList<>();
                    int priceOrder = 0;
                    for (OfficialVendorDTO.ItemsItemPricesDTO price : values(child.getItemPrices())){
                        prices.add(new PriceVO(null, null, null, null, OFFICIAL_SOURCE_CODE, priceOrder++, first(price.getItemType()),
                                values(price.getItemReferences()).stream().map(item -> first(item.getNameZh(), item.getNameEn(), item.getName())).findFirst().orElse(""), count(price.getItemCount())));
                    }
                    ItemDTO item = new ItemDTO(null, null, root.bkey(), order, first(child.getStoreItem()), count(child.getQuantity()),
                            bool(child.getAlwaysOffered()), integer(child.getBin()), integer(child.getDuplicates()),
                            decimal(child.getProbability()), integer(child.getNumRandomItemPrices()), count(child.getPurchaseLimit()),
                            bool(child.getRotatedWeekly()), null, null, null, "", null,
                            null, null,
                            null, "", null);
                    result.add(new EntryVO(null, null, null, null, root.bkey(), OFFICIAL_SOURCE_CODE, OFFICIAL_SOURCE_CODE, "ROOT_SELECTED",
                            order++, first(child.getStoreItem()), itemName, vendor, item, prices, null, null, null));
                }
            } else {
                int bin = 0;
                for (List<OfficialVendorDTO.RandomItemPricesPerBinDTO> members : values(value.getRandomItemPricesPerBin())){
                    for (OfficialVendorDTO.RandomItemPricesPerBinDTO price : values(members)){
                        String name = values(price.getItemReferences()).stream().map(item -> first(item.getNameZh(), item.getNameEn(), item.getName())).findFirst().orElse("");
                        if (selection.matches(root, keyword) || name.toLowerCase().contains(keyword.toLowerCase())) {
                            result.add(new EntryVO(null, null, null, null, root.bkey(), OFFICIAL_SOURCE_CODE, OFFICIAL_SOURCE_CODE, "ROOT_SELECTED",
                                    order++, first(price.getType()), name, vendor, null, List.of(), bin,
                                    count(price.getCountMinValue()), count(price.getCountMaxValue())));
                        }
                    }
                    bin++;
                }
            }
        }
        return WarframeVendorDefinitionVO.page(keyword, section, vendors, result, page);
    }

}
