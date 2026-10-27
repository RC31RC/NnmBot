package com.nnm.bot.warframe.storage;

import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRecordChangeDTO;
import java.io.IOException;
import java.util.function.Consumer;

@FunctionalInterface
public interface WarframeOfficialChangeSource {
    /**
     * 读取输入并生成类型化数据。
     *
     * @param consumer 逐条接收处理结果的回调。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    void read(Consumer<WarframeOfficialRecordChangeDTO> consumer) throws IOException;
}
