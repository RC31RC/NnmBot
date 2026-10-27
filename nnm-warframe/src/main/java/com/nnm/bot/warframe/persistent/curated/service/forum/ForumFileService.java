package com.nnm.bot.warframe.persistent.curated.service.forum;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumPreparedContentDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumStoredFileDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.forum.ForumFileVO;
import org.springframework.web.multipart.MultipartFile;
import java.util.Collection;
import java.util.Set;

public interface ForumFileService {

    /**
     * 保存尚未被正式记录引用的临时文件。
     *
     * @param file 本次上传或读取的文件。
     * @return 已上传文件的访问地址及文件信息。
     */
    ForumFileVO storeTemporary(MultipartFile file);

    /**
     * 将正文中的公开文件地址转换为稳定的存储引用。
     *
     * @param publicContent 公开内容。
     * @return 论坛已准备内容对象。
     */
    ForumPreparedContentDTO toStorageContent(ForumPreparedContentDTO publicContent);

    /**
     * 将正文中的存储引用转换为可访问的公开地址。
     *
     * @param coverObjectId 封面对象标识。
     * @param contentHtml 内容页面。
     * @param contentMarkdown 内容文档。
     * @return 论坛已准备内容对象。
     */
    ForumPreparedContentDTO toPublicContent(
            String coverObjectId,
            String contentHtml,
            String contentMarkdown);

    /**
     * 读取已经转为永久保存的文件内容。
     *
     * @param year 年份。
     * @param month 月份。
     * @param fileName 文件名称。
     * @return 论坛已保存文件对象。
     */
    ForumStoredFileDTO loadPermanent(String year, String month, String fileName);

    /**
     * 读取仍在有效期内的临时文件。
     *
     * @param year 年份。
     * @param month 月份。
     * @param fileName 文件名称。
     * @return 论坛已保存文件对象。
     */
    ForumStoredFileDTO loadTemporary(String year, String month, String fileName);

    /**
     * 将正文实际引用的临时文件转为永久文件，并登记事务完成后的清理操作。
     *
     * @param coverUrl 封面地址。
     * @param contentHtml 内容页面。
     * @param contentMarkdown 内容文档。
     * @return 论坛已准备内容对象。
     */
    ForumPreparedContentDTO promoteReferencedFiles(
            String coverUrl,
            String contentHtml,
            String contentMarkdown);

    /**
     * 提取正文实际引用的永久文件标识，供更新和删除时计算差异。
     *
     * @param coverUrl 封面地址。
     * @param contentHtml 内容页面。
     * @param contentMarkdown 内容文档。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    Set<String> referencedPermanentFiles(
            String coverUrl,
            String contentHtml,
            String contentMarkdown);

    /**
     * 登记永久文件的删除操作，仅在当前事务成功提交后执行。
     *
     * @param objectReferences 对象引用集合。
     */
    void deletePermanentFilesAfterCommit(Collection<String> objectReferences);

    /**
     * 清理已过期临时文件。
     *
     * @return 当前操作计算或读取的数值。
     */
    int cleanExpiredTemporaryFiles();

}
