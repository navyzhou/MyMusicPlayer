package com.yc.musicplayer.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

import com.yc.musicplayer.entity.ConstantData;

/**
 * 播放音乐
 * @company 源辰
 * @author navy
 */
public class PlayerMusic {
	// ?<= 前瞻 说明是要匹配时间前面是 [ 的这种
	private final Pattern pattern = Pattern.compile("(?<=\\[)[0-9]+?\\:[0-9]+?(\\.[0-9]+)?(?=\\])");		//匹配[xx:xx.xx]中的内容（不含[]）
	private AudioInputStream ais;
	private AudioFormat format;
	private Hashtable<Long, String> lrcs = new Hashtable<Long, String>();  // 以时间为键，歌词为值
	private List<Long> timeLrc = new ArrayList<Long>(); // 存储歌词对应的时间
	private boolean startStatus = true; // 启动状态
	private Thread thread = null; // 播放线程
	private ProgressBar progressBar; // 进度条
	private Label label; // 歌词显示
	private int index = 1; // 歌词索引
	private long timeLength = 0; // 总时长
	private long time = 0; // 当前播放时间
	private Label time_label; // 时间显示标签

	public PlayerMusic(ProgressBar progressBar, Label label,Label time_label) {
		this.progressBar = progressBar;
		this.label = label;
		this.time_label = time_label;
	}

