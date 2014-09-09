package org.He.W.auto.git;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.TreeMap;

import com.He.W.onebone.Circuit.Cu.parser.CCSParser;

public class GitPuller {
	public static void main(String args[]){
		try{
			File f = new File("autogit.ccs");
			if(!f.exists()){
				f.createNewFile();
				gen();
				System.out.println("Please write the Setting file (autogit.ccs).");
				System.exit(0);
			}
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			TreeMap<String, String> data = CCSParser.parseCCS(br).get("settings");
			if((data.get("gitpath") == "PATH_OF_GIT_HERE/bin") || (data.get("gitproject").equals("GIT_PROJECT_PATH_HERE"))){
				gen();
				System.out.println("Please write the Setting file (autogit.ccs).");
				System.exit(0);
			}
			File gitpath = new File(data.get("gitpath"));
			File gitproject = new File(data.get("gitproject"));
			if(!gitpath.exists() || !gitproject.exists()){
				System.out.println("The path on the setting file doesn't exists");
				System.exit(0);
			}
			String[] ignore = data.get("ignore").split(",");
			File[] projects = gitproject.listFiles(new FileSelector(ignore));
			for(int a = 0; a < projects.length; a++){
				System.out.println("working on : ");
				System.out.println(projects[a].getAbsolutePath());
				fetch(projects[a], gitpath.getAbsolutePath());
			}
			br.close();
			isr.close();
			fis.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void gen() throws IOException{
		File f = new File("autogit.ccs");
		FileOutputStream fos = new FileOutputStream(f);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write("Git Auto Puller Setting File v1");
		bw.newLine();
		bw.append("[Settings]");
		bw.newLine();
		bw.append("gitproject=GIT_PROJECT_PATH_HERE");
		bw.newLine();
		bw.append("gitpath=PATH_OF_GIT_HERE/bin");
		bw.newLine();
		bw.append("ignore=SPLITS_BY_COLUMN");
		bw.newLine();
		bw.append("[/]");
		bw.flush();
		bw.close();
		osw.close();
		fos.close();
	}
	
	public static void fetch(File project, String git){
		try{
			ProcessBuilder pb = new ProcessBuilder(new String[] {"cmd", "/c", git + "\\git.exe", "pull"});
			pb.directory(project);
			pb.redirectErrorStream();
			Process p = pb.start();
			InputStreamReader isr = new InputStreamReader(p.getInputStream(), "UTF-8");
			BufferedReader s  = new BufferedReader(isr);
			File f = new File("autogit_log_" + project.getName() + ".txt");
			if(!f.exists()){
				f.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(f);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			BufferedWriter bw = new BufferedWriter(osw);
			Date d = new Date();
			bw.write("autogit log : " + d.toString());
			bw.newLine();
			bw.append("working on : " + project.getName());
			String s2 = "";
			boolean utd = false;
			while((s2 = s.readLine()) != null){
				if(s2.equals("Already up-to-date.")){
					System.out.println("Repository " + project.getName() + " is up-to-date.");
					utd = true;
				}
				bw.newLine();
				bw.append(s2);
			}
			if(!utd){
				System.out.println("Repository " + project.getName() + " is not up-to-date.");
				System.out.println("Successfully pulled.");
			}
			p.destroy();
			bw.newLine();
			bw.append("End of report.");
			bw.flush();
			bw.close();
			s.close();
			System.out.println("Log saved.");
		}catch(IOException e){
			System.out.println("An Error Occured!!!!!");
			e.printStackTrace();
		}
	}
}
