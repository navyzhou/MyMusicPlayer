package com.yc.musicplayer.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.sound.sampled.FloatControl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.wb.swt.SWTResourceManager;

import com.yc.musicplayer.entity.ConstantData;
import com.yc.musicplayer.entity.MusicInfo;
import com.yc.mysicplayer.util.ImageUtil;
import com.yc.mysicplayer.util.ShellUtil;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;

public class MainPlayer {
	protected Shell shell;
	// 歌曲列表
	private List<MusicInfo> musicList = new ArrayList<MusicInfo>();
	private List<PlayerList> playerList= new ArrayList<PlayerList>(); 

	private ScrolledComposite scrolledComposite; 
	private Composite composite_2;

	private PlayerMusic playerMusic; // 播放音乐的对象
	private Label lrc_label; // 歌词显示标签
	private Label time_label; // 时间显示标签
	private Label right_label; // 下一首
	private int playIndex = 0; // 当前播放的音乐索引下标
	private int preIndex = 0; // 上一次播放的音乐索引
	private boolean volumeFlag = true; // 音量

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell(SWT.NONE);
		shell.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/yc.png"));
		shell.setSize(1200, 720);
		shell.setText("源辰音乐");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		ShellUtil.middlePosition(shell); // 居中显示
		ShellUtil.showTary(shell, "源辰音乐"); // 显示托盘

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setBackgroundMode(SWT.INHERIT_DEFAULT);
		composite.setBackgroundImage(SWTResourceManager.getImage(MainPlayer.class, "/images/11.jpg"));

		ShellUtil.shellMove(composite, shell);

		Label close_label = new Label(composite, SWT.NONE);
		close_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/close_3.png"));
		close_label.setBounds(1158, 4, 32, 32);

		ShellUtil.opCloseButton(shell, close_label); // 关闭操作

		Label min_label = new Label(composite, SWT.NONE);
		min_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/down_3.png"));
		min_label.setBounds(1120, 4, 32, 32);
		ShellUtil.minButton(shell, min_label); // 最小化操作

		Composite composite_1 = new Composite(composite, SWT.BORDER);
		composite_1.setBounds(0, 76, 219, 644);

		Label logo_label = new Label(composite, SWT.NONE);
		logo_label.setBounds(10, 10, 200, 60);
		logo_label.setImage(ImageUtil.scaleImage("logo.png", logo_label));

		scrolledComposite = new ScrolledComposite(composite, SWT.BORDER | SWT.V_SCROLL);
		scrolledComposite.setBackgroundImage(SWTResourceManager.getImage(MainPlayer.class, "/images/11.jpg"));
		scrolledComposite.setBackgroundMode(SWT.INHERIT_DEFAULT);
		scrolledComposite.setLocation(217, 108);
		scrolledComposite.setSize(981, 548);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		composite_2 = new Composite(scrolledComposite, SWT.NONE);
		composite_2.setForeground(SWTResourceManager.getColor(218, 165, 32));

		// 进度条
		ProgressBar progressBar = new ProgressBar(composite, SWT.NONE);
		progressBar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		progressBar.setBounds(217, 655, 980, 5);

		// 控制台
		Composite composite_3 = new Composite(composite, SWT.BORDER);
		composite_3.setBackgroundMode(SWT.INHERIT_DEFAULT);
		composite_3.setBounds(217, 660, 981, 58);

		Label play_label = new Label(composite_3, SWT.NONE);
		play_label.setToolTipText("播放/暂停");
		play_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/timeout_2.png"));
		play_label.setBounds(490, 4, 48, 48);

		Label left_label = new Label(composite_3, SWT.NONE);
		left_label.setToolTipText("上一首");
		left_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/left_2.png"));
		left_label.setBounds(448, 12, 32, 32);
		ShellUtil.opOver(left_label, "left_1.png", "left_2.png");

		right_label = new Label(composite_3, SWT.NONE);
		right_label.setToolTipText("下一首");
		right_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/right_2.png"));
		right_label.setBounds(542, 12, 32, 32);
		ShellUtil.opOver(right_label, "right_1.png", "right_2.png");

