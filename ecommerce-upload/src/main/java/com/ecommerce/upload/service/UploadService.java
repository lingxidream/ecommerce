package com.ecommerce.upload.service;


import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;


@Service
public class UploadService {
    @Resource
    private FastFileStorageClient fastFileStorageClient;

    private static final List<String> CONTENT_TYPES = Arrays.asList("image/jpeg", "image/gif");

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    /*
    1. 校验文件大小
    2. 校验文件的媒体类型
     3. 校验文件的内容
     */
    public String upload(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        //校验文件的类型
        String contentType = file.getContentType();
        if(!CONTENT_TYPES.contains(contentType)){
            //文件类型不合法，直接返回null
            LOGGER.info("文件类型不合法：{}",originalFilename);
            return null;
        }

        try {
            //检验文件内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if(bufferedImage == null){
                LOGGER.info("文件内容不合法:{}",originalFilename);
                return null;
            }

            //保存到服务器
            //file.transferTo(new File("C:\\ecommerce\\images\\" + originalFilename));


            String s = StringUtils.substringAfterLast(originalFilename, ".");//获取后缀名
            StorePath storePath = this.fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(),s, null);

            //生成URL地址，返回
            //return "http://image.ecommerce.com/"+originalFilename;
            return "http://image.ecommerce.com/"+storePath.getFullPath();

        }catch (Exception e){
            LOGGER.info("服务器内部错误：{}", originalFilename);
            e.printStackTrace();
        }
        return null;
    }
}
