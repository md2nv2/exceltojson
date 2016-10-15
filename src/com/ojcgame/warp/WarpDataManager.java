package com.ojcgame.warp;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.alibaba.fastjson.JSON;
import com.ojcgame.common.EnvironmentManager;
import com.ojcgame.common.OJCUtils;

public class WarpDataManager {
	String[] filesArr;

	public void WarpAll(String[] files) {
		filesArr = files;
		ProgressMonitorDialog progress = new ProgressMonitorDialog(null);
		IRunnableWithProgress progressTask = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				monitor.beginTask("正在导出数据", IProgressMonitor.UNKNOWN);
				WarpAll(filesArr, monitor);
			}
		};

		try {
			progress.run(true, false, progressTask);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			filesArr = null;
		}
	}

	@SuppressWarnings("deprecation")
	private void WarpAll(String[] files, IProgressMonitor monitor) {
		InputStream is = null;
		XSSFWorkbook xssfWorkbook = null;
		List<String> titles = null;
		Map<String, Object> oneCellData = null;
		List<Map<String, Object>> AllDataList = null;
		int fileIndex = 0;
		try {
			for (int f = 0, fLength = files.length; f < fLength; ++f) {
				fileIndex = f;
				// System.out.println("正在尝试导出：" + files[f]);
				monitor.subTask("尝试导出：" + files[f]);
				is = new FileInputStream(EnvironmentManager.getInstance()
						.getDataSourcesFloderPath() + "\\" + files[f]);
				xssfWorkbook = new XSSFWorkbook(is);
				// 读取sheet1
				XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
				if (xssfSheet == null)
					continue;

				titles = new ArrayList<String>();
				AllDataList = new ArrayList<Map<String, Object>>();
				// 先读取字段
				XSSFRow titleRow = xssfSheet.getRow(0);
				for (int rowIndex = 0, mLength = titleRow.getLastCellNum() + 1; rowIndex < mLength; ++rowIndex) {
					if (null == titleRow.getCell(rowIndex)
							|| titleRow.getCell(rowIndex).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
						break;
					} else {
						try {
							// System.out.println(titles.get(cellNum) + "---"
							// + xssfCell.getStringCellValue());
							titles.add(titleRow.getCell(rowIndex)
									.getStringCellValue());
						} catch (IllegalStateException e) {
							// System.out.println("rowIndex number:" + rowIndex
							// + " ---- " + files[f]);
							// System.out.println(titles.get(cellNum) + "---"
							// + xssfCell.getNumericCellValue());
							titles.add(titleRow.getCell(rowIndex)
									.getNumericCellValue() + "");
						}
					}
				}
				// System.out.println(xssfSheet
				// .getLastRowNum() + 1);
				// 读取行
				for (int rowNum = 2, rLength = xssfSheet.getLastRowNum() + 1; rowNum < rLength; ++rowNum) {
					XSSFRow xssfRow = xssfSheet.getRow(rowNum);
					if (xssfRow == null) {
						continue;
					}
					oneCellData = new HashMap<String, Object>();
					// 读取列
					for (int cellNum = 0; cellNum < titles.size(); ++cellNum) {
						XSSFCell xssfCell = xssfRow.getCell(cellNum);
						if (null == xssfCell)
							continue;

						if (xssfCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
							// System.out.println(titles.get(cellNum) + "---"
							// + xssfCell.getNumericCellValue());
							oneCellData.put(titles.get(cellNum),
									xssfCell.getNumericCellValue());
						} else if (xssfCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
							// System.out.println(titles.get(cellNum) + "---"
							// + xssfCell.getStringCellValue());
							oneCellData.put(titles.get(cellNum),
									xssfCell.getStringCellValue());
						} else if (xssfCell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
							// System.out.println(cellNum + "--- kong=======" +
							// rowNum);
							// System.out
							// .println(titles.get(cellNum) + "--- kong");
							oneCellData.put(titles.get(cellNum), "");
						} else if (xssfCell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
							try {
								// System.out.println(titles.get(cellNum) +
								// "---"
								// + xssfCell.getStringCellValue());
								oneCellData.put(titles.get(cellNum),
										xssfCell.getStringCellValue());
							} catch (IllegalStateException e) {
								// System.out.println(titles.get(cellNum) +
								// "---"
								// + xssfCell.getNumericCellValue());
								oneCellData.put(titles.get(cellNum),
										xssfCell.getNumericCellValue());
							}
						}
					}

					AllDataList.add(oneCellData);
				}

				if (null != xssfWorkbook)
					xssfWorkbook.close();
				if (null != is)
					is.close();

				ParseJson(AllDataList, OJCUtils.GetFileName(files[f], ".xlsx"));

				monitor.worked(f + 1);
			}

		} catch (Exception e) {
			e.printStackTrace();
			OJCUtils.ShowDialog("导出失败：" + files[fileIndex]);
		} finally {
			monitor.done();
			try {
				if (null != xssfWorkbook)
					xssfWorkbook.close();
			} catch (IOException e) {
				e.printStackTrace();
				OJCUtils.ShowDialog("导出失败：" + files[fileIndex]);
			} finally {
				try {
					if (null != is)
						is.close();
				} catch (Exception e) {
					e.printStackTrace();
					OJCUtils.ShowDialog("导出失败：" + files[fileIndex]);
				}
			}
		}
	}

	private void ParseJson(List<Map<String, Object>> pContents, String pFileName) {
		String jsonStr = JSON.toJSONString(pContents, true);
		if (null == jsonStr || jsonStr.isEmpty()) {
			return;
		}
		FileWriter writer = null;
		try {
			writer = new FileWriter(EnvironmentManager.getInstance()
					.getDataTargetFloderPath() + "\\" + pFileName + ".json");
			writer.write(jsonStr);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
			OJCUtils.ShowDialog("导出JSON失败：" + pFileName);
		} finally {
			try {
				if (null != writer) {
					writer.flush();
					writer.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				OJCUtils.ShowDialog("导出JSON失败：" + pFileName);
			}
		}
	}

//	public static void main(String[] args) {
//		ProgressMonitorDialog progress = new ProgressMonitorDialog(null);
//		IRunnableWithProgress progressTask = new IRunnableWithProgress() {
//			@Override
//			public void run(IProgressMonitor monitor)
//					throws InvocationTargetException, InterruptedException {
//				monitor.beginTask("正在导出数据", IProgressMonitor.UNKNOWN);
//				WarpDataManager wdMgr = new WarpDataManager();
//				wdMgr.WarpAll(new String[] { "skill.xlsx" }, monitor);
//				monitor.done();
//			}
//		};
//
//		try {
//			progress.run(true, false, progressTask);
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
}