		Label v_label = new Label(composite_3, SWT.NONE);
		v_label.setToolTipText("音量");
		v_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/volume_2.png"));
		v_label.setBounds(584, 12, 32, 32);
		// ShellUtil.opOver(v_label, "mute_1.png", "volume_2.png");

		Label l_label = new Label(composite_3, SWT.NONE);
		l_label.setToolTipText("随机播放");
		l_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/random_2.png"));
		l_label.setBounds(406, 12, 32, 32);

		lrc_label = new Label(composite_3, SWT.NONE);
		lrc_label.setFont(SWTResourceManager.getFont("等线", 10, SWT.NORMAL));
		lrc_label.setForeground(SWTResourceManager.getColor(255, 0, 255));
		lrc_label.setBounds(57, 18, 332, 22);

		Label like_label = new Label(composite_3, SWT.CENTER);
		like_label.setToolTipText("喜欢");
		like_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/love_2.png"));
		like_label.setBounds(10, 12, 32, 32);
		ShellUtil.opOver(like_label, "like_1.png", "like_2.png");

		time_label = new Label(composite_3, SWT.NONE);
		time_label.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		time_label.setAlignment(SWT.CENTER);
		time_label.setBounds(840, 16, 110, 22);
		time_label.setText("00:31 / 04:26");

		Label add_label = new Label(composite, SWT.NONE);
		add_label.setToolTipText("添加本地歌曲");
		add_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/add.png"));
		add_label.setAlignment(SWT.CENTER);
		add_label.setBounds(220, 37, 32, 32);

		// 默认检索路径
		findMap3("musics");
		addMusicList(); // 添加到播放列表

		Composite composite_5 = new Composite(composite, SWT.BORDER);
		composite_5.setBounds(217, 77, 981, 32);

