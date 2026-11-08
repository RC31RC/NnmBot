package com.nnm.bot.core.qqofficial.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** QQ 官方 HTTP 请求的固定协议字段；可选消息字段不写入 JSON。 */
public final class QqOfficialApiRequestDTO {
    private QqOfficialApiRequestDTO() {}

    public record ImagePrepareDTO(@JsonProperty("file_name") String fileName, @JsonProperty("file_size") long fileSize,
        @JsonProperty("content_type") String contentType, String md5, String sha1) {
    }

    public record ImageCompleteDTO(@JsonProperty("upload_id") String uploadId, String md5) {
    }

    public record FilePrepareDTO(@JsonProperty("file_type") int fileType, @JsonProperty("file_size") String fileSize,
        @JsonProperty("file_name") String fileName, String md5, String sha1,
        @JsonProperty("md5_10m") String md5First10m) {
    }

    public record FilePartFinishDTO(@JsonProperty("upload_id") String uploadId,
        @JsonProperty("part_index") int partIndex, @JsonProperty("block_size") String blockSize, String md5) {
    }

    public record FileUploadDTO(@JsonProperty("file_type") int fileType, @JsonProperty("upload_id") String uploadId,
        @JsonProperty("file_name") String fileName, @JsonProperty("srv_send_msg") boolean srvSendMsg) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record MessageDTO(String content, @JsonProperty("msg_type") Integer msgType,
        @JsonProperty("msg_seq") Integer msgSeq, @JsonProperty("msg_id") String msgId, MarkdownDTO markdown,
        MediaDTO media, KeyboardDTO keyboard) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record MarkdownDTO(String content, @JsonProperty("custom_template_id") String customTemplateId,
        List<MarkdownParamDTO> params, @JsonProperty("force_verify_image_resource") Boolean forceVerifyImageResource) {
    }

    public record MarkdownParamDTO(String key, List<String> values) {
    }

    public record MediaDTO(@JsonProperty("file_info") String fileInfo) {
    }

    public record KeyboardDTO(KeyboardContentDTO content) {
    }

    public record KeyboardContentDTO(List<KeyboardRowDTO> rows) {
    }

    public record KeyboardRowDTO(List<KeyboardButtonDTO> buttons) {
    }

    public record KeyboardButtonDTO(String id, @JsonProperty("render_data") KeyboardRenderDTO renderData,
        KeyboardActionDTO action) {
    }

    public record KeyboardRenderDTO(String label, @JsonProperty("visited_label") String visitedLabel, int style) {
    }

    public record KeyboardActionDTO(int type, String data, boolean enter, KeyboardPermissionDTO permission) {
    }

    public record KeyboardPermissionDTO(int type) {
    }
}
