package com.yc.mysicplayer.util;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.yc.musicplayer.entity.ConstantData;

/**
 * @author navy
 */
public class ShellUtil {
	private static boolean flag=false; //鼠标移动标识
	private static int clickx; //鼠标点击时的x轴
	private static int clicky; //鼠标点击时的y轴
	
	/**
	 * 居中显示
	 * @param shell
	 */
	public static void middlePosition(Shell shell) {
		// 界面居中显示
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		shell.setLocation((dimension.width - shell.getSize().x) / 2 , (dimension.height - shell.getSize().y) / 2);
	}

	/**
	 * 移动界面的方法
	 * @param composite：要移动的面板
	 * @param shell：形状
	 */
	public static void shellMove(Composite composite, Shell shell){
		Cursor cursor = new Cursor(Display.getDefault(), SWTResourceManager.getImage(ShellUtil.class, "/images/move.png").getImageData(), 0, 0);

		//添加移动监听器
		composite.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				if(flag){
					//面板的当前位置的x轴位置+鼠标当前的位置-鼠标点击时的位置
					shell.setLocation(shell.getLocation().x+e.x-clickx, shell.getLocation().y+e.y-clicky);
				}
			}
		});

		//鼠标监听事件
		composite.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) { //当按下鼠标左键时
				flag = true; //将拖动的标识设为true;
				clickx = e.x; //获取鼠标当前的x轴坐标
				clicky = e.y; //获取鼠标当前的y轴坐标
				composite.setCursor(cursor);
			}
			@Override
			public void mouseUp(MouseEvent e) { //当松开鼠标时
				flag=false; //将拖动的标识设为false
				composite.setCursor(new Cursor(Display.getDefault(), SWT.CURSOR_ARROW));
			}
		});
	}

	/**
	 * 主面板关闭按钮
	 * @param shell
	 * @param label
	 */
	public static void opCloseButton(Shell shell, Label label){
		//添加鼠标追踪事件
		label.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseExit(MouseEvent e) {
				label.setImage(SWTResourceManager.getImage(ShellUtil.class, "/images/close_3.png"));
			}
		});

		//鼠标移动监听
		label.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent arg0) {
				label.setImage(SWTResourceManager.getImage(ShellUtil.class, "/images/close_2.png"));
			}
		});

		//鼠标监听
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				label.setImage(SWTResourceManager.getImage(ShellUtil.class, "/images/close_1.png"));
			}
			@Override
			public void mouseUp(MouseEvent e) {
				// if(MessageDialog.openConfirm(shell, "退出提示","请问您真的要退出吗?")){
					Tray tray = Display.getDefault().getSystemTray();
					if (tray != null) {
						tray.dispose();
					}
					if (ConstantData.clip != null) {
						ConstantData.clip.close();
					}
					shell.dispose();
					System.exit(0);
				// }
			}
		});
	}

	/**
	 * 最小化按钮
	 * @param label
	 * @param shell
	 */
	public static void minButton(Shell shell, Label label){
		//添加鼠标追踪事件
		label.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseExit(MouseEvent e) {
				label.setImage(SWTResourceManager.getImage(ShellUtil.class, "/images/down_3.png"));
			}
		});

		label.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent arg0) {
				label.setImage(SWTResourceManager.getImage(ShellUtil.class, "/images/down_2.png"));
			}
		});

		label.addMouseListener(new MouseAdapter() {
			//最小化界面
			@Override
			public void mouseDown(MouseEvent e) {
				label.setImage(SWTResourceManager.getImage(ShellUtil.class, "/images/down_1.png"));
			}

			public void mouseUp(MouseEvent e) {
				shell.setMinimized(true);
			}
		});
	}
	
	/**
	 * 鼠标移上去或移开的时候
	 * @param label
	 * @param over
	 * @param out
	 */
	public static void opOver(Label label, String over, String out) {
		label.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseExit(MouseEvent e) {
				label.setImage(SWTResourceManager.getImage(ShellUtil.class, "/images/" + out));
			}
			
			@Override
			public void mouseHover(MouseEvent e) {
				label.setImage(SWTResourceManager.getImage(ShellUtil.class, "/images/" + over));
			}
		});
	}

	//最大化按钮事件
	public void maxButton(Shell shell, Label label){
		label.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseExit(MouseEvent e) {
				label.setImage(SWTResourceManager.getImage(ShellUtil.class, "/images/btn_max_normal.png"));
			}
		});

		label.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent arg0) {
				label.setImage(SWTResourceManager.getImage(ShellUtil.class, "/images/btn_max_highlight.png"));
			}
		});

		label.addMouseListener(new MouseAdapter() {
			//最大化界面
			@Override
			public void mouseDown(MouseEvent e) {
				label.setImage(SWTResourceManager.getImage(ShellUtil.class, "/images/btn_max_down.png"));
				shell.setMaximized(true);
			}
		});
	}

	/**
	 * 托盘
	 * @param shell
	 * @param tray
	 * @param title
	 */
	public static void showTary(Shell shell,String title){
		Display display = Display.getDefault();
		Tray tray= display.getSystemTray();
		TrayItem item = new TrayItem(tray,SWT.NONE);  

		if (tray == null) { //如果获取不到 
			MessageDialog.openWarning(shell, "温馨提示", "当前操作系统不支持托盘图标..");
			return;
		} 

		item.setToolTipText(title);  //tip：给小费、尖端
		
		item.addListener(SWT.Selection, new Listener() {//托盘选择事件  
			public void handleEvent(Event event) {
				if(shell.getMinimized()){ // 如果以前是最小化的，则直接显示
					shell.setMinimized(false);
					return;
				}

				if(shell.getVisible()){ //如果界面以前是显示的
					shell.setVisible(false);
					return;
				}

				shell.setVisible(true);
			}
		});

		//			item.addListener(SWT.DefaultSelection, new Listener(){//托盘双击事件
		//				public void handleEvent(Event event) {
		//					if(shell.getVisible()){
		//						shell.setVisible(false);
		//					}else{
		//						shell.setMaximized(true);
		//						shell.setVisible(true);
		//					}
		//				}
		//			});

		//托盘菜单
		Menu menu = new Menu(shell,SWT.POP_UP);   //SWT.BAR：菜单栏，用于主菜单 SWT.DROP_DOWN：下拉菜单，用于子菜单  SWT.POP_UP：鼠标右键弹出式菜单。

		item.addListener(SWT.MenuDetect, new Listener() {//Detect：发现、探测、察觉   托盘右击事件 
			public void handleEvent(Event event) {  
				menu.setVisible(true);  
			}  
		});  

		//SWT.CASCADE：有子菜单的菜单项。    SWT.CHECK：选中后前面显示一个小勾。  SWT.PUSH：普通型菜单。 
		//SWT.RADIO：选中后前面显示一个圆点。  SWT.SEPARATOR：分隔符
		MenuItem menuItemMaximize = new MenuItem(menu, SWT.PUSH);// 最大化菜单
		menuItemMaximize.setText("最大化");
		
		menuItemMaximize.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {  //widget：装饰、小部件
				shell.setVisible(true);
				shell.setMaximized(true);
			}
		});

		MenuItem menuItemMinimize = new MenuItem(menu, SWT.PUSH);// 最小化菜单
		menuItemMinimize.setText("最小化");
		menuItemMinimize.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.setMinimized(true);
			}
		});

		new MenuItem(menu,SWT.SEPARATOR);// 分割条
		MenuItem menuItemClose = new MenuItem(menu, SWT.PUSH);// 关闭菜单
		menuItemClose.setText("关闭");
		
		menuItemClose.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (ConstantData.clip != null) {
					ConstantData.clip.close();
				}
				tray.dispose();
				shell.dispose();
				System.exit(0);
			}
		});
		item.setImage(SWTResourceManager.getImage(ShellUtil.class, "/images/yc.png"));
	}
}
