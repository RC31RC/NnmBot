package com.nnm.bot.warframe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityRecordDTO.AliasDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.equipment.OfficialWeaponDTO;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.common.config.JacksonConfig;
import com.nnm.bot.common.task.AdminTaskService;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.MaintenanceType;
import com.nnm.bot.warframe.controller.persistent.curated.reference.WarframeCommunityDataController;
import com.nnm.bot.warframe.controller.persistent.official.WarframeDataManagementController;
import com.nnm.bot.warframe.controller.runtime.worldstate.WarframeWorldStateManagementController;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityApplyDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityApplyVO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityOperationDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityPreviewDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityPreviewPageDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityRecordVO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialVersionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialPreviewDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRecordChangeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialFieldChangeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.record.WarframeOfficialRecordVO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.maintenance.WarframeOfficialRecordSaveDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.query.reference.WarframeCommunityQuery;
import com.nnm.bot.warframe.persistent.official.pojo.query.WarframeOfficialRecordQuery;
import com.nnm.bot.warframe.persistent.curated.service.reference.WarframeCommunityDataService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialDataQueryService;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialMaintenanceService;
import com.nnm.bot.warframe.persistent.official.service.dataset.WarframeOfficialImportService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateManagementService;
import jakarta.annotation.Resource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.http.converter.autoconfigure.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 与管理端实际使用的 URL/JSON 合同对齐；权限拦截由既有安全集成测试负责。 */
@SpringBootTest(classes = WarframeManagementApiContractTest.Config.class)
@MockitoBean(types = {WarframeOfficialImportService.class, WarframeWorldStateService.class,
        WarframeWorldStateManagementService.class, AdminTaskService.class})
class WarframeManagementApiContractTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @MockitoBean
    private WarframeCommunityDataService community;
    @MockitoBean
    private WarframeOfficialDataQueryService official;
    @MockitoBean
    private WarframeOfficialMaintenanceService maintenance;
    @Resource
    private WarframeOfficialImportService dataset;
    @Resource
    private WebApplicationContext context;
    @Resource
    private Environment environment;
    @Resource
    private RequestMappingHandlerAdapter handlerAdapter;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void productionConfigurationUsesTheSameJacksonTreeModelForHttpAndDatasetInputs() {
        assertThat(environment.getProperty("spring.http.converters.preferred-json-mapper")).isEqualTo("jackson2");
        assertThat(handlerAdapter.getMessageConverters()).anyMatch(MappingJackson2HttpMessageConverter.class::isInstance);
    }

    @Test
    void communityPaginationAcceptsTheFrontendQueryAndReturnsBusinessJson() throws Exception {
        var query = new WarframeCommunityQuery("母牛", 2, 25);
        var row = new WarframeCommunityRecordVO(7L, "母牛", new AliasDTO("母牛", "Hildryn"),
                Instant.parse("2026-10-26T00:00:00Z"), 3);
        when(community.query("alias", query)).thenReturn(PageResult.of(List.of(row), 2, 25, 26));
        mvc.perform(post("/api/admin/v1/warframe/community/alias/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"keyword\":\"母牛\",\"current\":2,\"size\":25}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.current").value(2)).andExpect(jsonPath("$.data.total").value(26))
                .andExpect(jsonPath("$.data.records[0].value.targetName").value("Hildryn"))
                .andExpect(jsonPath("$.data.records[0].version").value(3))
                .andExpect(jsonPath("$.data.records[0].updatedAt").value("2026-10-26T00:00:00Z"));
        verify(community).query("alias", query);
    }

    @Test
    void previewAndApplyUsePagedOperationsAndSelectedIds() throws Exception {
        var now = Instant.parse("2026-10-26T00:00:00Z");
        var value = new AliasDTO("母牛", "Hildryn");
        var operation = new WarframeCommunityOperationDTO("operation-id", "母牛", "ADDED", value, null, null, Map.of());
        when(community.preview(eq("alias"), anyString())).thenReturn(new WarframeCommunityPreviewDTO("preview-id", "alias", now,
                now.plusSeconds(60), 1, 1, 0, List.of(operation)));
        when(community.previewPage("alias", "preview-id", 1, 20)).thenReturn(new WarframeCommunityPreviewPageDTO("preview-id", "alias", now,
                now.plusSeconds(60), 1, 1, 0, PageResult.of(List.of(operation), 1, 20, 1)));
        mvc.perform(post("/api/admin/v1/warframe/community/alias/preview")
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(value)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.operations.records[0].id").value("operation-id"))
                .andExpect(jsonPath("$.data.operations.records[0].value.aliasName").value("母牛"))
                .andExpect(jsonPath("$.data.operations.total").value(1))
                .andExpect(jsonPath("$.data.createdAt").value("2026-10-26T00:00:00Z"));
        when(community.apply("alias", new WarframeCommunityApplyDTO("preview-id", Set.of("operation-id"))))
                .thenReturn(new WarframeCommunityApplyVO("preview-id", 1));
        mvc.perform(post("/api/admin/v1/warframe/community/alias/apply").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"previewId\":\"preview-id\",\"selectedOperationIds\":[\"operation-id\"]}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.previewId").value("preview-id"))
                .andExpect(jsonPath("$.data.appliedCount").value(1));
        verify(community).preview("alias", mapper.writeValueAsString(value));
    }

    @Test
    void officialListPreservesMaintenanceEnumAndPagedResponseContract() throws Exception {
        var query = new WarframeOfficialRecordQuery("WEAPON", "Weapon", 1, 20, "SELF_MAINTAINED");
        var item = new WarframeOfficialRecordVO(9L, "WEAPON", "/Lotus/Weapon", "equipment/weapons.json", "WEAPON",
                "Weapon", "武器", null, null, false, null, null, null, null, true,
                "{}", "{}", Map.of(), LocalDateTime.of(2026, 10, 26, 8, 0), null, MaintenanceType.SELF_MAINTAINED);
        when(official.query(query)).thenReturn(PageResult.of(List.of(item), 1, 20, 1));
        mvc.perform(post("/api/admin/v1/warframe/official/records/query").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"domain\":\"WEAPON\",\"keyword\":\"Weapon\",\"current\":1,\"size\":20,\"maintenanceType\":\"SELF_MAINTAINED\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.records[0].maintenanceType").value("SELF_MAINTAINED"))
                .andExpect(jsonPath("$.data.records[0].domain").value("WEAPON"))
                .andExpect(jsonPath("$.data.records[0].createdAt").value("2026-10-26T08:00:00"));
        verify(official).query(query);
    }

    @Test
    void officialSaveBindsJsonObjectAndReturnsOnlyTheRecordId() throws Exception {
        var data = mapper.readValue("{\"nameEn\":\"Weapon\",\"accuracy\":1.25}", OfficialWeaponDTO.class);
        var result = new WarframeOfficialRecordDTO();
        result.setId(9L);
        var request = new WarframeOfficialRecordSaveDTO("WEAPON", "/Lotus/Weapon", data, false, false);
        when(maintenance.save(isNull(), any(WarframeOfficialRecordSaveDTO.class))).thenReturn(result);
        mvc.perform(post("/api/admin/v1/warframe/official/records").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"domain\":\"WEAPON\",\"bkey\":\"/Lotus/Weapon\",\"data\":" + mapper.writeValueAsString(data) + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").value(9));
        verify(maintenance).save(isNull(), argThat(actual -> actual.domain().equals(request.domain())
                && actual.bkey().equals(request.bkey()) && !actual.confirmed() && !actual.replaceExisting()
                && mapper.valueToTree(actual.data()).equals(mapper.valueToTree(data))));
    }
    @Test
    void officialDatasetDtosAreAssembledIntoViewsWithoutChangingFrontendJson() throws Exception {
        var version = new WarframeOfficialVersionDTO("20261026", "fixed-commit");
        when(dataset.versions()).thenReturn(List.of(version));
        mvc.perform(post("/api/admin/v1/warframe/official/versions"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].ref").value("20261026"));
        var preview = new WarframeOfficialPreviewDTO("preview", version, "build", "baseline", "PENDING",
                Instant.parse("2026-10-26T12:00:00Z"), 100, 1, 2, 0, List.of(), List.of(
                new WarframeOfficialPreviewDTO.FileChangeDTO("equipment/weapons.json", "sha", 100, 1L, "CHANGED")));
        when(dataset.preview("preview")).thenReturn(preview);
        mvc.perform(post("/api/admin/v1/warframe/official/preview/preview"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.version.commit").value("fixed-commit"))
                .andExpect(jsonPath("$.data.files[0].path").value("equipment/weapons.json"))
                .andExpect(jsonPath("$.data.expiresAt").value("2026-10-26T12:00:00Z"));
        var change = new WarframeOfficialRecordChangeDTO("equipment/weapons.json", "weapon", "武器", "CHANGED",
                "{}", "{\"nameEn\":\"Weapon\"}", "{}", "{}", List.of(
                new WarframeOfficialFieldChangeDTO("/nameEn", null, "\"Weapon\"")));
        when(dataset.changes("preview", 2, 20)).thenReturn(PageResult.of(List.of(change), 2, 20, 21));
        mvc.perform(post("/api/admin/v1/warframe/official/preview/preview/changes?current=2&size=20"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.current").value(2))
                .andExpect(jsonPath("$.data.total").value(21))
                .andExpect(jsonPath("$.data.records[0].fieldChanges[0].pointer").value("/nameEn"));
    }

    @Configuration(proxyBeanMethods = false)
    @Import({JacksonConfig.class, WarframeCommunityDataController.class, WarframeDataManagementController.class,
            WarframeWorldStateManagementController.class})
    @ImportAutoConfiguration({JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class,
            WebMvcAutoConfiguration.class})
    static class Config {
    }
}
