package com.yc.musicplayer.ui;

import com.yc.musicplayer.entity.ConstantData;

/**
 * 开启音乐播放器
 * @company 源辰
 * @author navy
 */
public class StartPlayerMusic {
	public static void main(String[] args) {
		ConstantData.mainPlayer = new MainPlayer();
		ConstantData.mainPlayer.open();
	}
}
