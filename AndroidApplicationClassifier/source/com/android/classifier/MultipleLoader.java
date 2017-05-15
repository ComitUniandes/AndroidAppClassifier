package com.android.classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MultipleLoader {

	private HashMap<String, List<String>> uses;
	
	private HashMap<String, List<String>> permissions;
	
	private HashMap<String, List<String>> features;
	
	private List<AndroidManifest> manifests;
	
	private String basePath;
	
	private String directoriesPath;
	
	public MultipleLoader( String pBasePath, String pDirectoriesPath)
	{
		manifests = new ArrayList<AndroidManifest>();
		uses = new HashMap<String, List<String>>();
		permissions = new HashMap<String, List<String>>();
		features = new HashMap<String,List<String>>();
		basePath = pBasePath;
		directoriesPath = pDirectoriesPath;
		System.out.println(basePath+directoriesPath);
		loadPaths();
		
	}
	
	public List<Permission> readPermissionTags(NodeList list, AndroidManifest app)
	{
		List<Permission> ps = new ArrayList<Permission>();
		int size = list.getLength();
		for(int i =0; i<size; i++)
		{
			Node act = list.item(i);
			if(act.getNodeType() == Node.ELEMENT_NODE){
				Element node = (Element) act;
				Permission permission = new Permission(node.getAttribute("android:name"), 
						node.getAttribute("android:protectionLevel"), node.getAttribute("android:permissionGroup"));
				ps.add(permission);
				System.out.println(permission.toString());
				List<String> apps = permissions.get(permission.getName());
				if(apps!=null)
				{
					apps.add(app.getProcessName());
					permissions.put(permission.getName(),apps);
				}else{
					apps = new ArrayList<String>();
					apps.add(app.getProcessName());
					permissions.put(permission.getName(),apps);
				}
			}
		}
		return ps;
	}
	
	
	public List<String> readUsesTags(NodeList list, AndroidManifest app)
	{
		List<String> us = new ArrayList<String>();
		int size = list.getLength();
		for(int i =0; i<size; i++)
		{
			Node act = list.item(i);
			if(act.getNodeType() == Node.ELEMENT_NODE){
				Element node = (Element) act;
				String name = node.getAttribute("android:name");
				us.add(name);
				System.out.println(name);
				List<String> apps = uses.get(name);
				if(apps!=null)
				{
					apps.add(app.getProcessName());
					uses.put(name,apps);
				}else{
					apps = new ArrayList<String>();
					apps.add(app.getProcessName());
					uses.put(name,apps);
				}
			}
			
		}
		return us;
	}
	
	public void loadXMLs( )
	{
		System.out.println("2. Reading AndroidManifest.xml");
			int size = manifests.size();
			for(int i =0; i<size; i++)
			{
				try {
					AndroidManifest act = manifests.get(i);
					System.out.println("\nAPPLICATION: " + act.getApplicationName() + " - PROCESS: " + act.getProcessName() );
					
					File f = new File(basePath + act.getProcessName() + AndroidApplicationClassifier.MANIFEST);
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document manifest = db.parse(f);
					manifest.getDocumentElement().normalize();
					
					System.out.println("\nUSES-PERMISSION \n");
					NodeList usesPermission = manifest.getElementsByTagName("uses-permission");
					act.setUsesPermissions(readUsesTags(usesPermission, act));
					
					System.out.println("\nUSES-FEATURES \n");
					NodeList usesFeature = manifest.getElementsByTagName("uses-feature");
					act.setUsesfeatures(readUsesTags(usesFeature,act));
					
					System.out.println("\nPERMISSION \n");
					NodeList permissions = manifest.getElementsByTagName("permission");
					act.setPermissions(readPermissionTags(permissions,act));
					
					System.out.println("\nUSES-PERMISSION-SDK \n");
					NodeList usesPermissionSDK = manifest.getElementsByTagName("uses-permission-sdk-23");
					act.setUsesPermissionsSDK(readUsesTags(usesPermissionSDK,act));
					
					
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			MultipleClassifier mc = new MultipleClassifier(uses,permissions,features);
					
		
	}
	
	public void loadPaths ()
	{
		System.out.println("1. Loading Manifests Paths");
		try {
			BufferedReader br = new BufferedReader(new FileReader(basePath + directoriesPath));
			String line;
			while((line = br.readLine())!= null)
			{
				String[] split = line.split("-");
				if(split.length == 3){
					AndroidManifest am = new AndroidManifest(split[1], split[2], split[0]);
					manifests.add(am);
				}
				
			}
			br.close();
			loadXMLs();
		} catch (FileNotFoundException e) {
			System.err.println("Error: File has not been found, please try again.");
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println("Error: File can't be read");
			System.err.println(e.getMessage());
		}
	}
}
