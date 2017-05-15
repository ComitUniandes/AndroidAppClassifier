package com.android.classifier;


import java.io.File;

import java.io.IOException;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SingleClassifier {

	public final static String PERMISSION_PREFIX = "android.permission.";
	public final static String HARDWARE_PREFIX = "android.hardware.";
	
	private String manifestPath;
	
	private AndroidManifest app;
	
	private HashMap<String, Permission> permissions;
	
	private HashSet<String> features;
	
	private HashSet<String> uses;
	
	private ArrayList<String> groups; 
	
	private ArrayList<String> criticalGroups;
	
	private ArrayList<String> hardwareFeatures;
	
	private ArrayList<String> thirdPartyPermissions;
	
	private ArrayList<TypeIdentifier> types;
	
	private PermissionLoader permissionLoader;
	
	private String domain;
	
	public SingleClassifier(String pManifestPath, PermissionLoader pPermissionLoader, String pName, String pCategory){
		manifestPath = pManifestPath;
		uses = new HashSet<String>();
		features = new HashSet<String>();
		permissions = new HashMap<String,Permission>();
		groups = new ArrayList<String>();
		hardwareFeatures = new ArrayList<String>();
		thirdPartyPermissions = new ArrayList<>();
		types = new ArrayList<TypeIdentifier>();
		permissionLoader = pPermissionLoader;
		domain = "";
		criticalGroups = new ArrayList<String>();
		app = new AndroidManifest(pName, pCategory,"");
		loadXML();
		identifyRequiredGroups();
		indentifyTypes();
		identifyDomain();
		generateHighLevelPolicy();
	}
	
	
	public void readPermissionTags(NodeList list)
	{
		
		int size = list.getLength();
		for(int i =0; i<size; i++)
		{
			Node act = list.item(i);
			if(act.getNodeType() == Node.ELEMENT_NODE){
				Element node = (Element) act;
				Permission permission = new Permission(node.getAttribute("android:name"), 
						node.getAttribute("android:protectionLevel"), node.getAttribute("android:permissionGroup"));
				permissions.put(permission.getName(), permission);
				
			}
		}
		
	}
	
	
	public void readUsesTags(NodeList list, HashSet<String> set)
	{
		int size = list.getLength();
		for(int i =0; i<size; i++)
		{
			Node act = list.item(i);
			if(act.getNodeType() == Node.ELEMENT_NODE){
				Element node = (Element) act;
				String name = node.getAttribute("android:name");
				if(name.startsWith("android"))
					set.add(name);
				else
					thirdPartyPermissions.add(name);
			}
			
		}
	}
	
	public void loadXML( )
	{
		System.out.println("1. Loading AndroidManifest.xml");

				try {
				
					File f = new File(manifestPath);
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document manifest = db.parse(f);
					manifest.getDocumentElement().normalize();
					app.setProcessName(manifest.getDocumentElement().getAttribute("package"));

					NodeList usesPermission = manifest.getElementsByTagName("uses-permission");
					readUsesTags(usesPermission, uses);
					
					NodeList usesFeature = manifest.getElementsByTagName("uses-feature");
					readUsesTags(usesFeature, features);
					
					NodeList permissions = manifest.getElementsByTagName("permission");
					readPermissionTags(permissions);

					NodeList usesPermissionSDK = manifest.getElementsByTagName("uses-permission-sdk-23");
					readUsesTags(usesPermissionSDK, uses);
					
					
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
	
	public void checkCriticalPermissions( )
	{
		Set<Entry<String, String[]>> set = permissionLoader.getCriticalPermissions().entrySet();
		Iterator<Entry<String,String[]>> i = set.iterator();
		while(i.hasNext())
		{
			Entry<String,String[]> current = i.next();
			String[] permissions = current.getValue();
			boolean found = false;
			for(int j = 0; j<permissions.length && !found;j++)
			{
				if(uses.contains(PERMISSION_PREFIX+permissions[j]))
				{
					criticalGroups.add(current.getKey());
					found = true;
				}
			}
		}
	}
	
	public void identifyRequiredGroups()
	{
		System.out.println("2. Identifying Required Permissions");
		Set<Entry<String, String[]>> set = permissionLoader.getDangerousPermissions().entrySet();
		Iterator<Entry<String,String[]>> i = set.iterator();
		while(i.hasNext())
		{
			Entry<String,String[]> current = i.next();
			String[] permissions = current.getValue();
			boolean found = false;
			for(int j = 0; j<permissions.length && !found;j++)
			{
				if(uses.contains(PERMISSION_PREFIX+permissions[j]))
				{
					groups.add(current.getKey());
					found = true;
				}
			}
		}
		Set<Entry<String, String[]>> setF = permissionLoader.getHardwareFeatures().entrySet();
		Iterator<Entry<String,String[]>> iF = setF.iterator();
		while(iF.hasNext())
		{
			Entry<String,String[]> current = iF.next();
			String[] fs = current.getValue();
			boolean found = false;
			for(int j = 0; j<fs.length && !found;j++)
			{
				if(features.contains(HARDWARE_PREFIX+fs[j]))
				{
					hardwareFeatures.add(current.getKey());
					found = true;
				}
			}
		}
		
		checkCriticalPermissions();
	}
	
	public void identifyDomain()
	{
		System.out.println("3. Identifying Potential Domain");
		Set<String> dangerous = permissionLoader.getDangerousPermissions().keySet();
		Set<String> critical = permissionLoader.getCriticalPermissions().keySet();
		Set<String> hardware = permissionLoader.getHardwareFeatures().keySet();
		if(critical.size() == criticalGroups.size()){
			domain = app.getApplicationName()+"_d";
			domain += "-DEDICATED DOMAIN TYPE-The application is requesting access at least to one permission of every group of critical permissions.";
			domain+="-The combination of more than two critical permissions can lead to an important violation \n of the application  data's integrity and confidentiality";
		}else if(dangerous.size() == groups.size()){
			domain = app.getApplicationName()+"_d";
			domain += "-DEDICATED DOMAIN TYPE-The application is requesting access at least to one permission of every group of dangerous permissions.";
			domain+="-The combination of more than two dangeours permissions can lead to a violation \n of the application data's integrity and confidentiality";
		}else if(groups.size()>0 && criticalGroups.size()>0){
			domain = app.getApplicationName()+"_d";
			domain += "-DEDICATED DOMAIN TYPE-The application is requesting access at least to one critical permission and one dangerous permission.";
			domain+="-The combination of a critital permission and a dangeorous one can lead to a violation \n of the application data's integrity and confidentiality";
		}else if(hardwareFeatures.size()==hardware.size()){
			domain = app.getApplicationName()+"_d";
			domain += "-DEDICATED DOMAIN TYPE-The application is requesting access at least to one feature of every group of hardware features.";
			domain+="-Access to all the hardware features might be dangerous to the device, please review the requested features.";
		}else{
			String chain = "";
			if(uses.contains(PERMISSION_PREFIX+"INTERNET") ){
				chain+="_internet";
			}if(uses.contains(PERMISSION_PREFIX+"WRITE_EXTERNAL_STORAGE") || uses.contains(PERMISSION_PREFIX+"ALLOCATE_AGGRESSIVE")){
				chain+="_storage";
			}if(uses.contains(PERMISSION_PREFIX+"DOWNLOAD_WITHOUT_NOTIFICATION")){
				chain+="_download";
			}if((uses.contains(PERMISSION_PREFIX+"RUN_IN_BACKGROUND") || uses.contains(PERMISSION_PREFIX+"USE_DATA_IN_BACKGROUND")) && !chain.equals("")){
				chain+="_background";
			}
			domain = app.getPlayStoreCategory()+chain+"_d";
			domain += "-SHARED DOMAIN TYPE-The application is requesting access to some permissions.";
			domain+="-Acces to permissions of internet, write in external storage,\n allocate space aggressive, download without notification o run in background, may classify \n yourapplication into a more specific domain.";
		}

	}
	
	public void indentifyTypes()
	{
		String name="";
		String reason = "";
		String classes = "";
		for(int i = 0;i<groups.size();i++){
			name="";
			reason = "";
			classes = "";
			if(groups.get(i).equals("NETWORK")){
				name = app.getApplicationName().toLowerCase()+"_network_t";
				reason = "The permissions requested to access the network,\n implies that udp or tcp sockets might be created to send or receive data.";
				classes = "socket;icmp_socket;tcp_socket;udp_socket;sock_file";
				types.add(new TypeIdentifier(name, classes, reason, "NETWORK", TypeIdentifier.GROUP));
			}else if(groups.get(i).equals("STORAGE")){
				name = app.getApplicationName().toLowerCase()+"_storage_t";
				reason = "The request of any STORAGE permission implies the modification or creation \n of new files in the external storage.";
				classes = "filesystem;file;dir;fifo_file";
				types.add(new TypeIdentifier(name, classes, reason, "STORAGE", TypeIdentifier.GROUP));
			}else if(groups.get(i).equals("PHONE")){
				
			}else if(groups.get(i).equals("MICROPHONE")){
				name = app.getApplicationName().toLowerCase()+"_audio_t";
				reason = "The request of any MICROPHONE permission,\n might lead to the creation and modification of voice recording files.";
				classes = "file;dir;fifo_file";
				types.add(new TypeIdentifier(name, classes, reason,"MICROPHONE", TypeIdentifier.GROUP));
			}
		}
		for(int j=0; j<hardwareFeatures.size();j++){
			name = "";
			reason = "";
			classes = "";
			if(hardwareFeatures.get(j).equals("AUDIO")){
				name = app.getApplicationName().toLowerCase()+"_audio_t";
				reason = "The permissions requested to access the AUDIO hardware features,\n implies that audio files might be created or modified.";
				classes = "file;dir;fifo_file";
				types.add(new TypeIdentifier(name, classes, reason,"AUDIO", TypeIdentifier.HARDWARE_FEATURE));
			}else if(hardwareFeatures.get(j).equals("BLUETOOTH")){
				name = app.getApplicationName().toLowerCase()+"_bluetooth_t";
				reason = "The permissions requested to access the BLUETOOTH hardware features,\n implies that sockets might be created to send or receive data";
				classes = "bluetooth_socket;sock_file";
				types.add(new TypeIdentifier(name, classes, reason, "BLUETOOTH", TypeIdentifier.HARDWARE_FEATURE));
			}else if(hardwareFeatures.get(j).equals("NFC")){
				name = app.getApplicationName().toLowerCase()+"_nfc_t";
				reason = "The permissions requested to access the NFC hardware features,\n implies that sockets might be created to send or receive data";
				classes = "bluetooth_socket;sock_file";
				types.add(new TypeIdentifier(name, classes, reason, "NFC", TypeIdentifier.HARDWARE_FEATURE));
			}else if(hardwareFeatures.get(j).equals("TELEPHONY")){
				
			}
		}
	}
	
	public void generateHighLevelPolicy()
	{
		
		try {
			System.out.println("4. Generating Policy Report");
			File file = new File("HighLevelPolicy.txt");
			PrintWriter pw = new PrintWriter(file.getAbsolutePath());
			pw.println("POLICY REPORT");
			
			pw.println("----------------------------------------------------------------------------------");
			pw.println("INFO");
			pw.println("----------------------------------------------------------------------------------");
			
			pw.println("Application Name: "+app.getApplicationName());
			pw.println("Play Store Category: "+app.getPlayStoreCategory());
			pw.println("Package-Process Name: " + app.getProcessName());
			if(criticalGroups.size()>0){
				pw.println("Critical Permission Groups used by the application: " );
				for(int i =0;i<criticalGroups.size();i++)
				{
					pw.println("\t" + criticalGroups.get(i));
				}
				pw.flush();
			}
			if(groups.size()>0){
				pw.println("Dangerous Permission Groups used by the application: " );
				for(int i =0;i<groups.size();i++)
				{
					pw.println("\t" + groups.get(i));
				}
				pw.flush();
			}
			if(hardwareFeatures.size()>0){
				pw.println("Hardware Feature Groups used by the application: " );
				for(int i =0;i<hardwareFeatures.size();i++)
				{
					pw.println("\t" + hardwareFeatures.get(i));
				}
				pw.flush();
			}
			pw.println("----------------------------------------------------------------------------------");
			pw.println("SUGGESTED DOMAIN TYPE");
			pw.println("----------------------------------------------------------------------------------");
			String[] split = domain.split("-");
			pw.println("Domain name: "+split[0]);
			pw.println("You should create a " + split[1] );
			pw.println(split[2].trim());
			pw.println(split[3].trim());
			if(thirdPartyPermissions.size()>0 ){
				pw.println("Please notice that if you are requesting third party permissions,\n you must share your " + split[0] + " with the processes or applications to be used");
			}
			pw.flush();
			pw.println("----------------------------------------------------------------------------------");
			pw.println("SUGGESTED OBJECTS TYPE IDENTIFIERS ");
			pw.println("----------------------------------------------------------------------------------");
			for(int i =0; i<types.size();i++){
				TypeIdentifier type = types.get(i);
				pw.println(type.getGroupType()+type.getGroup());
				pw.println("Type Name: "+ type.getNameType());
				pw.println("Security Objects Classes: " + type.getSecurityClasses());
				pw.println(type.getReason() );
				pw.println("");
			}
			if(thirdPartyPermissions.size()>0 ){
				pw.println("Please notice that if you are requesting third party permissions,\n you must share the type identifiers with the processes or applications to be used");
			}
			pw.flush();
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
