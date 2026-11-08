package com.nnm.bot.core.qqofficial.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** QQ 官方 HTTP 端点中已知的响应合同；未知的上游新增字段不影响现有协议。 */
public final class QqOfficialApiResponseDTO {
    private QqOfficialApiResponseDTO() {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GatewayDTO(String url) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MemberListDTO(List<MemberDTO> members, @JsonProperty("next_cursor") String nextCursor) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MemberDTO(@JsonProperty("member_openid") String memberOpenid, String username,
        @JsonProperty("member_role") String memberRole, Boolean bot) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ImagePrepareDTO(@JsonProperty("upload_id") String uploadId,
        @JsonProperty("presigned_url") String presignedUrl) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ImageCompleteDTO(@JsonProperty("image_id") String imageId, String url) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FilePrepareDTO(@JsonProperty("upload_id") String uploadId, @JsonProperty("block_size") Long blockSize,
        List<FilePartDTO> parts) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FilePartDTO(Integer index, @JsonProperty("presigned_url") String presignedUrl,
        @JsonProperty("block_size") Long blockSize) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FileUploadDTO(@JsonProperty("file_info") String fileInfo) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MessageDTO(String id, Integer code) {
    }
}
