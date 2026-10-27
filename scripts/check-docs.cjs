const fs = require('node:fs');
const path = require('node:path');
const { modelRows } = require('./database/generate-official-dataset-matrix.cjs');
const repo = path.resolve(__dirname, '..');
function markdownFiles(directory) {
  return fs.readdirSync(directory, { withFileTypes: true }).flatMap(entry => {
    const file = path.join(directory, entry.name);
    return entry.isDirectory() ? markdownFiles(file) : file.endsWith('.md') ? [file] : [];
  });
}
const files = [path.join(repo, 'README.md'), path.join(repo, 'AGENTS.md'), ...markdownFiles(path.join(repo, 'docs'))];
const failures = [];
for (const file of files) {
  // Fenced examples may intentionally demonstrate a link syntax.
  const text = fs.readFileSync(file, 'utf8').replace(/```[^\n]*\n[\s\S]*?```/g, '');
  for (const [, raw] of text.matchAll(/\]\(([^)]+)\)/g)) {
    const target = raw.replace(/^<|>$/g, '').split('#')[0];
    if (!target || /^(?:[a-z]+:|\/\/)/i.test(target)) continue;
    const absolute = path.resolve(path.dirname(file), decodeURIComponent(target));
    if (!fs.existsSync(absolute)) failures.push(`${path.relative(repo, file)}: missing ${target}`);
    if (path.isAbsolute(target)) failures.push(`${path.relative(repo, file)}: use a portable relative link: ${target}`);
  }
}
const models = JSON.parse(fs.readFileSync(path.join(repo, 'nnm-warframe/src/main/resources/warframe/official-models.json')));
modelRows(models);
if (failures.length) throw new Error(failures.join('\n'));
console.log(`Checked ${files.length} Markdown documents and 71 official JSON model mappings`);
