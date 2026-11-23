package com.nnm.bot.warframe.persistent.official.constant;

import cn.hutool.core.text.CharSequenceUtil;

import java.util.Arrays;
import java.util.List;

/** 固定版本成品的业务领域；完整路径与发布契约一致，不传播生产者的采集文件身份。 */
public enum WarframeOfficialDataDomain {
    /** 制造配方。蓝图业务键关联产物、材料、数量和制作耗时，供配方与材料反查使用。 */
    RECIPE("crafting/recipes.json.gz", "制作配方", false),
    /** 道场研究、制造、房间及装饰配方。按分组和完整配方键读取成本与产物，保留与普通铸造配方的区别。 */
    DOJO_RECIPE("crafting/dojo-recipes.json.gz", "道场配方", false),
    /** 技能定义。以技能身份读取双语名称、说明和图标，通过拥有者业务键关联装备。 */
    ABILITY("equipment/abilities.json.gz", "技能", true),
    /** 曲翼装备定义。按装备身份查询基础属性、外观及技能，用于曲翼资料和物品引用。 */
    ARCHWING("equipment/archwings.json.gz", "Archwing", true),
    /** 动物同伴及保育生物定义。按完整对象路径读取生物名称、保育声望和奖励引用。 */
    ANIMAL("equipment/companions-animals.json.gz", "动物同伴与保育生物", true),
    /** 同伴装备定义。按完整装备路径查询属性与图标，保留守护和其他同伴的独立身份。 */
    SENTINELS("equipment/companions-sentinels.json.gz", "机械同伴", true),
    /** 消耗品和装备栏道具定义。以道具完整路径读取名称、说明及图标，供奖励和商人展示使用。 */
    GEAR("equipment/gear.json.gz", "消耗道具", true),
    /** 九重天武器定义。以装备身份读取伤害、射速、槽位等属性，保留与普通武器的区别。 */
    RAILJACK_WEAPON("equipment/railjack-weapons.json.gz", "九重天武器", true),
    /** 资源无人机定义。按无人机身份查询容量、采集、耐久和概率参数。 */
    DRONES("equipment/resource-drones.json.gz", "资源无人机", true),
    /** 飞船与登陆艇定义。按飞船身份读取分类和外观资料，供装备或物品查询使用。 */
    SHIP("equipment/ships.json.gz", "飞船", true),
    /** 战甲定义。按战甲身份查询属性、变体、被动和技能；同名外观由外观表保存。 */
    WARFRAME("equipment/warframes.json.gz", "战甲", true),
    /** 武器定义。按武器身份查询槽位、伤害、暴击等属性，并为奖励与交易物品提供名称和图标。 */
    WEAPON("equipment/weapons.json.gz", "武器", true),
    /** 阿耶檀识珍宝定义。按珍宝路径读取稀有度、插槽及展示资料，供奖品识别。 */
    AYATAN("items/ayatan-treasures.json.gz", "阿耶檀识珍宝", true),
    /** 增幅器物品定义。按物品路径读取名称和类型；本轮全服增益的时间和倍率由动态快照提供。 */
    BOOSTER("items/boosters.json.gz", "增幅器", true),
    /** 礼包定义。以礼包路径读取组成物品及分类，用于商城和奖励中的礼包展示。 */
    BUNDLE("items/bundles.json.gz", "礼包", true),
    /** Captura 场景物品定义。按物品路径获取名称和图标；场景键与掉落地点分开解析。 */
    CAPTURA("items/captura-scenes.json.gz", "Captura 场景", true),
    /** 制造部件定义。按部件身份补齐配方材料、遗物及奖励的名称和图标。 */
    COMPONENT("items/components.json.gz", "制造部件", true),
    /** 有独立类型路径的现金包定义。可按路径确认奖励类型及现金数量；动态字段中的现金数额以原值为准。 */
    CREDIT_BUNDLE("items/credit-bundles.json.gz", "现金礼包", true),
    /** 定制选项及基础资源关系。按定制身份读取说明和图片，保持与外观皮肤的来源归属。 */
    CUSTOMS("items/customizations.json.gz", "外观定制", true),
    /** 装饰品定义。按装饰品身份查询图标、说明和容量消耗，供装饰及奖励展示。 */
    DECORATION("items/decorations.json.gz", "装饰", true),
    /** 内融核心包定义。按包路径读取内融核心数量和图标，适用于带物品身份的奖励。 */
    FUSION_BUNDLES("items/endo-bundles.json.gz", "内融核心包", true),
    /** 仅用于关联的内部定义。完整路径可定位父级及物品引用，该领域不加入普通物品名索引。 */
    INTERNAL_DEFINITION("items/internal-definitions.json.gz", "内部定义", false),
    /** 杂项物品定义。以具体对象路径查询名称、说明和图标，用于未归入专门类别的奖励与商品。 */
    MISC("items/misc-items.json.gz", "杂项", true),
    /** 资源物品定义。按路径查询材料、代币等资源名称与图标，供掉落、配方和兑换展示使用。 */
    RESOURCES("items/resources.json.gz", "资源", true),
    /** 随机奖励包定义。按包键查询候选组件和抽取规则；具体获得物品取自实际结算。 */
    REWARD_PACK("items/reward-packs.json.gz", "随机奖励包", true),
    /** 皮肤和外观物品定义。使用外观自身路径解析名称和图片，保留与基础装备的关系。 */
    SKIN("items/skins.json.gz", "外观", true),
    /** 歌曲物品定义。按物品身份查询名称、描述及购买属性，供音乐物品或商城资料展示。 */
    SONG("items/songs.json.gz", "歌曲", true),
    /** 补给类物品定义。按完整物品路径读取名称、说明和相关引用，供补给资料和奖品展示。 */
    SUPPLY_DROP("items/supply-drops.json.gz", "补给", true),
    /** Warframe Market 商品身份映射。通过市场商品代码与游戏物品路径关联交易命令和物品资料；即时订单独立获取。 */
    MARKET_ITEM("market/trade-items.json.gz", "市场商品", false),
    /** 成就定义。按成就键查询名称、目标和图标，用于成就资料和展示。 */
    ACHIEVEMENT("progression/achievements.json.gz", "成就", false),
    /** 通用挑战及 1999 日历挑战定义。按挑战完整路径读取目标、计数和奖励引用，用于解释动态挑战。 */
    CHALLENGE("progression/challenges.json.gz", "挑战", false),
    /** 内源之力成长定义。按分支及等级读取技能说明和晋级资料，用于成长查询。 */
    INTRINSIC("progression/intrinsics.json.gz", "内源之力", false),
    /** 午夜电波静态挑战及奖励定义。按挑战路径读取名称、计数和声望，当前发布的挑战及周次由动态快照提供。 */
    NIGHTWAVE("progression/nightwave.json.gz", "午夜电波静态定义", false),
    /** 图标资产路径与内容哈希的索引。按原始图片路径定位可用图片资源，物品归属由对应定义确定。 */
    IMAGE("reference/images.json.gz", "图片引用", false),
    /** 物品路径到双语名称、说明及图标的映射。为具有同一游戏身份的物品补展示信息。 */
    ITEM_LOCALIZATION("reference/item-localizations.json.gz", "物品双语映射", false),
    /** 技能拥有者的双语展示映射。按战甲或装备路径取得其技能列表、说明和图标。 */
    ABILITY_OWNER_LOCALIZATION("reference/ability-owner-localizations.json.gz", "技能拥有者双语映射", false),
    /** 升级对象的双语名称、图标及多段描述。按升级完整路径为 MOD 或其他升级资料补正文。 */
    UPGRADE_LOCALIZATION("reference/upgrade-localizations.json.gz", "升级多行双语映射", false),
    /** 游戏语言键到双语文本的字典。用完整语言键解析名称和说明；翻译文本本身不承担物品身份。 */
    LANGUAGE_STRING("reference/language-strings.json.gz", "双语语言字典", false),
    /** 文本图标标记在各平台的映射。以标记代码查询对应资源，供游戏文案显示。 */
    TEXT_ICON("reference/text-icons.json.gz", "文本图标", false),
    /** 赏金定义与按地区、难度整理的静态候选奖池。定义按任务完整路径取标题和候选阶段，地区奖池使用自身分组键。 */
    BOUNTY("rewards/bounties.json.gz", "赏金定义与奖励池", false),
    /** 按来源场景和地区组织的官方掉落资料。用于掉落来源反查与已核验的沉沦之地奖池映射，实际结算由游戏给出。 */
    DROP_TABLE("rewards/drop-tables.json.gz", "掉落表", false),
    /** 遗物与各精炼等级的奖励和来源。按遗物身份查询内容或反查部件，保留各稀有度的概率。 */
    RELIC("rewards/relics.json.gz", "虚空遗物", true),
    /** 以游戏奖励池完整路径保存阶段、物品、数量和概率。动态赏金下发池路径时用它连接静态奖励。 */
    REWARD_POOL("rewards/reward-pools.json.gz", "奖励池", false),
    /** 赋能定义。以赋能身份查询等级效果和稀有度，为掉落和物品引用补名称。 */
    ARCANE("upgrades/arcanes.json.gz", "赋能", true),
    /** 九重天航电定义。按升级对象路径读取极性、容量消耗和等级效果。 */
    AVIONIC("upgrades/avionics.json.gz", "航电", true),
    /** 专精能力升级定义。按升级对象路径查询专精消耗和等级效果，用于专精树说明。 */
    FOCUS("upgrades/focus-upgrades.json.gz", "专精", true),
    /** MOD 套装定义。按套装身份读取成员及套装效果，单张 MOD 的独立属性由 MOD 表提供。 */
    MOD_SET("upgrades/mod-sets.json.gz", "MOD 套装", true),
    /** MOD 主定义。按升级身份读取名称、容量消耗、适配对象和等级效果，供 MOD 与奖励查询。 */
    UPGRADE("upgrades/mods.json.gz", "MOD", true),
    /** 裂罅属性与市场属性代码的映射。说明前后缀、单位和适用范围，用于归一词条及数值分析。 */
    RIVEN_ATTRIBUTE("upgrades/riven-attributes.json.gz", "裂罅属性", false),
    /** 按类别归纳的裂罅静态定义。提供通用容量、极性与升级参数，用于裂罅结构解释。 */
    RIVEN_DEFINITION("upgrades/riven-definitions.json.gz", "裂罅定义", false),
    /** 裂罅武器、适配类别、游戏身份及市场交易标识的映射。用于组合成交记录和读取武器倾向。 */
    RIVEN_WEAPON("upgrades/riven-weapons.json.gz", "裂罅武器完整映射", false),
    /** 资料库对象、碎片、歌曲等分类定义。按分类和完整身份查询资料页、名称或说明。 */
    CODEX("world/codex.json.gz", "资料库", false),
    /** 敌人对象、行为层和关联掉落清单等定义。按分组及游戏对象路径定位敌人；同名代理和实体保留各自身份。 */
    ENEMY("world/enemies.json.gz", "敌人", false),
    /** 收件箱消息模板与附件引用定义。以邮件物品路径取得发件人、正文和附件，玩家是否收到由运行时决定。 */
    INBOX("world/inbox-messages.json.gz", "邮件", false),
    /** 任务钥匙与任务链定义。按钥匙路径读取任务身份、阶段和道具说明。 */
    KEYS("world/quest-keys.json.gz", "任务钥匙", true),
    /** 静态星图节点和所属星球或星系。以节点代码关联地域、基础任务类型和阵营，本轮裂缝等活动使用自身任务类型。 */
    REGION("world/star-chart.json.gz", "星图节点", true),
    /** 地图场景和其中可用任务的定义。以场景键查询基础阵营、关卡资料与任务定义。 */
    TILESET("world/tilesets.json.gz", "场景", false),
    /** 静态商人商品目录。以商人清单路径查询商品、价格和图标；当前轮换按动态来源或受管顺序确认。 */
    VENDOR("world/vendors.json.gz", "商人目录", false),
    /** 科研与沉沦之地的分组静态规则。以活动族、分组和短码确认模式、条件及说明；每周任务由官方快照提供。 */
    ACTIVITY("worldstate/activities.json.gz", "科研与下降静态定义", false),
    /** 执刑官代码到首领名称和源力石候选定义的映射。动态猎杀给出本轮首领，阶段实际奖励需有相应动态证据。 */
    BOSS("worldstate/bosses.json.gz", "执刑官", false),
    /** 阵营代码簿。通过官方阵营代码和可唯一确认的名称解析入侵双方、星图和活动阵营。 */
    FACTION("worldstate/factions.json.gz", "阵营", false),
    /** 裂缝等级代码簿。按等级代码解析遗物阶级和裂缝名称；节点、任务和时间来自动态活动。 */
    FISSURE_MODIFIER("worldstate/fissure-modifiers.json.gz", "裂缝", false),
    /** 官方任务类型代码簿。用稳定任务代码或唯一名称解释动态任务类型；补充源简称先核对受管映射。 */
    MISSION_TYPE("worldstate/mission-types.json.gz", "任务类型", false),
    /** 集团定义。按集团代码取双语名称、图标和基础规则，用于集团任务及赏金；不同赛季可能共享展示名。 */
    SYNDICATE("worldstate/syndicates.json.gz", "集团", false),
    /** 全局增益类型词典。用类型代码查询显示名；倍率、运算类型和有效时间来自官方动态快照。 */
    UPGRADE_TYPE("worldstate/upgrade-types.json.gz", "全局增益", false),
    /** 2020 年装备使用统计。按年份、平台、类别、物品及段位读取使用占比，适用于同统计口径下的比较。 */
    USAGE_2020("statistics/usage-2020.json.gz", "2020 使用统计", false),
    /** 2021 年装备使用统计。按年份、平台、类别、物品及段位读取使用占比，适用于同统计口径下的比较。 */
    USAGE_2021("statistics/usage-2021.json.gz", "2021 使用统计", false),
    /** 2022 年装备使用统计。按年份、平台、类别、物品及段位读取使用占比，适用于同统计口径下的比较。 */
    USAGE_2022("statistics/usage-2022.json.gz", "2022 使用统计", false),
    /** 2023 年装备使用统计。按年份、平台、类别、物品及段位读取使用占比，适用于同统计口径下的比较。 */
    USAGE_2023("statistics/usage-2023.json.gz", "2023 使用统计", false),
    /** 2024 年装备使用统计。按年份、平台、类别、物品及段位读取使用占比，适用于同统计口径下的比较。 */
    USAGE_2024("statistics/usage-2024.json.gz", "2024 使用统计", false),
    /** 2025 年装备使用统计。按年份、平台、类别、物品及段位读取使用占比，适用于同统计口径下的比较。 */
    USAGE_2025("statistics/usage-2025.json.gz", "2025 使用统计", false),
    /** 未识别发布文件后的占位领域。 */
    OTHER("", "未识别领域", false);

