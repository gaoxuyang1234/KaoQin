package com.yxysoft.utils;

import java.io.FileInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;


@Component
public class QiniuUtil{


    @Value("${qiniu.accessKey}")
    private String accessKey="eRwLoX6NIO68BdR0EWOa7ZvrA3NNCN9QVM7IBoB8";

    @Value("${qiniu.secretKey}")
    private String secretKey="h-yBuWBWTo9ZQZcu4wFirCNC5pkEbZpfu_fT4SyZ";

    @Value("${qiniu.bucket}")
    private String bucket="aft-nj";

    @Value("${qiniu.path}")
    private String path="http\\://source.tanyangnet.com";

    /**
     * 将图片上传到七牛云
     * @param file
     * @param key 保存在空间中的名字，如果为空会使用文件的hash值为文件名
     * @return
     */
    public String uploadImg(FileInputStream file, String key) {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
//...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
//...生成上传凭证，然后准备上传
//        String bucket = "oy09glbzm.bkt.clouddn.com";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        try {
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket,key);
            try {
                Response response = uploadManager.put(file, key, upToken, null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
//                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
//                System.out.println(putRet.key);
//                System.out.println(putRet.hash);
                String return_path = path+"/"+putRet.key;
                return return_path;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}