const fs = require('node:fs');
const path = require('node:path');

const repo = path.resolve(__dirname, '../..');
const contractPath = path.join(repo, 'nnm-warframe/src/main/resources/warframe/official-models.json');
const outputPath = path.join(repo, 'docs/reference/官方JSON与模型目录.md');
const javaBase = path.join(repo, 'nnm-warframe/src/main/java/com/nnm/bot/warframe');

function modelRows(contract, base = javaBase) {
  const domains = fs.readFileSync(path.join(base, 'persistent/official/constant/WarframeOfficialDataDomain.java'), 'utf8');
  const files = new Map([...domains.matchAll(/^\s+(\w+)\("([^"]+\.json)"/gm)].map(match => [match[1], match[2]]));
  const mains = contract.models.filter(model => model.parentTable === null);
  if (mains.length !== 71 || Object.keys(contract.roots).length !== 71) {
    throw new Error('The official contract must contain 71 independent JSON main models');
  }
  const rows = mains.map(model => {
    const domain = model.packageName.split('.').at(-1);
    const stem = model.className.replace(/Root$/, '');
    const file = files.get(model.domain);
    if (!file || contract.roots[model.domain] !== model.table) throw new Error(`Missing JSON identity for ${model.domain}`);
    const entity = path.join(base, 'persistent/official/pojo/entity', domain, model.className + '.java');
    const repository = path.join(base, 'persistent/official/repository', domain, stem + 'Repository.java');
    const dto = path.join(base, 'persistent/official/pojo/dto', domain, stem + 'DTO.java');
    for (const source of [entity, repository, dto]) {
      if (!fs.existsSync(source)) throw new Error(`Missing model source: ${source}`);
    }
    if (!fs.readFileSync(repository, 'utf8').includes(`OfficialMainRepository<${model.className}>`)) {
      throw new Error(`Repository does not own the expected main model: ${repository}`);
    }
    return { domain: model.domain, file, table: model.table, entity, repository, dto,
      children: contract.models.filter(child => child.domain === model.domain && child.parentTable !== null).length };
  });
  if (new Set(rows.map(row => row.file)).size !== 71 || new Set(rows.map(row => row.repository)).size !== 71
      || new Set(rows.map(row => row.dto)).size !== 71) throw new Error('Official JSON models must have distinct repositories and DTOs');
  return rows.sort((left, right) => left.file.localeCompare(right.file, 'en'));
}

function document(contract, target = outputPath) {
  const link = file => `[${path.basename(file, '.java')}](${path.relative(path.dirname(target), file).split(path.sep).join('/')})`;
  const rows = modelRows(contract);
  return `# 官方 JSON 与模型目录

由 \`scripts/database/generate-official-dataset-matrix.cjs\` 从当前模型合同生成，禁止手写推测字段或自然键。

71 个 JSON 各有独立主表、具体 Repository 和类型化 DTO；子表仅属于本 JSON，不存在跨文件总父表。
下列 DTO 是逐文件解析/传输合同；通用后台摘要和业务查询投影的区别见 [数据模型说明](../Warframe数据模型与导入.md)。
表实体保留原名，\`Root\` 只表示单个 JSON 内的一条主记录。

| JSON | 主表 | 主实体 | Repository | 独立 DTO | 必要子表数 |
| --- | --- | --- | --- | --- | --- |
${rows.map(row => `| \`${row.file}\` | \`${row.table}\` | ${link(row.entity)} | ${link(row.repository)} | ${link(row.dto)} | ${row.children} |`).join('\n')}

共 ${rows.length} 个独立主表、${rows.reduce((total, row) => total + row.children, 0)} 个必要子表。字段、类型和缺失状态以模型合同及真实数据往返测试为准。
`;
}

if (require.main === module) {
  const expected = document(JSON.parse(fs.readFileSync(contractPath, 'utf8')));
  if (process.argv.includes('--check')) {
    if (!fs.existsSync(outputPath) || fs.readFileSync(outputPath, 'utf8') !== expected) throw new Error('Official model reference is out of date');
  } else {
    fs.mkdirSync(path.dirname(outputPath), { recursive: true });
    fs.writeFileSync(outputPath, expected);
    console.log(`Wrote ${path.relative(repo, outputPath)}`);
  }
}

module.exports = { modelRows, document };
