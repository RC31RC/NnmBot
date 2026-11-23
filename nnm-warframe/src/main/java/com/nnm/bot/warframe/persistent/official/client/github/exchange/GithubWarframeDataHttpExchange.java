package com.nnm.bot.warframe.persistent.official.client.github.exchange;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/** Warframe 官方数据仓库版本发现所使用的 GitHub API 协议。 */
@HttpExchange(accept = "application/vnd.github+json")
public interface GithubWarframeDataHttpExchange {

    /**
     * 获取仓库 tag 分页。
     *
     * @param owner 仓库所有者。
     * @param repository 仓库名称。
     * @param perPage 单页记录数。
     * @param page 页码。
     * @param headers 授权与内容协商请求头。
     * @return 上游 HTTP 响应。
     */
    @GetExchange("/repos/{owner}/{repository}/tags")
    ResponseEntity<String> tags(@PathVariable("owner") String owner, @PathVariable("repository") String repository,
        @RequestParam("per_page") int perPage, @RequestParam("page") int page,
        @RequestHeader Map<String, String> headers);

    /**
     * 查询指定 tag 引用。
     *
     * @param owner 仓库所有者。
     * @param repository 仓库名称。
     * @param ref tag 引用名称。
     * @param headers 授权与内容协商请求头。
     * @return 上游 HTTP 响应。
     */
    @GetExchange("/repos/{owner}/{repository}/git/ref/tags/{ref}")
    ResponseEntity<String> tagReference(@PathVariable("owner") String owner,
        @PathVariable("repository") String repository, @PathVariable("ref") String ref,
        @RequestHeader Map<String, String> headers);

    /**
     * 查询注释 tag 对象。
     *
     * @param owner 仓库所有者。
     * @param repository 仓库名称。
     * @param sha tag 对象 SHA。
     * @param headers 授权与内容协商请求头。
     * @return 上游 HTTP 响应。
     */
    @GetExchange("/repos/{owner}/{repository}/git/tags/{sha}")
    ResponseEntity<String> tag(@PathVariable("owner") String owner, @PathVariable("repository") String repository,
        @PathVariable("sha") String sha, @RequestHeader Map<String, String> headers);
}
