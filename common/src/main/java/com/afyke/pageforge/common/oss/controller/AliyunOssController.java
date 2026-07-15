package com.afyke.pageforge.common.oss.controller;

import com.afyke.pageforge.common.model.ResultModel;
import com.afyke.pageforge.common.oss.model.OssDeleteRequest;
import com.afyke.pageforge.common.oss.model.OssFileVO;
import com.afyke.pageforge.common.oss.model.OssUploadRequest;
import com.afyke.pageforge.common.oss.service.AliyunOssService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 阿里云 OSS 文件接口。 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AliyunOssController {

    /** OSS 文件服务。 */
    private final AliyunOssService aliyunOssService;

    /**
     * 登录用户上传文件。
     * 文件上传必须使用 multipart/form-data，请求中的 file 和 directory 会绑定到同一个对象。
     */
    @PostMapping(value = "/user/oss/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultModel<OssFileVO> upload(@Valid @ModelAttribute OssUploadRequest request) {
        return ResultModel.success(
                aliyunOssService.upload(request.getFile(), request.getDirectory()));
    }

    /** 管理员根据 objectKey 删除 OSS 文件。 */
    @PostMapping("/admin/oss/delete")
    public ResultModel<Boolean> delete(@Valid @RequestBody OssDeleteRequest request) {
        aliyunOssService.delete(request.getObjectKey());
        return ResultModel.success();
    }
}
