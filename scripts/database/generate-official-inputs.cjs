const fs = require('node:fs');
const { methodComment } = require('./official-method-comments.cjs');
const path = require('node:path');
const crypto = require('node:crypto');
const { officialFieldMeaning } = require('./official-field-comments.cjs');

/** Generate typed JSON inputs, independently of JPA rows and table-flattening state. */
function generateOfficialInputs(root, models) {
  const byTable = new Map(models.map(model => [model.table, model]));
  const base = path.join(root, 'nnm-warframe/src/main/java/com/nnm/bot/warframe');
  const output = path.join(base, 'persistent/official/pojo/dto');
  const expected = new Set();
  const bindings = [];
  const header = {
    officialName: ['nameEn', 'displayNameEn', 'abilityName', 'name', 'node', 'text'],
    localizedName: ['zhName', 'nameZh', 'displayNameZh', 'zhAbilityName', 'abilityNameZh', 'nodeZh', 'zhText'],
    parentName: ['parentName'], productCategory: ['productCategory'],
    description: ['zhDescription', 'descriptionZh', 'descriptionEn', 'description']
  };
  const pascal = value => value.replace(/[^a-zA-Z0-9]+/g, '_').split('_').filter(Boolean)
    .map(part => part[0].toUpperCase() + part.slice(1)).join('') || 'Value';
  const propertyName = value => value.split(/[^a-zA-Z0-9]+/).filter(Boolean).map((part, index) => {
    const normalized = /^[A-Z0-9]+$/.test(part) ? part.toLowerCase() : part;
    return index === 0 ? normalized[0].toLowerCase() + normalized.slice(1)
      : normalized[0].toUpperCase() + normalized.slice(1);
  }).join('') || 'value';
  const hash = value => crypto.createHash('sha256').update(value).digest('hex').slice(0, 8);
  for (const model of models.filter(model => !model.parentTable)) {
    const domain = model.packageName.split('.').at(-1);
    const className = model.className.replace(/Root$/, 'DTO');
    const packageName = 'com.nnm.bot.warframe.persistent.official.pojo.dto.' + domain;
    const classes = [];
    const names = new Set([className]);
    function compileObject(field, name, pointer, top) {
      const fields = [];
      const properties = new Map();
      const used = new Set();
      for (const child of field.children) {
        const key = child.path.at(-1);
        let property = propertyName(key);
        if (['class', 'default', 'new', 'enum', 'package', 'private', 'public', 'static', 'null', 'true', 'false'].includes(property)) property += 'Value';
        if (used.has(property)) property += '_' + hash(key);
        used.add(property); properties.set(key, property);
        const fieldType = type(child, pointer + '/' + key);
        const meaning = officialFieldMeaning([...pointer.split('/').filter(Boolean), key]);
        fields.push('        /** ' + meaning + '；保留该字段的原始类型与缺省状态。 */',
          '        @JsonProperty(' + JSON.stringify(key) + ')',
          ...(fieldType.includes('Void') ? ['        @JsonDeserialize(' + (fieldType === 'Void' ? 'using' : 'contentUsing') + ' = OfficialNullDeserializer.class)'] : []),
          '        private ' + fieldType + ' ' + property + ';', '',
          methodComment('记录' + meaning + '是否出现，同时区分未提供与显式空值。', { value: meaning + '，保留协议中的显式空值。' }, null, '        '),
          '        @JsonSetter(' + JSON.stringify(key) + ')',
          '        public void set' + property[0].toUpperCase() + property.slice(1) + '(' + fieldType + ' value) {',
          '            this.' + property + ' = value;',
          '            markPresent(' + JSON.stringify(key) + ');', '        }', '');
      }
      const methods = [];
      if (top) {
        const expressions = Object.values(header).map(keys => {
          const values = keys.filter(key => properties.has(key)).map(key => properties.get(key));
          return values.length ? 'OfficialInputText.first(' + values.join(', ') + ')' : 'null';
        });
        methods.push(methodComment('提取本记录的常用检索字段，不携带 JPA 受管状态。', {}, '名称、分类和说明等检索摘要；上游缺省字段保持为空。', '        '), '        @Override', '        public OfficialRecordHeaderDTO header() {',
          '            return new OfficialRecordHeaderDTO(' + expressions.join(', ') + ');', '        }', '');
        const identityKeys = ['recordKey', 'uniqueName', 'nodeKey', 'poolId', 'id', 'slug'];
        const identityValues = identityKeys.filter(key => properties.has(key)).map(key => properties.get(key));
        methods.push(methodComment('读取上游显式业务键，不使用数组位置生成身份。', {}, '上游提供的首个稳定业务键；未提供时为空。', '        '), '        @Override', '        public String stableKey() {',
          '            return ' + (identityValues.length ? 'OfficialInputText.first(' + identityValues.join(', ') + ')' : 'null') + ';', '        }', '');
      }
      classes.push({ name, top, body: [...fields, ...methods] });
      return name;
    }
    function type(field, pointer) {
      const types = field.types.filter(type => type !== 'null');
      if (types.length === 0) return 'Void';
      if (types.length !== 1) {
        if (model.domain === 'BOUNTY' && pointer === '/stages/*') {
          compileObject(field, 'RewardPoolDTO', pointer, false);
          return 'OfficialBountyStageDTO';
        }
        throw new Error('未定义类型化联合字段：' + model.domain + pointer + ' ' + types.join(','));
      }
      switch (types[0]) {
        case 'string': return 'String';
        case 'number': return 'BigDecimal';
        case 'boolean': return 'Boolean';
        case 'array': return 'List<' + type(byTable.get(field.relation).fields, pointer + '/*') + '>';
        case 'object':
          if (field.dictionary) return 'Map<String, ' + type(byTable.get(field.relation).fields, pointer + '/{key}') + '>';
          {
            let name = pascal(pointer.split('/').filter(Boolean).join('_')) + 'DTO';
            if (names.has(name)) name += '_' + hash(pointer);
            names.add(name);
            return compileObject(field, name, pointer, false);
          }
        default: throw new Error('未知官方字段类型：' + types[0]);
      }
    }
    compileObject(model.fields, className, '', true);
    const top = classes.find(value => value.top);
    const imports = [
      'com.fasterxml.jackson.annotation.JsonAutoDetect', 'com.fasterxml.jackson.annotation.JsonProperty', 'com.fasterxml.jackson.annotation.JsonSetter',
      'com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO',
      'com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO',
      'com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO',
      'lombok.Getter'
    ];
    const body = classes.flatMap(value => value.body).join('\n');
    if (body.includes('OfficialInputText.')) imports.push('com.nnm.bot.warframe.persistent.official.parser.input.OfficialInputText');
    if (body.includes('Void')) imports.push('com.fasterxml.jackson.databind.annotation.JsonDeserialize',
      'com.nnm.bot.warframe.persistent.official.parser.input.OfficialNullDeserializer');
    if (body.includes('OfficialBountyStageDTO')) imports.push('com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.OfficialBountyStageDTO');
    if (body.includes('BigDecimal')) imports.push('java.math.BigDecimal');
    if (body.includes('List<')) imports.push('java.util.List');
    if (body.includes('Map<')) imports.push('java.util.Map');
    const annotation = '@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,\n'
      + '        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)';
    const lines = ['package ' + packageName + ';', '', ...imports.sort().map(value => 'import ' + value + ';'), '',
      '/** ' + model.domain + ' 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */',
      '@Getter', annotation, 'public class ' + className + ' extends OfficialObjectDTO implements OfficialRecordDTO {',
      ...top.body.flatMap(line => line.split('\n')).map(line => line.startsWith('    ') ? line.slice(4) : line)];
    for (const nested of classes.filter(value => !value.top)) {
      lines.push('    /** 官方嵌套对象 ' + nested.name + ' 的固定字段。 */', '    @Getter', '    ' + annotation.replaceAll('\n', '\n    '),
        '    public static class ' + nested.name + ' extends OfficialObjectDTO {', ...nested.body, '    }', '');
    }
    lines.push('}', '');
    const directory = path.join(output, domain); fs.mkdirSync(directory, { recursive: true });
    const file = path.join(directory, className + '.java'); expected.add(file); fs.writeFileSync(file, lines.join('\n'));
    bindings.push({ domain: model.domain, className, packageName });
  }
  function prune(directory) {
    if (!fs.existsSync(directory)) return;
    for (const entry of fs.readdirSync(directory, { withFileTypes: true })) {
      const file = path.join(directory, entry.name);
      if (entry.isDirectory()) prune(file);
      else if (entry.name.endsWith('.java') && !expected.has(file)
          && fs.readFileSync(file, 'utf8').includes('由生成器和契约测试共同维护')) fs.unlinkSync(file);
    }
  }
  prune(output);
  const registry = path.join(base, 'persistent/official/parser/input/OfficialInputTypes.java');
  const lines = ['package com.nnm.bot.warframe.persistent.official.parser.input;', '',
    'import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;',
    'import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;',
    ...bindings.map(value => 'import ' + value.packageName + '.' + value.className + ';').sort(), '',
    '/** 每个官方数据 JSON 的显式输入类型；由生成器和契约测试共同维护。 */',
    'public final class OfficialInputTypes {', methodComment('禁止实例化逐文件输入类型登记表。'), '    private OfficialInputTypes() {', '    }', '',
    methodComment('取得官方 JSON 所属的具体输入类型。', { domain: '已经确认的官方文件领域。' }, '该文件独立的 DTO 类型；未登记领域直接失败。'),
    '    public static Class<? extends OfficialRecordDTO> type(WarframeOfficialDataDomain domain) {',
    '        return switch (domain) {', ...bindings.map(value => '            case ' + value.domain + ' -> ' + value.className + '.class;'),
    '            case OTHER -> throw new IllegalArgumentException("Unknown official dataset");', '        };', '    }', '}', ''];
  fs.writeFileSync(registry, lines.join('\n'));
  return bindings.length;
}
module.exports = { generateOfficialInputs };
