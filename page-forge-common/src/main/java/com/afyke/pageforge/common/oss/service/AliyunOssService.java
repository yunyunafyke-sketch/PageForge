package com.afyke.pageforge.common.oss.service;

import com.afyke.pageforge.common.oss.model.OssFileVO;
import org.springframework.web.multipart.MultipartFile;

/** 阿里云 OSS 文件服务。 */
public interface AliyunOssService {

    /**
     * 上传文件并返回 OSS 文件信息。
     *
     * @param file 上传文件
     * @param directory 自定义存储目录，可以为空
     * @return 文件标识和访问地址
     */
    OssFileVO upload(MultipartFile file, String directory);

    /**
     * 根据文件标识删除 OSS 对象。
     *
     * @param objectKey OSS 文件标识
     */
    void delete(String objectKey);
}
