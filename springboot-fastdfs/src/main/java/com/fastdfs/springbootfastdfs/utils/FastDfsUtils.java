package com.fastdfs.springbootfastdfs.utils;

import com.fastdfs.springbootfastdfs.file.FastDFSFile;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/3/19 16:16
 * @Description: 文件上传工具类
 */
public class FastDfsUtils {

    private static TrackerClient trackerClient;

    private static String storageIp = "file.zjyuyue.com";

    private static Integer storagePort = 23000;

    static {
        try {
            ClientGlobal.initByProperties("fastdfs-client.conf");
            trackerClient = new TrackerClient(ClientGlobal.g_tracker_group);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 上传文件路径
     */
    public synchronized static String upload(FastDFSFile file, NameValuePair[] valuePairs, int storePath) {
        String[] uploadResults = null;
        try {
            StorageServer storageServer = new StorageServer(storageIp, storagePort, storePath);
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageClient storageClient = new StorageClient(trackerServer, storageServer);
            uploadResults = storageClient.upload_file("image", file.getContent(), file.getExt(), valuePairs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert uploadResults != null;
        return uploadResults[0].concat("/").concat(uploadResults[1]);
    }

    /**
     * 删除文件
     *
     * @param fileUrl   文件名
     * @param storePath store_path
     */
    public static void deletefile(String fileUrl, int storePath) {
        try {
            StorageServer storageServer = new StorageServer(storageIp, storagePort, storePath);
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageClient storageClient = new StorageClient(trackerServer, storageServer);
            storageClient.delete_file("image", fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件下载
     *
     * @param fileUrl   文件名
     * @param storePath store_path
     * @return byte[]
     */
    public static byte[] download(String fileUrl, int storePath) {
        byte[] groups = null;
        try {
            StorageServer storageServer = new StorageServer(storageIp, storagePort, storePath);
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageClient storageClient = new StorageClient(trackerServer, storageServer);
            groups = storageClient.download_file("image", fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groups;
    }
}
