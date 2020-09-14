//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bedrock.module_base.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.security.NoSuchAlgorithmException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtils {
    private static final String TAG = "FileUtils";
    public static final int ZIP_BUFFER_SIZE = 4096;
    public static final FileUtils.FileComparator SIMPLE_COMPARATOR = new FileUtils.FileComparator() {
        public boolean equals(File lhs, File rhs) {
            return lhs.length() == rhs.length() && lhs.lastModified() == rhs.lastModified();
        }
    };
    private static final Object sCacheDirLock = new Object();
    private static volatile String mSdcardState;
    private static BroadcastReceiver mSdcardStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            try {
                FileUtils.mSdcardState = Environment.getExternalStorageState();
            } catch (Throwable var4) {
            }

        }
    };
    private static volatile boolean sHasRegister = false;

    public FileUtils() {
    }

    public static boolean copyFiles(File src, File dst) {
        return copyFiles(src, dst, (FileFilter)null);
    }

    public static boolean copyFiles(File src, File dst, FileFilter filter) {
        return copyFiles(src, dst, filter, SIMPLE_COMPARATOR);
    }

    public static boolean copyFiles(File src, File dst, FileFilter filter, FileUtils.FileComparator comparator) {
        if (src != null && dst != null) {
            if (!src.exists()) {
                return false;
            } else if (src.isFile()) {
                return performCopyFile(src, dst, filter, comparator);
            } else {
                File[] paths = src.listFiles();
                if (paths == null) {
                    return false;
                } else {
                    boolean result = true;
                    File[] var6 = paths;
                    int var7 = paths.length;

                    for(int var8 = 0; var8 < var7; ++var8) {
                        File sub = var6[var8];
                        if (!copyFiles(sub, new File(dst, sub.getName()), filter)) {
                            result = false;
                        }
                    }

                    return result;
                }
            }
        } else {
            return false;
        }
    }

    public static void copyFile(String srcFilename, String destFilename, boolean overwrite) throws IOException {
        File srcFile = new File(srcFilename);
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Cannot find the source file: " + srcFile.getAbsolutePath());
        } else if (!srcFile.canRead()) {
            throw new IOException("Cannot read the source file: " + srcFile.getAbsolutePath());
        } else {
            File destFile = new File(destFilename);
            if (!overwrite) {
                if (destFile.exists()) {
                    return;
                }
            } else if (destFile.exists()) {
                if (!destFile.canWrite()) {
                    throw new IOException("Cannot write the destination file: " + destFile.getAbsolutePath());
                }
            } else if (!destFile.createNewFile()) {
                throw new IOException("Cannot write the destination file: " + destFile.getAbsolutePath());
            }

            BufferedInputStream inputStream = null;
            BufferedOutputStream outputStream = null;
            byte[] block = new byte[1024];

            try {
                inputStream = new BufferedInputStream(new FileInputStream(srcFile));
                outputStream = new BufferedOutputStream(new FileOutputStream(destFile));

                while(true) {
                    int readLength = inputStream.read(block);
                    if (readLength == -1) {
                        return;
                    }

                    outputStream.write(block, 0, readLength);
                }
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException var17) {
                    }
                }

                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException var16) {
                    }
                }

            }
        }
    }

    public static long getDirSize(File file) {
        if (file.exists()) {
            if (!file.isDirectory()) {
                return file.length();
            } else {
                File[] children = file.listFiles();
                long size = 0L;
                if (children != null) {
                    File[] var4 = children;
                    int var5 = children.length;

                    for(int var6 = 0; var6 < var5; ++var6) {
                        File f = var4[var6];
                        size += getDirSize(f);
                    }
                }

                return size;
            }
        } else {
            return 0L;
        }
    }

    private static boolean performCopyFile(File srcFile, File dstFile, FileFilter filter, FileUtils.FileComparator comparator) {
        if (srcFile != null && dstFile != null) {
            if (filter != null && !filter.accept(srcFile)) {
                return false;
            } else {
                FileChannel inc = null;
                FileChannel ouc = null;

                boolean var7;
                try {
                    boolean var22;
                    if (!srcFile.exists() || !srcFile.isFile()) {
                        var22 = false;
                        return var22;
                    }

                    if (dstFile.exists()) {
                        if (comparator != null && comparator.equals(srcFile, dstFile)) {
                            var22 = true;
                            return var22;
                        }

                        delete(dstFile);
                    }

                    File toParent = dstFile.getParentFile();
                    if (toParent.isFile()) {
                        delete(toParent);
                    }

                    if (toParent.exists() || toParent.mkdirs()) {
                        inc = (new FileInputStream(srcFile)).getChannel();
                        ouc = (new FileOutputStream(dstFile)).getChannel();
                        ouc.transferFrom(inc, 0L, inc.size());
                        return true;
                    }

                    var7 = false;
                } catch (Throwable var20) {
                    var20.printStackTrace();
                    delete(dstFile);
                    var7 = false;
                    return var7;
                } finally {
                    try {
                        if (inc != null) {
                            inc.close();
                        }

                        if (ouc != null) {
                            ouc.close();
                        }
                    } catch (Throwable var19) {
                    }

                }

                return var7;
            }
        } else {
            return false;
        }
    }

    public static void copyAssets(Context context, String assetName, String dst) {
        if (!isEmpty(dst)) {
            if (assetName == null) {
                assetName = "";
            }

            AssetManager assetManager = context.getAssets();
            String[] files = null;

            try {
                files = assetManager.list(assetName);
            } catch (FileNotFoundException var11) {
                if (assetName.length() > 0) {
                    performCopyAssetsFile(context, assetName, dst);
                }
            } catch (IOException var12) {
                var12.printStackTrace();
            }

            if (files != null) {
                if (files.length == 0 && assetName.length() > 0) {
                    performCopyAssetsFile(context, assetName, dst);
                }

                String[] var5 = files;
                int var6 = files.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    String file = var5[var7];
                    if (!isEmpty(file)) {
                        String newAssetDir = assetName.length() == 0 ? file : assetName + File.separator + file;
                        String newDestDir = dst + File.separator + file;
                        copyAssets(context, newAssetDir, newDestDir);
                    }
                }

            }
        }
    }

    private static void performCopyAssetsFile(Context context, String assetPath, String dstPath) {
        if (!isEmpty(assetPath) && !isEmpty(dstPath)) {
            AssetManager assetManager = context.getAssets();
            File dstFile = new File(dstPath);
            InputStream in = null;
            BufferedOutputStream out = null;

            try {
                if (dstFile.exists()) {
                    boolean tryStream = false;

                    try {
                        AssetFileDescriptor fd = assetManager.openFd(assetPath);
                        if (dstFile.length() == fd.getLength()) {
                            return;
                        }

                        if (dstFile.isDirectory()) {
                            delete(dstFile);
                        }
                    } catch (IOException var33) {
                        tryStream = true;
                    }

                    if (tryStream) {
                        InputStream tmpIn = assetManager.open(assetPath);

                        try {
                            if (dstFile.length() == (long)tmpIn.available()) {
                                return;
                            }

                            if (dstFile.isDirectory()) {
                                delete(dstFile);
                            }
                        } catch (IOException var31) {
                        } finally {
                            tmpIn.close();
                        }
                    }
                }

                File parent = dstFile.getParentFile();
                if (parent.isFile()) {
                    delete(parent);
                }

                if (!parent.exists() && !parent.mkdirs()) {
                    return;
                }

                in = assetManager.open(assetPath);
                out = new BufferedOutputStream(new FileOutputStream(dstFile));
                byte[] buf = new byte[1024];

                int len;
                while((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } catch (Throwable var34) {
                var34.printStackTrace();
                delete(dstFile);
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }

                    if (out != null) {
                        out.close();
                    }
                } catch (Throwable var30) {
                }

            }

        }
    }

    public static void delete(File file) {
        delete(file, false);
    }

    public static void delete(File file, boolean ignoreDir) {
        if (file != null && file.exists()) {
            if (file.isFile()) {
                boolean isDeleteSuccuss = file.delete();
                if (!isDeleteSuccuss) {
                    Log.d("FileUtils", "delete() delete failed");
                }

            } else {
                File[] fileList = file.listFiles();
                if (fileList != null) {
                    File[] var3 = fileList;
                    int var4 = fileList.length;

                    for(int var5 = 0; var5 < var4; ++var5) {
                        File f = var3[var5];
                        delete(f, ignoreDir);
                    }

                    if (!ignoreDir) {
                        boolean isDeleteSuccuss = file.delete();
                        if (!isDeleteSuccuss) {
                            Log.d("FileUtils", "ignoreDir = false ,delete() delete failed");
                        }
                    }

                }
            }
        }
    }

    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean zip(File[] srcFiles, FileOutputStream dest) {
        if (srcFiles != null && srcFiles.length >= 1 && dest != null) {
            boolean resu = false;
            ZipOutputStream zos = null;

            try {
                byte[] buffer = new byte[4096];
                zos = new ZipOutputStream(new BufferedOutputStream(dest));
                File[] var5 = srcFiles;
                int var6 = srcFiles.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    File src = var5[var7];
                    doZip(zos, src, (String)null, buffer);
                }

                zos.flush();
                zos.closeEntry();
                resu = true;
            } catch (IOException var12) {
                resu = false;
            } finally {
                IOUtils.closeQuietly(zos);
            }

            return resu;
        } else {
            return false;
        }
    }

    public static boolean zip(File[] srcFiles, File dest) {
        try {
            return zip(srcFiles, new FileOutputStream(dest));
        } catch (FileNotFoundException var3) {
            return false;
        }
    }

    public static boolean zip(File src, File dest) {
        return zip(new File[]{src}, dest);
    }

    public static boolean unzip(File src, File destFolder) {
        if (src != null && src.length() >= 1L && src.canRead()) {
            boolean resu = false;
            if (!destFolder.exists()) {
                destFolder.mkdirs();
            }

            ZipInputStream zis = null;
            BufferedOutputStream bos = null;
            ZipEntry entry = null;
            byte[] buffer = new byte[8192];
            boolean var7 = false;

            try {
                zis = new ZipInputStream(new FileInputStream(src));

                label117:
                while(true) {
                    while(true) {
                        if (null == (entry = zis.getNextEntry())) {
                            break label117;
                        }

                        System.out.println(entry.getName());
                        if (entry.getName().startsWith("../")) {
                            break label117;
                        }

                        if (entry.isDirectory()) {
                            (new File(destFolder, entry.getName())).mkdirs();
                        } else {
                            File entryFile = new File(destFolder, entry.getName());
                            entryFile.getParentFile().mkdirs();
                            bos = new BufferedOutputStream(new FileOutputStream(entryFile));

                            int readLen;
                            while(-1 != (readLen = zis.read(buffer, 0, buffer.length))) {
                                bos.write(buffer, 0, readLen);
                            }

                            bos.flush();
                            bos.close();
                        }
                    }
                }

                if (zis != null) {
                    zis.closeEntry();
                    zis.close();
                }

                resu = true;
            } catch (IOException var12) {
                resu = false;
            } finally {
                IOUtils.closeQuietly(bos);
                IOUtils.closeQuietly(zis);
            }

            return resu;
        } else {
            return false;
        }
    }

    public static void doZip(ZipOutputStream zos, File file, String root, byte[] buffer) throws IOException {
        if (zos != null && file != null) {
            if (!file.exists()) {
                throw new FileNotFoundException("Target File is missing");
            } else {
                BufferedInputStream bis = null;
                int readLen = 0;
                String rootName = TextUtils.isEmpty(root) ? file.getName() : root + File.separator + file.getName();
                if (file.isFile()) {
                    try {
                        bis = new BufferedInputStream(new FileInputStream(file));
                        zos.putNextEntry(new ZipEntry(rootName));

                        while(-1 != (readLen = bis.read(buffer, 0, buffer.length))) {
                            zos.write(buffer, 0, readLen);
                        }

                        IOUtils.closeQuietly(bis);
                    } catch (IOException var12) {
                        IOUtils.closeQuietly(bis);
                        throw var12;
                    }
                } else if (file.isDirectory()) {
                    File[] subFiles = file.listFiles();
                    if (subFiles != null) {
                        File[] var8 = subFiles;
                        int var9 = subFiles.length;

                        for(int var10 = 0; var10 < var9; ++var10) {
                            File subFile = var8[var10];
                            doZip(zos, subFile, rootName, buffer);
                        }
                    }
                }

            }
        } else {
            throw new IOException("I/O Object got NullPointerException");
        }
    }

    public static boolean unjar(File src, File destFolder) {
        if (src != null && src.length() >= 1L && src.canRead()) {
            boolean resu = false;
            if (!destFolder.exists()) {
                destFolder.mkdirs();
            }

            JarInputStream zis = null;
            BufferedOutputStream bos = null;
            JarEntry entry = null;
            byte[] buffer = new byte[8192];
            boolean var7 = false;

            try {
                zis = new JarInputStream(new FileInputStream(src));

                label117:
                while(true) {
                    while(true) {
                        if (null == (entry = zis.getNextJarEntry())) {
                            break label117;
                        }

                        System.out.println(entry.getName());
                        if (entry.getName().startsWith("../")) {
                            break label117;
                        }

                        if (entry.isDirectory()) {
                            (new File(destFolder, entry.getName())).mkdirs();
                        } else {
                            bos = new BufferedOutputStream(new FileOutputStream(new File(destFolder, entry.getName())));

                            int readLen;
                            while(-1 != (readLen = zis.read(buffer, 0, buffer.length))) {
                                bos.write(buffer, 0, readLen);
                            }

                            bos.flush();
                            bos.close();
                        }
                    }
                }

                if (zis != null) {
                    zis.closeEntry();
                    zis.close();
                }

                resu = true;
            } catch (IOException var12) {
                resu = false;
            } finally {
                IOUtils.closeQuietly(bos);
                IOUtils.closeQuietly(zis);
            }

            return resu;
        } else {
            return false;
        }
    }

    public static boolean isExistFile(String uploadFilePath) {
        if (TextUtils.isEmpty(uploadFilePath)) {
            return false;
        } else {
            try {
                File file = new File(uploadFilePath);
                return file.exists() && file.isFile() && file.length() != 0L;
            } catch (Exception var2) {
                return false;
            }
        }
    }

    public static boolean createFile(String filePath) throws IOException {
        return create(new File(filePath));
    }

    public static boolean create(File file) throws IOException {
        if (file.exists()) {
            return true;
        } else {
            File parent = file.getParentFile();
            boolean flag = parent.mkdirs();
            if (!flag) {
            }

            return file.createNewFile();
        }
    }

    public static boolean moveFile(String srcPath, String destPath) {
        File src = new File(srcPath);
        File dest = new File(destPath);
        boolean ret = copyFiles(src, dest);
        if (ret) {
            deleteFile(srcPath);
        }

        return ret;
    }

    public static boolean moveFile(File src, File dest) {
        boolean ret = copyFiles(src, dest);
        if (ret) {
            ret = deleteFile(src.getAbsolutePath());
        }

        return ret;
    }

    public static boolean writeFile(String destFilePath, byte[] data, int startPos, int length) {
        FileOutputStream fos = null;

        try {
            boolean var5;
            if (!createFile(destFilePath)) {
                var5 = false;
                return var5;
            }

            fos = new FileOutputStream(destFilePath);
            fos.write(data, startPos, length);
            var5 = true;
            return var5;
        } catch (FileNotFoundException var10) {
        } catch (IOException var11) {
        } finally {
            IOUtils.closeQuietly(fos);
        }

        return false;
    }

    public static boolean writeFile(String destFilePath, InputStream in) {
        FileOutputStream fos = null;

        try {
            if (createFile(destFilePath)) {
                fos = new FileOutputStream(destFilePath);
                int len = 8192;
                byte[] buffer = new byte[len];

                int readCount;
                while((readCount = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, readCount);
                }

                boolean var6 = true;
                return var6;
            }

            boolean var3 = false;
            return var3;
        } catch (IOException var10) {
            var10.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fos);
        }

        return false;
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return true;
        } else {
            delete(file);
            return true;
        }
    }

    public static boolean mkdir(String path) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            return true;
        } else {
            boolean flag = file.mkdirs();
            if (!flag) {
            }

            return true;
        }
    }

    public static boolean copyFile(String srcPath, String destPath) {
        return copyFiles(new File(srcPath), new File(destPath));
    }

    public static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static boolean copyStream(InputStream src, OutputStream dest) {
        try {
            byte[] buffer = new byte[2048];

            int bytesread;
            while((bytesread = src.read(buffer)) != -1) {
                if (bytesread > 0) {
                    dest.write(buffer, 0, bytesread);
                }
            }

            boolean var4 = true;
            return var4;
        } catch (IOException var8) {
            var8.printStackTrace();
        } finally {
            IOUtils.closeQuietly(src);
            IOUtils.closeQuietly(dest);
        }

        return false;
    }

    public static byte[] readFile(String filePath) {
        if (isFileExist(filePath)) {
            try {
                return readFile(new File(filePath));
            } catch (Exception var2) {
                return new byte[0];
            }
        } else {
            return new byte[0];
        }
    }

    public static byte[] readFile(File file) throws IOException {
        int len = (int)file.length();
        if (len == 0) {
            return new byte[0];
        } else {
            byte[] data = null;
            BufferedInputStream bis = null;

            try {
                FileInputStream fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                data = new byte[len];
                bis.read(data);
            } finally {
                IOUtils.closeQuietly(bis);
            }

            return data;
        }
    }

    public static byte[] readFile(InputStream is) throws IOException {
        if (is == null) {
            return new byte[0];
        } else {
            byte[] data = null;
            BufferedInputStream bis = null;

            try {
                bis = new BufferedInputStream(is);
                data = new byte[is.available()];
                bis.read(data);
            } finally {
                IOUtils.closeQuietly(bis);
            }

            return data;
        }
    }

    public static boolean saveImg(Bitmap bitmap, File outputFile, CompressFormat format, int quality) {
        if (bitmap != null && outputFile != null) {
            boolean success = false;
            FileOutputStream os = null;

            boolean var7;
            try {
                File parentFile = outputFile.getParentFile();
                if (parentFile == null || parentFile.exists() || parentFile.mkdirs()) {
                    os = new FileOutputStream(outputFile.getAbsolutePath());
                    format = format == null ? CompressFormat.JPEG : format;
                    quality = quality >= 0 && quality <= 100 ? quality : 100;
                    bitmap.compress(format, quality, os);
                    success = true;
                    return success;
                }

                var7 = false;
            } catch (Exception var11) {
                var11.printStackTrace();
                return success;
            } finally {
                IOUtils.closeQuietly(os);
            }

            return var7;
        } else {
            return false;
        }
    }

    public static long getFileCount(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) {
            return 0L;
        } else {
            long count = 0L;
            File[] flist = f.listFiles();

            for(int i = 0; i < flist.length; ++i) {
                if (flist[i].isDirectory()) {
                    count += getFileCount(flist[i].getPath());
                } else {
                    ++count;
                }
            }

            return count;
        }
    }

