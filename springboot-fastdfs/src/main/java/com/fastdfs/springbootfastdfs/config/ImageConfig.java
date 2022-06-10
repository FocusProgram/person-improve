package com.fastdfs.springbootfastdfs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/3/19 17:19
 * @Description:
 */
@Configuration
public class ImageConfig {

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(100 * 1024 * 1024);
        multipartResolver.setMaxInMemorySize(4096000);
        multipartResolver.setDefaultEncoding("UTF-8");
        return multipartResolver;
    }

}
