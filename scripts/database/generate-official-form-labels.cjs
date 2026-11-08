const fs = require('node:fs');
const path = require('node:path');
const { officialFieldMeaning } = require('./official-field-comments.cjs');

// 复用 71 份官方 JSON 的既有中文字段合同，表单标签与类型化输入同步生成。
function generateOfficialFormLabels(root, models) {
  const byTable = new Map(models.map(model => [model.table, model]));
  const labels = {};
  for (const model of models.filter(item => !item.parentTable)) {
    const domainLabels = {};
    function visit(container, parts) {
      for (const child of container.children) {
        const key = child.path.at(-1);
        const current = [...parts, key];
        domainLabels[current.join('/')] = officialFieldMeaning(current);
        if (child.relation || child.children.length) visit(child, current);
      }
      if (container.relation) {
        const marker = container.types.includes('array') ? '*' : '{key}';
        visit(byTable.get(container.relation).fields, [...parts, marker]);
      }
    }
    visit(model.fields, []);
    labels[model.domain] = domainLabels;
  }
  const output = path.join(root, 'nnm-warframe/src/main/resources/warframe/official-form-labels.json');
  fs.writeFileSync(output, JSON.stringify(labels, null, 2) + '\n');
  return labels;
}

module.exports = { generateOfficialFormLabels };
if (require.main === module) {
  const root = path.resolve(__dirname, '../..');
  const models = JSON.parse(fs.readFileSync(path.join(root,
    'nnm-warframe/src/main/resources/warframe/official-models.json'), 'utf8')).models;
  const labels = generateOfficialFormLabels(root, models);
  console.log(`Generated ${Object.keys(labels).length} official form label domains.`);
}
