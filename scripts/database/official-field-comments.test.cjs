const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const { test } = require('node:test');
const { officialFieldMeaning } = require('./official-field-comments.cjs');
const root = path.resolve(__dirname, '../..');

test('字段注释解释中文含义，而不是重复协议键或使用通用占位文本', () => {
  assert.equal(officialFieldMeaning('drawCount'), '抽取次数');
  assert.equal(officialFieldMeaning('nameEn'), '英文名称');
  assert.equal(officialFieldMeaning('item__nameZh'), '物品的中文名称');
  assert.equal(officialFieldMeaning(['states', '{key}', 'refinement']), '状态集合的精炼等级');
  assert.throws(() => officialFieldMeaning('unknownFutureField'), /缺少官方字段中文释义/);
});

test('所有七十一个官方文件及生成式子表字段都有明确中文释义', () => {
  const contract = JSON.parse(fs.readFileSync(path.join(root,
    'nnm-warframe/src/main/resources/warframe/official-models.json'), 'utf8'));
  assert.equal(contract.models.filter(model => !model.parentTable).length, 71);
  let fields = 0;
  function verify(field) {
    if (field.path.length) {
      const meaning = officialFieldMeaning(field.path);
      assert.match(meaning, /[\u4e00-\u9fff]/);
      assert.doesNotMatch(meaning, /扩展属性|官方字段|源业务字段/);
      fields++;
    }
    field.children.forEach(verify);
  }
  for (const model of contract.models) {
    verify(model.fields);
    const source = fs.readFileSync(path.join(root, 'nnm-warframe/src/main/java',
      ...model.packageName.split('.'), model.className + '.java'), 'utf8');
    assert.doesNotMatch(source, /官方字段\s+[a-zA-Z]|源业务字段|扩展属性/);
    if (model.parentTable) {
      assert.match(source, /@JoinColumn\(name = "bkey"/);
      assert.doesNotMatch(source, /parent_id/);
    }
  }
  assert.ok(fields > 1000);
});
