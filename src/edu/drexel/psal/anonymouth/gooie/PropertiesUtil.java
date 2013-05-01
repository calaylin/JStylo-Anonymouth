package edu.drexel.psal.anonymouth.gooie;

import java.awt.Point;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.jgaap.generics.Document;

import edu.drexel.psal.jstylo.generics.Logger.LogOut;
import edu.drexel.psal.jstylo.generics.*;

public class PropertiesUtil {
	
	private static final String NAME = "( PropertiesUtil ) - ";

	protected static String propFileName = "jsan_resources/anonymouth_prop.prop";
	protected static File propFile = new File(propFileName);
	protected static Properties prop = new Properties();
	protected static JFileChooser load = new JFileChooser();
	protected static JFileChooser save = new JFileChooser();
	protected static String defaultClass = "SMO";
	protected static String defaultFeat = "WritePrints (Limited)";
	protected static String defaultProbSet = "";
	protected static Boolean defaultAutoSave = false;
	protected static Boolean defaultWarnQuit = true;
	protected static int defaultThreads = 4;
	protected static int defaultFeatures = 1000;
	private static String[] DEFAULT_LOCATIONS = new String[]{"top","left","right","bottom"};
	
	public static enum Location // just so you cant mess up the input to methods by spelling stuff wrong
	{
		LEFT("left"), TOP("top"), RIGHT("right"), BOTTOM("bottom"), NONE("none");
		
		public String strRep;
		Location(String rep)
		{
			strRep = rep;
		}
	}
	
	protected static Location stringToLocation(String loc)
	{
		switch (loc)
		{
		case "left":
			return Location.LEFT;
		case "top":
			return Location.TOP;
		case "right":
			return Location.RIGHT;
		case "bottom":
			return Location.BOTTOM;
		case "none":
			return Location.NONE;
		}
		return null;
	}
	
	protected static String locationToString(Location loc)
	{
		switch (loc)
		{
		case LEFT:
			return "left";
		case TOP:
			return "top";
		case RIGHT:
			return "right";
		case BOTTOM:
			return "bottom";
		case NONE:
			return "none";
		}
		return null;
	}
	
	/**
	 * Resets all values in the prop file to their default values, thereby erasing all the user's changes.
	 */
	protected static void reset() {
		setThreadCount(defaultThreads);
		setMaximumFeatures(defaultFeatures);
		setWarnQuit(defaultWarnQuit);
		setAutoSave(defaultAutoSave);
		setProbSet(defaultProbSet);
		setFeature(defaultFeat);
		setClassifier(defaultClass);
	}
	
	/**
	 * Sets the user's thread count preference.
	 * @param threads - the maximum number of threads the user wants used
	 */
	protected static void setThreadCount(int threads) {
		BufferedWriter writer;
		try {
			prop.setProperty("numOfThreads", Integer.toString(threads));
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
			ThePresident.NUM_TAGGING_THREADS = threads;
		} catch (Exception e) {
			Logger.logln(NAME + "Failed setting thread count", LogOut.STDERR);
		}
	}
	
	/**
	 * Gets the user's preferred thread count
	 * @return 
	 */
	protected static int getThreadCount() {
		String threads = "";
		try {
			threads = prop.getProperty("numOfThreads");
			if (threads == null) {
				prop.setProperty("numOfThreads", Integer.toString(defaultThreads));
				threads = prop.getProperty("numOfThreads");
			}
		} catch (NullPointerException e) {
			prop.setProperty("numOfThreads", Integer.toString(defaultThreads));
			threads = prop.getProperty("numOfThreads");
		}
		
		return Integer.parseInt(threads);
	}
	
	/**
	 * Sets the user's feature count preference.
	 * @param max - the maximum number of features the user wants used
	 */
	protected static void setMaximumFeatures(int max) {
		BufferedWriter writer;
		try {
			prop.setProperty("maxFeatures", Integer.toString(max));
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
			
			ThePresident.MAX_FEATURES_TO_CONSIDER = max;
		} catch (Exception e) {
			Logger.logln(NAME + "Failed setting maximum features", LogOut.STDERR);
		}
	}
	
