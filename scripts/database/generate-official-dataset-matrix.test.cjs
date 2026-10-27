const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const { test } = require('node:test');
const { modelRows, document } = require('./generate-official-dataset-matrix.cjs');
const contract = JSON.parse(fs.readFileSync(path.resolve(__dirname, '../../nnm-warframe/src/main/resources/warframe/official-models.json')));

test('every official JSON has its own real main entity, Repository and DTO', () => {
  const rows = modelRows(contract);
  assert.equal(rows.length, 71);
  assert.equal(new Set(rows.map(row => row.entity)).size, 71);
  assert.equal(new Set(rows.map(row => row.repository)).size, 71);
  assert.equal(new Set(rows.map(row => row.dto)).size, 71);
  assert.equal(rows.reduce((sum, row) => sum + row.children, 0), 236);
  const weapons = rows.find(row => row.file === 'equipment/weapons.json');
  assert.equal(path.basename(weapons.repository), 'OfficialWeaponRepository.java');
  assert.equal(path.basename(weapons.dto), 'OfficialWeaponDTO.java');
  const output = document(contract);
  assert.equal(output.split('\n').filter(line => /^\| `.*\.json` \|/.test(line)).length, 71);
  assert.doesNotMatch(output, /\/Users\//);
});

test('fails on an incomplete contract instead of publishing a partial reference', () => {
  const broken = structuredClone(contract);
  broken.models = broken.models.filter(model => model.domain !== 'WEAPON');
  assert.throws(() => modelRows(broken), /71 independent/);
});
