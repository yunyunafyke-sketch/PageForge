package com.afyke.pageforge.common.oss.service.impl;

import com.afyke.pageforge.common.exception.BusinessException;
import com.afyke.pageforge.common.oss.config.AliyunOssProperties;
import com.afyke.pageforge.common.oss.model.OssFileVO;
import com.afyke.pageforge.common.oss.service.AliyunOssService;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

/** 阿里云 OSS 文件服务实现。 */
@Service
@RequiredArgsConstructor
public class AliyunOssServiceImpl implements AliyunOssService {

    /** OSS 客户端。 */
    private final OSS ossClient;

    /** OSS 配置。 */
    private final AliyunOssProperties properties;

    /**
     * 将文件流上传到配置的 Bucket。
     * 上传成功后返回 objectKey，业务表只需保存 objectKey 或返回的完整地址。
     */
    @Override
    public OssFileVO upload(MultipartFile file, String directory) {
        // 空文件没有实际内容，直接作为参数错误返回，不调用 OSS。
        if (file == null || file.isEmpty()) {
            throw BusinessException.badRequest("FILE_EMPTY", "上传文件不能为空");
        }
        String objectKey = buildObjectKey(file.getOriginalFilename(), directory);

        // 将文件大小和类型传给 OSS，确保下载或浏览器访问时响应信息正确。
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        if (StringUtils.hasText(file.getContentType())) {
            metadata.setContentType(file.getContentType());
        }
        try {
            // objectKey 是文件在 Bucket 中的唯一完整路径，不使用用户原始文件名防止重名覆盖。
            ossClient.putObject(properties.getBucket(), objectKey, file.getInputStream(), metadata);
            return OssFileVO.builder()
                    .originalName(file.getOriginalFilename())
                    .objectKey(objectKey)
                    .url(buildPublicUrl(objectKey))
                    .size(file.getSize())
                    .build();
        } catch (IOException | OSSException | ClientException exception) {
            // 屏蔽第三方 SDK 异常细节，统一转换为项目业务异常返回给客户端。
            throw new BusinessException(500, "OSS_UPLOAD_FAILED", "文件上传失败");
        }
    }

    /** 删除指定 Bucket 中的文件。 */
    @Override
    public void delete(String objectKey) {
        // 禁止绝对路径和路径穿越形式，避免客户端传入异常对象路径。
        if (!StringUtils.hasText(objectKey) || objectKey.startsWith("/") || objectKey.contains("..")) {
            throw BusinessException.badRequest("OBJECT_KEY_ERROR", "文件标识格式错误");
        }
        try {
            ossClient.deleteObject(properties.getBucket(), objectKey);
        } catch (OSSException | ClientException exception) {
            throw new BusinessException(500, "OSS_DELETE_FAILED", "文件删除失败");
        }
    }

    /**
     * 生成不会重复的文件路径。
     * 最终格式：业务目录/年/月/日/UUID.扩展名。
     */
    private String buildObjectKey(String originalName, String directory) {
        String prefix = StringUtils.hasText(directory)
                ? directory.replaceAll("^/+|/+$", "") + "/"
                : "files/";
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return prefix + date + "/" + UUID.randomUUID().toString().replace("-", "")
                + getSafeExtension(originalName);
    }

    /**
     * 提取安全的文件扩展名。
     * 仅保留长度不超过 10 的字母或数字，防止原始文件名污染对象路径。
     */
    private String getSafeExtension(String originalName) {
        if (!StringUtils.hasText(originalName)) {
            return "";
        }
        int index = originalName.lastIndexOf('.');
        if (index < 0 || index == originalName.length() - 1) {
            return "";
        }
        String extension = originalName.substring(index + 1).toLowerCase(Locale.ROOT);
        return extension.matches("[a-z0-9]{1,10}") ? "." + extension : "";
    }

    /** 使用配置的公开域名拼接文件访问地址，并统一处理多余斜杠。 */
    private String buildPublicUrl(String objectKey) {
        return properties.getPublicDomain().replaceAll("/+$", "") + "/" + objectKey;
    }
}
