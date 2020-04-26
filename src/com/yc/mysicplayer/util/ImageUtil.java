package com.yc.mysicplayer.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.yc.musicplayer.ui.MainPlayer;

public class ImageUtil {
	/**
	 * 缩放图片大小
	 * @param path 图片路径
	 * @param width 缩放后的图片宽度
	 * @param height 缩放后的图片高度
	 * @return
	 */
	public static Image scaleImage(String path, int width, int height) {
		Image image = null;
		try (FileInputStream fis = new FileInputStream(path)) {
			ImageData imageData = new ImageData(fis);
			imageData = imageData.scaledTo(width, height); // 缩放图片数据
			image = new Image(Display.getDefault(), imageData);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return image;
	}

	public static Image scaleImage(String rpath, Label label) {
		Image image = null;
		ImageData imageData = SWTResourceManager.getImage(MainPlayer.class, "/images/" + rpath).getImageData();
		imageData = imageData.scaledTo(label.getSize().x, label.getSize().y); // 缩放图片数据
		image = new Image(Display.getDefault(), imageData);
		return image;
	}

	/**
	 * 将图片字节数组反向生成图片
	 * @param bt
	 * @param width
	 * @param height
	 * @return
	 */
	public static Image getImage(byte[] bt, int width, int height) {
		Image image = null;
		try (InputStream is = new ByteArrayInputStream(bt)) {
			ImageData imageData = new ImageData(is);
			imageData = imageData.scaledTo(width, height); // 缩放图片数据
			image = new Image(Display.getDefault(), imageData);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return image;
	}

	/**
	 * 读取文件数据到字节数组
	 * @param path 要读取的文件路径
	 * @return 返回该文件的字节数组
	 */
	public static byte[] readFileToArray(String path) {
		if (StringUtil.checkNull(path)) {
			return null;
		}
		File fl = new File(path);
		if (!fl.exists() || !fl.isFile()) {
			return null;
		}

		byte[] bt = null;

		try (FileInputStream fis = new FileInputStream(path)) {

			// 初始化字节数组 -> 大小为多大呢? -> 这个文件流能读到多少数据就为多大
			bt = new byte[fis.available()];

			// 将数据从这个文件输入流读取到字节数组中
			fis.read(bt);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bt;
	}
}
