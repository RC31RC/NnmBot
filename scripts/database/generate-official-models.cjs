#!/usr/bin/env node
const { methodComment } = require('./official-method-comments.cjs');
const fs = require('node:fs');
const path = require('node:path');
const crypto = require('node:crypto');
const { officialFieldMeaning } = require('./official-field-comments.cjs');
const root = path.resolve(__dirname, '../..');
const source = path.resolve(process.argv[2] ?? path.join(root, '../warframe-datasource-nnm/data/latest'));
const javaRoot = path.join(root, 'nnm-warframe/src/main/java/com/nnm/bot/warframe/persistent/official/pojo/entity');
const resources = path.join(root, 'nnm-warframe/src/main/resources/warframe');
const manifest = JSON.parse(fs.readFileSync(path.join(source, 'manifest.json'), 'utf8'));
const domains = new Map([...fs.readFileSync(path.join(root,
  'nnm-warframe/src/main/java/com/nnm/bot/warframe/persistent/official/constant/WarframeOfficialDataDomain.java'), 'utf8')
  .matchAll(/^    ([A-Z_0-9]+)\("([^"]+\.json)"/gm)].map(match => [match[2], match[1]]));
const scalar = value => value === null ? 'null' : Array.isArray(value) ? 'array' : typeof value;
const identifier = value => value.replace(/[^a-zA-Z0-9]+/g, '_').replace(/^_+|_+$/g, '') || 'value';
const camel = value => identifier(value).split('_').map(part => part[0].toUpperCase() + part.slice(1)).join('');
const propertyName = value => identifier(value).split('_').filter(Boolean).map((part, index) => {
  const normalized = /^[A-Z0-9]+$/.test(part) ? part.toLowerCase() : part;
  return index === 0 ? normalized[0].toLowerCase() + normalized.slice(1)
    : normalized[0].toUpperCase() + normalized.slice(1);
}).join('') || 'value';
const digest = value => crypto.createHash('sha256').update(value).digest('hex').slice(0, 10);
const shorten = (value, length) => value.length <= length ? value : value.slice(0, length - 11) + '_' + digest(value);
function shape() { return { types: new Set(), fields: new Map(), item: null, dictionary: false }; }
function observe(node, value, pointer) {
  const type = scalar(value);
  node.types.add(type);
  if (type === 'array') {
    node.item ??= shape();
    for (const item of value) observe(node.item, item, pointer + '/*');
  } else if (type === 'object') {
    const entries = Object.entries(value);
    const dictionary = node.dictionary || pointer.endsWith('/i18n') || pointer.endsWith('/states')
      || entries.length > 64 || entries.some(([key]) => key.startsWith('/') || /^\d+$/.test(key));
    if (dictionary && !node.dictionary) {
      if (node.fields.size) throw new Error(`Dictionary classification changed at ${pointer}; add an explicit contract`);
      node.dictionary = true;
    }
    if (node.dictionary) {
      node.item ??= shape();
      for (const [, item] of entries) observe(node.item, item, pointer + '/{key}');
    } else {
      for (const [key, item] of entries) {
        if (!node.fields.has(key)) node.fields.set(key, shape());
        observe(node.fields.get(key), item, pointer + '/' + key);
      }
    }
  }
}
function records(domain, document) {
  if (domain.startsWith('USAGE_')) return document;
  if (domain === 'REGION') return [...document.systems.flatMap(system => system.nodes),
    { systems: document.systems.map(({ nodes, ...system }) => system), nodeCount: document.nodeCount, systemCount: document.systemCount }];
  if (['DOJO_RECIPE', 'ENEMY', 'CODEX'].includes(domain)) return Object.values(document).flatMap(group => Object.entries(group)
    .map(([poolKey, value]) => Array.isArray(value) ? { poolKey, stages: value } : value));
  if (domain === 'RELIC') return [...document.relics, Object.fromEntries(Object.entries(document).filter(([key]) => key !== 'relics'))];
  if (domain === 'RIVEN_WEAPON') return [...document.weapons, Object.fromEntries(Object.entries(document).filter(([key]) => key !== 'weapons'))];
  if (domain === 'ACTIVITY') {
    const result = [];
    for (const name of ['types', 'conditions', 'personalModifiers']) result.push(...Object.values(document["conquests__" + name]));
    for (const group of Object.values(document.conquests__missionVariants)) result.push(...Object.values(group));
    for (const name of ['challenges', 'gameModes', 'protoframes']) result.push(...Object.values(document["descents__" + name]));
    return result;
  }
  if (domain === 'NIGHTWAVE') return [...Object.values(document.challenges), ...document.rewards,
    Object.fromEntries(Object.entries(document).filter(([key]) => !['challenges', 'rewards'].includes(key)))];
  if (domain === 'BOUNTY') return [...Object.values(document.definitions), ...Object.entries(document.rewardPools)
    .map(([poolKey, value]) => Array.isArray(value) ? { poolKey, stages: value } : value),
    Object.fromEntries(Object.entries(document).filter(([key]) => !['definitions', 'rewardPools'].includes(key)))];
  if (domain === 'DROP_TABLE') return [...document.contexts, ...document.regionResources,
    Object.fromEntries(Object.entries(document).filter(([key]) => !['contexts', 'regionResources'].includes(key)))];
  if (Array.isArray(document)) return document;
  return Object.values(document).map(value => Array.isArray(value) ? { stages: value, poolKey: '' } : value);
}
const mainColumns = ["maintenance_type varchar(20) DEFAULT 'OFFICIAL' NOT NULL", "bkey varchar(500) NOT NULL", "official_name varchar(500)", "localized_name varchar(500)", "parent_name varchar(500)", "product_category varchar(160)", "exclude_from_codex boolean", "description text", "long_description text", "wiki_slug varchar(300)", "market_slug varchar(300)", "raw_json text NOT NULL", "localized_raw_json text", "enabled boolean DEFAULT true NOT NULL", "created_at timestamp without time zone NOT NULL", "updated_at timestamp without time zone NOT NULL"];
const mainComments = {"maintenance_type": "记录维护状态：OFFICIAL 为上游同步，SELF_MAINTAINED 为自维护；两者均属于官方数据模型，不表示来源类别", "id": "独立 JSON 主表记录主键", "bkey": "当前独立 JSON 内稳定业务键；维护状态与业务键共同唯一标识一条记录", "official_name": "官方英文名称", "localized_name": "官方本地化名称", "parent_name": "官方父级名称", "product_category": "官方产品分类", "exclude_from_codex": "是否排除在 Codex 之外", "description": "官方短描述", "long_description": "官方长描述", "wiki_slug": "灰机 Wiki 页面路径", "market_slug": "Warframe Market slug", "raw_json": "官方英文原始 JSON", "localized_raw_json": "用于本地化差异审核的原始 JSON", "enabled": "是否参与运行时解析", "created_at": "首次写入时间", "updated_at": "最后更新时间"};
const models = [];
const sql = ['-- BEGIN GENERATED OFFICIAL MODELS',
  '-- 独立主表共享主键分配序列，无跨 JSON 总父表。',
  'CREATE SEQUENCE nnmbot.wf_official_row_id_seq INCREMENT BY 50;',
  "COMMENT ON SEQUENCE nnmbot.wf_official_row_id_seq IS '官方主子表唯一 ID 分配；不表示数据关联';",
  '-- 按源领域生成固定业务列；子表 bkey 保存直接父表 id，JPA 维护逻辑关联，不建数据库外键。'];
// 与 SQL 关键字冲突的源字段保留其 Java 属性，仅为持久化列追加值后缀。
const sqlKeywords = new Set(['all', 'and', 'as', 'asc', 'by', 'case', 'check', 'column', 'constraint', 'default', 'desc', 'distinct', 'end', 'false', 'from', 'group', 'having', 'in', 'index', 'is', 'join', 'key', 'limit', 'not', 'null', 'offset', 'on', 'or', 'order', 'primary', 'references', 'select', 'table', 'then', 'true', 'union', 'unique', 'user', 'using', 'value', 'values', 'when', 'where', 'year']);
const types = { string: ['String', 'TEXT'], number: ['BigDecimal', 'NUMERIC'], boolean: ['Boolean', 'boolean'] };
function compile(domain, node, parts, parentTable) {
  const suffix = parts.length ? parts.join('_') : 'root';
  const table = shorten('wf_official_' + domain.toLowerCase() + '_' + identifier(suffix).toLowerCase(), 63);
  const className = shorten('Official' + camel(domain.toLowerCase()) + camel(suffix), 150).replaceAll('_', '');
  const packageName = 'com.nnm.bot.warframe.persistent.official.pojo.entity.' + [...domains].find(([, code]) => code === domain)[0].split('/')[0];
  const model = { domain, path: parts.join('/'), table, className, packageName, parentTable, fields: [], children: [], columns: [], types: [...node.types] };
  models.push(model);
  const properties = new Set();
  function uniqueProperty(name, identity) {
    const reserved = new Set(['id', 'bkey', 'memberKey', 'sortOrder', 'parent',
      ...(parentTable ? [] : mainColumns.map(column => column.split(' ')[0].replace(/_([a-z])/g, (_, letter) => letter.toUpperCase())))]);
    if (reserved.has(name)) name += 'Value';
    const candidate = properties.has(name) ? name + digest(identity) : name;
    if (properties.has(candidate)) throw new Error(`Duplicate Java property ${table}.${candidate}`);
    properties.add(candidate);
    return candidate;
  }
  function property(node, jsonPath, prefix) {
    const meaning = officialFieldMeaning(jsonPath.length ? jsonPath : [prefix]);
    const field = { path: jsonPath, types: [...node.types], state: uniqueProperty(propertyName(prefix) + 'State', JSON.stringify(jsonPath) + ':state'), values: {}, children: [] };
    const baseName = identifier(prefix).toLowerCase();
    const name = shorten(mainColumns.some(column => column.startsWith(baseName + ' ')) || sqlKeywords.has(baseName) || ['id', 'bkey', 'member_key', 'sort_order'].includes(baseName) ? baseName + '_value' : baseName, 52);
    const state = { property: field.state, column: name + '_state', type: 'Short', sql: 'smallint', comment: meaning + '是否缺失、为空或属于特定 JSON 类型' };
    model.columns.push(state);
    const supported = [...node.types].filter(type => types[type]);
    for (const type of supported) {
      const property = uniqueProperty(propertyName(prefix) + (supported.length > 1 ? camel(type) : ''), JSON.stringify(jsonPath) + ':' + type);
      const column = name + (supported.length > 1 ? '_' + type : '');
      field.values[type] = property;
      model.columns.push({ property, column, type: types[type][0], sql: types[type][1], comment: meaning });
    }
    if (node.types.has('object') && !node.dictionary) {
      for (const [key, child] of [...node.fields].sort(([a], [b]) => a.localeCompare(b))) {
        field.children.push(property(child, [...jsonPath, key], jsonPath.length === 0 ? key : prefix + '_' + key));
      }
    }
    if (node.types.has('array') || node.dictionary) {
      const child = compile(domain, node.item ?? shape(), [...parts, ...jsonPath, node.dictionary ? 'entries' : 'items'], table);
      field.relation = child.table;
      field.dictionary = node.dictionary;
      model.children.push(child.table);
    }
    return field;
  }
  if (node.types.size === 0) node.types.add('null');
  model.fields = property(node, [], 'root');
  model.types = [...node.types];
  const persistence = ['Column', 'Entity', 'Table',
    ...(parentTable ? ['ManyToOne', 'FetchType', 'JoinColumn', 'ForeignKey', 'ConstraintMode'] : []),
    ...(model.children.length ? ['OneToMany', 'CascadeType'] : [])];
  const java = ['package ' + packageName + ';', '',
    'import com.nnm.bot.warframe.persistent.official.pojo.entity.support.' + (parentTable ? 'OfficialChildRow' : 'OfficialRecord') + ';',
    ...persistence.sort().map(name => 'import jakarta.persistence.' + name + ';'),
    ...(model.children.length ? ['import java.util.List;', 'import java.util.ArrayList;'] : []),
    'import lombok.Getter;', 'import lombok.Setter;',
    ...(model.columns.some(column => column.type === 'BigDecimal') ? ['import java.math.BigDecimal;'] : []), '',
    '/** 源 ' + domain + ' ' + (parts.join('/') || '独立主表') + ' 的固定业务列；由生成器和契约测试共同维护。 */',
    '@Getter', '@Setter', '@Entity', '@Table(name = "' + table + '")',
    'public class ' + className + ' extends ' + (parentTable ? 'OfficialChildRow' : 'OfficialRecord') + ' {'];
  const names = new Set();
  for (const column of model.columns) {
    if (names.has(column.column)) throw new Error('Duplicate column ' + table + '.' + column.column);
    names.add(column.column);
    java.push('    /** ' + column.comment + '。 */', '    @Column(name = "' + column.column + '"' + (column.type === 'String' ? ', columnDefinition = "TEXT"' : column.type === 'BigDecimal' ? ', columnDefinition = "NUMERIC"' : '') + ')',
      '    private ' + column.type + ' ' + column.property + ';', '');
  }
  java.push('}', '');
  model.java = java;
  sql.push('CREATE TABLE nnmbot.' + table + ' (',
    "    id bigint DEFAULT nextval('nnmbot.wf_official_row_id_seq') PRIMARY KEY,",
    ...(parentTable ? ['    bkey bigint NOT NULL,', '    member_key TEXT,', '    sort_order integer NOT NULL,']
      : mainColumns.map(column => '    ' + column + ',')),
    model.columns.map(column => '    ' + column.column + ' ' + column.sql).join(',\n'), ');',
    "COMMENT ON TABLE nnmbot." + table + " IS '" + domain + ' ' + (parts.join('/') || '独立主表') + "; 不建数据库外键';",
    "COMMENT ON COLUMN nnmbot." + table + ".id IS '本表记录 ID；序列只保证唯一，不关联其他 JSON 主表';");
  if (parentTable) {
    sql.push("COMMENT ON COLUMN nnmbot." + table + ".bkey IS '直接父记录的本地主键；与父表主键相同';",
      "COMMENT ON COLUMN nnmbot." + table + ".member_key IS '字典原始成员键；数组成员为空';",
      "COMMENT ON COLUMN nnmbot." + table + ".sort_order IS '源成员展示顺序，不参与自然身份';");
  } else {
    for (const column of mainColumns) {
      const name = column.split(' ')[0];
      const comment = name === 'maintenance_type' ? '记录维护状态 OFFICIAL 或 SELF_MAINTAINED；来源类别均为官方模型'
        : mainComments[name];
      if (!comment) throw new Error('Missing main column comment: ' + name);
      sql.push("COMMENT ON COLUMN nnmbot." + table + '.' + name + " IS '" + comment + "';");
    }
    const identity = shorten('uq_' + table + '_identity', 63);
    sql.push('-- 在本 JSON 主表中按维护状态和业务键区分官方记录与自维护记录。',
      'CREATE UNIQUE INDEX ' + identity + ' ON nnmbot.' + table + ' (maintenance_type, bkey);',
      "COMMENT ON INDEX nnmbot." + identity + " IS '本独立 JSON 表维护状态与业务键唯一';");
    const search = shorten('idx_' + table + '_names', 63);
    sql.push('-- 支持本数据集名称检索与稳定排序。',
      'CREATE INDEX ' + search + ' ON nnmbot.' + table + ' (official_name, localized_name);',
      "COMMENT ON INDEX nnmbot." + search + " IS '数据集记录名称查询';");
  }
  for (const column of model.columns) sql.push("COMMENT ON COLUMN nnmbot." + table + '.' + column.column
    + " IS '" + (column.type === 'Short' ? column.comment + '（0 缺失、1 空值、2 字符串、3 数值、4 布尔、5 对象、6 数组）' : column.comment)
    .replaceAll("'", "''") + "';");
  if (parentTable) {
    const index = shorten('idx_' + table + '_bkey', 63);
    sql.push('-- 按 bkey 批量加载和删除本 JSON 内的直接子表记录。',
      'CREATE INDEX ' + index + ' ON nnmbot.' + table + ' (bkey);',
      "COMMENT ON INDEX nnmbot." + index + " IS '本 JSON 内父子关系查询';");
    if (parts.at(-1) === 'entries') {
      const unique = shorten('uq_' + table + '_bkey_identity', 63);
      sql.push('-- 字典成员在直接父记录内按原始键唯一。',
        'CREATE UNIQUE INDEX ' + unique + ' ON nnmbot.' + table + ' (bkey, member_key);',
        "COMMENT ON INDEX nnmbot." + unique + " IS '父记录内字典成员键唯一';");
    }
  }
  if (domain === 'RIVEN_WEAPON' && parts.join('/') === 'marketListings/items') {
    sql.push('-- 同一裂罅武器下 Market listing id 必须唯一，防止导入重复商品身份。',
      'CREATE UNIQUE INDEX uq_wf_official_riven_listing_identity ON nnmbot.' + table + ' (bkey, id_value);',
      "COMMENT ON INDEX nnmbot.uq_wf_official_riven_listing_identity IS '父武器内唯一 Market 商品 id';", '');
  }
  return model;
}
fs.mkdirSync(javaRoot, { recursive: true });
fs.mkdirSync(resources, { recursive: true });
const roots = {};
for (const entry of manifest.files) {
  const domain = domains.get(entry.path);
  if (!domain) throw new Error('No domain for ' + entry.path);
  const node = shape();
  const document = JSON.parse(fs.readFileSync(path.join(source, entry.path), 'utf8'));
  for (const record of records(domain, document)) observe(node, record, '');
  const model = compile(domain, node, [], null);
  roots[domain] = model.table;
  console.log(entry.path + ': ' + models.filter(model => model.domain === domain).length + ' relation tables');
}
// Generate owning associations only after every parent and child model is known.
const byTable = new Map(models.map(model => [model.table, model]));
for (const model of models) {
  const parent = model.parentTable ? byTable.get(model.parentTable).className : null;
  const links = parent ? [
    '    @ManyToOne(fetch = FetchType.LAZY, optional = false)',
    '    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))',
    '    private ' + parent + ' parent;', ''
  ] : [];
  for (const table of model.children) {
    const child = byTable.get(table);
    links.push('    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)',
      '    private List<' + child.className + '> ' + child.className[0].toLowerCase() + child.className.slice(1) + 'Rows = new ArrayList<>();', '');
  }
  const text = model.java.slice(0, -2).join('\n') + '\n' + links.join('\n') + '}\n';
  delete model.java;
  const directory = path.join(javaRoot, model.packageName.split('.').at(-1));
  fs.mkdirSync(directory, { recursive: true });
  fs.writeFileSync(path.join(directory, model.className + '.java'), text);
}
// One concrete JpaRepository per JSON main table; no repository is generated for children.
for (const model of models.filter(model => !model.parentTable)) {
  const category = model.packageName.split('.').at(-1);
  const directory = path.join(root, 'nnm-warframe/src/main/java/com/nnm/bot/warframe/persistent/official/repository', category);
  fs.mkdirSync(directory, { recursive: true });
  const name = model.className.replace(/Root$/, '') + 'Repository';
  const fragment = model.domain === 'RELIC' ? ', OfficialRelicRepositoryCustom' : '';
  const text = 'package com.nnm.bot.warframe.persistent.official.repository.' + category + ';\n\n'
    + 'import ' + model.packageName + '.' + model.className + ';\n'
    + 'import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;\n'
    + 'import com.nnm.bot.warframe.persistent.official.repository.support.OfficialMainRepository;\n'
    + (fragment ? 'import com.nnm.bot.warframe.persistent.official.repository.rewards.custom.OfficialRelicRepositoryCustom;\n' : '')
    + '\n/** ' + [...domains].find(([, code]) => code === model.domain)[0] + ' 主表数据访问；由生成器和契约测试共同维护。 */\n'
    + 'public interface ' + name + ' extends OfficialMainRepository<' + model.className + '>' + fragment + ' {\n'
    + methodComment('取得本 Repository 对应的官方 JSON 领域。', {}, '当前独立 JSON 的领域标识。') + '\n'
    + '    @Override\n    default WarframeOfficialDataDomain domain() {\n        return WarframeOfficialDataDomain.' + model.domain + ';\n    }\n\n'
    + methodComment('创建本 JSON 主表的待写入记录。', {}, '尚未持久化的具体主表实体。') + '\n'
    + '    @Override\n    default ' + model.className + ' newRecord() {\n        return new ' + model.className + '();\n    }\n}\n';
  fs.writeFileSync(path.join(directory, name + '.java'), text);
}
const queryDslDirectory = path.join(root, 'nnm-warframe/src/main/java/com/nnm/bot/warframe/persistent/official/repository/support');
const queryDslPartSize = 8;
const queryDslParts = [];
for (let offset = 0; offset < models.length; offset += queryDslPartSize) {
  queryDslParts.push(models.slice(offset, offset + queryDslPartSize));
}
const queryDslCommonImports = [
  'package com.nnm.bot.warframe.persistent.official.repository.support;', '',
  'import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;',
  'import com.querydsl.core.types.Expression;',
  'import com.querydsl.core.types.dsl.EntityPathBase;',
  'import com.querydsl.core.types.dsl.NumberPath;',
  'import com.querydsl.jpa.impl.JPAQueryFactory;',
  'import java.util.Collection;',
  'import java.util.List;', ''
];
const generatedQueryDslFiles = new Set();
for (const [partIndex, part] of queryDslParts.entries()) {
  const className = 'OfficialRelationQueryDslPart' + String(partIndex).padStart(2, '0');
  const lines = [...queryDslCommonImports,
    ...part.map(model => 'import ' + model.packageName + '.Q' + model.className + ';'), '',
    '/** 官方关系表 QueryDSL 标量投影分片 ' + partIndex + '；由模型生成器维护。 */',
    'final class ' + className + ' {',
    '    private ' + className + '() { }', '',
    '    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {',
    '        return switch (table) {'];
  for (const model of part) {
    const qClass = 'Q' + model.className;
    const properties = ['id', 'bkey', ...model.columns.map(column => column.property)];
    const qFields = ['row.id', 'row.bkey', ...model.columns.map(column => 'row.' + column.property)];
    lines.push('            case "' + model.table + '" -> {',
      '                ' + qClass + ' row = new ' + qClass + '("row");',
      '                var projection = new OfficialRelationRowProjection(List.of(' + properties.map(value => '"' + value + '"').join(', ') + '),',
      '                        List.<Expression<?>>of(' + qFields.join(', ') + '));',
      model.parentTable
        ? '                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);'
        : '                yield selectRoot(query, row, row.id, ids, projection);',
      '            }');
  }
  lines.push('            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);',
    '        };', '    }', '',
    '    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {',
    '        return switch (table) {');
  for (const model of part.filter(model => model.parentTable)) {
    const qClass = 'Q' + model.className;
    lines.push('            case "' + model.table + '" -> {',
      '                ' + qClass + ' row = new ' + qClass + '("row");',
      '                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();',
      '            }');
  }
  lines.push('            default -> throw new IllegalArgumentException("Not an official child table: " + table);',
    '        };', '    }', '',
    '    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {',
    '        return switch (table) {');
  for (const model of part) {
    const qClass = 'Q' + model.className;
    lines.push('            case "' + model.table + '" -> {',
      '                ' + qClass + ' row = new ' + qClass + '("row");',
      '                yield query.delete(row).where(row.id.in(ids)).execute();',
      '            }');
  }
  lines.push('            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);',
    '        };', '    }', '',
    '    private static <T> List<OfficialRelationRowDTO> selectRoot(JPAQueryFactory query, EntityPathBase<T> row,',
    '            NumberPath<Long> id, Collection<Long> ids, OfficialRelationRowProjection projection) {',
    '        return query.select(projection).from(row).where(id.in(ids)).orderBy(id.asc()).fetch();',
    '    }', '',
    '    private static <T> List<OfficialRelationRowDTO> selectChildren(JPAQueryFactory query, EntityPathBase<T> row,',
    '            NumberPath<Long> id, NumberPath<Long> parentId, NumberPath<Integer> sortOrder, Collection<Long> ids,',
    '            OfficialRelationRowProjection projection) {',
    '        return query.select(projection).from(row).where(parentId.in(ids))',
    '                .orderBy(parentId.asc(), sortOrder.asc(), id.asc()).fetch();',
    '    }', '}', '');
  const file = className + '.java';
  generatedQueryDslFiles.add(file);
  fs.writeFileSync(path.join(queryDslDirectory, file), lines.join('\n'));
}
const queryDslFacadeImports = queryDslCommonImports.filter(line => ![
  'import com.querydsl.core.types.Expression;',
  'import com.querydsl.core.types.dsl.EntityPathBase;',
  'import com.querydsl.core.types.dsl.NumberPath;'
].includes(line));
const facade = [...queryDslFacadeImports,
  '/** 各官方关系表的强类型 QueryDSL 标量投影与批量 DML。由模型生成器维护。 */',
  'public final class OfficialRelationQueryDsl {',
  '    private OfficialRelationQueryDsl() { }', '',
  '    public static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {',
  '        if (ids.isEmpty()) return List.of();',
  '        return switch (part(table)) {'];
for (const [partIndex] of queryDslParts.entries()) {
  facade.push('            case ' + partIndex + ' -> OfficialRelationQueryDslPart' + String(partIndex).padStart(2, '0') + '.select(query, table, ids);');
}
facade.push('            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);',
  '        };', '    }', '',
  '    public static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {',
  '        if (parentIds.isEmpty()) return List.of();',
  '        return switch (part(table)) {');
for (const [partIndex] of queryDslParts.entries()) {
  facade.push('            case ' + partIndex + ' -> OfficialRelationQueryDslPart' + String(partIndex).padStart(2, '0') + '.selectChildIds(query, table, parentIds);');
}
facade.push('            default -> throw new IllegalArgumentException("Not an official child table: " + table);',
  '        };', '    }', '',
  '    public static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {',
  '        if (ids.isEmpty()) return 0;',
  '        return switch (part(table)) {');
for (const [partIndex] of queryDslParts.entries()) {
  facade.push('            case ' + partIndex + ' -> OfficialRelationQueryDslPart' + String(partIndex).padStart(2, '0') + '.deleteByIds(query, table, ids);');
}
facade.push('            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);',
  '        };', '    }', '',
  '    private static int part(String table) {', '        return switch (table) {');
for (const [partIndex, part] of queryDslParts.entries()) {
  for (const model of part) {
    facade.push('            case "' + model.table + '" -> ' + partIndex + ';');
  }
}
facade.push('            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);',
  '        };', '    }', '}', '');
fs.writeFileSync(path.join(queryDslDirectory, 'OfficialRelationQueryDsl.java'), facade.join('\n'));
for (const entry of fs.readdirSync(queryDslDirectory)) {
  if (entry.startsWith('OfficialRelationQueryDslPart') && entry.endsWith('.java') && !generatedQueryDslFiles.has(entry)) {
    fs.unlinkSync(path.join(queryDslDirectory, entry));
  }
}
// Remove only generator-owned classes that no longer belong to the source contract.
const expected = new Set(models.map(model => path.join(javaRoot, model.packageName.split('.').at(-1), model.className + '.java')));
function prune(directory) {
  for (const entry of fs.readdirSync(directory, { withFileTypes: true })) {
    const file = path.join(directory, entry.name);
    if (entry.isDirectory()) prune(file);
    else if (entry.name.endsWith('.java') && !expected.has(file) && fs.readFileSync(file, 'utf8').includes('由生成器和契约测试共同维护')) fs.unlinkSync(file);
  }
}
prune(javaRoot);
sql.push('-- END GENERATED OFFICIAL MODELS');
fs.writeFileSync(path.join(resources, 'official-models.json'), JSON.stringify({ version: 3, buildId: manifest.buildId, roots, models: models.map(model => ({ ...model, columns: model.columns.map(({ comment, ...column }) => column) })) }, null, 2) + '\n');
const sqlPath = path.join(root, 'docs/sql/init.sql');
const baseline = fs.readFileSync(sqlPath, 'utf8');
fs.writeFileSync(sqlPath, baseline.replace(/-- BEGIN GENERATED OFFICIAL MODELS[\s\S]*?-- END GENERATED OFFICIAL MODELS/, sql.join('\n')));
const inputs = require('./generate-official-inputs.cjs').generateOfficialInputs(root, models);
console.log('Generated ' + inputs + ' typed official JSON inputs.');
console.log('Generated ' + models.length + ' typed relation entities; SQL baseline updated.');
