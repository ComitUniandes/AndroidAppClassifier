package com.android.classifier;

import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.io.InputStreamReader;


public class AndroidApplicationClassifier {
	
	public final static String MANIFEST = "/AndroidManifest.xml";
	
	public final static String BASE = "C:/Users/Q551/Documents/Universidad/Tesis/apk/";
	
	private String basePath; 
	private String directoriesPath;
	private String manifestPath;
	private PermissionLoader permissionLoader;
	
	
	public AndroidApplicationClassifier( )
	{
		directoriesPath = "";
		basePath = "" ; 
		manifestPath = "";
		permissionLoader = new PermissionLoader();
	}
	
	

	public void multipleClassifier()
	{
		
		try 
		{
			System.out.println("Input the directory path where apks can be found: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String line;
			while((line=br.readLine())!= null)
			{
				File f = new File(line);
				if(f.exists()){
					if(f.isDirectory()){
						basePath = line.trim();
						break;
					}else{
						System.out.println("ERROR - This is not a directory");
						System.out.println("Input the directory path where apks can be found: ");
					}
				}else{
					System.out.println("ERROR - This path can't be found");
					System.out.println("Input the directory path where apks can be found: ");
				}
			}
			System.out.println("Input the file name where the apks names can be found: ");
			while((line=br.readLine())!= null)
			{
				File f = new File(basePath+File.separator+line);
				if(f.exists()){
					if(f.isFile()){
						directoriesPath = line.trim();
						break;
					}else{
						System.out.println("ERROR - This is not a file");
						System.out.println("Input the file name where the apks names can be found: ");
					}
				}else{
					System.out.println("ERROR - This path can't be found");
					System.out.println("Input the file name where the apks names can be found: ");
				}
			}
			br.close();
			MultipleLoader ml = new MultipleLoader(basePath, directoriesPath, permissionLoader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void singleClassifier()
	{
		
		try {
			System.out.println("Input the path where AndroidManifest.xml can be found: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String line;
			while((line=br.readLine())!= null)
			{
				File f = new File(line+"AndroidManifest.xml");
					if(f.exists()){
						if(f.isFile()){
							manifestPath = line.trim();
							break;
						}else{
							System.err.println("ERROR - This is not a file");
							System.out.println("Input the path where AndroidManifest.xml can be found: ");
						}
					}else{
						System.err.println("ERROR - This path can't be found");
						System.out.println("Input the directory path where AndroidManifest.xml can be found: ");
					}
				
				
			}
			System.out.println("Input the Application name: ");
			String name = "";
			while((line=br.readLine())!= null)
			{
				if(line.equals("")){
					System.err.println("ERROR - This is not an application name");
					System.out.println("Input the application name: ");
					
				}else{
					name=line;
					break;
				}
			}
			
			System.out.println("Input the (potential) Application Google Play Store Category : ");
			String category = "";
			while((line=br.readLine())!= null)
			{
				if(line.equals("")){
					System.err.println("ERROR - This is not an application category");
					System.out.println("Input the (potential) Application Google Play Store Category : ");
					
				}else{
					category=line;
					break;
				}
			}
			br.close();
			SingleClassifier sc = new SingleClassifier(manifestPath, permissionLoader, name, category);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		try {
			System.out.println("Android Application Classifier");
			System.out.println("Do you want to classify more than one application? (y|n)");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String line;
			while((line=br.readLine())!= null){
				if(line.equals("y")){
					AndroidApplicationClassifier aac = new AndroidApplicationClassifier();
					aac.multipleClassifier();
					break;
				}else if(line.equals("n")){
					AndroidApplicationClassifier aac = new AndroidApplicationClassifier();
					aac.singleClassifier();
					break;
				}else{
					System.out.println("Do you want to classify more than one application? (y|n)");
				}
			}
			br.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
