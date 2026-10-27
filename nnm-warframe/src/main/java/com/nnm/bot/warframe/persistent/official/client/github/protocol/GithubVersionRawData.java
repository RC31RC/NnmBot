package com.nnm.bot.warframe.persistent.official.client.github.protocol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/** GitHub 版本接口的实际协议模型；仅用于数据源标签与提交定位。 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubVersionRawData {
    /** 标签名称。 */
    @JsonProperty("name")
    private String name;

    /** 引用的完整路径，例如标签引用。 */
    @JsonProperty("ref")
    private String ref;

    /** 提交或对象的完整哈希。 */
    @JsonProperty("sha")
    private String sha;

    /** 标签列表中的提交对象。 */
    @JsonProperty("commit")
    private GitObjectRawData commit;

    /** 标签引用或注解标签的目标对象。 */
    @JsonProperty("object")
    private GitObjectRawData object;

    /** Git 引用目标。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GitObjectRawData {
        /** 目标对象的完整哈希。 */
        @JsonProperty("sha")
        private String sha;

        /** 目标对象类型，例如提交或注解标签。 */
        @JsonProperty("type")
        private String type;

    }

}