	/**
	 * Gets the user's preferred feature count.
	 * @return
	 */
	protected static int getMaximumFeatures() {
		String max = "";
		try {
			max = prop.getProperty("maxFeatures");
			if (max == null) {
				prop.setProperty("maxFeatures", Integer.toString(defaultFeatures));
				max = prop.getProperty("maxFeatures");
			}
		} catch (NullPointerException e) {
			prop.setProperty("maxFeatures", Integer.toString(defaultFeatures));
			max = prop.getProperty("maxFeatures");
		}
		
		return Integer.parseInt(max);
	}
	
	/**
	 * Sets the user's preference on having a warning upon exit.
	 * @param b - whether or not the user wants to be warned upon exit of unsaved changes
	 */
	protected static void setWarnQuit(Boolean b) {
		BufferedWriter writer;
		try {
			prop.setProperty("warnQuit", b.toString());
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
		} catch (Exception e) {
			Logger.logln(NAME+"Failed setting warn on quit", LogOut.STDERR);
		}
	}
	
	/**
	 * Gets the user's warn on quit preference
	 * @return
	 */
	protected static Boolean getWarnQuit() {
		String warnQuit = "";
		try {
			warnQuit = prop.getProperty("warnQuit");
			if (warnQuit == null) {
				prop.setProperty("warnQuit", defaultWarnQuit.toString());
				warnQuit = prop.getProperty("warnQuit");
			}
		} catch (NullPointerException e) {
			prop.setProperty("warnQuit", defaultWarnQuit.toString());
			warnQuit = prop.getProperty("warnQuit");
		}
		
		if (warnQuit.equals("true"))
			return true;
		else
			return false;
	}
	
	/**
	 * Sets the user's preference on auto-saving
	 * @param b - whether or not the user wants their document to be auto-saved
	 */
	protected static void setAutoSave(Boolean b) {
		BufferedWriter writer;
		try {
			prop.setProperty("autoSave", b.toString());
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
			
			ThePresident.SHOULD_KEEP_AUTO_SAVED_ANONYMIZED_DOCS = b;
		} catch (Exception e) {
			Logger.logln(NAME+"Failed setting auto save", LogOut.STDERR);
		}
	}
	
	/**
	 * Gets the user's preference on auto-saving
	 * @return
	 */
	protected static Boolean getAutoSave() {
		String autoSave = "";
		try {
			autoSave = prop.getProperty("autoSave");
			if (autoSave == null) {
				prop.setProperty("autoSave", defaultAutoSave.toString());
				autoSave = prop.getProperty("autoSave");
			}
		} catch (NullPointerException e) {
				prop.setProperty("autoSave", defaultAutoSave.toString());
				autoSave = prop.getProperty("autoSave");
		}
		
		if (autoSave.equals("true"))
			return true;
		else
			return false;
	}
	
	/**
	 * Sets the user's preference of problem set path.
	 * @param probSet - path to the default problem set.
	 */
	protected static void setProbSet(String probSet)
	{
		BufferedWriter writer;
		try {
			prop.setProperty("recentProbSet", probSet);
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
		} catch (Exception e) {
			Logger.logln(NAME+"Failed setting problem set \""+probSet+"\"", LogOut.STDERR);
		}
	}
	
	/**
	 * Gets the path to the default problem set.
	 * @return probSet - path to the default problem set.
	 */
	protected static String getProbSet() {
		String probSet = "";
		try {
			probSet = prop.getProperty("recentProbSet");
			if (probSet == null) {
				prop.setProperty("recentProbSet", defaultProbSet);
				probSet = prop.getProperty("recentProbSet");
			}
		} catch (NullPointerException e) {
			prop.setProperty("recentProbSet", defaultProbSet);
			probSet = prop.getProperty("recentProbSet");
		}
		
		return probSet;
	}
	
