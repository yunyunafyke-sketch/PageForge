package com.afyke.pageforge.common.oss.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 阿里云 OSS 客户端配置。 */
@Configuration
@EnableConfigurationProperties(AliyunOssProperties.class)
public class AliyunOssConfig {

    /**
     * 创建全局复用的 OSS 客户端。
     * 客户端创建成本较高，因此由 Spring 作为单例管理；应用关闭时自动调用 shutdown 释放连接资源。
     *
     * @param properties OSS 配置
     * @return OSS 客户端
     */
    @Bean(destroyMethod = "shutdown")
    public OSS ossClient(AliyunOssProperties properties) {
        return new OSSClientBuilder().build(
                properties.getEndpoint(),
                properties.getAccessKeyId(),
                properties.getAccessKeySecret());
    }
}
