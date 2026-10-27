package com.nnm.bot.warframe.persistent.official.service.reference.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.OfficialLanguageStringDTO;
import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.first;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeTranslationService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeTranslationCommandVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeTranslationCommandVO.TranslationVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Locale;

@Service
public class WarframeTranslationServiceImpl implements WarframeTranslationService {
    @Resource
    private WarframeOfficialRecordReadService catalog;

    /**
     * 按关键词查询已采纳的官方译名。
     *
     * @param keyword 筛选使用的关键词。
     * @return 查询资料的命令展示内容。
     */
    @Override
    public WarframeTranslationCommandVO translations(String keyword) {
        String query = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        var selected = catalog.records(WarframeOfficialDataDomain.LANGUAGE_STRING).stream()
                .filter(item -> StrUtil.isBlank(query) || (item.bkey() + " " + first(((OfficialLanguageStringDTO) item.data()).getText())
                        + " " + first(((OfficialLanguageStringDTO) item.data()).getZhText())).toLowerCase(Locale.ROOT).contains(query))
                .limit(21).toList();
        var entries = new ArrayList<TranslationVO>();
        selected.stream().limit(20).forEach(item -> {
            for (String locale : new String[]{"en", "zh"}) {
                String text = first(locale.equals("en") ? ((OfficialLanguageStringDTO) item.data()).getText() : ((OfficialLanguageStringDTO) item.data()).getZhText());
                if (StrUtil.isNotBlank(text)) entries.add(new TranslationVO(item.bkey(), locale, text));
            }
        });
        return new WarframeTranslationCommandVO(query, entries,
                selected.size() > 20 ? "本次展示前 20 条，请补充关键词缩小范围" : null);
    }
}