//    public static String getFileMd5(String path) {
//        if (TextUtils.isEmpty(path)) {
//            return null;
//        } else {
//            try {
//                File file = new File(path);
//                if (file.exists()) {
//                    return getFileMd5(file);
//                }
//            } catch (Exception var2) {
//                var2.printStackTrace();
//            }
//
//            return null;
//        }
//    }

    private static String getCacheDir(Context context, String name) {
        return getCacheDir(context, name, false);
    }

    public static String getCacheDir(Context context, String name, boolean persist) {
        init(context);
        String dir = getExternalCacheDir(context, name, persist);
        return dir != null ? dir : getInternalCacheDir(context, name, persist);
    }

    public static String getExternalCacheDir(Context context, String name) {
        return getExternalCacheDir(context, name, false);
    }

    public static String getExternalCacheDir(Context context, String name, boolean persist) {
        init(context);
        String dir = getExternalCacheDir(context, persist);
        if (dir == null) {
            return null;
        } else if (isEmpty(name)) {
            return dir;
        } else {
            File file = new File(dir + File.separator + name);
            if (!file.exists() || !file.isDirectory()) {
                synchronized(sCacheDirLock) {
                    if (!file.isDirectory()) {
                        delete(file);
                        file.mkdirs();
                    } else if (!file.exists()) {
                        file.mkdirs();
                    }
                }
            }

            return file.getAbsolutePath();
        }
    }

    public static String getExternalCacheDir(Context context, boolean persist) {
        init(context);
        if (!isExternalAvailable()) {
            return null;
        } else {
            File externalDir = !persist ? FileUtils.InnerEnvironment.getExternalCacheDir(context, false) : FileUtils.InnerEnvironment.getExternalFilesDir(context, (String)null, false);
            return externalDir == null ? null : externalDir.getAbsolutePath();
        }
    }

    public static String getExternalCacheDirExt(Context context, String name) {
        return getExternalCacheDirExt(context, name, false);
    }

    public static String getExternalCacheDirExt(Context context, String name, boolean persist) {
        init(context);
        String dir = getExternalCacheDirExt(context, persist);
        if (dir == null) {
            return null;
        } else if (isEmpty(name)) {
            return dir;
        } else {
            File file = new File(dir + File.separator + name);
            if (!file.exists() || !file.isDirectory()) {
                synchronized(sCacheDirLock) {
                    if (!file.isDirectory()) {
                        delete(file);
                        file.mkdirs();
                    } else if (!file.exists()) {
                        file.mkdirs();
                    }
                }
            }

            return file.getAbsolutePath();
        }
    }

    public static String getExternalCacheDirExt(Context context, boolean persist) {
        init(context);
        if (!isExternalAvailable()) {
            return null;
        } else {
            File externalDir = !persist ? FileUtils.InnerEnvironment.getExternalCacheDir(context, true) : FileUtils.InnerEnvironment.getExternalFilesDir(context, (String)null, true);
            return externalDir == null ? null : externalDir.getAbsolutePath();
        }
    }

    public static String getInternalCacheDir(Context context, String name) {
        return getInternalCacheDir(context, name, false);
    }

    public static String getInternalCacheDir(Context context, String name, boolean persist) {
        init(context);
        String dir = getInternalCacheDir(context, persist);
        if (dir == null) {
            return null;
        } else if (isEmpty(name)) {
            return dir;
        } else {
            File file = new File(dir + File.separator + name);
            if (!file.exists() || !file.isDirectory()) {
                synchronized(sCacheDirLock) {
                    if (!file.isDirectory()) {
                        delete(file);
                        file.mkdirs();
                    } else if (!file.exists()) {
                        file.mkdirs();
                    }
                }
            }

            return file.getAbsolutePath();
        }
    }

    public static String getInternalCacheDir(Context context, boolean persist) {
        init(context);
        File cacheDir;
        if (!persist) {
            cacheDir = context.getCacheDir();
            if (cacheDir != null) {
                return cacheDir.getAbsolutePath();
            }
        } else {
            cacheDir = context.getFilesDir();
            if (cacheDir != null) {
                return cacheDir.getAbsolutePath() + File.separator + "cache";
            }
        }

        return null;
    }

    public static String getInternalFileDir(Context context, boolean persist) {
        init(context);
        return !persist ? context.getCacheDir().getAbsolutePath() : context.getFilesDir().getAbsolutePath() + File.separator;
    }

    public static boolean isExternal(String path) {
        String externalCacheDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        return path != null && path.startsWith(externalCacheDir);
    }

    public static boolean isInternal(String path) {
        String internalCacheDir = Environment.getDataDirectory().getAbsolutePath();
        return path != null && path.startsWith(internalCacheDir);
    }

    public static boolean isExternalAvailable() {
        if (sHasRegister) {
            String state = mSdcardState;
            if (state == null) {
                state = Environment.getExternalStorageState();
                mSdcardState = state;
            }

            return "mounted".equals(state);
        } else {
            return "mounted".equals(Environment.getExternalStorageState());
        }
    }

    private static void registerSdcardReceiver(Context context) {
        try {
            if (!sHasRegister) {
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
                filter.addAction("android.intent.action.MEDIA_EJECT");
                filter.addAction("android.intent.action.MEDIA_MOUNTED");
                filter.addAction("android.intent.action.MEDIA_REMOVED");
                filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
                filter.addDataScheme("file");
                context.registerReceiver(mSdcardStateReceiver, filter);
                sHasRegister = true;
            }
        } catch (Throwable var2) {
        }

    }

    public static void init(Context context) {
        context = context.getApplicationContext();
        registerSdcardReceiver(context);
    }

    static class InnerEnvironment {
        private static final String TAG = "InnerEnvironment";
        private static final String EXTEND_SUFFIX = "-ext";
        private static final File EXTERNAL_STORAGE_ANDROID_DATA_DIRECTORY = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");

        InnerEnvironment() {
        }

        public static File getExternalStorageAndroidDataDir() {
            return EXTERNAL_STORAGE_ANDROID_DATA_DIRECTORY;
        }

        public static File getExternalStorageAppCacheDirectory(String packageName) {
            return new File(new File(EXTERNAL_STORAGE_ANDROID_DATA_DIRECTORY, packageName), "cache");
        }

        public static File getExternalStorageAppFilesDirectory(String packageName) {
            return new File(new File(EXTERNAL_STORAGE_ANDROID_DATA_DIRECTORY, packageName), "files");
        }

        public static File getExternalCacheDir(Context context, boolean extend) {
            if (!extend && PlatformUtil.version() >= 8) {
                try {
                    return context.getExternalCacheDir();
                } catch (Throwable var8) {
                }
            }

            Class var2 = FileUtils.InnerEnvironment.class;
            synchronized(FileUtils.InnerEnvironment.class) {
                File externalCacheDir = getExternalStorageAppCacheDirectory(context.getPackageName() + (extend ? "-ext" : ""));
                if (!externalCacheDir.exists()) {
                    try {
                        boolean isCreateSuccuss = (new File(getExternalStorageAndroidDataDir(), ".nomedia")).createNewFile();
                        if (!isCreateSuccuss) {
                            Log.w("InnerEnvironment", "Unable to create new file");
                        }
                    } catch (Throwable var6) {
                    }

                    if (!externalCacheDir.mkdirs()) {
                        Log.w("InnerEnvironment", "Unable to create external cache directory");
                        return null;
                    }
                }

                return externalCacheDir;
            }
        }

        public static File getExternalFilesDir(Context context, String type, boolean extend) {
            if (!extend && PlatformUtil.version() >= 8) {
                return context.getExternalFilesDir(type);
            } else {
                Class var3 = FileUtils.InnerEnvironment.class;
                synchronized(FileUtils.InnerEnvironment.class) {
                    File externalFilesDir = getExternalStorageAppFilesDirectory(context.getPackageName() + (extend ? "-ext" : ""));
                    if (!externalFilesDir.exists()) {
                        try {
                            boolean isCreateSuccuss = (new File(getExternalStorageAndroidDataDir(), ".nomedia")).createNewFile();
                            if (!isCreateSuccuss) {
                                Log.w("InnerEnvironment", "Unable to create nomedia file");
                            }
                        } catch (IOException var7) {
                        }

                        if (!externalFilesDir.mkdirs()) {
                            Log.w("InnerEnvironment", "Unable to create external files directory");
                            return null;
                        }
                    }

                    if (type == null) {
                        return externalFilesDir;
                    } else {
                        File dir = new File(externalFilesDir, type);
                        if (!dir.exists() && !dir.mkdirs()) {
                            Log.w("InnerEnvironment", "Unable to create external media directory " + dir);
                            return null;
                        } else {
                            return dir;
                        }
                    }
                }
            }
        }
    }

    public interface FileComparator {
        boolean equals(File var1, File var2);
    }
}
