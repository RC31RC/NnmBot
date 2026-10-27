/** 生成可读的中文方法合同，避免重新生成模型时丢失参数与返回值说明。 */
function methodComment(summary, parameters = {}, result, indent = '    ') {
  const lines = [indent + '/**', indent + ' * ' + summary];
  if (Object.keys(parameters).length || result) lines.push(indent + ' *');
  for (const [name, meaning] of Object.entries(parameters)) lines.push(indent + ' * @param ' + name + ' ' + meaning);
  if (result) lines.push(indent + ' * @return ' + result);
  lines.push(indent + ' */');
  return lines.join('\n');
}
module.exports = { methodComment };
