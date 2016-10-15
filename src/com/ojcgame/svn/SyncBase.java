package com.ojcgame.svn;

import java.io.File;
import java.io.IOException;

import com.ojcgame.common.EnvironmentManager;
import com.ojcgame.warp.WarpDataManager;

public class SyncBase {
	private String[] drives = new String[] { "c:", "d:", "e:", "f:" };
	private String svnBin = "/Program Files/TortoiseSVN/bin/";
	private String svnProcName = "TortoiseProc.exe";
	private String svnProcPath = null;

	WarpDataManager warpDataMgr;

	public SyncBase() {
		warpDataMgr = new WarpDataManager();
	}

	public void Update() {
		if (svnProcPath == null || svnProcPath.isEmpty()) {
			svnProcPath = GetSVNBinPath();
		}
		ExcuteSVNCommand(" /command:update /path:\""
				+ EnvironmentManager.getInstance().getDataTargetFloderPath()
				+ "\" /closeonend:0");
	}

	public void Commit() {
		if (svnProcPath == null || svnProcPath.isEmpty()) {
			svnProcPath = GetSVNBinPath();
		}
		ExcuteSVNCommand(" /command:commit /path:\""
				+ EnvironmentManager.getInstance().getDataTargetFloderPath()
				+ "\"");
	}

	public void CommitAndWarp(String[] pTargetFiles, boolean isCommit) {
		warpDataMgr.WarpAll(pTargetFiles);
		if (isCommit)
			this.Commit();
	}

	private void ExcuteSVNCommand(String command) {
		// BufferedReader br = null;
		if (null == svnProcPath || svnProcPath.isEmpty()) {
			return;
		}
		try {
			Runtime.getRuntime().exec(svnProcPath + command);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		// String line = null;
		// StringBuilder sb = new StringBuilder();
		// while ((line = br.readLine()) != null) {
		// sb.append(line + "\n");
		// }

		// OJCUtils.ShowDialog("执行操作成功！");
	}

	private String GetSVNBinPath() {
		StringBuffer sb = new StringBuffer();
		File binFile;
		for (String drive : drives) {
			sb.setLength(0);
			sb.append(drive);
			sb.append(svnBin);
			sb.append(svnProcName);
			binFile = new File(sb.toString());
			if (binFile.exists())
				return sb.toString();
		}

		return EnvironmentManager.getInstance().getSvnProcPath();
	}
}