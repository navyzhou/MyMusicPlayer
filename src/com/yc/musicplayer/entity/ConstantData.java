package com.yc.musicplayer.entity;

import javax.sound.sampled.Clip;

import com.yc.musicplayer.test.MusicPlayer;
import com.yc.musicplayer.ui.MainPlayer;

public class ConstantData {
	public static MusicPlayer player = null;
	public static MainPlayer mainPlayer = null;
	public static Clip clip = null;
	public static boolean playFlag = true; // 播放，暂停是flase

}
