package com.nnm.bot.warframe.persistent.curated.mapper.reference;

import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityPreviewPageDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityFieldChangeVO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityOperationVO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityPreviewPageVO;
import java.util.LinkedHashMap;
import java.util.Map;

/** 冻结的社区预览快照只作为 DTO 流转，前端只接收分页 VO。 */
public final class WarframeCommunityViewMapper {
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private WarframeCommunityViewMapper() {
    }

    /**
     * 生成变更预览，供后续确认采纳。
     *
     * @param value 待处理的字段值。
     * @return 社区导入差异的预览分页。
     */
    public static WarframeCommunityPreviewPageVO preview(WarframeCommunityPreviewPageDTO value) {
        var operations = value.operations();
        var records = operations.records().stream().map(operation -> {
            Map<String, WarframeCommunityFieldChangeVO> fields = new LinkedHashMap<>();
            operation.fields().forEach((key, field) -> fields.put(key,
                    new WarframeCommunityFieldChangeVO(field.before(), field.after())));
            return new WarframeCommunityOperationVO(operation.id(), operation.key(), operation.type(),
                    operation.value(), operation.baselineId(), operation.baselineVersion(), fields);
        }).toList();
        return new WarframeCommunityPreviewPageVO(value.id(), value.dataset(), value.createdAt(), value.expiresAt(),
                value.inputCount(), value.addedCount(), value.changedCount(),
                PageResult.of(records, operations.current(), operations.size(), operations.total()));
    }
}