	/**
	 * 读取歌词
	 */
	public void readFile(String path) {
		path = path.substring(0, path.lastIndexOf("\\")) + "\\lrc" +  path.substring(path.lastIndexOf("\\")).replace(".mp3", ".lrc");
		File file = new File(path);
		if (!file.exists() || !file.isFile()) {
			lrcs = null;
			return;
		}
		
		lrcs.clear(); // 清空歌词

		try (FileInputStream fis = new FileInputStream(file);BufferedReader reader = new BufferedReader(new InputStreamReader(fis,"utf-8"))){
			String lrcString = null;
			while ((lrcString = reader.readLine()) != null) {
				parseLine(lrcString);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 配置格式
	 * @param line
	 */
	public void parseLine(String line) {
		Matcher matcher = pattern.matcher(line);
		
		String time = "";
		String str = "";
		long times = 0;
		while (matcher.find()) { // 匹配获取每一句歌词
			time = matcher.group(); // 获取每句歌词前面的时间
			str = line.substring(line.indexOf(time) + time.length() + 1); // 获取后面的歌词
			str = str.replaceAll("^(\\[[0-9]+?\\:[0-9]+?(\\.[0-9]+?)?\\])*", ""); // 处理一行有多个时间
			
			times = strToLong(time);
			lrcs.put(times, str);
			timeLrc.add(times);
		}
	}

	/**
	 * 获取歌词时长，及其转为毫秒数
	 * @param timeStr
	 * @return
	 */
	private long strToLong(String timeStr) {
		String[] s = timeStr.split(":");
		int min = Integer.parseInt(s[0]);
		if (s[1].contains(".")) { // [00:00.00]
			String[] ss = s[1].split("\\.");
			int sec = Integer.parseInt(ss[0]);
			int mill = Integer.parseInt(ss[1]);
			return min * 60 * 1000 + sec * 1000 + mill * 10;
		} else { // [00:00]
			int sec = Integer.parseInt(s[1]);
			return min * 60 * 1000 + sec * 1000;
		}
	}

	/**
	 * 读取音频
	 */
	public void readMp3(File fl) {
		try {
			ais = AudioSystem.getAudioInputStream(fl); // 获取音频流
			format = ais.getFormat(); // 获得此音频输入流中声音数据的音频格式

			// 获取此格式声音的编码类型 如果不是  有符号的线性 PCM 数据
			if(format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
				format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,  // 音频编码技术
						format.getSampleRate(), // 每秒的样本数
						16, // 每个样本中的位数
						format.getChannels(), // 声道数（单声道 1 个，立体声 2 个，等等）
						format.getChannels() * 2, // 每帧中的字节数
						format.getSampleRate(), // 每秒的帧数
						false // 指示是否以 big-endian 字节顺序存储单个样本中的数据（false 意味着 little-endian）
						);
				ais = AudioSystem.getAudioInputStream(format, ais);
			}

			//初始化Clip
			ConstantData.clip = AudioSystem.getClip();
			ConstantData.clip.open(ais);

			timeLength = ConstantData.clip.getMicrosecondLength() / 1000; // 获取音频文件的时长
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 播放音乐
	 */
	@SuppressWarnings("deprecation")
	public void start(String path) {
		// 先结束以前的歌曲
		if (ConstantData.clip != null) {
			ConstantData.clip.close();
			ConstantData.clip = null;
		}

		if (thread != null) {
			startStatus = false;
			thread.stop();
		}

		progressBar.setSelection(0);

		File fl = new File(path);
		if (!fl.exists() || !fl.isFile()) {
			throw new RuntimeException("音乐文件格式不正确...");
		}
		readFile(path); // 读取歌词
		readMp3(fl);
		ConstantData.clip.start();


		// 同步歌词和进度条
		thread = new Thread() {
			@Override
			public void run() {
				index = 1;
				boolean mark = false;
				boolean end = false; // 歌曲播放是否结束
				startStatus = true;

				int size = 0;
				if (lrcs == null || lrcs.isEmpty()) {
					size = 0;
					end = true; // 歌词结束
					Display.getDefault().asyncExec(new Runnable(){
						@Override
						public void run() {
							label.setText("暂无歌词");
						}
					});
				} else {
					size = lrcs.size();
				}
				Collections.sort(timeLrc); // 根据歌词时间排序
				
				synchronized(thread) {
					while (startStatus) {
						time = ConstantData.clip.getMicrosecondPosition() / 1000;
						if (!end && time < timeLrc.get(index)) {
							if (!mark ) {
								Display.getDefault().asyncExec(new Runnable(){
									@Override
									public void run() {
										label.setText(lrcs.get(timeLrc.get(index)));
									}
								});
							}
							mark = true;
						} else {
							index++;
							if (index >= size) {
								end = true;
							}
							mark = false;
						}

						// 进度条
						Display.getDefault().asyncExec(new Runnable(){
							@Override
							public void run() {
								time_label.setText(showTime(time, timeLength));
								progressBar.setSelection( (int)( (float) time / timeLength * 100) );
							}
						});

						if (time == timeLength) {
							break;
						}

						try {
							sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					// 说明这首歌曲已经播完了
					Display.getDefault().asyncExec(new Runnable(){
						@Override
						public void run() {
							time_label.setText(showTime(time, timeLength));
							progressBar.setSelection(100);
						}
					});

					ConstantData.mainPlayer.nextSong();
				}
			}
		};

		thread.start();
	}

	/**
	 * 时间调整
	 * @param time
	 * @param timeLength
	 * @return
	 */
	private String showTime(long time, long timeLength) {
		long temp = 0;
		String str = "";
		if (time < 60000) {
			str = "00:";
			temp = time / 1000;
			str += temp >= 10 ? String.valueOf(temp) : "0" + temp;
		} else {
			temp = time / 60000;
			str = temp >= 10 ? String.valueOf(temp) : "0" + temp;
			temp = time % 60000 / 1000;
			str += ":";
			str += temp >= 10 ? String.valueOf(temp) : "0" + temp;
		}

		str += " / ";
		temp = timeLength / 60000;
		str += temp >= 10 ? String.valueOf(temp) : "0" + temp;
		temp = timeLength % 60000 / 1000;
		str += ":";
		str += temp >= 10 ? String.valueOf(temp) : "0" + temp;
		return str;
	}

	/**
	 * 暂停
	 */
	public void pause() {
		ConstantData.clip.stop();

		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (thread) {
					try {
						thread.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

	}

	/**
	 * 继续播放
	 */
	public void continues() {
		ConstantData.clip.start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (thread) {
					thread.notify();
				}
			}
		}).start();
	}

	/**
	 * 停止
	 */
	@SuppressWarnings("deprecation")
	public void stop() {
		if (ConstantData.clip == null) {
			return;
		}
		ConstantData.clip.close();
		ConstantData.clip = null;
		startStatus = false;
		thread.stop();
	}
}