    private final String productPath;
    private final String label;
    private final boolean itemIndex;

    /**
     * 构造官方数据业务领域，并保存初始化参数。
     *
     * @param productPath 官方 JSON 文件在发布包内的相对路径。
     * @param label 标签标题。
     * @param itemIndex 物品索引。
     */
    WarframeOfficialDataDomain(String productPath, String label, boolean itemIndex) {
        this.productPath = productPath;
        this.label = label;
        this.itemIndex = itemIndex;
    }

    /**
     * 读取当前官方 JSON 在应用中的稳定领域代码。
     *
     * @return 官方文件在应用中的稳定领域代码。
     */
    public String domainCode() {
        return name();
    }

    /**
     * 读取当前官方 JSON 在发布包内的相对路径。
     *
     * @return 官方 JSON 在发布包内的相对路径。
     */
    public String productPath() {
        return productPath;
    }

    /**
     * 读取当前代码对应的展示名称。
     *
     * @return 当前代码对应的展示名称对应的文本。
     */
    public String label() {
        return label;
    }

    /**
     * 判断是否满足物品索引条件。
     *
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    public boolean itemIndex() {
        return itemIndex;
    }

    /**
     * 取得当前合同声明的业务路径。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public static List<String> paths() {
        return Arrays.stream(values()).filter(domain -> domain != OTHER).map(WarframeOfficialDataDomain::productPath)
            .sorted().toList();
    }

    /**
     * 按发布包相对路径查找官方 JSON 领域，未登记路径归为未知领域。
     *
     * @param path 当前文件或业务资源的路径。
     * @return 游戏资料官方数据业务领域对象。
     */
    public static WarframeOfficialDataDomain fromPath(String path) {
        return Arrays.stream(values()).filter(domain -> domain != OTHER && domain.productPath().equals(path))
            .findFirst().orElse(null);
    }

    /**
     * 按应用领域代码查找官方 JSON 领域，未登记代码归为未知领域。
     *
     * @param code 当前数据集或业务对象的稳定代码。
     * @return 游戏资料官方数据业务领域对象。
     */
    public static WarframeOfficialDataDomain fromCode(String code) {
        return Arrays.stream(values()).filter(domain -> CharSequenceUtil.equalsIgnoreCase(domain.name(), code))
            .findFirst().orElse(OTHER);
    }

    /**
     * 列出所有已登记官方 JSON 的稳定领域代码。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public static List<String> knownCodes() {
        return Arrays.stream(values()).filter(domain -> domain != OTHER).map(WarframeOfficialDataDomain::domainCode)
            .toList();
    }

    /**
     * 列出需要加入物品搜索索引的官方数据领域。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public static List<String> itemIndexCodes() {
        return Arrays.stream(values()).filter(domain -> domain != OTHER && domain.itemIndex())
            .map(WarframeOfficialDataDomain::domainCode).toList();
    }
}
