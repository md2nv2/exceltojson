package com.ojcgame.common;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class OJCUtils {
	/**
	 * 获取文件夹下所有文件名，含拓展名，不含子文件夹内容
	 */
	public static String[] GetAllFileName(String pFloderPath, String pFilterType) {
		File file = new File(pFloderPath);
		List<String> tmpFilesList = new ArrayList<String>();
		for (String fullName : file.list()) {
			if (fullName.endsWith(".xlsx") == false || fullName.contains("~$")) {
				continue;
			}
			tmpFilesList.add(fullName);
		}
		String[] filesArr = new String[] {};
		return tmpFilesList.toArray(filesArr);
	}

	/**
	 * 获取文件夹下所有文件名，含拓展名，含子文件夹内容
	 */
	public static void GetAllFileName(String path, ArrayList<String> fileName) {
		File file = new File(path);
		File[] files = file.listFiles();
		String[] names = file.list();
		if (names != null)
			fileName.addAll(Arrays.asList(names));
		for (File a : files) {
			if (a.isDirectory()) {
				GetAllFileName(a.getAbsolutePath(), fileName);
			}
		}
	}

	public static String FilePath(String title) {
		return Open(null, JFileChooser.FILES_ONLY, title);
	}

	public static String FilePath(String fileName, String title) {
		return Open(fileName, JFileChooser.FILES_ONLY, title);
	}

	public static String FolderPath(String title) {
		return Open(null, JFileChooser.DIRECTORIES_ONLY, title);
	}

	private static String Open(String fileName, int mode, String title) {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(mode);
		jfc.setDialogTitle(title);
		int result = jfc.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			if (fileName != null && !fileName.isEmpty()) {
				if (file.getName().contains(fileName) == false) {
					return null;
				}
			}
			return file.getAbsolutePath();
		}

		return null;
	}

	public static String GetFileName(String fullName, String pExtenName) {
		return fullName.replaceFirst(pExtenName, "");
	}

	public static void ShowDialog(String _message) {
		JOptionPane.showMessageDialog(null, _message);
	}

	// public static void main(String[] args) {
	// OJCUtils.FolderPath("选择TortoiseProc.exe 在SVN/bin/");
	// }
}
