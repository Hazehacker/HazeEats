package top.hazenix.controller.admin;


import top.hazenix.constant.MessageConstant;
import top.hazenix.result.Result;
import top.hazenix.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/admin/common")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}",file);
        String filePath;
        try {

            filePath = aliOssUtil.upload(file.getBytes(), file.getOriginalFilename());
            //前端请求路径

            return Result.success(filePath);//success方法放在这里而不是放在末尾


        } catch (IOException e) {
            log.info("文件上传失败：{}",e);

        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }





}