	/**
	 * Sets the user's feature preference that will load up on start up.
	 * @param feature - name of the desired feature.
	 */
	protected static void setFeature(String feature) {
		// saves the currently selected feature to the properties file
		BufferedWriter writer;
		try {
			prop.setProperty("recentFeat", feature);
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the user's feature preference.
	 * @return feature - name of the default feature.
	 */
	protected static String getFeature() {
		String feature = "";
		try {
			feature = prop.getProperty("recentFeat");
			if (feature == null) {
				prop.setProperty("recentFeat", defaultFeat);
				feature = prop.getProperty("recentFeat");
			}
		} catch (NullPointerException e) {
			Logger.logln(NAME+"RecentFeat not set, default value \"" + defaultFeat + "\" used", LogOut.STDOUT);
			prop.setProperty("recentFeat", defaultFeat);
			feature = prop.getProperty("recentFeat", defaultFeat);
		}
		
		return feature;
	}
	
	/**
	 * Sets the user's classifier preference that will load up on start up.
	 * @param classifier - name of the desired classifier
	 */
	protected static void setClassifier(String classifier) {
		// saves the currently selected classifier to the properties file
		BufferedWriter writer;
		try {
			prop.setProperty("recentClass", classifier);
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the user's classifier preference.
	 * @return classifier - name of the default classifier.
	 */
	protected static String getClassifier() {
		String classifier = "";
		try {
			classifier = prop.getProperty("recentClass");
			if (classifier == null) {
				prop.setProperty("recentClass", defaultClass);
				classifier = prop.getProperty("recentClass");
			}
		} catch (NullPointerException e) {
			Logger.logln(NAME+"RecentClass not set, default value \"" + defaultClass + "\" used", LogOut.STDOUT);
			prop.setProperty("recentClass", defaultClass);
			classifier = prop.getProperty("recentClass", defaultClass);
		}
		
		return classifier;
	}
	
	/**
	 * Sets the location of the documents tab
	 * @param location - Should only use LEFT, TOP, or RIGHT
	 */
	protected static void setDocumentsTabLocation(Location location)
	{
		// saves the path of the file chosen in the properties file
		BufferedWriter writer;
		try {
			prop.setProperty("documentsTabLocation", "" + location.strRep);
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Gets the location of the translations tab (default location also set here)
	 */
	protected static Location getDocumentsTabLocation()
	{
		String location = "";
		try {
			location = prop.getProperty("documentsTabLocation");
			if (location == null)
			{
				prop.setProperty("documentsTabLocation", DEFAULT_LOCATIONS[0]); // top
				location = prop.getProperty("documentsTabLocation");
			}
		} catch (NullPointerException e) {
			prop.setProperty("documentsTabLocation", DEFAULT_LOCATIONS[0]);
			location = prop.getProperty("documentsTabLocation");
		}
		return stringToLocation(location);
	}
	
	/**
	 * Sets the location of the clusters tab
	 * @param location - Should only use LEFT, TOP, or RIGHT
	 */
	protected static void setResultsTabLocation(Location location)
	{
		// saves the path of the file chosen in the properties file
		BufferedWriter writer;
		try {
			prop.setProperty("resultsTabLocation", "" + location.strRep);
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Gets the location of the translations tab (default location also set here)
	 */
	protected static Location getResultsTabLocation()
	{
		String location = "";
		try {
			location = prop.getProperty("resultsTabLocation");
			if (location == null)
			{
				prop.setProperty("resultsTabLocation", DEFAULT_LOCATIONS[1]);
				location = prop.getProperty("resultsTabLocation");
			}
		} catch (NullPointerException e) {
			prop.setProperty("resultsTabLocation", DEFAULT_LOCATIONS[1]);
			location = prop.getProperty("resultsTabLocation");
		}
		return stringToLocation(location);
	}
	
	/**
	 * Sets the location of the clusters tab
	 * @param location - Should only use LEFT, TOP, or RIGHT
	 */
	protected static void setClustersTabLocation(Location location)
	{
		// saves the path of the file chosen in the properties file
		BufferedWriter writer;
		try {
			prop.setProperty("clustersTabLocation", "" + location.strRep);
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Gets the location of the translations tab (default location also set here)
	 */
	protected static Location getClustersTabLocation()
	{
		String location = "";
		try {
			location = prop.getProperty("clustersTabLocation");
			if (location == null)
			{
				prop.setProperty("clustersTabLocation", DEFAULT_LOCATIONS[1]); // left
				location = prop.getProperty("clustersTabLocation");
			}
		} catch (NullPointerException e) {
			prop.setProperty("clustersTabLocation", DEFAULT_LOCATIONS[1]);
			location = prop.getProperty("clustersTabLocation");
		}
		return stringToLocation(location);
	}
	
	/**
	 * Sets the location of the preprocess tab
	 * @param location - Should only use LEFT, TOP, or RIGHT
	 */
	protected static void setPreProcessTabLocation(Location location)
	{
		// saves the path of the file chosen in the properties file
		BufferedWriter writer;
		try {
			prop.setProperty("preProcessTabLocation", "" + location.strRep);
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Gets the location of the translations tab (default location also set here)
	 */
	protected static Location getPreProcessTabLocation()
	{
		String location = "";
		try {
			location = prop.getProperty("preProcessTabLocation");
			if (location == null)
			{
				prop.setProperty("preProcessTabLocation", DEFAULT_LOCATIONS[2]); // right
				location = prop.getProperty("preProcessTabLocation");
			}
		} catch (NullPointerException e) {
			prop.setProperty("preProcessTabLocation", DEFAULT_LOCATIONS[2]);
			location = prop.getProperty("preProcessTabLocation");
		}
		return stringToLocation(location);
	}
	
	/**
	 * Sets the location of the suggestions tab
	 * @param location - Should only use LEFT, TOP, or RIGHT
	 */
	protected static void setSuggestionsTabLocation(Location location)
	{
		// saves the path of the file chosen in the properties file
		BufferedWriter writer;
		try {
			prop.setProperty("suggestionsTabLocation", "" + location.strRep);
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Gets the location of the translations tab (default location also set here)
	 */
	protected static Location getSuggestionsTabLocation()
	{
		String location = "";
		try {
			location = prop.getProperty("suggestionsTabLocation");
			if (location == null)
			{
				prop.setProperty("suggestionsTabLocation", DEFAULT_LOCATIONS[2]); // right
				location = prop.getProperty("suggestionsTabLocation");
			}
		} catch (NullPointerException e) {
			prop.setProperty("suggestionsTabLocation", DEFAULT_LOCATIONS[2]);
			location = prop.getProperty("preProcessTabLocation");
		}
		return stringToLocation(location);
	}
	
	/**
	 * Sets the location of the translations tab
	 * @param location - Should only use LEFT, TOP, or RIGHT
	 */
	protected static void setTranslationsTabLocation(Location location)
	{
		// saves the path of the file chosen in the properties file
		BufferedWriter writer;
		try {
			prop.setProperty("translationsTabLocation", "" + location.strRep);
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Gets the location of the translations tab (default location also set here)
	 */
	protected static Location getTranslationsTabLocation()
	{
		String location = "";
		try {
			location = prop.getProperty("translationsTabLocation");
			if (location == null)
			{
				prop.setProperty("translationsTabLocation", DEFAULT_LOCATIONS[2]); // right
				location = prop.getProperty("translationsTabLocation");
			}
		} catch (NullPointerException e) {
			prop.setProperty("translationsTabLocation", DEFAULT_LOCATIONS[2]);
			location = prop.getProperty("translationsTabLocation");
		}
		return stringToLocation(location);
	}
	
	/**
	 * Sets the location of the clusters tab
	 * @param location - Should only use LEFT, TOP, or RIGHT
	 */
	protected static void setAnonymityTabLocation(Location location)
	{
		// saves the path of the file chosen in the properties file
		BufferedWriter writer;
		try {
			prop.setProperty("anonymousTabLocation", "" + location.strRep);
			writer = new BufferedWriter(new FileWriter(propFileName));
			prop.store(writer, "User Preferences");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Gets the location of the translations tab (default location also set here)
	 */
	protected static Location getAnonymityTabLocation()
	{
		String location = "";
		try {
			location = prop.getProperty("anonymousTabLocation");
			if (location == null)
			{
				prop.setProperty("anonymousTabLocation", DEFAULT_LOCATIONS[1]); // left
				location = prop.getProperty("anonymousTabLocation");
			}
		} catch (NullPointerException e) {
			prop.setProperty("anonymousTabLocation", DEFAULT_LOCATIONS[1]);
			location = prop.getProperty("anonymousTabLocation");
		}
		return stringToLocation(location);
	}
}
