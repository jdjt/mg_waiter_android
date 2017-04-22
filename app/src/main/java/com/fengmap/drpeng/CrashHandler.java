package com.fengmap.drpeng;

import android.content.Context;
import android.os.Build;

import com.fengmap.android.data.FMDataManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

/**
 * @Description 应用程序异常类：用于捕获异常和提示错误信息
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private final String ERROR_REPORT_DIR;
    private final String FILE_MIME_ZIP = ".zip";
    private final String ERR_FILE_NAME = "error.log";
    private final String ERR_ZIP_FILE_NAME = "error.zip";
    private final String UTF_8 = "UTF-8";

    /**
     * 系统默认的UncaughtException处理类
     */
    private final UncaughtExceptionHandler mDefaultHandler;
    private final Context mContext;

    private static CrashHandler mCrashHandler;

    private CrashHandler(Context context) {
        this.ERROR_REPORT_DIR = FMDataManager.getCacheDirectory();
        this.mContext = context;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    /**
     * 上传错误 信息
     */
    private class UploadErrThread extends Thread {
        private final File mSourceFile;
        private final Thread thread;
        private final Throwable ex;

        public UploadErrThread(File mFile, Thread thread, Throwable ex) {
            super();
            this.mSourceFile = mFile;
            this.thread = thread;
            this.ex = ex;
        }

        @Override
        public void run() {
            super.run();
//            // 删除原有zip文件
//            File dir = new File(ERROR_REPORT_DIR);
//            File[] files = dir.listFiles();
//            if (files != null) {
//                final int size = files.length;
//                // 不要在 err目录下放 重要的 zip文件
//                for (int i = 0; i < size; i++) {
//                    if (files[i].getName().contains(FILE_MIME_ZIP)) {
//                        files[i].delete();
//                    }
//                }
//            }
//            try {
//                // 重新压缩文件
//                File zipFile = new File(getRootDir() + ERR_ZIP_FILE_NAME);
//                ZipUtils.zipFolder(mSourceFile.getAbsolutePath(), zipFile.getAbsolutePath());
//                // 发送邮件
//                // SendEmailUtils.sendClientErrorLogEmail(zipFile.getAbsolutePath(),
//                // mContext);
//                // 删除压缩包
//                if (zipFile != null && zipFile.exists()) {
//                    zipFile.delete();
//                }
//                // 遍历删除日志
//                File[] fs = dir.listFiles();
//                if (fs != null) {
//                    final int size = fs.length;
//                    for (int i = 0; i < size; i++) {
//                        fs[i].delete();
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            // 退出
            existApp(thread, ex);
        }
    }

    /**
     * 获取根目录
     *
     * @return
     */
    String getRootDir(){
        return FMDataManager.getCacheDirectory();
    }

    /**
     * 退出应用
     *
     * @param thread
     * @param ex
     */
    private void existApp(Thread thread, Throwable ex) {
        mDefaultHandler.uncaughtException(thread, ex);

    }

    /**
     * 用于catch中 存储log.e 相关的错误日志
     */
    public void createCatchReportFile(String e) {
        try {
            final Throwable t = new Throwable(e);
            creatCrashReportFile(t);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 生成错误报告文件，包含手机信息，系统版本，错误详情等内容
     *
     * @param throwable
     * @return
     */
    private void creatCrashReportFile(Throwable throwable) throws IOException, FileNotFoundException {
        write("msg:  "+throwable.getMessage() + "\n");

        String rtn = throwable.getStackTrace().toString();
        try {
            Writer      writer      = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            throwable.printStackTrace(printWriter);
            printWriter.flush();
            writer.flush();
            rtn = writer.toString();
            printWriter.close();
            writer.close();

            write("stack: " + rtn + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
        }

        //        final File fileDir = new File(ERROR_REPORT_DIR);
//        if (!fileDir.exists()) {
//            fileDir.mkdirs();
//        }
//        // 记录当前时间,文件名即是
//        boolean isRecordDevInfo = false;
//        File errFile = new File(fileDir, ERR_FILE_NAME);
//        if (!errFile.exists()) {
//            isRecordDevInfo = true;
//        }
//        final FileOutputStream output = new FileOutputStream(errFile, true);
        // 创建文件时，记录 IMEI和设备信息，其余情况，追加错误和时间即可
        // 记录imei号
//        if (isRecordDevInfo) {
//            try {
//                final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
//                final String imei = "IMEI=" + tm.getDeviceId();
//                output.write(imei.getBytes(UTF_8));
//                output.write("\n".getBytes(UTF_8));
//            } catch (Exception e) {
//                output.write("IMEI=0\n".getBytes(UTF_8));
//            }
//            // 设备详情
//            writeBuildDetails(output);
//            // 软件版本
//            String verInfo = "VersionCode=" + PackageUtils.getVersionCode(mContext) + "\n";
//            output.write(verInfo.getBytes(UTF_8));
//        }
        // 记录时间
//        final String timeInfo = "\n" + DateUtils.getCurrentTime() + "\n";
//        output.write(timeInfo.getBytes(UTF_8));
        // 错误详情
//        final PrintStream printStream = new PrintStream(output);
//        writeStackTrace(throwable, printStream);
//
//        throwable.printStackTrace();
//        printStream.close();
//        output.close();
    }

    /**
     *
     * 获取APP异常崩溃处理对象
     *
     * @param context
     * @return
     */
    public static synchronized CrashHandler getAppExceptionHandler(Context context) {
        if (mCrashHandler == null) {
            mCrashHandler = new CrashHandler(context);
        }
        return mCrashHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            creatCrashReportFile(ex);
            // 上传log到服务器
//            File errFile = new File(ERROR_REPORT_DIR);
//            File[] files = errFile.listFiles();
//            if (files != null && files.length != 0) {
//                if (NetWorkUtils.isNetworkAvailable(mContext)) {
//                    final UploadErrThread errThread = new UploadErrThread(errFile, thread, ex);
//                    errThread.start();
//                } else {
//                    existApp(thread, ex);
//                }
//            } else {
//                existApp(thread, ex);
//            }
        } catch (Exception e) {
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    /**
     * 写入设备和系统详情
     *
     * @param os
     * @throws IOException
     */
    private void writeBuildDetails(OutputStream os) throws IOException {
        final StringBuilder result = new StringBuilder();
        final Field[] fields_build = Build.class.getFields();
        // 设备信息
        for (final Field field : fields_build) {
            result.append(field.getName()).append("=");
            try {
                result.append(field.get(null).toString());
            } catch (Exception e) {
                result.append("N/A");
            }
            result.append("\n");
        }
        // 版本信息
        final Field[] fields_version = Build.VERSION.class.getFields();
        for (final Field field : fields_version) {
            result.append(field.getName()).append("=");
            try {
                result.append(field.get(null).toString());
            } catch (Exception e) {
                result.append("N/A");
            }
            result.append("\n");
        }
        os.write(result.toString().getBytes());
    }

    /**
     * 写入 错误信息
     *
     * @param throwable
     * @param printStream
     */
    private void writeStackTrace(Throwable throwable, PrintStream printStream) {
        Throwable cause = throwable;
        while (cause != null) {
            cause.printStackTrace(printStream);
            cause = cause.getCause();
        }
    }


    public static String LogFile = FMDataManager.getCacheDirectory() + "crash.txt";
    public static File f;
    public static FileOutputStream fos = null ;
    public static void write(String content) {
        try {

            f = new File(LogFile);
            if(!f.exists()) {
                f.createNewFile();
            }
            if (fos == null) {
                fos = new FileOutputStream(f, false);  //追加
            }
            fos.write(content.getBytes());
            fos.flush();
        } catch (Exception e) {

        }
    }

}
