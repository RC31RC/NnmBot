package com.nnm.bot.warframe.runtime.service.vendor.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialReferenceKeys;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialVendorDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialVendorDTO.ItemsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.OfficialVendorDTO.ItemsItemReferencesDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialLookupService;
import com.nnm.bot.warframe.runtime.constant.WarframeValenceElement;
import com.nnm.bot.warframe.runtime.constant.WarframeWeaponVendor;
import com.nnm.bot.warframe.runtime.constant.WarframeWorldStateErrorCode;
import com.nnm.bot.warframe.runtime.mapper.WarframeTimeDisplay;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;
import com.nnm.bot.warframe.runtime.pojo.dto.vendor.WarframeVendorObservationDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.pojo.vo.vendor.WarframeWeaponVendorVO;
import com.nnm.bot.warframe.runtime.pojo.vo.vendor.WarframeWeaponVendorVO.ElementVO;
import com.nnm.bot.warframe.runtime.pojo.vo.vendor.WarframeWeaponVendorVO.VendorVO;
import com.nnm.bot.warframe.runtime.pojo.vo.vendor.WarframeWeaponVendorVO.WeaponVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import com.nnm.bot.warframe.runtime.service.vendor.WarframeVendorRotationService;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/** 官方商品顺序和分组按采用版本读取，人工加成只保留当前四日窗口。 */
@Service
public class WarframeVendorRotationServiceImpl implements WarframeVendorRotationService {
    private static final String STEEL_VENDOR = "/Lotus/Types/Game/VendorManifests/Hubs/TeshinHardModeVendorManifest";
    // WFCD 2020-11-16 周的起始商品，只标定 Vendor 序列的起点。
    private static final Instant STEEL_ANCHOR = Instant.parse("2020-11-16T00:00:00Z");
    private static final String STEEL_ANCHOR_ITEM = "/Lotus/StoreItems/Types/Recipes/Components/UmbraFormaBlueprint";
    private static final int STEEL_ROTATION_LENGTH = 8;
    private static final int WEEK_DAYS = 7;
    private static final int WEAPON_ROTATION_DAYS = 4;
    private static final BigDecimal MIN_BONUS = BigDecimal.valueOf(25);
    private static final BigDecimal MAX_BONUS = BigDecimal.valueOf(60);

    /** 读取每个商人的已采纳完整清单和物品引用。 */
    @Resource
    private WarframeOfficialLookupService officialLookup;

    /** 四日周期分别按各商人的 UTC 起点计算。 */
    @Resource(name = "warframeClock")
    private Clock clock;

    private final Map<WarframeWeaponVendor, Observations> observations = new EnumMap<>(WarframeWeaponVendor.class);

    @Override
    public WarframeWorldStateCommandVO steelPath(WorldStateDTO state) {
        var supplied = WarframeWorldStateProjector.steelPath(state);
        if (supplied.data().get("items") instanceof List<?> items && !items.isEmpty())
            return supplied;
        var weekly =
            items(vendor(STEEL_VENDOR)).stream().filter(item -> Boolean.TRUE.equals(item.getRotatedWeekly())).toList();
        // 概率为 1 的每周限购商品随每轮一起展示，例如裂罅破解器。
        var fixed = weekly.stream().filter(item -> hasProbability(item, BigDecimal.ONE))
            .map(WarframeVendorRotationServiceImpl::product).toList();
        var candidates = weekly.stream().filter(item -> !hasProbability(item, BigDecimal.ONE))
            .map(WarframeVendorRotationServiceImpl::product).toList();
        var data = new LinkedHashMap<>(supplied.data());
        var ordered = orderedSteelItems(candidates);
        if (ordered.isEmpty()) {
            List<Map<String, Object>> rows = new ArrayList<>();
            candidates.forEach(item -> rows.add(steelItem(item, null, "轮换候选")));
            fixed.forEach(item -> rows.add(steelItem(item, null, "每周固定")));
            data.put("items", rows);
            data.put("itemCount", rows.size());
            return new WarframeWorldStateCommandVO(supplied.text(), data);
        }
        long week = elapsedWindows(STEEL_ANCHOR, clock.instant(), WEEK_DAYS);
        int index = Math.floorMod(week, ordered.size());
        Instant start = STEEL_ANCHOR.plus(week * WEEK_DAYS, ChronoUnit.DAYS);
        List<Map<String, Object>> cycle = new ArrayList<>();
        for (int i = 0; i < ordered.size(); i++)
            cycle.add(steelItem(ordered.get(i), i + 1, null));
        var current = new LinkedHashMap<>(cycle.get(index));
        current.put("rotation", cycle);
        Instant expiry = start.plus(WEEK_DAYS, ChronoUnit.DAYS);
        current.put("activationInstant", start.toString());
        current.put("activation", WarframeTimeDisplay.format(start));
        current.put("rotationNotice", "按官方商人顺序与周次锚点计算");
        current.put("offeringStatus", "本周轮换");
        current.put("expiryInstant", expiry.toString());
        current.put("expiry", WarframeTimeDisplay.format(expiry));
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(current);
        for (Product item : fixed) {
            var offer = steelItem(item, null, "每周固定");
            offer.put("expiryInstant", expiry.toString());
            offer.put("expiry", WarframeTimeDisplay.format(expiry));
            rows.add(offer);
        }
        data.put("items", rows);
        data.put("itemCount", rows.size());
        return new WarframeWorldStateCommandVO("本周钢铁奖励：" + ordered.get(index).name(), data);
    }

