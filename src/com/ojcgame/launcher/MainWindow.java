package com.ojcgame.launcher;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.ojcgame.common.EnvironmentManager;
import com.ojcgame.common.OJCUtils;
import com.ojcgame.svn.SyncBase;
import com.ojcgame.warp.SaveTargetFiles;

public class MainWindow {

	enum SYNC_TYPE {
		导出并同步, 只导出, 只同步,
	}

	private SYNC_TYPE syncType = SYNC_TYPE.导出并同步;

	private List filterList;
	private List targetList;

	private String[] filterFiles;
	private String[] targetFiles;

	Label filterCount;
	Label targetCount;

	/**
	 * setting page
	 */
	Text cehua_path_label;
	Text project_path_label;
	Text svn_path_label;

	SyncBase syncData;

	protected Shell shlv;
	private final FormToolkit formToolkit = new FormToolkit(
			Display.getDefault());

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
			window.open();
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
		shlv.open();
		shlv.layout();

		TakeList();
		syncData = new SyncBase();

		while (!shlv.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	void TakeList() {
		targetFiles = SaveTargetFiles.GetCurrentFiles();

		String[] The_All_Files = OJCUtils.GetAllFileName(EnvironmentManager
				.getInstance().getDataSourcesFloderPath() + "\\", ".xlsx");
		if (targetFiles == null || targetFiles.length == 0) {
			filterFiles = The_All_Files;
		} else {
			for (int i = 0, iLength = targetFiles.length; i < iLength; ++i) {
				for (int j = 0, jLength = The_All_Files.length; j < jLength; ++j) {
					if (targetFiles[i].equals(The_All_Files[j]) == true) {
						The_All_Files[j] = null;
						continue;
					}
				}
			}

			filterFiles = new String[The_All_Files.length - targetFiles.length];
			int index = 0;
			for (int j = 0, jLength = The_All_Files.length; j < jLength; ++j) {
				if (The_All_Files[j] == null || The_All_Files[j].isEmpty())
					continue;

				filterFiles[index] = The_All_Files[j];
				++index;
			}

			targetList.setItems(targetFiles);
		}

		filterList.setItems(filterFiles);
		RefreshFileCount();
	}

	void RefreshFileCount() {
		filterCount.setText(filterList.getItemCount() + "");
		targetCount.setText(targetList.getItemCount() + "");
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlv = new Shell();
		shlv.setSize(800, 480);
		shlv.setText(EnvironmentManager.getInstance().getAppName()
				+ EnvironmentManager.getInstance().getVersionNumber());
		shlv.setLayout(null);

		Menu menu = new Menu(shlv, SWT.BAR);
		shlv.setMenuBar(menu);

		MenuItem saveSyncListMenu = new MenuItem(menu, SWT.NONE);
		saveSyncListMenu.setText("保存双向列表");
		saveSyncListMenu.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				targetFiles = SaveTargetFiles.Save(targetList.getItems());
				targetList.setItems(targetFiles);
				OJCUtils.ShowDialog("保存双向列表成功！");
			}
		});

		MenuItem help = new MenuItem(menu, SWT.CASCADE);
		help.setText("帮助");

		Menu group = new Menu(help);
		help.setMenu(group);

		TabFolder tabFolder = new TabFolder(shlv, SWT.NONE);
		tabFolder.setBounds(10, 10, 764, 402);
		formToolkit.adapt(tabFolder);
		formToolkit.paintBordersFor(tabFolder);

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("同步到客户端");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite);
		formToolkit.paintBordersFor(composite);

		filterList = new List(composite, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);
		filterList.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		filterList.setForeground(SWTResourceManager.getColor(240, 230, 140));
		filterList.setBackground(SWTResourceManager.getColor(105, 105, 105));
		filterList.setBounds(0, 40, 220, 322);
		filterList.setToolTipText("该列表内容不会被同步");

		targetList = new List(composite, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);
		targetList.setForeground(SWTResourceManager.getColor(255, 255, 255));
		targetList.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		targetList.setBackground(SWTResourceManager.getColor(0, 128, 128));
		targetList.setBounds(331, 40, 220, 322);

		SelectionAdapter listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button selected = (Button) e.widget;
				// 通过widget上的text进行匹配。
				if (selected.getText().equalsIgnoreCase("<过滤选中项")) {
					move(targetList.getSelection(), targetList, filterList);
				} else if (selected.getText().equalsIgnoreCase("<<全部过滤")) {
					move(targetList.getItems(), targetList, filterList);
				} else if (selected.getText().equalsIgnoreCase("同步选中项>")) {
					move(filterList.getSelection(), filterList, targetList);
				} else if (selected.getText().equalsIgnoreCase("全部同步>>")) {
					move(filterList.getItems(), filterList, targetList);
				}
			}

			public void move(String[] items, List from, List to) {
				for (int i = 0; i < items.length; i++) {
					from.remove(items[i]);
					to.add(items[i]);
				}
				RefreshFileCount();
			}
		};
		final Button lb = new Button(composite, SWT.NONE);
		lb.setText("<过滤选中项");
		lb.setBounds(226, 105, 97, 27);
		lb.addSelectionListener(listener);
		final Button llb = new Button(composite, SWT.NONE);
		llb.setBounds(226, 138, 97, 27);
		llb.setText("<<全部过滤");
		llb.addSelectionListener(listener);
		final Button rb = new Button(composite, SWT.NONE);
		rb.setBounds(226, 216, 97, 27);
		rb.setText("同步选中项>");
		rb.addSelectionListener(listener);
		final Button rrb = new Button(composite, SWT.NONE);
		rrb.setBounds(226, 249, 97, 27);
		rrb.setText("全部同步>>");
		rrb.addSelectionListener(listener);

		Label label = new Label(composite, SWT.NONE);
		label.setBounds(0, 17, 97, 17);
		formToolkit.adapt(label, true, true);
		label.setText("以下不会被同步：");

		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setBounds(331, 17, 84, 17);
		formToolkit.adapt(label_1, true, true);
		label_1.setText("以下将被同步：");

		Button warpsync_btn = new Button(composite, SWT.RADIO);
		warpsync_btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				syncType = SYNC_TYPE.导出并同步;
			}
		});
		warpsync_btn.setSelection(true);
		warpsync_btn.setBounds(578, 216, 122, 17);
		formToolkit.adapt(warpsync_btn, true, true);
		warpsync_btn.setText("导出并同步至SVN");

		Button warp_btn = new Button(composite, SWT.RADIO);
		warp_btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				syncType = SYNC_TYPE.只导出;
			}
		});
		warp_btn.setBounds(578, 239, 122, 17);
		formToolkit.adapt(warp_btn, true, true);
		warp_btn.setText("只导出到客户端");

		Button sync_btn = new Button(composite, SWT.RADIO);
		sync_btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				syncType = SYNC_TYPE.只同步;
			}
		});
		sync_btn.setBounds(578, 262, 97, 17);
		formToolkit.adapt(sync_btn, true, true);
		sync_btn.setText("只提交到SVN");

		Button refreshFileList_btn = new Button(composite, SWT.NONE);
		refreshFileList_btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TakeList();
				OJCUtils.ShowDialog("成功获取文件列表！");
			}
		});
		refreshFileList_btn.setBounds(624, 10, 122, 35);
		formToolkit.adapt(refreshFileList_btn, true, true);
		refreshFileList_btn.setText("重新获取文件列表");

		Button confirm_btn = new Button(composite, SWT.NONE);
		confirm_btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (syncType == SYNC_TYPE.导出并同步) {
					if (null == targetList || targetList.getItemCount() == 0) {
						return;
					}
					syncData.CommitAndWarp(targetList.getItems(), true);

				} else if (syncType == SYNC_TYPE.只导出) {
					if (null == targetList || targetList.getItemCount() == 0) {
						return;
					}
					syncData.CommitAndWarp(targetList.getItems(), false);
				} else if (syncType == SYNC_TYPE.只同步) {
					syncData.Commit();
				}
			}
		});
		confirm_btn.setBounds(578, 291, 168, 71);
		formToolkit.adapt(confirm_btn, true, true);
		confirm_btn.setText("执行选项");

		filterCount = new Label(composite, SWT.NONE);
		filterCount.setBounds(103, 17, 117, 17);
		formToolkit.adapt(filterCount, true, true);
		filterCount.setText("000");

		targetCount = new Label(composite, SWT.NONE);
		targetCount.setBounds(420, 17, 131, 17);
		formToolkit.adapt(targetCount, true, true);
		targetCount.setText("000");

		TabItem tabItem_1 = new TabItem(tabFolder, SWT.NONE);
		tabItem_1.setText("环境设置");

		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		composite_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		tabItem_1.setControl(composite_1);
		formToolkit.paintBordersFor(composite_1);

		Label label_2 = new Label(composite_1, SWT.NONE);
		label_2.setAlignment(SWT.RIGHT);
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_2.setBounds(10, 29, 103, 26);
		formToolkit.adapt(label_2, true, true);
		label_2.setText("策划表位置：");

		Label label_3 = new Label(composite_1, SWT.NONE);
		label_3.setAlignment(SWT.RIGHT);
		label_3.setText("导出表位置：");
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_3.setBounds(10, 98, 103, 26);
		formToolkit.adapt(label_3, true, true);

		Label lblSvn = new Label(composite_1, SWT.NONE);
		lblSvn.setAlignment(SWT.RIGHT);
		lblSvn.setText("SVN路径：");
		lblSvn.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblSvn.setBounds(10, 167, 103, 26);
		formToolkit.adapt(lblSvn, true, true);

		cehua_path_label = new Text(composite_1, SWT.NONE);
		cehua_path_label.setFont(SWTResourceManager.getFont("微软雅黑", 12,
				SWT.NORMAL));
		cehua_path_label.setBounds(119, 29, 381, 26);
		cehua_path_label.setText(EnvironmentManager.getInstance().getPathStr(
				EnvironmentManager.getInstance().SOURCES_FOLDER_PATH_KEY));
		formToolkit.adapt(cehua_path_label, true, true);

		project_path_label = new Text(composite_1, SWT.NONE);
		project_path_label.setFont(SWTResourceManager.getFont("微软雅黑", 12,
				SWT.NORMAL));
		project_path_label.setBounds(119, 98, 381, 26);
		project_path_label.setText(EnvironmentManager.getInstance().getPathStr(
				EnvironmentManager.getInstance().TARGET_FOLDER_PATH_KEY));
		formToolkit.adapt(project_path_label, true, true);

		svn_path_label = new Text(composite_1, SWT.NONE);
		svn_path_label.setFont(SWTResourceManager.getFont("微软雅黑", 12,
				SWT.NORMAL));
		svn_path_label.setBounds(119, 167, 381, 26);
		String tmpSvn_path_label = EnvironmentManager.getInstance().getPathStr(
				EnvironmentManager.getInstance().SVN_BIN_PATH_KEY);
		if (null == tmpSvn_path_label) {
			tmpSvn_path_label = "";
		}
		svn_path_label.setText(tmpSvn_path_label);
		formToolkit.adapt(svn_path_label, true, true);

		Button btnNewButton = new Button(composite_1, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EnvironmentManager.getInstance().setDataSourcesFloderPath();
				cehua_path_label.setText(EnvironmentManager.getInstance()
						.getDataSourcesFloderPath());
			}
		});
		btnNewButton.setBounds(561, 28, 131, 27);
		formToolkit.adapt(btnNewButton, true, true);
		btnNewButton.setText("选择文件夹");

		Button button = new Button(composite_1, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EnvironmentManager.getInstance().setDataTargetFloderPath();
				project_path_label.setText(EnvironmentManager.getInstance()
						.getDataTargetFloderPath());
			}
		});
		button.setText("选择文件夹");
		button.setBounds(561, 98, 131, 27);
		formToolkit.adapt(button, true, true);

		Button btnsvn = new Button(composite_1, SWT.NONE);
		btnsvn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EnvironmentManager.getInstance().setSvnProcPath();
				svn_path_label.setText(EnvironmentManager.getInstance()
						.getSvnProcPath());
			}
		});
		btnsvn.setText("选择SVN程序");
		btnsvn.setBounds(561, 166, 131, 27);
		formToolkit.adapt(btnsvn, true, true);

	}
}
