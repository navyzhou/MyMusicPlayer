package com.yc.mysicplayer.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

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

	/**
	 * 缩放图片
	 * @param srcImageFile  缩放的图片
	 * @param scale  缩放比例
	 * @return
	 */
	public static ByteArrayOutputStream scale(File srcImageFile, int width, int height){
		ByteArrayOutputStream output = null;
		try {
			BufferedImage read = ImageIO.read(srcImageFile);  //使用ImageIO的read方法读取图片

			// int width = (int) (read.getWidth()*scale);  //获取缩放后的宽高
			// int height = (int) (read.getHeight()*scale);  //获取缩放后的宽高

			/*
			 * hints 参数取值为以下之一（Image 类中的常量）:
			 *     SCALE_AREA_AVERAGING: 使用 Area Averaging 图像缩放算法;
			 *     SCALE_DEFAULT: 使用默认的图像缩放算法;
			 *     SCALE_SMOOTH: 选择图像平滑度比缩放速度具有更高优先级的图像缩放算法。
			 */
			java.awt.Image img = read.getScaledInstance(width , height, java.awt.Image.SCALE_SMOOTH);  //调用缩放方法获取缩放后的图片
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  //创建一个新的缓存图片
			Graphics2D graphics = image.createGraphics(); //获取画笔
			graphics.drawImage(img, 0, 0,null);  //将Image对象画在画布上,最后一个参数,ImageObserver:接收有关 Image信息通知的异步更新接口,没用到直接传空
			graphics.dispose();  //一定要释放资源
			// String fileName = srcImageFile.getName();  //获取到文件的后缀名
			// String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);  //使用ImageIO的write方法进行输出
			// ImageIO.write(image,"png", destImageFile); // 将缩放后的图片存到指定文件中
			output = new ByteArrayOutputStream();
			ImageIO.write(image, "png", output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}

	/**
	 * JAVA裁剪图片
	 * @param srcImageFile  需要裁剪的图片
	 * @param x     裁剪时x的坐标（左上角）
	 * @param y     裁剪时y的坐标（左上角）
	 * @param width  裁剪后的图片宽度
	 * @param height 裁剪后的图片高度
	 * @return
	 */
	public static ByteArrayOutputStream cut(File srcImageFile, int x, int y, int width, int height){
		ByteArrayOutputStream output = null;
		try {
			BufferedImage read = ImageIO.read(srcImageFile); //使用ImageIO的read方法读取图片
			
			BufferedImage image = read.getSubimage(x, y, width, height); //调用裁剪方法
			
			String formatName = srcImageFile.getName(); //获取到文件的后缀名
			formatName = formatName.substring(formatName.lastIndexOf(".") + 1);
			
			//使用ImageIO的write方法进行输出
			// ImageIO.write(image,formatName,destImageFile);
			output = new ByteArrayOutputStream();
			ImageIO.write(image, formatName, output);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}

	/**
	 * 图片圆角化
	 * @param path
	 * @param size
	 * @param radius
	 * @return
	 * @throws IOException
	 */
	public static Image makeRoundedImage(String path, int size, int radius) {
		Image image = null;
		ByteArrayOutputStream output = null;
		InputStream is = null;

		try {
			BufferedImage bufferedImage = ImageIO.read(new File(path));
			java.awt.Image img = bufferedImage.getScaledInstance(size , size, java.awt.Image.SCALE_DEFAULT);  // 调用缩放方法获取缩放后的图片
			BufferedImage scaleImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);  // 创建一个新的缓存图片
			Graphics2D graphics = scaleImage.createGraphics(); // 获取画笔
			graphics.drawImage(img, 0, 0,null);  // 将Image对象画在画布上,最后一个参数,ImageObserver:接收有关 Image信息通知的异步更新接口,没用到直接传空
			graphics.dispose();  //一定要释放资源

			// 然后圆角化
			BufferedImage roundBufferImage = roundImage(scaleImage, size, radius);
			//ImageIO.write(roundBufferImage, "png", new File(savePath));
			output = new ByteArrayOutputStream();
			ImageIO.write(roundBufferImage, "png", output);
			is = new ByteArrayInputStream(output.toByteArray());
			image = new Image(Display.getDefault(), is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return image;
	}

	/**
	 * 圆角化图片
	 * @param image 要圆角化的图片数据
	 * @param targetSize 图片的目标大小
	 * @param radius 圆角化半径
	 * @return
	 */
	private static BufferedImage roundImage(BufferedImage image, int targetSize, int radius) {
		BufferedImage outputImage = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = outputImage.createGraphics(); // 创建2D绘制工具

		// 它指定在渲染过程中如何将新像素与图形设备上的现有像素组合。
		g2.setComposite(AlphaComposite.Src); // AlphaComposite对象，实现alpha为1.0f的不透明SRC规则。

		// 设置渲染效果                                                         ANTIALIASING： 消除混叠现象，消除走样，图形保真                    ANTIALIAS：  平滑、抗锯齿;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.WHITE); // 画笔颜色为白色

		// 2D 圆角
		g2.fill(new RoundRectangle2D.Float(0, 0, targetSize, targetSize, radius, radius));
		g2.setComposite(AlphaComposite.SrcAtop);
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		return outputImage;
	}
}
