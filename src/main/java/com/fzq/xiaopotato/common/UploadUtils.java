package com.fzq.xiaopotato.common;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public class UploadUtils {
    // domain
    public static final String ALI_DOMAIN = "https://fzqqq-test.oss-us-east-1.aliyuncs.com/";

    public static String uploadImage(MultipartFile file) throws IOException {
        // file name generator
        String originalFileName = file.getOriginalFilename();
        String ext = "." + FilenameUtils.getExtension(originalFileName);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String fileName = uuid + ext;

        // end point
        String endPoint = "http://oss-us-east-1.aliyuncs.com";

        String accessKeyId = "LTAI5t9j3DW7ZSvuBEirhw5V";
        String accessKeySecret = "pVhaGprnUdGPCjN8O4VYzJHkMw70VO";

        // OSS client object
        OSS ossCilent = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);
        ossCilent.putObject(
                "fzqqq-test",
                fileName,
                file.getInputStream()
        );
        ossCilent.shutdown();
        return ALI_DOMAIN + fileName;
    }





}
