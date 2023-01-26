package com.upuphone.cloudplatform.usercenter.service.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/4/18
 */
@Slf4j
public final class FileUtil {

    private FileUtil() {

    }

    /**
     * 根据url拿取file
     *
     * @param url    文件Url
     * @param suffix 文件后缀名
     */
    @SneakyThrows
    public static File createFileByUrl(String url, String suffix) {
        return getFileFromInputStream(getInputStreamFromNetByUrl(url), suffix);
    }

    /**
     * 根据地址获得数据的字节流
     *
     * @param strUrl 网络连接地址
     * @return byte[]
     */
    private static InputStream getInputStreamFromNetByUrl(String strUrl) throws Exception {
        URL url = new URL(strUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5 * 1000);
        // 通过输入流获取图片数据
        return conn.getInputStream();
    }

    /**
     * 从输入流中获取数据
     *
     * @param inStream 输入流
     * @return byte[]
     */
    private static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    private static File getFileFromBytes(byte[] b, String suffix) throws IOException {
        BufferedOutputStream stream;
        File file;
        file = File.createTempFile("pattern", "." + suffix);
        log.info("临时文件位置：[{}]", file.getCanonicalPath());
        try (FileOutputStream fs = new FileOutputStream(file)) {
            stream = new BufferedOutputStream(fs);
            stream.write(b);
            return file;
        }
    }

    @SneakyThrows
    private static File getFileFromInputStream(InputStream in, String suffix) {
        File file = File.createTempFile("pattern", "." + suffix);
        log.info("临时文件位置：[{}]", file.getCanonicalPath());
        byte[] buffer = new byte[1024];
        try (FileOutputStream fs = new FileOutputStream(file)) {
            int len;
            while ((len = in.read(buffer)) != -1) {
                fs.write(buffer, 0, len);
            }
            return file;
        }
    }
}
