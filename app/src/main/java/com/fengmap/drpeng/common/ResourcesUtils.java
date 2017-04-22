package com.fengmap.drpeng.common;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 处理资源的类
 * @author Yang
 * @date 2015年1月19日 上午10:19:51
 */
public class ResourcesUtils {
	
	/**
	 * 判断是否有内存卡
	 * @return 返回true 存在，否则不存在
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 返回sdcard路径,若是没有内存卡则返回null
	 * @return
	 */
	public static String getSDPath() {
		if(hasSdcard())
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		return null;
	}
	
	
	/**
     * 写入默认的主题文件到SD卡里面。
     * @param c             上下文环境
     * @param dstFileName   写入文件的名字
     * @param srcAssetsPath 源文件的路径
     */
    public static void writeRc(Context c, String dstDir, String dstFileName,String srcAssetsPath) {
    	InputStream is = null;
    	FileOutputStream fos = null;
        try {
     	   File f = new File(dstDir);
     	   if (!f.exists()){   
     		   f.mkdirs();
     	   } 
     	   
     	   File ff = new File(dstDir,dstFileName);
     	   if(ff.exists()) {
//			   ff.delete();
			   return;
     	   }
     	   is = c.getAssets().open(srcAssetsPath);
	       fos = new FileOutputStream(ff);
	       byte[] buffer = new byte[10240];
	       int byteCount=0;               
	       while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节        
	           fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
	       }
	       fos.flush();//刷新缓冲区
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	try {
        		if(is != null)
            		is.close();
            	if(fos != null)
            		fos.close();
			} catch (Exception e2) {
			}
        }

    }


	public static void write(File dstFile, String content) throws IOException {
		FileOutputStream fos = null ;
		fos = new FileOutputStream(dstFile, true);  //追加
		fos.write(content.getBytes());
		fos.flush();
		if(fos!=null)
			fos.close();
	}


	public static byte[] readAssetsFile(Context c, String filePath) {
		InputStream is 		       = null;
		ByteArrayOutputStream baos = null;
		BufferedInputStream   bis  = null;

		try {
			is = c.getAssets().open(filePath);
			baos = new ByteArrayOutputStream(is.available());
			bis = new BufferedInputStream(is);
			int bufferSize = 1024;
			int len = 0;
			byte[] buffer = new byte[bufferSize];
			while((len = bis.read(buffer, 0, bufferSize))!=-1){
				baos.write(buffer, 0, len);
			}

			return baos.toByteArray();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(is != null)
					is.close();
				if(bis!=null)
					bis.close();
				if(baos!=null)
					baos.close();
			} catch (Exception e2) {
			}
		}

		return null;
	}

}
