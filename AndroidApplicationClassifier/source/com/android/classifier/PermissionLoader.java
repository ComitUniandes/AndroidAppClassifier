package com.android.classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class PermissionLoader {
	
	private HashMap<String, String[]> dangerousPermissions;
	
	private HashMap<String, String[]> criticalPermissions;
	
	private HashMap<String, String[]> hardwareFeatures;
	
	private HashMap<String, String[]> softwareFeatures;
	
	public PermissionLoader( )
	{
		dangerousPermissions = new HashMap<String, String[]>();
		criticalPermissions = new HashMap<String, String[]>();
		hardwareFeatures = new HashMap<String, String[]>();
		softwareFeatures = new HashMap<String,String[]>();
		loadPermissionLists("data/dangerousPermissions.txt", dangerousPermissions);
		loadPermissionLists("data/criticalPermissions.txt", criticalPermissions);
		loadPermissionLists("data/hardwareFeatures.txt", hardwareFeatures);
		loadPermissionLists("data/softwareFeatures.txt", softwareFeatures);
	}

	public void loadPermissionLists(String path, HashMap<String, String[]> collection ){
		 
		try {
			File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
			String line;
			while((line=br.readLine())!= null){
				String[] split = line.split(";");
				String[] perms = split[1].split(",");
				collection.put(split[0], perms);
			}
		} catch (FileNotFoundException e) {
			System.err.println("ERROR: File has not been found \""+path+"\"" );
			System.out.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, String[]> getDangerousPermissions() {
		return dangerousPermissions;
	}

	public HashMap<String, String[]> getCriticalPermissions() {
		return criticalPermissions;
	}

	public HashMap<String, String[]> getHardwareFeatures() {
		return hardwareFeatures;
	}

	public HashMap<String, String[]> getSoftwareFeatures() {
		return softwareFeatures;
	}
	
	
}
