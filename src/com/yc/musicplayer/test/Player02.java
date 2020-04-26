package com.yc.musicplayer.test;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Player02 {
	public final Pattern pattern = Pattern.compile("(?<=\\\\[)[0-9].+?\\\\:[0-9].+?(\\\\.[0-9].+)?(?=\\\\])");		//匹配[xx:xx.xx]中的内容（不含[]）
	public AudioInputStream ais;
	public AudioFormat format;
	private Clip clip;
	public ArrayList<Code> lrc = new ArrayList<Code>();
	
	public static void main(String[] args) {
		Player02 playLrc = new Player02();
		playLrc.readFile();
		playLrc.readMp3();
	}

	/**
	 * 读取歌词
	 */
	public void readFile() {
		File file = new File("musics\\好儿好女好家园.lrc");
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
		while (matcher.find()) { // 匹配获取每一句歌词
			String time = matcher.group(); // 获取每句歌词前面的时间
			String str = line.substring(line.indexOf(time) + time.length() + 1); // 获取后面的歌词
			lrc.add(new Code(strToLong(time), str));
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
	public void readMp3() {
		File file = new File("musics/好儿好女好家园.mp3");
		try {
			ais = AudioSystem.getAudioInputStream(file); // 获取音频流
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
			clip = AudioSystem.getClip();
			clip.open(ais);
			clip.start();

			// 同步歌词
			new Thread() {
				@Override
				public void run() {
					int index = 1;
					boolean mark = false;
					while (true) {
						long time = clip.getMicrosecondPosition() / 1000;
						if (time < lrc.get(index).getTime()) {
							if (!mark)
								System.out.println(lrc.get(index - 1).getStr());
							mark = true;
						} else {
							index++;
							mark = false;
						}
						try {
							sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	class Code {
		private long time;
		private String str;

		public Code(long time, String str) {
			setTime(time);
			setStr(str);
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

		public String getStr() {
			return str;
		}

		public void setStr(String str) {
			this.str = str;
		}
	}
}