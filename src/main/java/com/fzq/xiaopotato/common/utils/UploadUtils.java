package com.fzq.xiaopotato.common.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public class UploadUtils {
    // domain
    public static final String ALI_DOMAIN = "https://fzqqq-test.oss-us-east-1.aliyuncs.com/";

    public static final String END_POINT = "http://oss-us-east-1.aliyuncs.com";

    public static final String ACCESS_KEY_ID = "LTAI5t9j3DW7ZSvuBEirhw5V";
    public static final String ACCESS_KEY_SECRET = "pVhaGprnUdGPCjN8O4VYzJHkMw70VO";
    public static final String BUCKET_NAME = "fzqqq-test";
    public static String uploadImage(MultipartFile file) throws IOException {
        // file name generator
        String originalFileName = file.getOriginalFilename();
        String ext = "." + FilenameUtils.getExtension(originalFileName);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String fileName = uuid + ext;

        // OSS client object
        OSS ossCilent = new OSSClientBuilder().build(END_POINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        ossCilent.putObject(
                BUCKET_NAME,
                fileName,
                file.getInputStream()
        );
        ossCilent.shutdown();
        return ALI_DOMAIN + fileName;
    }

    // delete image
    public static void deleteImage(String imageUrl) {
        String fileName = imageUrl.replace(ALI_DOMAIN, "");
        OSS ossCilent = new OSSClientBuilder().build(END_POINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        ossCilent.deleteObject(BUCKET_NAME, fileName);
        ossCilent.shutdown();
    }





}
