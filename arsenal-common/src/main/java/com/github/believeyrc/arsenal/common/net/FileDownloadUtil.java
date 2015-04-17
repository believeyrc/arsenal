package com.github.believeyrc.arsenal.common.net;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 文件下载工具类
 * @author yangrucheng
 * @created 2015年4月17日 下午4:48:49
 * @since 1.0
 * @version 1.0
 *
 */
public class FileDownloadUtil {
	
	/**
	 * 
	 *
	 * @author yangrucheng
	 * @created 2015年4月17日 下午4:49:03
	 *
	 * @param fileUrl 文件URL
	 * @param savePath 保存路径
	 * @param fileName 文件名
	 */
	public static void download(String fileUrl, String savePath, String fileName) {
		OutputStream out = null;
		DataInputStream input = null;
		try {
			File directory = new File(savePath);
			if (! directory.exists() && ! directory.isDirectory()) {
				directory.mkdirs();
			}
			if (! checkFileExists(fileUrl)) {
				return;
			}
			URL url = new URL(fileUrl);
			input = new DataInputStream(url.openStream());
			File downFile = new File(savePath + fileName);
			if (downFile.exists() && downFile.isFile()) {
				downFile.delete();
			}
			out = new FileOutputStream(downFile);
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = input.read(buffer)) != -1) {
				out.write(buffer, 0, length);
			}
			buffer = null;
			
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
			}
			
		}
		
	}
	
	/**
	 * 检查文件是否存在
	 *
	 * @author yangrucheng
	 * @created 2015年4月17日 下午4:49:27
	 *
	 * @param fileUrl
	 * @return
	 */
	public static boolean checkFileExists(String fileUrl) {
		boolean exist = false;
		
		try {
			URL url = new URL(fileUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			int code = connection.getResponseCode();
			if (code == 200) {
				exist = true;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return exist;
	}

	
	public static void main(String[] args) {
	
		String fileUrl = "http://blog.chinaunix.net/css/default/style22432.css";
		String savePath = "D://pic//";
		String fileName = "style.css";
		
		//download(fileUrl, savePath, fileName);
		
		boolean exist = checkFileExists(fileUrl);
		System.out.println("exist : " + exist);
		
	}

}
