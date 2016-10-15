package com.ojcgame.warp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.ojcgame.common.EnvironmentManager;
import com.ojcgame.common.OJCUtils;

public class SaveTargetFiles {
	static String[] FileNameArr;

	static final String encoding = "UTF-8";
	static String dataURL;

	public static String getDataURL() {
		if (dataURL == null || dataURL.isEmpty()) {
			dataURL = System.getProperty("user.dir") + "/config/targetfiles.txt";
		}
		return dataURL;
	}

	public static String[] GetCurrentFiles() {
		InputStreamReader is = null;
		try {
			File file = new File(getDataURL());
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				is = new InputStreamReader(new FileInputStream(file), encoding);
			} catch (UnsupportedEncodingException e) {
				is = new InputStreamReader(new FileInputStream(file));
			}

			BufferedReader bufferedReader = new BufferedReader(is);
			String lineTxt = null;
			List<String> tmpFileList = new ArrayList<String>();
			try {
				while ((lineTxt = bufferedReader.readLine()) != null
						&& lineTxt.isEmpty() == false) {
					tmpFileList.add(lineTxt);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != bufferedReader) {
					try {
						bufferedReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			FileNameArr = new String[] {};

			return tmpFileList.toArray(FileNameArr);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return FileNameArr;
	}

	public static String[] Save(String[] pFilesArray) {
		// 需要根据真实文件列表进行筛选
		String[] allFiles = OJCUtils.GetAllFileName(EnvironmentManager
				.getInstance().getDataSourcesFloderPath() + "\\", ".xlsx");
		List<String> realyFilesList = new ArrayList<String>();
		for (int i = 0, iLength = pFilesArray.length; i < iLength; ++i) {
			for (int j = 0, jLength = allFiles.length; j < jLength; ++j) {
				if (pFilesArray[i].equals(allFiles[j])) {
					realyFilesList.add(pFilesArray[i]);
					continue;
				}
			}
		}

		realyFilesList.toArray(pFilesArray);

		File file = new File(getDataURL());
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		FileWriter writer;
		try {
			writer = new FileWriter(file);
			for (int i = 0, mLength = pFilesArray.length; i < mLength; ++i) {
				writer.write(pFilesArray[i]);
				if (i < mLength - 1) {
					writer.write("\r");
				}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return pFilesArray;
	}

//	public static void main(String[] args) {
//		SaveTargetFiles.GetCurrentFiles();
//	}
}