    @Override
    public WarframeWeaponVendorVO weapons() {
        Instant now = clock.instant();
        List<VendorVO> vendors = Arrays.stream(WarframeWeaponVendor.values())
            .map(vendor -> weapons(vendor, weaponRound(vendor, now))).toList();
        var elements = Arrays.stream(WarframeValenceElement.values())
            .map(element -> new ElementVO(element.name(), element.getLabel())).toList();
        return new WarframeWeaponVendorVO(elements, vendors);
    }

    @Override
    public WarframeWeaponVendorVO observe(WarframeVendorObservationDTO observation) {
        var vendor = observation == null ? null : WarframeWeaponVendor.find(observation.vendor());
        var element = observation == null ? null : WarframeValenceElement.find(observation.element());
        BigDecimal percentage = observation == null ? null : observation.bonusPercent();
        boolean clear = observation != null && observation.element() == null && percentage == null;
        BusinessAssert.isTrue(
            vendor != null && (clear || (element != null && percentage != null && percentage.compareTo(MIN_BONUS) >= 0
                && percentage.compareTo(MAX_BONUS) <= 0 && percentage.stripTrailingZeros().scale() <= 2)),
            WarframeWorldStateErrorCode.VENDOR_OBSERVATION_INVALID);
        var rotation = weaponRound(vendor, clock.instant());
        BusinessAssert.isTrue(rotation.id().equals(observation.round()),
            WarframeWorldStateErrorCode.VENDOR_ROUND_CHANGED);
        BusinessAssert.isTrue(
            weapons(vendor, rotation).weapons().stream().anyMatch(item -> item.key().equals(observation.weaponKey())),
            WarframeWorldStateErrorCode.VENDOR_OBSERVATION_INVALID);
        save(vendor, rotation, observation.weaponKey(), clear ? null : new Bonus(element, percentage));
        return weapons();
    }

    private synchronized void save(WarframeWeaponVendor vendor, Rotation rotation, String weapon, Bonus bonus) {
        BusinessAssert.isTrue(weaponRound(vendor, clock.instant()).equals(rotation),
            WarframeWorldStateErrorCode.VENDOR_ROUND_CHANGED);
        var current = observations.get(vendor);
        if (current == null || !rotation.equals(current.rotation())) {
            current = new Observations(rotation, new LinkedHashMap<>());
            observations.put(vendor, current);
        }
        if (bonus == null)
            current.weapons().remove(weapon);
        else
            current.weapons().put(weapon, bonus);
    }

    private synchronized Map<String, Bonus> observations(WarframeWeaponVendor vendor, Rotation rotation) {
        var current = observations.get(vendor);
        return current != null && rotation.equals(current.rotation()) ? Map.copyOf(current.weapons()) : Map.of();
    }

    private VendorVO weapons(WarframeWeaponVendor vendor, Rotation rotation) {
        OfficialVendorDTO manifest = vendor(vendor.getManifest());
        var entries = items(manifest);
        boolean grouped = vendor == WarframeWeaponVendor.CODA;
        int bin = Math.floorMod(rotation.index(), 2);
        if (grouped && (manifest == null || !Boolean.TRUE.equals(manifest.getIsOneBinPerCycle())
            || !entries.stream().map(ItemsDTO::getBin).filter(Objects::nonNull).map(BigDecimal::stripTrailingZeros)
                .collect(Collectors.toSet()).equals(Set.of(BigDecimal.ZERO, BigDecimal.ONE))))
            return new VendorVO(vendor.name(), vendor.getLabel(), rotation.id(), rotation.expiry(), null, List.of());
        var bonuses = observations(vendor, rotation);
        var weapons = entries.stream()
            .filter(item -> !grouped || item.getBin() != null && item.getBin().compareTo(BigDecimal.valueOf(bin)) == 0)
            .map(WarframeVendorRotationServiceImpl::product).map(item -> {
                Bonus bonus = bonuses.get(item.key());
                return new WeaponVO(item.key(), item.name(), item.image(), item.price(),
                    bonus == null ? null : bonus.element().name(), bonus == null ? null : bonus.percentage());
            }).toList();
        return new VendorVO(vendor.name(), vendor.getLabel(), rotation.id(), rotation.expiry(),
            grouped ? (bin == 0 ? "B" : "A") : null, weapons);
    }

    private OfficialVendorDTO vendor(String key) {
        var row =
            officialLookup.records(WarframeOfficialDataDomain.VENDOR, List.of(key)).get(key.toLowerCase(Locale.ROOT));
        return row != null && row.data() instanceof OfficialVendorDTO dto ? dto : null;
    }

    private static List<ItemsDTO> items(OfficialVendorDTO vendor) {
        return vendor == null || vendor.getItems() == null ? List.of() : vendor.getItems().stream()
            .filter(item -> item != null && StrUtil.isNotBlank(item.getStoreItem())).toList();
    }

