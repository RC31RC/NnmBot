const fs = require("node:fs");
const os = require("node:os");
const path = require("node:path");
const net = require("node:net");
const { execFileSync } = require("node:child_process");

const root = path.resolve(__dirname, "../..");

function postgresBinary(name) {
  if (process.env.PG_BIN) return path.join(process.env.PG_BIN, name);
  const local = path.join("/Library/PostgreSQL/18/bin", name);
  return fs.existsSync(local) ? local : name;
}

async function unusedPort() {
  const server = net.createServer();
  await new Promise((resolve, reject) => {
    server.once("error", reject);
    server.listen(0, "127.0.0.1", resolve);
  });
  const port = server.address().port;
  await new Promise(resolve => server.close(resolve));
  return port;
}

async function withPostgres(check, { jdbc = false } = {}) {
  const directory = fs.mkdtempSync(path.join(os.tmpdir(), "nnmbot-sql-test-"));
  const data = path.join(directory, "data");
  let port = 55439;
  const env = Object.fromEntries(Object.entries(process.env)
    .filter(([key]) => !key.startsWith("PG")));
  const connection = ["-X", "-h", directory, "-p", String(port), "-U", "sql_test",
    "-d", "nnmbot_sql_test", "-v", "ON_ERROR_STOP=1"];
  let started = false;
  function run(name, args, reportError = true) {
    try {
      return execFileSync(postgresBinary(name), args, {
        env, encoding: "utf8", stdio: ["ignore", "pipe", "pipe"],
        timeout: 120000, maxBuffer: 64 * 1024 * 1024
      });
    } catch (error) {
      if (reportError && error.stderr) console.error(error.stderr);
      throw error;
    }
  }
  const query = (sql, reportError = true) =>
    run("psql", [...connection, "-At", "-c", sql], reportError).trim();
  const apply = file => {
    run("psql", [...connection, "-q", "-f", path.resolve(root, file)]);
    console.log(`PASS ${file}`);
  };
  try {
    if (jdbc) {
      port = await unusedPort();
      connection[4] = String(port);
    }
    run("initdb", ["-D", data, "-U", "sql_test", "-A", "trust", "--no-locale", "-E", "UTF8"]);
    // Only JDBC validation opens a random loopback port; SQL checks use a private socket.
    run("pg_ctl", ["-D", data, "-l", path.join(directory, "postgres.log"), "-o",
      `-k ${directory} -p ${port} -c listen_addresses='${jdbc ? "127.0.0.1" : ""}'`
        + " -c shared_buffers=32MB -c max_connections=5", "-w", "start"]);
    started = true;
    run("createdb", ["-h", directory, "-p", String(port), "-U", "sql_test", "nnmbot_sql_test"]);
    return await check({
      root, directory, port, connection, query, apply, run,
      jdbcUrl: `jdbc:postgresql://127.0.0.1:${port}/nnmbot_sql_test`
    });
  } finally {
    try {
      if (started) run("pg_ctl", ["-D", data, "-m", "fast", "-w", "stop"]);
    } finally {
      fs.rmSync(directory, { recursive: true, force: true });
    }
  }
}

module.exports = { root, withPostgres };
