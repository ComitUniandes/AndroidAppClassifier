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
	
	private String basePath;
	
	private String directoriesPath;
	
	private PermissionLoader permissionLoader;
	
	public MultipleLoader( String pBasePath, String pDirectoriesPath, PermissionLoader pPermissionLoader)
	{
		basePath = pBasePath;
		directoriesPath = pDirectoriesPath;
		permissionLoader = pPermissionLoader;
		loadPaths();
		
	}

	
	public void loadPaths ()
	{
		System.out.println("1. Loading Manifests Paths");
		try {
			BufferedReader br = new BufferedReader(new FileReader(basePath + File.separator+directoriesPath));
			String line;
			SingleClassifier sc;
			while((line = br.readLine())!= null)
			{
				String[] split = line.split("-");
				if(split.length == 3){
					sc = new SingleClassifier(basePath+File.separator+split[0]+File.separator, permissionLoader, split[1], split[2]);
				}
				
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error: File has not been found, please try again.");
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println("Error: File can't be read");
			System.err.println(e.getMessage());
		}
	}
}
