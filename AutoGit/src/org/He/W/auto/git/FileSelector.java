package org.He.W.auto.git;

import java.io.File;
import java.io.FileFilter;

public class FileSelector implements FileFilter {
	private String[] ignoreList = {};
	public FileSelector(String[] ignoreList){
		this.ignoreList = ignoreList;
	}
	public boolean accept(File f){
		if(!f.exists()) return false;
		if(!f.isDirectory()) return false;
		for(int a = 0; a < ignoreList.length;a++){
			if(f.getName().equals(ignoreList[a])){
				return false;
			}
		}
		FileFilter gitFilter = (f2) -> f2.getName().equals(".git");;
		if(f.listFiles(gitFilter).length <= 0)return false;
		return true;
	}
}
