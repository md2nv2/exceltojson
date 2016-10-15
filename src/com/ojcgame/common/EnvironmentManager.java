package com.ojcgame.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EnvironmentManager {
	// 属性文件的路径
	private final String ConfigFilePath = "config/ini.properties";

	public final String SOURCES_FOLDER_PATH_KEY = "sources";
	public final String TARGET_FOLDER_PATH_KEY = "target";
	public final String SVN_BIN_PATH_KEY = "svn";

	Properties mConfig = null;

	private String dataSourcesFloderPath;
	private String dataTargetFloderPath;
	private String svnProcPath;

	public String getAppName() {
		return GetPropertyByKey("appname");
	}

	public String getVersionNumber() {
		return GetPropertyByKey("version");
	}

	public String getDataSourcesFloderPath() {
		if (null == dataSourcesFloderPath || dataSourcesFloderPath.isEmpty()
				|| FolderExists(dataSourcesFloderPath) == false) {
			setDataSourcesFloderPath();
		}
		return dataSourcesFloderPath;
	}

	public String getDataTargetFloderPath() {
		if (null == dataTargetFloderPath || dataTargetFloderPath.isEmpty()
				|| FolderExists(dataTargetFloderPath) == false) {
			setDataTargetFloderPath();
		}
		return dataTargetFloderPath;
	}

	public String getSvnProcPath() {
		if (null == svnProcPath || svnProcPath.isEmpty()
				|| FolderExists(svnProcPath) == false) {
			setSvnProcPath();
		}
		return svnProcPath;
	}

	public boolean FolderExists(String pPath) {
		if (null == pPath || pPath.isEmpty()) {
			return false;
		}
		File folderPath = new File(pPath);
		return folderPath.exists();
	}

	public void setDataSourcesFloderPath() {
		this.dataSourcesFloderPath = OJCUtils.FolderPath("选择策划表位置（文件夹）");
		UpdatePropertyByKey(SOURCES_FOLDER_PATH_KEY, this.dataSourcesFloderPath);
	}

	public void setDataTargetFloderPath() {
		this.dataTargetFloderPath = OJCUtils.FolderPath("选择导出表位置（文件夹）");
		UpdatePropertyByKey(TARGET_FOLDER_PATH_KEY, this.dataTargetFloderPath);
	}

	public void setSvnProcPath() {
		this.svnProcPath = OJCUtils.FilePath("TortoiseProc.exe",
				"选择TortoiseProc.exe 在SVN/bin/");
		UpdatePropertyByKey(SVN_BIN_PATH_KEY, this.svnProcPath);
	}

	public String getPathStr(String type) {
		if (SOURCES_FOLDER_PATH_KEY.equals(type)) {
			return dataSourcesFloderPath;
		} else if (TARGET_FOLDER_PATH_KEY.equals(type)) {
			return dataTargetFloderPath;
		} else if (SVN_BIN_PATH_KEY.equals(type)) {
			return svnProcPath;
		}

		return "";
	}

	private static EnvironmentManager mInstance = new EnvironmentManager();

	public static EnvironmentManager getInstance() {
		return mInstance;
	}

	public EnvironmentManager() {
		ReadConfig();
	}

	private void ReadConfig() {
		mConfig = new Properties();
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(ConfigFilePath));
			mConfig.load(in);

			dataSourcesFloderPath = mConfig
					.getProperty(SOURCES_FOLDER_PATH_KEY);
			dataTargetFloderPath = mConfig.getProperty(TARGET_FOLDER_PATH_KEY);
			svnProcPath = mConfig.getProperty(SVN_BIN_PATH_KEY);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != in)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public String GetPropertyByKey(String pKey) {
		if (null == mConfig) {
			ReadConfig();
		}
		if (mConfig.containsKey(pKey) == false) {
			return null;
		}
		return mConfig.getProperty(pKey);
	}

	public void UpdatePropertyByKey(String pKey, String pValue) {
		if (null == pValue || pValue.isEmpty())
			return;

		if (pKey == SOURCES_FOLDER_PATH_KEY) {
			dataSourcesFloderPath = pValue;
		} else if (pKey == TARGET_FOLDER_PATH_KEY) {
			dataTargetFloderPath = pValue;
		} else if (pKey == SVN_BIN_PATH_KEY) {
			svnProcPath = pValue;
		}

		if (null == mConfig) {
			ReadConfig();
		}
		FileOutputStream oFile = null;
		try {
			oFile = new FileOutputStream(ConfigFilePath, true);
			mConfig.setProperty(pKey, pValue);
			mConfig.store(oFile, null);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != oFile)
				try {
					oFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

	// public static void main(String[] args) {
	// EnvironmentManager enmgr = new EnvironmentManager();
	// System.out.println(enmgr.getSvnProcPath());
	// // enmgr.UpdatePropertyByKey("target",
	// // "F:/svn project/Design/定稿/测试数据/");
	// // enmgr.GetPropertyByKey("target");
	// }
}