    private static Product product(ItemsDTO entry) {
        var key = entry.getStoreItem();
        var identities = WarframeOfficialReferenceKeys.candidates(key);
        var references = entry.getItemReferences() == null ? List.<ItemsItemReferencesDTO>of()
            : entry.getItemReferences().stream().filter(Objects::nonNull)
                .filter(ref -> identities.stream().anyMatch(identity -> identity.equalsIgnoreCase(ref.getUniqueName())))
                .toList();
        var reference = references.size() == 1 ? references.getFirst() : null;
        String name =
            reference == null ? key : first(reference.getNameZh(), reference.getNameEn(), reference.getName(), key);
        List<Price> prices = entry.getItemPrices() == null ? List.of() : entry.getItemPrices().stream()
            .filter(amount -> amount != null && amount.getItemCount() != null).map(amount -> {
                var text = amount.getItemReferences() == null ? null
                    : amount.getItemReferences().stream().filter(Objects::nonNull).findFirst().orElse(null);
                String currency = text == null ? amount.getItemType()
                    : first(text.getNameZh(), text.getNameEn(), text.getName(), amount.getItemType());
                return new Price(amount.getItemType(), currency, amount.getItemCount(),
                    text == null ? null : text.getIconUrl());
            }).filter(amount -> StrUtil.isNotBlank(amount.name())).toList();
        return new Product(key, name, reference == null ? null : reference.getIconUrl(), prices, entry.getQuantity(),
            entry.getPurchaseLimit());
    }

    private static boolean hasProbability(ItemsDTO item, BigDecimal probability) {
        return item.getProbability() != null && item.getProbability().compareTo(probability) == 0;
    }

    private static String first(String... values) {
        return Arrays.stream(values).filter(StrUtil::isNotBlank).findFirst().orElse(null);
    }

    private static List<Product> orderedSteelItems(List<Product> candidates) {
        if (candidates.size() != STEEL_ROTATION_LENGTH
            || candidates.stream().map(Product::key).distinct().count() != STEEL_ROTATION_LENGTH)
            return List.of();
        for (int index = 0; index < candidates.size(); index++) {
            if (STEEL_ANCHOR_ITEM.equals(candidates.get(index).key())) {
                var ordered = new ArrayList<>(candidates.subList(index, candidates.size()));
                ordered.addAll(candidates.subList(0, index));
                return ordered;
            }
        }
        return List.of();
    }

    private static Map<String, Object> steelItem(Product product, Integer index, String status) {
        var item = new LinkedHashMap<String, Object>();
        item.put("item", product.name());
        item.put("reward", product.name());
        item.put("rewardKey", product.key());
        item.put("itemKey", product.key());
        if (product.image() != null)
            item.put("imageUrl", product.image());
        if (!product.prices().isEmpty()) {
            item.put("price", product.price());
            item.put("prices", product.prices().stream().map(price -> {
                var amount = new LinkedHashMap<String, Object>();
                amount.put("itemKey", price.key());
                amount.put("name", price.name());
                amount.put("count", price.count());
                if (price.image() != null)
                    amount.put("imageUrl", price.image());
                return amount;
            }).toList());
        }
        if (product.quantity() != null)
            item.put("count", product.quantity());
        if (product.purchaseLimit() != null)
            item.put("purchaseLimit", product.purchaseLimit());
        if (index != null)
            item.put("rotationIndex", index);
        if (status != null)
            item.put("offeringStatus", status);
        return item;
    }

    private static long elapsedWindows(Instant anchor, Instant asOf, int days) {
        return Math.floorDiv(asOf.getEpochSecond() - anchor.getEpochSecond(), days * 86_400L);
    }

    private static Rotation weaponRound(WarframeWeaponVendor vendor, Instant now) {
        Instant anchor = vendor.getAnchor();
        long index = elapsedWindows(anchor, now, WEAPON_ROTATION_DAYS);
        return new Rotation(index, anchor.plus(index * WEAPON_ROTATION_DAYS, ChronoUnit.DAYS));
    }

    /** Vendor 中确认的商品。 */
    private record Product(String key, String name, String image, List<Price> prices, BigDecimal quantity,
        BigDecimal purchaseLimit) {
        String price() {
            return prices.stream().map(amount -> amount.count().toPlainString() + " " + amount.name())
                .collect(Collectors.joining("、"));
        }
    }

    /** 官方 Vendor 中的货币身份、每件价格和货币图标。 */
    private record Price(String key, String name, BigDecimal count, String image) {
    }

    /** 四日窗口。 */
    private record Rotation(long index, Instant start) {
        String id() {
            return start.toString();
        }

        Instant expiry() {
            return start.plus(WEAPON_ROTATION_DAYS, ChronoUnit.DAYS);
        }
    }

    /** 一个商人的单轮填报。 */
    private record Observations(Rotation rotation, Map<String, Bonus> weapons) {
    }

    /** 人工观察的元素与加成。 */
    private record Bonus(WarframeValenceElement element, BigDecimal percentage) {
    }
}
