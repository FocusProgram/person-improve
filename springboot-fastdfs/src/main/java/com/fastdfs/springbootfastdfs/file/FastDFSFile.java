package com.fastdfs.springbootfastdfs.file;

import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/3/19 16:12
 * @Description: 文件信息类
 */
@Data
public class FastDFSFile implements Serializable {

    private static final long serialVersionUID = -1322280197089344117L;

    private byte[] content;

    private String name;

    private String ext;

    private String length;

    public FastDFSFile(byte[] content, String ext) {
        this.content = content;
        this.ext = ext;
    }

    public FastDFSFile(byte[] content, String name, String ext) {
        this.content = content;
        this.name = name;
        this.ext = ext;
    }

    public FastDFSFile(byte[] content, String name, String ext, String length) {
        this.content = content;
        this.name = name;
        this.ext = ext;
        this.length = length;
    }

}
