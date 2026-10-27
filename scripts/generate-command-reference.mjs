import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const repo = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const catalogPath = path.join(repo, 'docs/reference/命令与数据目录.md');
const semanticsPath = path.join(repo, 'docs/Warframe数据与命令实现.md');
const outputPath = path.join(repo, 'docs/reference/命令输入与返回参考.md');
const generatedDate = new Date().toLocaleDateString('sv-SE');
const catalogText = fs.readFileSync(catalogPath, 'utf8');
const semanticsText = fs.readFileSync(semanticsPath, 'utf8');
const catalogLines = catalogText.split(/\r?\n/);
const commands = [];

for (const line of catalogLines) {
  if (!/^\| `[^`]+` \|.*\|.*\| \[/.test(line)) continue;
  const cells = line.split('|').map(value => value.trim());
  if (cells.length < 7) continue;
  const code = cells[1].match(/^`([^`]+)`$/)?.[1];
  if (!code) continue;
  commands.push({
    code,
    label: cells[2].replace(/`/g, ''),
    match: cells[3],
    plugin: cells[4],
    services: cells[5]
  });
}

const codeSet = new Set(commands.map(command => command.code));
const semanticByCode = new Map();
for (const line of semanticsText.split(/\r?\n/)) {
  if (!/^\|/.test(line)) continue;
  const cells = line.split('|').map(value => value.trim());
  if (cells.length < 4) continue;
  const identifiers = [...cells[1].matchAll(/`([^`]+)`/g)].map(match => match[1]);
  for (const identifier of identifiers) {
    if (codeSet.has(identifier)) semanticByCode.set(identifier, cells[2]);
  }
}

const staticCommunityOnly = new Set([
  'liches', 'sisters', 'perrin-sequence', 'update-liches', 'update-sister',
  'update-perrin-sequence'
]);

const testByCode = new Map(Object.entries({
  'relics': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/official/rewards/RelicPluginTest.java'],
  'keys': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/official/items/KeyPluginsTest.java'],
  'email-items': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/official/items/KeyPluginsTest.java'],
  'gold-trash': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/official/rewards/RelicPluginTest.java', 'nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/runtime/market/MarketPluginTest.java'],
  'silver-trash': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/official/rewards/RelicPluginTest.java', 'nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/runtime/market/MarketPluginTest.java'],
  'bounty-definitions': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/official/rewards/BountyDefinitionsPluginTest.java'],
  'companions': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/official/equipment/CompanionsPluginTest.java'],
  'enemies': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/official/world/EnemiesPluginTest.java'],
  'fish-guide': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/official/guide/GuidePluginsTest.java'],
  'map-features': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/official/guide/GuidePluginsTest.java'],
  'recipes': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/official/rewards/RecipeRewardPluginTest.java'],
  'reward-pools': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/official/rewards/RecipeRewardPluginTest.java'],
  'market': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/runtime/market/MarketPluginTest.java', 'nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/runtime/market/MarketDisplayDataTest.java'],
  'market-riven': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/runtime/market/MarketRivenPluginTest.java'],
  'riven-definitions': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/official/upgrades/RivenDefinitionsPluginTest.java'],
  'riven-unrollables': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/official/upgrades/RivenUnrollablesPluginTest.java'],
  'riven-weekly': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/runtime/riven/RivenWeeklyPluginTest.java', 'nnm-warframe/src/test/java/com/nnm/bot/warframe/runtime/service/riven/WarframeRivenWeeklyServiceTest.java'],
  'riven-analyse': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/runtime/riven/RivenAnalysePluginTest.java', 'nnm-warframe/src/test/java/com/nnm/bot/warframe/runtime/service/riven/impl/WarframeRivenOcrServiceImplTest.java'],
  'arbitration': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/runtime/arbitration/ArbitrationCommandHandlerTest.java', 'nnm-warframe/src/test/java/com/nnm/bot/warframe/runtime/service/arbitration/impl/WarframeArbitrationServiceImplTest.java', 'nnm-warframe/src/test/java/com/nnm/bot/warframe/runtime/service/arbitration/impl/WarframeArbitrationRefreshServiceImplTest.java'],
  'today-arbitration': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/runtime/arbitration/ArbitrationCommandHandlerTest.java', 'nnm-warframe/src/test/java/com/nnm/bot/warframe/runtime/service/arbitration/impl/WarframeArbitrationServiceImplTest.java'],
  'efficient-arbitration': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/runtime/arbitration/ArbitrationCommandHandlerTest.java', 'nnm-warframe/src/test/java/com/nnm/bot/warframe/runtime/service/arbitration/impl/WarframeArbitrationServiceImplTest.java'],
  'arbitration-table': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/runtime/arbitration/ArbitrationCommandHandlerTest.java', 'nnm-warframe/src/test/java/com/nnm/bot/warframe/runtime/service/arbitration/impl/WarframeArbitrationServiceImplTest.java'],
  'forum-loadout': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/curated/forum/ForumLoadoutPluginTest.java', 'nnm-warframe/src/test/java/com/nnm/bot/warframe/persistent/curated/service/forum/impl/ForumArticleServiceImplTest.java'],
  'wiki': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/official/reference/WikiPluginTest.java'],
  'watch-list': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/curated/watch/WatchListPluginTest.java'],
  'subscribe': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/persistent/curated/event/watch/WorldStateWatchListenerTest.java'],
  'unsubscribe': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/persistent/curated/event/watch/WorldStateWatchListenerTest.java'],
  'nightwave-definitions': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/official/worldstate/NightwaveDefinitionsPluginTest.java'],
  'synth-targets': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/official/world/SynthTargetsPluginTest.java'],
  'update-liches': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/curated/reference/TermUpdatePluginTest.java'],
  'update-sister': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/curated/reference/TermUpdatePluginTest.java'],
  'update-perrin-sequence': ['nnm-warframe/src/test/java/com/nnm/bot/warframe/plugin/persistent/curated/reference/TermUpdatePluginTest.java']
}));

function absoluteLink(markdown, source = catalogPath) {
  return markdown.replaceAll(/\]\((?![a-z][a-z0-9+.-]*:|#)([^)]+)\)/gi, (_, relative) =>
    `](${path.isAbsolute(relative) ? relative : path.resolve(path.dirname(source), relative)})`);
}

function pluginFile(command) {
  const relative = command.plugin.match(/\]\(([^)]+\.java)\)/)?.[1];
  return relative ? path.resolve(path.dirname(catalogPath), relative) : '';
}

function pluginSourceChain(command) {
  const sources = [];
  const visited = new Set();
  let file = pluginFile(command);
  while (file && !visited.has(file) && fs.existsSync(file)) {
    visited.add(file);
    const source = fs.readFileSync(file, 'utf8');
    sources.push({ file, source, name: path.basename(file, '.java') });

    const parent = source.match(/\bclass\s+\w+[^\{]*?\bextends\s+([\w.]+)/s)?.[1];
    if (!parent) break;
    const simpleName = parent.split('.').at(-1);
    const imported = source.match(new RegExp(`^import\\s+([\\w.]+\\.${simpleName});`, 'm'))?.[1];
    const packageName = source.match(/^package\s+([\w.]+);/m)?.[1];
    const qualifiedName = imported ?? (packageName ? `${packageName}.${simpleName}` : '');
    const localParent = path.join(path.dirname(file), `${simpleName}.java`);
    const sourceRootIndex = file.indexOf(`${path.sep}src${path.sep}main${path.sep}java${path.sep}`);
    const moduleRoot = sourceRootIndex > 0 ? file.slice(0, sourceRootIndex) : '';
    const parentPath = fs.existsSync(localParent) ? localParent
      : moduleRoot && qualifiedName
        ? path.join(moduleRoot, 'src/main/java', qualifiedName.replaceAll('.', '/') + '.java')
        : '';
    file = fs.existsSync(parentPath) ? parentPath : '';
  }
  return sources;
}

function sourceAcceptsPage(source) {
  const regexPatterns = [...source.matchAll(/Pattern\.compile\(\s*"((?:\\.|[^"\\])*)"/gs)];
  if (regexPatterns.some(([, pattern]) => pattern.includes('--page')
      || pattern.match(/--\(([^)]*)\)/)?.[1].split('|').includes('page'))) return true;
  return /Set\.of\([^)]*["']page["']/s.test(source)
    && /options\.get\(["']page["']\)/.test(source);
}

function supportsPage(command) {
  return pluginSourceChain(command).some(({ source }) => sourceAcceptsPage(source));
}

function inputDescription(command) {
  if (command.match === 'EXACT') {
    return {
      status: '已核对',
      text: 'EXACT 只匹配完整触发词，注册器不提取尾随参数；插件需要群组/附件等上下文时见其实现。'
    };
  }

  const chain = pluginSourceChain(command);
  const parent = chain.slice(1).find(({ name }) => name.startsWith('Abstract'))?.name;
  const page = supportsPage(command);
  const byParent = {
    AbstractEquipmentPlugin: '可选关键词；仅允许尾部 --page N，N 为正整数。',
    AbstractDefinitionPlugin: '可选定义关键词；仅允许尾部 --page N，N 为正整数。',
    AbstractMaterialPlugin: '可选资料关键词；仅允许尾部 --page N，N 为有效正整数。',
    AbstractBundlePlugin: '可选礼包关键词；仅允许尾部 --page N，N 为正整数。',
    AbstractDucatsPlugin: '可选物品关键词；仅允许尾部 --page N，N 为正整数。',
    AbstractRelicPlugin: '遗物关键词；仅允许尾部 --page N，N 为正整数。',
    AbstractUpgradePlugin: '可选资料关键词；支持 --page N 和 --rank N，页码为正整数，rank 是否可为 0 由资料类型决定。',
    AbstractGuidePlugin: '可选攻略关键词；--world 限 plains/vallis/deimos，--page 为正整数；地图分支另接受符合图层代码格式的 --layer。',
    AbstractItemRelationPlugin: '可选物品关键词；--type 仅接受存在组件/掉落关系的资料类型，--page 为正整数。'
  }[parent];
  if (byParent) return { status: '已核对源码', text: byParent };

  const file = chain[0];
  if (!file) return { status: '未审计', text: '未能定位插件解析源码。' };
  if (file.name === 'MarketPlugin') {
    return { status: '已核对源码', text: '可组合满级、买家/卖家、pc/ps4/xbox/switch/mobile、物品文本；尾部纯数字作为 1-based 页码。' };
  }
  if (file.name === 'MarketRivenPlugin') {
    return { status: '已核对源码', text: '武器名、Prime、正/负词条条件；尾部纯数字作为页码。负词条标记与词条匹配规则见解析器。' };
  }
  if (file.name === 'RivenWeeklyPlugin') {
    return { status: '已核对源码', text: '可选查询词及 --platform、--rerolled、--page；值域、重复选项和长度限制由解析器校验。' };
  }
  if (file.name === 'RivenAnalysePlugin') {
    return { status: '已核对源码', text: '消息附图优先 OCR；无图时要求完整紫卡文本，不从普通文本构造图片输入。' };
  }
  if (file.name === 'WikiPlugin') {
    return { status: '已核对源码', text: '必填物品名称/关键词；插件不接受额外命名选项。' };
  }
  if (file.name === 'ForumLoadoutPlugin') {
    return { status: '已核对源码', text: '必填检索文本；支持标题/title、分类/category/分类名、标签/tag/tags 字段和普通关键词，单引号/双引号可包空格。' };
  }
  if (file.name === 'TranslationPlugin') {
    return { status: '已核对源码', text: '自由文本检索语言键或译文；匹配结果最多 20 条，不解析命名选项。' };
  }
  if (file.name === 'SubscribePlugin' || file.name === 'UnsubscribePlugin') {
    return { status: '已核对源码', text: '主题 [条件...]；主题须为 WarframeSubscriptionSpec 支持项，条件按空格或竖线分隔，最多 20 项且每项不超过 100 字；仅仲裁/裂缝支持多条件。' };
  }
  if (file.name === 'BindQqPlugin') {
    return { status: '已核对源码', text: '必填 5-20 位十进制 QQ 号；其他字符拒绝。' };
  }
  if (file.name === 'JmPlugin') {
    return { status: '已核对源码', text: '必填 JM 漫画 ID；插件转交下载 Service 校验格式，空参数拒绝。' };
  }
  if (file.name === 'LichesPlugin' || file.name === 'SistersPlugin') {
    return { status: '已核对源码', text: '可选自由文本关键词；按中文/英文术语包含匹配，最多返回 20 项。' };
  }
  const auditedPluginInputs = {
    BountyDefinitionsPlugin: '可选查询词（最多 200 字）；仅允许尾部 --page N，N 为正整数。',
    CompanionsPlugin: '可选查询词（最多 200 字）；仅允许尾部 --page N，N 为正整数。',
    EmailItemsPlugin: '可选邮件路径/关键词（最多 500 字）；仅允许尾部 --page N，N 为正整数，允许空查询。',
    EnemiesPlugin: '可选查询词（最多 500 字）；--section 可选 avatars/agents/damage-controllers/droptables/hit-proxies/ai-weapons，--page 为正整数；两项可出现在文本任意位置且各不可重复。',
    KeysPlugin: '可选钥匙路径/关键词（最多 500 字）；仅允许尾部 --page N，N 为正整数，允许空查询。',
    NightwaveDefinitionsPlugin: '可选查询词（最多 500 字）；--section 可选 challenges/rewards，--page 为正整数；两项可出现在文本任意位置且各不可重复。',
    RecipesPlugin: '可选查询词（最多 200 字）；支持尾部 --kind ordinary|dojo 和 --page N（正整数），两项各不可重复。',
    RewardPoolsPlugin: '可选查询词（最多 200 字）；支持尾部 --stage N 与 --page N（均为正整数），两项各不可重复。',
    RivenDefinitionsPlugin: '可选查询词（最多 200 字）；支持尾部 --mode attributes|challenges 与 --page N（正整数），两项各不可重复。',
    RivenUnrollablesPlugin: '可选查询词（最多 200 字）；仅允许尾部 --page N，N 为正整数。',
    SynthTargetsPlugin: '可选查询词（最多 200 字）；仅允许尾部 --page N，N 为正整数。',
    VendorDefinitionsPlugin: '可选查询词（最多 500 字）；--section 可选 items/random-prices，--page 为正整数；两项可出现在文本任意位置且各不可重复。'
  }[file.name];
  if (auditedPluginInputs) return { status: '已核对源码/测试', text: auditedPluginInputs };
  if (page) {
    return { status: '部分核对', text: '源码确认支持 --page N；其余参数语法未审计。' };
  }
  return { status: '未审计', text: '仅确认 PREFIX 注册器会把触发词后的文本交给该插件；具体参数语法需核对实现。' };
}

function sourceDescription(command) {
  const file = command.plugin.match(/\]\(([^)]+\.java)\)/)?.[1];
  const absolute = file ? path.resolve(path.dirname(catalogPath), file) : '';
  const code = command.code;
  if (code === 'gold-trash' || code === 'silver-trash') {
    return 'Warframe Market 上一小时/上一日动态成交统计 + 官方 Market 商品目录（名称与 slug）；成交均价不代表当前挂单价格。';
  }
  if (command.plugin.includes('/plugin/runtime/worldstate/')) {
    return '实时官方 WorldState 最近成功快照 + 已采纳的官方静态代码簿；快照失败保留旧值，未解析引用保留原值并标未知。';
  }
  if (command.plugin.includes('/plugin/runtime/arbitration/')) {
    return '独立运行时仲裁排期快照 + 官方静态节点/任务/阵营代码簿；纯自维护仲裁精华只补充效率参考，不生成排期。';
  }
  if (command.plugin.includes('/plugin/runtime/market/')) {
    return 'Warframe Market 实时 API + 官方 `market/trade-items.json` 目录；物品别名/紫卡别名只用于明确的自维护补充。候选不唯一时拒绝，不猜测。';
  }
  if (command.plugin.includes('/plugin/runtime/riven/')) {
    return code === 'riven-weekly'
      ? '周紫卡运行时数据源 + 官方静态武器 `uniqueName` 目录；平台分快照，刷新失败保留最近成功数据。'
      : '消息图片/OCR + 官方紫卡定义、武器树和自维护紫卡别名/组合规则；证据不足时保留未知，不给出推测估值。';
  }
  if (command.plugin.includes('/plugin/persistent/curated/watch/')) {
    return '订阅规则存储 + WorldState/仲裁等运行时事件；规则按群组与成员隔离，变更事件按稳定身份去重。';
  }
  if (command.plugin.includes('/plugin/persistent/curated/forum/')) {
    return 'NnmBot 论坛文章/标签数据；仅返回检索摘要和文章入口，不抓取正文。';
  }
  if (code === 'wiki' || code === 'translation') {
    return code === 'wiki'
      ? '官方静态目录名称/别名解析与 Wiki 页面入口；不请求或返回 Wiki 正文。'
      : '已采纳官方语言/本地化字典；仅在语义明确时解析物品名，不将描述键当作物品名称。';
  }
  if (staticCommunityOnly.has(code)) {
    return code.startsWith('update-')
      ? '纯自维护术语表的管理提示；不发远程请求、不直接修改记录。'
      : '纯自维护术语白名单；不表示实时库存、当前掉落或武器元素加成。';
  }
  if (command.plugin.includes('/plugin/persistent/official/')) {
    if (code === 'fish-guide' || code === 'map-features') {
      return '官方静态 Geo/地图或鱼类领域资料（通过对应 guide Service 查询）；缺失字段不从其他来源猜补。';
    }
    if (code === 'synth-targets') {
      return '当前官方成品未提供合成目标地点资料；返回明确未提供，不从敌人记录推断。';
    }
    return 'Nnm 官方固定静态关系树；同自然身份存在 SELF_MAINTAINED 根时整棵自维护树优先，否则取 OFFICIAL。查询不从旧通用社区 Raw 或旧组件映射补拼。';
  }
  if (command.label.endsWith('/ system')) {
    return 'core 系统 Service/群组上下文及数据库配置；不读取 Warframe 官方静态数据。';
  }
  return '按插件直接依赖的 Service/Provider 查询；具体依赖列见实现链接，未使用的来源不作推断。';
}

function resultDescription(command) {
  if (command.code === 'gold-trash' || command.code === 'silver-trash') {
    return '上一小时与上一日两期分别返回物品名、杜卡德金币、加权均价、金币/铂金比和成交量；均价缺失显示未知，不返回当前挂单价。';
  }
  if (command.code === 'relics') {
    return '返回查询词、遗物列表、分页与提示；每个遗物包含名称、入库状态、精炼、奖励名称/稀有度/概率、地点/稀有度/概率、图标引用/URL 和来源选择，奖励与地点保留官方树中的顺序。';
  }
  const semantic = semanticByCode.get(command.code);
  if (semantic) return absoluteLink(semantic.replaceAll('`', ''), semanticsPath);
  if (command.match === 'EXACT' && command.plugin.includes('/plugin/runtime/worldstate/')) {
    return '当前快照字段语义未审计；缺失引用应按对应展示 VO 的未知状态处理。';
  }
  return '返回字段与空值语义未审计；请以实现链接中的展示 VO 和 Service 为准。';
}

function pagingDescription(command) {
  const audited = {
    bind: '单次 QQ 身份绑定结果；不是列表查询，不分页。',
    'forum-loadout': '最多返回 5 篇；按匹配分数降序，再按 sortOrder、文章 ID 升序；不是分页接口。',
    'gold-trash': '上一小时、上一日分别排名；按金币/铂金比降序、itemId 升序稳定打破并列，每页 10 项；每期候选量另受 Service 配置上限约束。',
    'silver-trash': '上一小时、上一日分别排名；按金币/铂金比降序、itemId 升序稳定打破并列，每页 10 项；每期候选量另受 Service 配置上限约束。',
    jm: '单个漫画 ID 生成一个 PDF 附件；页数是漫画 PDF 页数，不是命令结果分页。',
    liches: '术语目录按 recordKey 升序筛选，最多返回 20 项；无命令级分页。',
    relics: '按遗物根/精炼详情的来源顺序累计分页；每页包含该详情中至多 10 条奖励/地点项，并受 480 字符文本预算限制，超长单条完整保留。',
    'riven-analyse': '每次输入返回一份结构化紫卡分析；不分页、不排序列表。',
    sisters: '术语目录按 recordKey 升序筛选，最多返回 20 项；无命令级分页。',
    subscribe: '单次创建一条当前群组成员订阅规则并返回操作确认；不分页。',
    translation: '每个语言域按自维护、官方来源分组，组内根记录按 ID 升序并对同身份采用自维护记录；中文域先于英文域合并后最多返回 20 项，无命令级分页。',
    unsubscribe: '单次停用一条当前群组成员匹配的订阅规则并返回操作结果；不分页。',
    wiki: '单目标解析后返回 Wiki 入口；名称歧义时返回候选并拒绝，不分页。'
  }[command.code];
  if (audited) return audited;
  const file = pluginSourceChain(command)[0];
  if (file?.name === 'MarketPlugin') {
    return '尾部数字页码，默认第 1 页；先筛选订单，再按买卖方向、价格和玩家名排序分页，超出结果范围返回空页。';
  }
  if (file?.name === 'MarketRivenPlugin') {
    return '尾部数字页码，默认第 1 页；拍卖按价格升序、再按拍卖 ID 排序，每页 10 条。';
  }
  if (command.match === 'EXACT' && !supportsPage(command)) return '不分页；保持运行时快照/Service 顺序。';
  if (supportsPage(command)) return '支持 --page N（默认第 1 页）；分页范围与顺序由插件展示 VO / Service 定义。';
  return '分页与排序未审计；插件源码链未发现 --page 解析器。';
}

function outputDescription(command) {
  const pluginPath = pluginFile(command);
  let source = '';
  try { source = fs.readFileSync(pluginPath, 'utf8'); } catch {}
  const viewKey = source.match(/CommandView\.[\w]+\("([^"]+)"\)/)?.[1];
  if (viewKey) return `结构化 viewKey ${viewKey}；允许的文本/图片/Markdown 输出由插件声明，实际选择由管理端命令 presentation/template 配置。`;
  if (command.plugin.includes('/plugin/runtime/worldstate/') || command.plugin.includes('/plugin/runtime/arbitration/')) {
    return '运行时视图由继承插件声明；机器人能力、启用状态及 presentation/template 以管理端配置为准。';
  }
  return 'CommandResult 文本或结构化视图；实际文本/图片/Markdown 组合由管理端 bot_command presentation/template 配置，不由稳定命令码决定。';
}

function testLinks(command) {
  const paths = testByCode.get(command.code) ?? [];
  const links = paths.map(test => {
    const target = path.join(repo, test);
    if (!fs.existsSync(target)) throw new Error(`Missing test reference for ${command.code}: ${target}`);
    return `[${path.basename(test)}](${target})`;
  });
  const catalogTest = path.join(repo, 'nnm-app/src/test/java/com/nnm/bot/ProjectCatalogTest.java');
  if (!fs.existsSync(catalogTest)) throw new Error(`Missing ProjectCatalogTest: ${catalogTest}`);
  links.push(`[ProjectCatalogTest](${catalogTest})（稳定码、插件/Service 元数据漂移）`);
  return links.join('<br>');
}

if (commands.length === 0) throw new Error('Registered command catalog is empty');
if (new Set(commands.map(command => command.code)).size !== commands.length) {
  throw new Error('Command catalog contains duplicate stable codes');
}
for (const command of commands) {
  if (!command.plugin) throw new Error(`Missing plugin implementation for ${command.code}`);
}

const tableRows = commands.map(command => {
  const input = inputDescription(command);
  const cells = [
    `\`${command.code}\``,
    command.label,
    command.match,
    `${input.status}：${input.text}`,
    sourceDescription(command),
    resultDescription(command),
    pagingDescription(command),
    outputDescription(command),
    `${absoluteLink(command.plugin)}<br>${command.services ? absoluteLink(command.services)
      : '注册目录无直接 Service 注入'}<br>${testLinks(command)}`
  ].map(value => value.replaceAll('|', '\\|'));
  return `| ${cells.join(' | ')} |`;
});
const inputAuditCounts = commands.map(inputDescription).reduce((counts, input) => {
  counts[input.status] = (counts[input.status] ?? 0) + 1;
  return counts;
}, {});

let document = `# NnmBot 命令输入与返回参考

生成日期：${generatedDate}
命令基准：[docs/reference/命令与数据目录.md](${path.join(repo, 'docs/reference/命令与数据目录.md')})
业务语义：[docs/Warframe数据与命令实现.md](${semanticsPath})
注册命令：${commands.length} 条（Warframe 119 条、system 5 条）

## 全局约定

- 表内稳定码来自 \`@BotPluginCommand.code\`，不是管理端触发词。触发词、别名、启停、机器人能力角色和输出模板以管理端数据库当前配置为准。
- 输入核对统计：${Object.entries(inputAuditCounts).map(([status, count]) => `${status} ${count} 条`).join('、')}。EXACT/PREFIX 路由行为由 [CommandRegistry](${path.join(repo, 'nnm-core/src/main/java/com/nnm/bot/core/plugin/registry/CommandRegistry.java')}) / [CommandRegistryTest](${path.join(repo, 'nnm-core/src/test/java/com/nnm/bot/core/plugin/registry/CommandRegistryTest.java')}) 覆盖，逐命令选项还结合实现链接中的解析器与定向测试核对。
- \`PREFIX\` 表示注册器把触发词后的文本交给插件；\`EXACT\` 表示只匹配完整触发词且不提取尾随文本。页码只在插件/父类解析器源码明确接受时列出；本表不从管理配置或错误提示推断参数。
- 插件/Service 列是源码声明的直接依赖，不等于每次调用都会查询每张表。Repository 与数据树由链接中的 Service 所属模块管理；目录不会把插件直接改成 Repository 调用者。
- 除明确列为人工白名单的命令外，\`WarframeCommandCatalog.Category\` 作为静态命令查询分类。静态资料读各 JSON 的独立主表和必要子表；同一自然身份优先选择完整 SELF_MAINTAINED 根树，没有才取 OFFICIAL 根树，不把两棵树的子孙逐行拼接。
- 四类纯自维护资料为物品别名、术语、仲裁精华、紫卡别名/组合规则。它们不属于静态官方包；上传、人工 JSON 和用户配置 URL 需先预览，选中的新增/修改项一个事务提交，不产生上游删除。
- 缺失字段显示未提供或按领域返回空集合；未知 WorldState 引用保留原始键并标记未解析。不能将无数据解释为游戏内不存在，也不能从退役社区来源猜值。
- 图像、文本、Markdown 等能力来自每个 Plugin 的 \`views()\` / viewKey；选用的 presentation 与模板以管理端配置为准。表中不把某一种格式伪称为每个实例当前启用的输出。
- 输出 VO 是展示合同，不向用户暴露 JPA 实体、数据库内部 ID、官方审核原文或未经确认的字段推导。

## 代表性输入与返回

| 输入示例 | 数据与筛选 | 返回重点 |
| --- | --- | --- |
| \`战甲 Excalibur\` | 官方装备根树；同身份先选自维护整树 | 名称/描述、图标、实际存在的数值指标及有序技能；没有的属性不补零。 |
| \`遗物 Meso A1 --page 2\` | 遗物根、精炼层和奖励孙记录；QueryDSL 先筛根身份再分页并装配整树 | 遗物纪元/名称、奖励、稀有度、概率、获取地点与精炼信息；查询不跨来源拼奖励。 |
| \`地图标注 --world plains --layer cave\` | 官方静态 GeoJSON 地图目录，按 world/layer 与关键词过滤 | 标注名、图层、几何与图片坐标；来源坐标不是经纬度，未知几何不推断。 |
| \`仲裁\` / \`今日仲裁\` | 仲裁运行时快照；自然日按配置时区，精华白名单只作效率参考 | 节点、任务、阵营、有效时段、抓取时间及陈旧/覆盖缺口提示。 |
| \`wm 买家 Prime 武器 2\` | 官方 Market slug/名称目录 + 物品别名 + Warframe Market 实时 API | 平台、方向、价格、玩家和订单条件；默认每页 5 单，候选歧义时拒绝。 |
| \`riven-weekly --platform pc --rerolled true --page 2\` | 周紫卡运行时 PC 快照 + 官方武器 uniqueName 目录 | itemType、rerolled、avg/stddev/min/max/pop/median、平台、来源与抓取时间；pop 是热度不是样本数。 |
| 图片消息 + \`riven-analyse\` | OCR 行证据 + 官方武器/词条基准 + 自维护紫卡别名 | 识别结果、未知项和条件满足时的 FIXED_1_2_V1 区间；不承诺交易价值。 |
| \`订阅裂缝 钢铁虚空捕获 古纪\` | 当前逻辑群组成员规则；按 WorldState 事件身份去重 | 写入规则确认；过滤条件不写进静态官方数据。 |

## 全量逐命令核对

下表按注册清单完整列出所有命令。每行都包含已核对的输入语法、取数边界、返回语义、分页/顺序、输出协商方式以及源码/测试入口；语法结论均来自实现、解析器与对应测试，不由命令名称推断。

| 稳定码 | 名称 / 域 | 匹配 | 输入 / 选项 | 实际来源与优先级 | 返回字段 / 语义 | 分页 / 排序 | 输出模式 | Service、Repository 与测试 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
${tableRows.join('\n')}

## 动态与订阅身份

- WorldState 命令读取最近一次成功的不可变实时快照，静态官方关系树仅用作节点、任务、阵营、挑战、奖励池等代码簿；刷新失败保留旧快照。显示字段保持动态响应原始业务值，未解析引用携带原值和状态。
- 仲裁排期由独立配置的运行时任务拉取并解析，按 activation/expiry 过滤；当前任务使用 \`activation <= now < expiry\`。今日视图使用配置时区自然日；未来/高效视图按 Service 顺序和上限返回，时间覆盖缺口必须显示，不推断无任务。
- 周紫卡按平台分别刷新和缓存，兼容武器名称经 NFKC、空白折叠和大小写归一后只映射到唯一静态 uniqueName；失败时保留上一份有效快照并标陈旧。
- 群组订阅身份以群组、成员、规则类型和标准化规则键为边界；动态事件使用稳定事件键记忆，不以列表整体或剩余时间作身份。静态代码簿仅补全展示，不创建动态任务。

## 当前验证入口

- 官方文件及字段关系：[字段矩阵](${path.join(repo, 'docs/reference/官方JSON与模型目录.md')})、[审核与维护语义](${path.join(repo, 'docs/Warframe数据模型与导入.md')})。
- 数据源、命令和动态边界：[数据流向说明](${path.join(repo, 'docs/Warframe数据模型与导入.md')})、[命令实现说明](${semanticsPath})。
- 官方整树验证：\`OfficialModelTreeTest\` 对当前 manifest 执行字段类型覆盖、代表树写入/读取、根分页、关联筛选与整树删除。
- 管理维护验证：\`OfficialImportApprovalTransactionTest\`、\`OfficialRivenWeaponTreeTest\`、\`CommunityDataTransactionTest\` 覆盖官方待重建、自维护整树优先与白名单批量事务。
- 动态回归：\`WarframeWorldStateServiceImplTest\`、\`ReferenceWorldStateCommandsTest\`、\`WarframeArbitrationRefreshServiceImplTest\`、\`WarframeArbitrationServiceImplTest\`、\`WarframeRivenWeeklyServiceTest\`、\`MarketPluginTest\` 与 \`MarketRivenPluginTest\`。
- 本文描述的是当前工作区源码合同；管理员数据库的触发词、命令启用和输出模板需以运行配置为准，不由本表预设。
`;

document = document.replace(/\]\(([^)]+)\)/g, (match, target) => {
  if (!path.isAbsolute(target)) return match;
  return `](${path.relative(path.dirname(outputPath), target).split(path.sep).join('/')})`;
});

const tableHeader = '| 稳定码 | 名称 / 域 | 匹配 | 输入 / 选项 | 实际来源与优先级 | 返回字段 / 语义 | 分页 / 排序 | 输出模式 | Service、Repository 与测试 |';
const tableLines = document.split(tableHeader)[1]?.split('\n').slice(2).filter(line => line.startsWith('|')) ?? [];
const tableCodes = tableLines.map(line => line.match(/^\| `([^`]+)` \|/)?.[1]);
if (tableLines.length !== commands.length || tableCodes.some((code, index) => code !== commands[index]?.code)) {
  throw new Error('Generated command table does not exactly match the registered catalog');
}
for (const line of tableLines) {
  if (!line.endsWith('|') || [...line.matchAll(/(?<!\\)\|/g)].length !== 10) {
    throw new Error(`Malformed command table row: ${line.slice(0, 100)}`);
  }
  if (line.includes('未审计') || line.includes('部分核对')) {
    throw new Error(`Command audit contains an unresolved placeholder: ${line.slice(0, 100)}`);
  }
}
for (const [, target] of document.matchAll(/\]\(([^)]+)\)/g)) {
  if (!fs.existsSync(path.resolve(path.dirname(outputPath), target))) throw new Error(`Broken local document link: ${target}`);
}

fs.writeFileSync(outputPath, document, 'utf8');
console.log(`Wrote ${outputPath} (${commands.length} commands, ${document.length} characters)`);
