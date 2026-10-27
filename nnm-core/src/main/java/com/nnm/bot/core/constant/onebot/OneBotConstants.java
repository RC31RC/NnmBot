package com.nnm.bot.core.constant.onebot;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class OneBotConstants {
    private OneBotConstants() {
    }

    public enum FileTransferMode {
        /** 本机连接使用路径，远端连接使用 NapCat WebSocket 分片上传。 */
        AUTO,
        /** 文件路径在 NnmBot 与 OneBot 进程间可见时使用。 */
        LOCAL_PATH,
        /** 通过 NapCat upload_file_stream 将文件传到 OneBot 主机。 */
        STREAM
    }

    public enum StatusAction {
        CONNECTING, CONNECTED, DISCONNECTED, FAILED
    }
}
