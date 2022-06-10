package com.fastdfs.springbootfastdfs.controller;


import com.fastdfs.springbootfastdfs.base.Result;
import com.fastdfs.springbootfastdfs.file.FastDFSFile;
import com.fastdfs.springbootfastdfs.utils.FastDfsUtils;
import com.fastdfs.springbootfastdfs.utils.ResultUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ProtoCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/3/19 16:19
 * @Description: 文件上传
 */
@RestController
@RequestMapping(value = "fastdfs")
public class UploadController {

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString());
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${fastdfs.http_secret_key}")
    private String secretKey;

    @Value("${hk.base.url}")
    private String hkBaseUrl;

    @PostMapping("getToken")
    public ResponseEntity<Result<Map<String, Object>>> getToken(@RequestParam("url") String url) throws IOException, MyException, NoSuchAlgorithmException {
        //unix时间戳 以秒为单位
        int ts = (int) (System.currentTimeMillis() / 1000);
        String token = new String();
        token = ProtoCommon.getToken(url, ts, secretKey);
        StringBuilder sb = new StringBuilder();
        sb.append(hkBaseUrl);
        sb.append(url);
        sb.append("?token=").append(token);
        sb.append("&ts=").append(ts);
        Map<String, Object> result = new HashMap<>(10);
        result.put("filePath", sb.toString());
        return ResponseEntity.ok(ResultUtil.success(result));
    }

    @PostMapping("upload")
    public ResponseEntity<Result<Map<String, Object>>> upload(@RequestParam("storePath") int storePath, MultipartFile multipartFile, HttpServletRequest request) throws IOException {
        Map<String, Object> result = new HashMap<String, Object>();
        String ext = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
        FastDFSFile file = new FastDFSFile(multipartFile.getBytes(), ext);
        NameValuePair[] meta_list = new NameValuePair[3];
        meta_list[0] = new NameValuePair("fileName", multipartFile.getOriginalFilename());
        meta_list[1] = new NameValuePair("fileLength", String.valueOf(multipartFile.getSize()));
        meta_list[2] = new NameValuePair("fileExt", ext);
        String filePath = null;
        result.put("filename", multipartFile.getOriginalFilename());
        try {
            filePath = FastDfsUtils.upload(file, meta_list, storePath);
            logger.info("上传文件成功");
        } catch (Exception e) {
            logger.error("上传文件失败", e);
            result.put("error", 900001);
            result.put("msg", "上传图片失败");
            return ResponseEntity.ok(ResultUtil.fail());
        }
        result.put("msg", "上传图片成功");
        result.put("url", filePath);

        return ResponseEntity.ok(ResultUtil.success(result));
    }

//    @PostMapping("upload")
//    @ApiOperation(value = "上传文件", httpMethod = "POST")
//    public ServiceResult<Map<String, Object>> upload(@RequestParam("storePath") int storePath, HttpServletRequest request) throws IOException {
//        List<FileItem> fileItems = (List<FileItem>) request.getAttribute("List<FileItem>");
//        if (fileItems == null) {
//            DiskFileItemFactory factory = new DiskFileItemFactory();
//            ServletFileUpload upload = new ServletFileUpload(factory);
//            upload.setHeaderEncoding("UTF-8");
//            try {
//                fileItems = upload.parseRequest(request);
//            } catch (FileUploadException e) {
//                e.printStackTrace();
//            }
//        }
//        Map<String, Object> result = new HashMap<String, Object>();
//        for (FileItem item : fileItems) {
//            if (!item.isFormField()) {
//                MultipartFile multipartFile = new CommonsMultipartFile(item);
//                String ext = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
//                FastDFSFile file = new FastDFSFile(multipartFile.getBytes(), ext);
//                NameValuePair[] meta_list = new NameValuePair[3];
//                meta_list[0] = new NameValuePair("fileName", multipartFile.getOriginalFilename());
//                meta_list[1] = new NameValuePair("fileLength", String.valueOf(multipartFile.getSize()));
//                meta_list[2] = new NameValuePair("fileExt", ext);
//                String filePath = null;
//                try {
//                    filePath = FastDfsUtils.upload(file, meta_list, storePath);
//                    logger.info("上传文件成功");
//                } catch (Exception e) {
//                    logger.error("上传文件失败", e);
//                    return new ServiceResult<Map<String, Object>>(StatusKey.UPLOAD_IMAGE_FAIL, "上传文件失败", null);
//                }
//                result.put("filePath", filePath);
//            }
//        }
//        return new ServiceResult<Map<String, Object>>(StatusKey.UPLOAD_IMAGE_SUCCESS, "上传文件成功", result);
//    }

    @DeleteMapping("delete")
    public ResponseEntity<Result<Map<String, Object>>> delete(@RequestParam("fileUrl") String fileUrl, int storePath, HttpServletRequest request) {
        if (StringUtils.isEmpty(fileUrl)) {
            return ResponseEntity.ok(ResultUtil.custom(false, "不存在该文件"));
        }
        try {
            FastDfsUtils.deletefile(fileUrl, storePath);
            logger.info("删除文件成功");
        } catch (Exception e) {
            logger.error("删除文件失败", e);
            return ResponseEntity.ok(ResultUtil.custom(false, "删除文件失败"));
        }
        return ResponseEntity.ok(ResultUtil.custom(true, "成功删除该文件"));
    }

    @PostMapping(value = "download")
    public ResponseEntity<Result<Map<String, Object>>> download(@RequestParam("fileUrl") String fileUrl, int storePath, HttpServletRequest request) {
        if (StringUtils.isEmpty(fileUrl)) {
            return ResponseEntity.ok(ResultUtil.custom(false, "不存在该文件"));
        }
        String[] split = fileUrl.split("/");
        String downloadFile = "D:\\" + split[3];
        try {
            byte[] download = FastDfsUtils.download(fileUrl, storePath);
            IOUtils.write(download, new FileOutputStream(downloadFile));
            logger.info("下载文件成功,文件位置为" + downloadFile);
        } catch (Exception e) {
            logger.error("下载文件失败", e);
            return ResponseEntity.ok(ResultUtil.custom(false, "下载文件失败"));
        }
        return ResponseEntity.ok(ResultUtil.custom(true, "成功下载该文件,文件位置为" + downloadFile));
    }
}