		Label label_1 = new Label(composite_5, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 10, SWT.BOLD));
		label_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		label_1.setBounds(0, 3, 470, 24);
		label_1.setText("  歌曲");

		Label label_2 = new Label(composite_5, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 10, SWT.BOLD));
		label_2.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		label_2.setBounds(546, 3, 200, 24);
		label_2.setText(" 歌手");


		Label label_3 = new Label(composite_5, SWT.CENTER);
		label_3.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 10, SWT.BOLD));
		label_3.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		label_3.setBounds(760, 3, 180, 24);
		label_3.setText(" 大小");

		scrolledComposite.setContent(composite_2);
		scrolledComposite.setMinSize(composite_2.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		// 添加本地歌曲
		add_label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				// 目录选择框
				DirectoryDialog folderdlg = new DirectoryDialog(shell, SWT.SELECTED | SWT.OPEN); 
				folderdlg.setText("目录选择"); 
				folderdlg.setFilterPath("SystemDrive"); 
				folderdlg.setMessage("请选择要导入的音乐所在目录...."); 
				String selectedDir = folderdlg.open();

				findMap3(selectedDir);
				// 可以将此路径存到注册表，当下次启动的时候自动从注册表中搜索出来扫描加载
				addMusicList(); // 添加到播放列表
			}
		});

		playerMusic = new PlayerMusic(progressBar, lrc_label, time_label);
		
		Label photo_label = new Label(composite, SWT.NONE);
		photo_label.setImage(ImageUtil.makeRoundedImage("bin/images/2.gif", 70, 70));
		photo_label.setBounds(994, 4, 70, 70);

		// 播放、暂停
		play_label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (ConstantData.playFlag) { // 说明当前正在播放，按下说明是要暂停，显示暂停的图标
					play_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/play_1.png"));
				} else { // 说你以前是暂停，现在要播放
					play_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/timeout_1.png"));
				}
			}

			@Override
			public void mouseUp(MouseEvent e) {
				if (ConstantData.playFlag) { // 说明当前正在播放
					ConstantData.playFlag = false; // 说你已经暂停
					play_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/play_3.png"));
					playerMusic.pause();
				} else {
					ConstantData.playFlag = true; // 说你已经播放
					playerMusic.continues();
					play_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/timeout_2.png"));
				}
			}
		});

		play_label.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseHover(MouseEvent e) {
				if (ConstantData.playFlag) { // 说明当前正在播放
					play_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/timeout_1.png"));
				} else {
					play_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/play_1.png"));
				}
			}

			@Override
			public void mouseExit(MouseEvent e) {
				if (ConstantData.playFlag) { // 说明当前正在播放
					play_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/timeout_2.png"));
				} else {
					play_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/play_3.png"));
				}
			}
		});

		// 上一首
		left_label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				left_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/left_1.png"));
			}

			@Override
			public void mouseUp(MouseEvent e) {
				left_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/left_2.png"));
				preIndex = playIndex;
				playIndex --;
				if (playIndex < 0) {
					playIndex = musicList.size() - 1; // 播放最后一首
				}
				playerList.get(playIndex).startPlay(); // 播放这首歌
			}
		});

		// 下一首
		right_label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				right_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/right_1.png"));
			}

			@Override
			public void mouseUp(MouseEvent e) {
				right_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/right_2.png"));
				nextSong();
			}
		});

		// 声音控制
		v_label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (volumeFlag) { // 说明没有禁音
					v_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/mute_1.png"));
				} else { // 说明以前是禁音了的
					v_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/volume_1.png"));
				}
			}

			@Override
			public void mouseUp(MouseEvent e) {
				/*
				 * 这个方法可以调节音量大小
				 */
				FloatControl gainControl =  (FloatControl) ConstantData.clip.getControl(FloatControl.Type.MASTER_GAIN);
				if (volumeFlag) { // 说明没有禁音
					volumeFlag = false;
					gainControl.setValue(gainControl.getMinimum()); // 调小10分贝
					v_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/mute_2.png"));
				} else { // 说明以前是禁音了的
					volumeFlag = true;
					gainControl.setValue(gainControl.getMaximum());
					v_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/volume_2.png"));
				}
			}
		});

		v_label.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseHover(MouseEvent e) {
				if (volumeFlag) { // 说明没有禁音
					v_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/volume_1.png"));
				} else { // 说明以前是禁音了的
					v_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/mute_1.png"));
				}
			}

			@Override
			public void mouseExit(MouseEvent e) {
				if (volumeFlag) { // 说明没有禁音
					v_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/volume_2.png"));
				} else { // 说明以前是禁音了的
					v_label.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/mute_2.png"));
				}
			}
		});
	}
	
	/**
	 * 下一首
	 */
	public void nextSong() {
		preIndex = playIndex;
		playIndex ++;
		if (playIndex >= musicList.size()) {
			playIndex = 0; // 播放第一首
		}
		playerList.get(playIndex).startPlay(); // 播放这首歌
	}

	/**
	 * 检索音乐文件
	 * @param path
	 */
	private void findMap3(String path) {
		File file = new File(path);
		// 如果文件不存在或者文件不是一个目录，则返回
		if (!(file.exists() && file.isDirectory())) {
			return;
		}

		File[] fls = file.listFiles();
		if (fls == null || fls.length == 0) { // 如果选定的目录里面没有文件，则返回
			return;
		}

		String fileName = null;
		MusicInfo mf = null;
		Random rd = new Random();
		DecimalFormat df = new DecimalFormat("00.00");
		for (File fl : fls) {
			// 如果是一个文件并且以.mp3结尾，则认为是mp3文件。当然，这个地方会有问题，如果用户随便将一个
			// 文件命名为.mp3，那么也会被当做是音乐文件，最好是通过魔数来判断
			fileName = fl.getName().toLowerCase();
			if (fl.isFile() && fileName.endsWith(".mp3")) {
				mf = new MusicInfo(fileName, df.format(fl.length() / 1024.0 / 1024.0), fl.getAbsolutePath() ,rd.nextInt(2));
				musicList.add(mf); // 我这里随机标注是否喜欢
			}
		}
	}

	/**
	 * 将音乐显示在播放列表中
	 */
	private void addMusicList() {
		if (musicList == null || musicList.isEmpty()) {
			return;
		}
		int i = 0;
		for (MusicInfo mf : musicList) {
			playerList.add(new PlayerList(composite_2, mf, i, lrc_label));
			++ i;
		}

		composite_2.layout();
		scrolledComposite.setContent(composite_2);
		scrolledComposite.setMinSize(composite_2.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	class PlayerList {
		private Composite parent;
		private Label lab_2;
		private Label lab_3;
		private Label lab_4;
		private MusicInfo mf;
		private int y;
		private Label lrc_label;
		private Composite composite;

		public PlayerList(Composite parent, MusicInfo mf, int y, Label lrc_label) {
			this.parent = parent;
			this.mf = mf;
			this.y = y;
			this.lrc_label = lrc_label;

			createContent();
		}

		/**
		 * 创建控制
		 */
		protected void createContent() {
			composite = new Composite(parent, SWT.NONE);
			composite.setBounds(0, y * 36, 977, 36);
			
			Label lab_1 = new Label(composite, SWT.NONE);
			if (mf.getFlag() == 1) {
				lab_1.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/love_2.png"));
			} else {
				lab_1.setImage(SWTResourceManager.getImage(MainPlayer.class, "/images/love_1.png"));
			}
			lab_1.setBounds(0, 4, 32, 32);

			lab_2 = new Label(composite, SWT.NONE);
			lab_2.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
			lab_2.setBounds(40, 8, 424, 20);
			lab_2.setText(mf.getMname());

			lab_3 = new Label(composite, SWT.NONE);
			lab_3.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
			lab_3.setBounds(546, 8, 200, 20);
			lab_3.setText(mf.getAuthor());

			lab_4 = new Label(composite, SWT.NONE | SWT.CENTER);
			lab_4.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
			lab_4.setBounds(760, 8, 180, 20);
			lab_4.setText(mf.getSize() + "MB");

			bindEvent(composite);
			bindEvent(lab_2);
			bindEvent(lab_3);
			bindEvent(lab_4);

			// 双击播放
			lab_2.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDoubleClick(MouseEvent e) {
					startPlay();
				}
			});
		}

		/**
		 * 开始播放
		 */
		public void startPlay() {
			playIndex = y; // 记录当前播放的歌曲索引
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					lrc_label.setText(mf.getMname());
					playColor();
					playerList.get(preIndex).outColor(); // 恢复颜色
					preIndex = y;
					playerMusic.start(mf.getPath());
				}
			});
		}


		// 事件绑定
		public void bindEvent(Control control) {
			control.addMouseTrackListener(new MouseTrackAdapter() {
				@Override
				public void mouseHover(MouseEvent e) {
					overColor();
				}

				@Override
				public void mouseExit(MouseEvent e) {
					outColor();
				}
			});
		}

		/**
		 * 鼠标移上去的颜色
		 */
		private void overColor() {
			// lab_2.setForeground(SWTResourceManager.getColor(165, 42, 42));
			// lab_3.setForeground(SWTResourceManager.getColor(165, 42, 42));
			// lab_4.setForeground(SWTResourceManager.getColor(165, 42, 42));
			composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		}

		/**
		 * 鼠标移开的颜色
		 */
		protected void outColor() {
			if (playIndex == y) {
				playColor();
			}else {
				lab_2.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
				lab_3.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
				lab_4.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
				composite.setBackground(null);
			}
		}

		/**
		 * 当前播放的颜色
		 */
		private void playColor() {
			lab_2.setForeground(SWTResourceManager.getColor(255, 69, 0));
			lab_3.setForeground(SWTResourceManager.getColor(255, 69, 0));
			lab_4.setForeground(SWTResourceManager.getColor(255, 69, 0));
			composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		}
	}	
}
