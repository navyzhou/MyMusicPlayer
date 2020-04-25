package com.yc.musicplayer.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;

public class MusicPlayer {
	private Player playLrc;
	protected Shell shell;
	private Button btn_1;
	private Button btn_2;
	private Button btn_3;
	private Button btn_4;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ConstantData.player = new MusicPlayer();
			ConstantData.player.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setImage(SWTResourceManager.getImage(MusicPlayer.class, "/images/yc.png"));
		shell.setSize(450, 247);
		shell.setText("音乐播放器");

		// 界面居中显示
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		shell.setLocation((dimension.width - shell.getSize().x) / 2 , (dimension.height - shell.getSize().y) / 2);

		btn_1 = new Button(shell, SWT.NONE);
		btn_1.setBounds(39, 136, 78, 30);
		btn_1.setText("播放");

		btn_2 = new Button(shell, SWT.NONE);
		btn_2.setBounds(123, 136, 78, 30);
		btn_2.setText("暂停");
		btn_2.setEnabled(false);


		btn_3 = new Button(shell, SWT.NONE);
		btn_3.setBounds(299, 136, 73, 30);
		btn_3.setText("停止");
		btn_3.setEnabled(false);

		ProgressBar progressBar = new ProgressBar(shell, SWT.NONE);
		progressBar.setBounds(42, 90, 330, 21);

		Label label = new Label(shell, SWT.WRAP);
		label.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.BOLD));
		label.setForeground(SWTResourceManager.getColor(255, 140, 0));
		label.setBounds(41, 28, 330, 56);

		btn_4 = new Button(shell, SWT.NONE);
		btn_4.setText("继续");
		btn_4.setEnabled(false);
		btn_4.setBounds(207, 136, 78, 30);

		// 开始播放
		btn_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btn_1.setEnabled(false);
				btn_2.setEnabled(true);
				btn_3.setEnabled(true);

				playLrc = new Player(progressBar, label);
				playLrc.start();
			}
		});

		// 暂停
		btn_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btn_1.setEnabled(false);
				btn_2.setEnabled(false);
				btn_3.setEnabled(true);
				btn_4.setEnabled(true);

				playLrc.pause();
			}
		});

		// 继续
		btn_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btn_1.setEnabled(false);
				btn_2.setEnabled(true);
				btn_3.setEnabled(true);
				btn_4.setEnabled(false);

				playLrc.continues();
			}
		});


		// 停止
		btn_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btn_1.setEnabled(true);
				btn_2.setEnabled(false);
				btn_3.setEnabled(false);
				btn_4.setEnabled(false);

				playLrc.stop();
			}
		});

		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				if (playLrc != null) {
					playLrc.stop();
				}
			}
		});
	}
	
	public void reset() {
		btn_1.setEnabled(true);
		btn_2.setEnabled(false);
		btn_3.setEnabled(false);
		btn_4.setEnabled(false);
	}
}
