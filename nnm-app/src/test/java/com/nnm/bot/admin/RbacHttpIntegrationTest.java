package com.nnm.bot.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.nnm.bot.admin.domain.SysRole;
import com.nnm.bot.admin.domain.SysUser;
import com.nnm.bot.admin.repository.SysRoleRepository;
import com.nnm.bot.admin.repository.SysUserRepository;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.url=jdbc:h2:mem:nnm-rbac-http;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.default_schema=PUBLIC",
        "nnm.admin.bootstrap.enabled=false",
        "nnm.warframe.polling.enabled=false",
        "shiro.ws.server.enable=false",
        "shiro.ws.client.enable=false"
        }
)
@Sql(
        scripts = "classpath:db/rbac-http-seed.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class RbacHttpIntegrationTest {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @LocalServerPort
    private int port;

    @Resource
    private SysRoleRepository roleRepository;

    @Resource
    private SysUserRepository userRepository;

    @Resource
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void createTestUsers() {
        SysRole superRole = roleRepository.findByCode("R_SUPER").orElseThrow();
        SysRole readOnlyRole = roleRepository.findByCode("R_USER").orElseThrow();
        saveUser("admin", "admin123", superRole);
        saveUser("readonly", "readonly123", readOnlyRole);
    }

    @Test
    void enforcesRoleHierarchyForUserStatusAndRoleAssignment() throws Exception {
        SysRole adminRole = roleRepository.findByCode("R_ADMIN").orElseThrow();
        SysRole superRole = roleRepository.findByCode("R_SUPER").orElseThrow();
        SysUser manager = saveUser("hierarchy-manager", "manager123", adminRole);
        SysUser peer = saveUser("hierarchy-peer", "peer123", adminRole);
        SysUser superAdmin = saveUser("hierarchy-super", "super123", superRole);

        String managerToken = login("hierarchy-manager", "manager123");

        assertThat(send(
                "PUT",
                "/api/admin/v1/users/" + userId("readonly") + "/status",
                managerToken,
                "{\"enabled\":false}"
        ).statusCode()).isEqualTo(200);

        assertThat(send(
                "PUT",
                "/api/admin/v1/users/" + peer.getId() + "/status",
                managerToken,
                "{\"enabled\":false}"
        ).statusCode()).isEqualTo(403);

        assertThat(send(
                "PUT",
                "/api/admin/v1/users/" + superAdmin.getId() + "/status",
                managerToken,
                "{\"enabled\":false}"
        ).statusCode()).isEqualTo(403);

        assertThat(send(
                "PUT",
                "/api/admin/v1/users/" + peer.getId() + "/roles",
                managerToken,
                "{\"roleCodes\":[\"R_SUPER\"]}"
        ).statusCode()).isEqualTo(403);
    }

    @Test
    void enforcesSaTokenAuthenticationAndRbacPermissions() throws Exception {
        HttpResponse<String> unauthenticated = send("POST", "/api/admin/v1/me", null, "{}");
        assertThat(unauthenticated.statusCode()).isEqualTo(401);

        HttpResponse<String> login = send(
                "POST",
                "/api/admin/v1/auth/login",
                null,
                "{\"userName\":\"admin\",\"password\":\"admin123\"}"
        );
        assertThat(login.statusCode()).isEqualTo(200);
        String initialAdminToken = data(login).path("token").asText();
        assertThat(initialAdminToken).isNotBlank();

        HttpResponse<String> refresh = send(
                "POST",
                "/api/admin/v1/auth/refresh",
                null,
                "{\"refreshToken\":\"" + initialAdminToken + "\"}"
        );
        assertThat(refresh.statusCode()).isEqualTo(200);
        String adminToken = data(refresh).path("token").asText();
        assertThat(adminToken).isNotBlank().isNotEqualTo(initialAdminToken);
        assertThat(send("POST", "/api/admin/v1/me", initialAdminToken, "{}").statusCode()).isEqualTo(401);

        HttpResponse<String> me = send("POST", "/api/admin/v1/me", adminToken, "{}");
        assertThat(me.statusCode()).isEqualTo(200);
        assertThat(data(me).path("roles").toString()).contains("R_SUPER");
        assertThat(data(me).path("buttons").toString()).contains("system:user:view", "system:menu:delete");

        HttpResponse<String> users = send(
                "POST",
                "/api/admin/v1/users/query",
                adminToken,
                "{\"current\":1,\"size\":5}"
        );
        assertThat(users.statusCode()).isEqualTo(200);

        JsonNode menus = data(send("POST", "/api/admin/v1/menus/query", adminToken, "{}"));
        List<String> rootMenuNames = StreamSupport.stream(menus.spliterator(), false)
                .map(menu -> menu.path("name").asText())
                .toList();
        assertThat(rootMenuNames).containsExactly(
                "Dashboard",
                "System",
                "BotManagement",
                "Template",
                "Widgets",
                "Examples",
                "Article",
                "Result",
                "Exception",
                "Safeguard",
                "Document",
                "LiteVersion",
                "OldVersion",
                "ChangeLog"
        );
        assertThat(findMenu(menus, "Ecommerce").path("component").asText())
                .isEqualTo("/dashboard/ecommerce");
        assertThat(findMenu(menus, "Console").path("meta").path("fixedTab").asBoolean()).isTrue();
        assertThat(findMenu(menus, "ArticleDetail").path("meta").path("isHide").asBoolean()).isTrue();
        assertThat(findMenu(menus, "ArticleDetail").path("meta").path("activePath").asText())
                .isEqualTo("/article/article-list");
        assertThat(findMenu(menus, "ElementUI").path("meta").path("isIframe").asBoolean()).isTrue();
        assertThat(findMenu(menus, "Document").path("meta").path("link").asText())
                .isEqualTo("https://www.artd.pro/docs/zh/");
        assertThat(findMenu(menus, "PermissionButtonAuth").path("meta").path("authList").toString())
                .contains("\"add\"", "\"manage\"");

        HttpResponse<String> readOnlyLogin = send(
                "POST",
                "/api/admin/v1/auth/login",
                null,
                "{\"userName\":\"readonly\",\"password\":\"readonly123\"}"
        );
        String readOnlyToken = data(readOnlyLogin).path("token").asText();
        HttpResponse<String> forbidden = send(
                "POST",
                "/api/admin/v1/users/query",
                readOnlyToken,
                "{\"current\":1,\"size\":5}"
        );
        assertThat(forbidden.statusCode()).isEqualTo(403);

        HttpResponse<String> logout = send("POST", "/api/admin/v1/auth/logout", adminToken, null);
        assertThat(logout.statusCode()).isEqualTo(200);

        HttpResponse<String> afterLogout = send("POST", "/api/admin/v1/me", adminToken, "{}");
        assertThat(afterLogout.statusCode()).isEqualTo(401);
    }

    @Test
    void managesUsersRolesAndSelfDataScope() throws Exception {
        String adminToken = login("admin", "admin123");
        long viewUserPermissionId = StreamSupport.stream(
                        data(send("POST", "/api/admin/v1/permissions/query", adminToken, "{}")).spliterator(),
                        false
                )
                .filter(permission -> "system:user:view".equals(permission.path("code").asText()))
                .findFirst()
                .orElseThrow()
                .path("id")
                .asLong();

        long roleId = data(send(
                "POST",
                "/api/admin/v1/roles",
                adminToken,
                """
                        {
                          "roleName":"自助数据角色",
                          "roleCode":"R_SELF_TEST",
                          "description":"集成测试角色",
                          "enabled":true,
                          "dataScope":"SELF"
                        }
                        """
        )).asLong();

        HttpResponse<String> updateRole = send(
                "PUT",
                "/api/admin/v1/roles/" + roleId,
                adminToken,
                """
                        {
                          "roleName":"自助数据角色（已更新）",
                          "roleCode":"R_SELF_TEST",
                          "description":"验证角色更新",
                          "enabled":true,
                          "dataScope":"SELF"
                        }
                        """
        );
        assertThat(updateRole.statusCode()).isEqualTo(200);

        HttpResponse<String> grantRole = send(
                "PUT",
                "/api/admin/v1/roles/" + roleId + "/grants",
                adminToken,
                "{\"menuIds\":[],\"permissionIds\":[" + viewUserPermissionId + "]}"
        );
        assertThat(grantRole.statusCode()).isEqualTo(200);

        JsonNode grants = data(send(
                "POST",
                "/api/admin/v1/roles/" + roleId + "/grants/query",
                adminToken,
                "{}"
        ));
        assertThat(grants.path("permissionIds").toString()).contains(String.valueOf(viewUserPermissionId));

        long userId = data(send(
                "POST",
                "/api/admin/v1/users",
                adminToken,
                """
                        {
                          "userName":"self-test",
                          "password":"self-test-123",
                          "nickName":"自助测试用户",
                          "userEmail":"self-test@localhost",
                          "enabled":true,
                          "roleCodes":["R_SELF_TEST"]
                        }
                        """
        )).asLong();

        HttpResponse<String> updateUser = send(
                "PUT",
                "/api/admin/v1/users/" + userId,
                adminToken,
                """
                        {
                          "userName":"self-test",
                          "nickName":"自助测试用户（已更新）",
                          "userPhone":"13800000000",
                          "userEmail":"self-test@localhost",
                          "userGender":"unknown",
                          "enabled":true,
                          "roleCodes":["R_SELF_TEST"]
                        }
                        """
        );
        assertThat(updateUser.statusCode()).isEqualTo(200);

        String selfToken = login("self-test", "self-test-123");
        JsonNode selfUsers = data(send(
                "POST",
                "/api/admin/v1/users/query",
                selfToken,
                "{\"current\":1,\"size\":20}"
        ));
        assertThat(selfUsers.path("total").asLong()).isEqualTo(1);
        assertThat(selfUsers.path("records").get(0).path("userName").asText()).isEqualTo("self-test");

        HttpResponse<String> disable = send(
                "PUT",
                "/api/admin/v1/users/" + userId + "/status",
                adminToken,
                "{\"enabled\":false}"
        );
        assertThat(disable.statusCode()).isEqualTo(200);
        assertThat(send("POST", "/api/admin/v1/me", selfToken, "{}").statusCode()).isEqualTo(401);

        assertThat(send(
                "PUT",
                "/api/admin/v1/users/" + userId + "/status",
                adminToken,
                "{\"enabled\":true}"
        ).statusCode()).isEqualTo(200);
        assertThat(send(
                "PUT",
                "/api/admin/v1/users/" + userId + "/roles",
                adminToken,
                "{\"roleCodes\":[]}"
        ).statusCode()).isEqualTo(200);
        assertThat(send("DELETE", "/api/admin/v1/roles/" + roleId, adminToken, null).statusCode())
                .isEqualTo(200);
    }

    @Test
    void managesMenusPermissionsAndOperationAudit() throws Exception {
        String adminToken = login("admin", "admin123");
        long menuId = data(send(
                "POST",
                "/api/admin/v1/menus",
                adminToken,
                """
                        {
                          "name":"IntegrationMenu",
                          "path":"/integration",
                          "component":"/integration/index",
                          "title":"集成测试菜单",
                          "icon":"ri:test-tube-line",
                          "sortOrder":90,
                          "keepAlive":false,
                          "visible":true,
                          "enabled":true
                        }
                        """
        )).asLong();

        assertThat(send(
                "PUT",
                "/api/admin/v1/menus/" + menuId,
                adminToken,
                """
                        {
                          "name":"IntegrationMenu",
                          "path":"/integration-updated",
                          "component":"/integration/index",
                          "title":"集成测试菜单（已更新）",
                          "icon":"ri:test-tube-line",
                          "sortOrder":91,
                          "keepAlive":true,
                          "visible":true,
                          "enabled":true
                        }
                        """
        ).statusCode()).isEqualTo(200);

        long permissionId = data(send(
                "POST",
                "/api/admin/v1/permissions",
                adminToken,
                """
                        {
                          "menuId":%d,
                          "code":"integration:button:create",
                          "name":"集成按钮",
                          "type":"BUTTON",
                          "enabled":true
                        }
                        """.formatted(menuId)
        )).asLong();

        assertThat(send(
                "PUT",
                "/api/admin/v1/permissions/" + permissionId,
                adminToken,
                """
                        {
                          "menuId":%d,
                          "code":"integration:button:update",
                          "name":"集成按钮（已更新）",
                          "type":"BUTTON",
                          "enabled":true
                        }
                        """.formatted(menuId)
        ).statusCode()).isEqualTo(200);

        JsonNode allMenus = data(send("POST", "/api/admin/v1/menus/all/query", adminToken, "{}"));
        JsonNode integrationMenu = StreamSupport.stream(allMenus.spliterator(), false)
                .filter(menu -> menu.path("id").asLong() == menuId)
                .findFirst()
                .orElseThrow();
        assertThat(integrationMenu.path("path").asText()).isEqualTo("/integration-updated");
        assertThat(integrationMenu.path("meta").path("authList").toString())
                .contains("integration:button:update");

        assertThat(send("DELETE", "/api/admin/v1/menus/" + menuId, adminToken, null).statusCode())
                .isEqualTo(409);
        assertThat(send(
                "DELETE",
                "/api/admin/v1/permissions/" + permissionId,
                adminToken,
                null
        ).statusCode()).isEqualTo(200);
        assertThat(send("DELETE", "/api/admin/v1/menus/" + menuId, adminToken, null).statusCode())
                .isEqualTo(200);

        JsonNode logs = data(send(
                "POST",
                "/api/admin/v1/operation-logs/query",
                adminToken,
                "{\"operatorName\":\"admin\",\"current\":1,\"size\":100}"
        ));
        assertThat(logs.path("records").toString())
                .contains("MENU_CREATE", "MENU_UPDATE", "PERMISSION_CREATE", "PERMISSION_DELETE", "MENU_DELETE");
    }

    @Test
    void listsAndKicksOutOnlineSessions() throws Exception {
        String adminToken = login("admin", "admin123");
        String readOnlyToken = login("readonly", "readonly123");

        JsonNode sessions = data(send("POST", "/api/admin/v1/sessions/query", adminToken, "{}"));
        JsonNode readOnlySession = StreamSupport.stream(sessions.spliterator(), false)
                .filter(session -> readOnlyToken.equals(session.path("token").asText()))
                .findFirst()
                .orElseThrow();
        assertThat(readOnlySession.path("userName").asText()).isEqualTo("readonly");

        assertThat(send(
                "DELETE",
                "/api/admin/v1/sessions/" + readOnlyToken,
                adminToken,
                null
        ).statusCode()).isEqualTo(200);
        assertThat(send("POST", "/api/admin/v1/me", readOnlyToken, "{}").statusCode()).isEqualTo(401);
    }

    private String login(String username, String password) throws Exception {
        HttpResponse<String> response = send(
                "POST",
                "/api/admin/v1/auth/login",
                null,
                "{\"userName\":\"" + username + "\",\"password\":\"" + password + "\"}"
        );
        assertThat(response.statusCode()).isEqualTo(200);
        return data(response).path("token").asText();
    }

    private HttpResponse<String> send(
            String method,
            String path,
            String token,
            String body
    ) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .header("Accept", "application/json");
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }

        HttpRequest.BodyPublisher publisher = body == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(body);
        if (body != null) {
            builder.header("Content-Type", "application/json");
        }

        return httpClient.send(builder.method(method, publisher).build(), HttpResponse.BodyHandlers.ofString());
    }

    private JsonNode data(HttpResponse<String> response) throws IOException {
        return objectMapper.readTree(response.body()).path("data");
    }

    private SysUser saveUser(String username, String password, SysRole role) {
        SysUser user = userRepository.findByUsername(username).orElseGet(SysUser::new);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.getRoles().clear();
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    private long userId(String username) {
        return userRepository.findByUsername(username).orElseThrow().getId();
    }

    private JsonNode findMenu(JsonNode menus, String name) {
        for (JsonNode menu : menus) {
            if (name.equals(menu.path("name").asText())) {
                return menu;
            }
            JsonNode children = menu.path("children");
            if (children.isArray()) {
                JsonNode child = findMenu(children, name);
                if (!child.isMissingNode()) {
                    return child;
                }
            }
        }
        return MissingNode.getInstance();
    }
}
