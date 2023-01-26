package com.upuphone.cloudplatform.usercenter.service.common.thirdparty;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class UrlToMultipartFile {

    /**
     * inputStream 转 File
     */
    public static File inputStreamToFile(InputStream ins, String name) throws Exception {
        //System.getProperty("java.io.tmpdir")临时目录+File.separator目录中间的间隔符+文件名
        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + name);
        //        if (file.exists()) {
        //            return file;
        //        }
        OutputStream os = new FileOutputStream(file);
        int bytesRead;
        int len = 8192;
        byte[] buffer = new byte[len];
        while ((bytesRead = ins.read(buffer, 0, len)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        ins.close();
        return file;
    }

    /**
     * file转multipartFile
     */
    public static MultipartFile fileToMultipartFile(File file) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem item = factory.createItem(file.getName(), "text/plain", true, file.getName());
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try {
            FileInputStream fis = new FileInputStream(file);
            OutputStream os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new CommonsMultipartFile(item);
    }

    /**
     * url转MultipartFile
     */
    public static MultipartFile urlToMultipartFile(String url) throws Exception {
        File file = null;
        MultipartFile multipartFile = null;
        try {
            HttpURLConnection httpUrl = (HttpURLConnection) new URL(url).openConnection();
            httpUrl.connect();
            file = UrlToMultipartFile.inputStreamToFile(httpUrl.getInputStream(), "template.png");
            log.info("---------" + file + "-------------");

            multipartFile = UrlToMultipartFile.fileToMultipartFile(file);
            httpUrl.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return multipartFile;
    }
}

