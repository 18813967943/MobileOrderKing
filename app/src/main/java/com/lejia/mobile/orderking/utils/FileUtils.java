package com.lejia.mobile.orderking.utils;

import java.io.File;

/**
 * Author by HEKE
 *
 * @time 2017/12/1 9:21
 * TODO: 文件操作封装对象
 */
public class FileUtils {

    /**
     * 创建目录
     *
     * @param absolutelyDir
     */
    public static boolean createDirectory(String absolutelyDir) {
        if (TextUtils.isTextEmpity(absolutelyDir))
            return false;
        try {
            File dir = new File(absolutelyDir);
            if (!dir.exists()) {
                dir.setExecutable(true);
                dir.setReadable(true);
                dir.setWritable(true);
                dir.mkdirs();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建文件
     *
     * @param absolutelyFile
     */
    public static boolean createFile(String absolutelyFile) {
        if (TextUtils.isTextEmpity(absolutelyFile))
            return false;
        try {
            File file = new File(absolutelyFile);
            if (!file.exists()) {
                file.setExecutable(true);
                file.setReadable(true);
                file.setWritable(true);
                file.createNewFile();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除文件
     *
     * @param filePath
     */
    public static boolean deleteFile(String filePath) {
        if (TextUtils.isTextEmpity(filePath))
            return false;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除目录下文件
     *
     * @param dir
     */
    public static boolean deleteAllFiles(String dir) {
        if (TextUtils.isTextEmpity(dir))
            return false;
        try {
            File dirFile = new File(dir);
            if (dirFile.exists()) {
                File[] files = dirFile.listFiles();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        File file = files[i];
                        if (file.isFile()) {
                            file.delete();
                        }
                    }
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}
