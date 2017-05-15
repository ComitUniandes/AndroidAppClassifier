package com.android.classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MultipleClassifier {
	
	
	
	private HashMap<String, List<String>> uses;
	
	private HashMap<String, List<String>> permissions;
	
	private HashMap<String, List<String>> features;
	
	public MultipleClassifier( HashMap<String, List<String>> pUses, HashMap<String, List<String>> pPermissions, HashMap<String, List<String>> pFeatures ){
		
		uses = pUses;
		permissions = pPermissions;
		features = pFeatures;
		identifyUsesType();
	}

	
	
	public void identifyUsesType( )
	{
		
		

		
	}
	
	public void identifyShared( )
	{
		
	}
}
