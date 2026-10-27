const { execFileSync } = require("node:child_process");
const path = require("node:path");
const { withPostgres } = require("./postgres-test-cluster.cjs");

withPostgres(async ({ root, apply, jdbcUrl }) => {
  apply("docs/sql/init.sql");
  const source = process.env.NNM_OFFICIAL_DATA_DIRECTORY
    || path.resolve(root, "../warframe-datasource-nnm/data/latest");
  execFileSync("sh", ["mvnw", "-pl", "nnm-app", "-am", "test",
    "-Dtest=PostgresSchemaValidationTest,OfficialModelTreeTest,OfficialRivenWeaponTreeTest",
    "-Dsurefire.failIfNoSpecifiedTests=false", `-Dnnm.test.postgres-url=${jdbcUrl}`,
    `-Dnnm.test.dataset-directory=${source}`], {
    cwd: root, stdio: "inherit", timeout: 600000
  });
}, { jdbc: true }).catch(error => {
  console.error(error.message);
  process.exitCode = 1;
});
