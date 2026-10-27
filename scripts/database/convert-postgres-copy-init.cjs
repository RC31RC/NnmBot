#!/usr/bin/env node

const fs = require("node:fs");
const path = require("node:path");

const [sourceArgument, targetArgument = sourceArgument] = process.argv.slice(2);
if (!sourceArgument) {
  console.error("Usage: node scripts/database/convert-postgres-copy-init.cjs <source.sql> [target.sql]");
  process.exit(1);
}

const source = path.resolve(sourceArgument);
const target = path.resolve(targetArgument);
const lines = fs.readFileSync(source, "utf8").split(/\r?\n/);
const output = [];

function decodeCopyField(raw) {
  let value = "";
  for (let index = 0; index < raw.length; index += 1) {
    const character = raw[index];
    if (character !== "\\") {
      value += character;
      continue;
    }

    if (index + 1 >= raw.length) {
      throw new Error("COPY 字段以未完成的反斜杠转义结尾");
    }
    const escaped = raw[++index];
    const simpleEscapes = {
      b: "\b",
      f: "\f",
      n: "\n",
      r: "\r",
      t: "\t",
      v: "\v",
      "\\": "\\"
    };
    if (Object.hasOwn(simpleEscapes, escaped)) {
      value += simpleEscapes[escaped];
      continue;
    }
    if (escaped === "x") {
      const hex = raw.slice(index + 1, index + 3);
      if (!/^[0-9A-Fa-f]{2}$/.test(hex)) {
        throw new Error(`非法 COPY 十六进制转义：\\x${hex}`);
      }
      value += String.fromCharCode(Number.parseInt(hex, 16));
      index += 2;
      continue;
    }
    if (/^[0-7]$/.test(escaped)) {
      const octal = raw.slice(index, index + 3);
      const matched = octal.match(/^[0-7]{1,3}/)?.[0];
      value += String.fromCharCode(Number.parseInt(matched, 8));
      index += matched.length - 1;
      continue;
    }
    value += escaped;
  }
  return value;
}

function parseCopyRow(line, expectedColumnCount) {
  const fields = [];
  let field = "";
  for (let index = 0; index < line.length; index += 1) {
    if (line[index] === "\t") {
      fields.push(field);
      field = "";
    } else {
      field += line[index];
    }
  }
  fields.push(field);
  if (fields.length !== expectedColumnCount) {
    throw new Error(`COPY 数据列数错误：期望 ${expectedColumnCount}，实际 ${fields.length}`);
  }
  return fields.map(fieldValue => fieldValue === "\\N" ? null : decodeCopyField(fieldValue));
}

function sqlLiteral(value) {
  if (value === null) {
    return "NULL";
  }
  return `'${value.replaceAll("'", "''")}'`;
}

for (let index = 0; index < lines.length; index += 1) {
  const line = lines[index];
  if (/^\\(?:un)?restrict(?:\s+.*)?$/.test(line)) {
    continue;
  }

  const copyMatch = line.match(/^COPY (.+?) \((.*)\) FROM stdin;$/);
  if (!copyMatch) {
    output.push(line);
    continue;
  }

  const targetName = copyMatch[1];
  const columns = copyMatch[2].split(", ");
  const rows = [];
  let terminatorFound = false;
  while (++index < lines.length) {
    if (lines[index] === "\\.") {
      terminatorFound = true;
      break;
    }
    rows.push(parseCopyRow(lines[index], columns.length));
  }
  if (!terminatorFound) {
    throw new Error(`COPY 数据段缺少 \\. 结束标记：${targetName}`);
  }
  if (rows.length === 0) {
    output.push(`-- Empty data set: ${targetName}`);
    continue;
  }

  output.push(`INSERT INTO ${targetName} (${copyMatch[2]}) VALUES`);
  output.push(rows.map(row => `(${row.map(sqlLiteral).join(", ")})`).join(",\n") + ";");
}

fs.writeFileSync(target, output.join("\n"), "utf8");
console.log(`Converted ${source} -> ${target}`);
