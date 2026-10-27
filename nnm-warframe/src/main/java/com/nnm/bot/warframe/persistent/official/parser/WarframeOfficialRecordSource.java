package com.nnm.bot.warframe.persistent.official.parser;

import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialImportRecordDTO;
import java.io.IOException;
import java.util.function.Consumer;

/** 可重放的冻结文件输入；调用期间逐条交付，不在堆中累积整个发布包。 */
@FunctionalInterface
public interface WarframeOfficialRecordSource {
    /**
     * 读取输入并生成类型化数据。
     *
     * @param consumer 逐条接收处理结果的回调。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    void forEachRecord(Consumer<OfficialImportRecordDTO> consumer) throws IOException;
}
