package edu.drexel.psal.anonymouth.gooie;

import edu.drexel.psal.anonymouth.engine.Attribute;
import edu.drexel.psal.anonymouth.engine.DataAnalyzer;
import edu.drexel.psal.anonymouth.engine.DocumentMagician;
import edu.drexel.psal.anonymouth.engine.FeatureList;
import edu.drexel.psal.anonymouth.utils.ConsolidationStation;
import edu.drexel.psal.anonymouth.utils.TaggedDocument;
import edu.drexel.psal.anonymouth.utils.TaggedSentence;
import edu.drexel.psal.jstylo.generics.Logger;
import edu.drexel.psal.jstylo.generics.Logger.LogOut;

import edu.drexel.psal.jstylo.GUI.DocsTabDriver.ExtFilter;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
/**
 * editorTabDriver does the work for the editorTab (Editor) in the main GUI (GUIMain)
 * @author Andrew W.E. McDonald
 * @author Marc Barrowclift
 * @author Joe Muoio
 * 
 */
public class DriverDocumentsTab {
	
	private final static String NAME = "( DriverDocumentsTab ) - ";
	
//	public final static int UNDOCHARACTERBUFFER = 5;
//	public static int currentCharacterBuffer = 0;
	
	public static boolean isUsingNineFeatures = false;
	protected static boolean hasBeenInitialized = false;
	protected static String[] condensedSuggestions;
	protected static int numEdits = 0;
	protected static boolean isFirstRun = true; 
	protected static DataAnalyzer wizard;
	private static DocumentMagician magician;
	protected static String[] theFeatures;
	protected static ArrayList<HighlightMapper> highlightedObjects = new ArrayList<HighlightMapper>();
	public static int resultsMaxIndex;
	public static Object maxValue;
	public static String chosenAuthor = "n/a";
	protected static Attribute currentAttrib;
	public static boolean hasCurrentAttrib = false;
	public static boolean isWorkingOnUpdating = false;
	// It seems redundant to have these next four variables, but they are used in slightly different ways, and are all necessary.
	public static int currentCaretPosition = -1;
	public static int startSelection = -1;
	public static int oldStartSelection = -1;
	public static int endSelection = -1;
	public static int oldEndSelection = -1;
//	private static int lastCaretPosition = -1;
	private static int thisKeyCaretPosition = -1;
	private static int lastKeyCaretPosition = -1;
	protected static boolean okayToSelectSuggestion = false;
	private static boolean keyJustTyped = false;
//	private static boolean keyJustPressed = false;
//	private static int mouseEndPosition;
//	private static boolean checkForMouseInfluence =false;
	protected static int selectedIndexTP;
	protected static int sizeOfCfd;
	protected static boolean consoleDead = true;
	protected static boolean dictDead = true;
	protected static ArrayList<String> featuresInCfd;
	protected static String selectedFeature;
	protected static boolean shouldReset = false;
	protected static boolean isCalcHist = false;
	protected static ArrayList<FeatureList> noCalcHistFeatures;
	protected static ArrayList<FeatureList> yesCalcHistFeatures;
	protected static String searchBoxInputText;
	public static Attribute[] attribs;
	public static HashMap<FeatureList,Integer> attributesMappedByName;
	public static HashMap<Integer,Integer> suggestionToAttributeMap;
	protected static ConsolidationStation consolidator;
	
	private static String cleanWordRegex=".*([\\.,!?])+";//REFINE THIS??

	private static final Color HILIT_COLOR = new Color(255,0,0,100);//Color.yellow; //new Color(50, 161,227);// Color.blue;
	protected static DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(new Color(255,255,0,128));
	protected static DefaultHighlighter.DefaultHighlightPainter painterRemove = new DefaultHighlighter.DefaultHighlightPainter(HILIT_COLOR);
	protected static DefaultHighlighter.DefaultHighlightPainter painterAdd = new DefaultHighlighter.DefaultHighlightPainter(new Color(0,255,0,128));

	protected static Translation translator = new Translation();
	
	public static TaggedDocument taggedDoc;
	protected static Map<String, TaggedSentence> originals = new HashMap<String, TaggedSentence>();
	protected static ArrayList<String> originalSents = new ArrayList<String>();
	public static int currentSentNum = 0;
	protected static int lastSentNum = -1;
	protected static int sentToTranslate = 0;
	public static int[] selectedSentIndexRange = new int[]{-2,-2}; 
	protected static int[] lastSelectedSentIndexRange = new int[]{-3,-3};
	protected static int lastCaretLocation = -1;
	protected static int charsInserted = -1;
	protected static int charsRemoved = -1;
	protected static String currentSentenceString = "";
	protected static Object currentHighlight = null;
	protected static int ignoreNumActions = 0;
	protected static int caretPositionPriorToCharInsertion = 0;
	protected static int caretPositionPriorToCharRemoval = 0;
	protected static int caretPositionPriorToAction = 0;
	public static Boolean firstRun = true;
	public static int[] oldSelectionInfo = new int[3];
	protected static Map<String, int[]> wordsToRemove = new HashMap<String, int[]>();
	
	protected static SuggestionCalculator suggestionCalculator;
	protected static Boolean shouldUpdate = false;
	protected static Boolean EOSPreviouslyRemoved = false;
	protected static Boolean EOSesRemoved = false;
	protected static Boolean changedCaret = false;
	protected static String newLine = System.lineSeparator();
	public static Boolean deleting = false;
	protected static Boolean charsWereInserted = false;
	protected static Boolean charsWereRemoved = false;
	public static Boolean EOSJustRemoved = false;
	public static int[] leftSentInfo = new int[0];
	public static int[] rightSentInfo = new int[0];
	private static boolean translate = false;
	protected static ActionListener saveAsTestDoc;
	protected static Object lock = new Object();
	private static boolean wholeLastSentDeleted = false;
	private static boolean wholeBeginningSentDeleted = false;
	public static boolean skipDeletingEOSes = false;
	public static boolean ignoreVersion = false;
	public static TaggedDocument backedUpTaggedDoc;
	
	public static int getCurrentSentNum(){
		return currentSentNum;
	}
	
	protected static void doTranslations(ArrayList<TaggedSentence> sentences, GUIMain main)
	{
		GUIMain.GUITranslator.load(sentences);
	}
	
	
	protected static boolean checkSentFor(String currentSent, String str) 
	{
		@SuppressWarnings("resource")
		Scanner parser = new Scanner(currentSent);
		boolean inSent = false;
		String tempStr;
		while(parser.hasNext())
		{
			tempStr = parser.next();
			if(tempStr.matches(cleanWordRegex))
				tempStr = tempStr.substring(0,tempStr.length()-1);
			
			if(tempStr.equalsIgnoreCase(str))
			{
				inSent = true;
				break;
			}
		}
		return inSent;
	}
	
	/**
	 * Sets all the components within the editor inner tab spawner to disabled, except for the Process button.
	 * @param b boolean determining if the components are enabled or disabled
	 * @param main GUIMain object
	 */
	public static void setAllDocTabUseable(boolean b, GUIMain main) {
		main.saveButton.setEnabled(b);
		main.fileSaveTestDocMenuItem.setEnabled(b);
		main.fileSaveAsTestDocMenuItem.setEnabled(b);
		main.viewClustersMenuItem.setEnabled(b);
		main.elementsToAddPane.setEnabled(b);
		main.elementsToAddPane.setFocusable(b);
		main.elementsToRemovePane.setEnabled(b);
		main.elementsToRemovePane.setFocusable(b);
		main.getDocumentPane().setEnabled(b);
		main.clipboard.setEnabled(b);
	}
	
	/**
	 * Removes the sentence at index sentenceNumberToRemove in the current TaggedDocument, and replaces it with the sentenceToReplaceWith.
	 * Then, converts the updated TaggedDocument to a string, and puts the new version in the editor window.
	 * @param main
	 * @param sentenceNumberToRemove
	 * @param sentenceToReplaceWith
	 * @param shouldUpdate if true, it replaces the text in the JTextPane (documentPane) with the text in the TaggedDocument (taggedDoc).
	 */
	protected static void removeReplaceAndUpdate(GUIMain main, int sentenceNumberToRemove, String sentenceToReplaceWith, boolean shouldUpdate) {
		taggedDoc.removeAndReplace(sentenceNumberToRemove, sentenceToReplaceWith);
		System.out.println("   To Remove = \"" + taggedDoc.getSentenceNumber(sentenceNumberToRemove).getUntagged(false) + "\"");
		System.out.println("   To Add = \"" + sentenceToReplaceWith + "\"");

		/**
		 * We must do this AFTER creating the new tagged sentence so that the translations are attached to the most recent tagged sentence, not the old
		 * one that was replaced. 
		 */
		if (translate) {
			translate = false;
			GUIMain.GUITranslator.replace(taggedDoc.getSentenceNumber(oldSelectionInfo[0]), originals.get(originalSents.get(oldSelectionInfo[0])));//new old
			main.anonymityDrawingPanel.updateAnonymityBar();
			originals.remove(originalSents.get(oldSelectionInfo[0]));
			originals.put(taggedDoc.getSentenceNumber(oldSelectionInfo[0]).getUntagged(false), taggedDoc.getSentenceNumber(oldSelectionInfo[0]));
			originalSents.remove(oldSelectionInfo[0]);
			originalSents.add(taggedDoc.getSentenceNumber(oldSelectionInfo[0]).getUntagged(false));
			SuggestionCalculator.placeSuggestions(main);
		}
		
		if (shouldUpdate) {
			ignoreNumActions = 3;
			main.getDocumentPane().setText(taggedDoc.getUntaggedDocument(false)); // NOTE should be false after testing!!!
			main.getDocumentPane().getCaret().setDot(caretPositionPriorToAction);
			main.getDocumentPane().setCaretPosition(caretPositionPriorToAction);
			ignoreNumActions = 0;
		}

		int[] selectionInfo = calculateIndicesOfSentences(currentCaretPosition)[0];
		currentSentNum = selectionInfo[0];
		selectedSentIndexRange[0] = selectionInfo[1]; //start highlight
		selectedSentIndexRange[1] = selectionInfo[2]; //end highlight
		moveHighlight(main,selectedSentIndexRange);
		main.anonymityDrawingPanel.updateAnonymityBar();
	}
	
	/**
	 * Does the same thing as <code>removeReplaceAndUpdate</code>, except it doesn't remove and replace. 
	 * It simply updates the text editor box with the contents of <code>taggedDoc</code>,
	 * sets the caret to <code>caretPositionPriorToCharInsertion</code>,
	 * and moves the highlight the sentence that the caret has been moved to.
	 * @param main
	 * @param shouldUpdate
	 */
	public static void update(GUIMain main, Boolean shouldUpdate) {
		if (shouldUpdate) {
			ignoreNumActions = 3;
			main.getDocumentPane().setText(taggedDoc.getUntaggedDocument(false));
			main.getDocumentPane().getCaret().setDot(caretPositionPriorToCharInsertion);
			main.getDocumentPane().setCaretPosition(caretPositionPriorToCharInsertion);	
		}
		
		int[] selectionInfo = calculateIndicesOfSentences(caretPositionPriorToCharInsertion)[0];
		currentSentNum = selectionInfo[0];
		selectedSentIndexRange[0] = selectionInfo[1]; //start highlight
		selectedSentIndexRange[1] = selectionInfo[2]; //end highlight

		moveHighlight(main,selectedSentIndexRange);
		main.anonymityDrawingPanel.updateAnonymityBar();
	}

	/**
	 * resets the highlight to a new start and end.
	 * @param main 
	 * @param start
	 * @param end
	 */
	protected static void moveHighlight(final GUIMain main, int[] bounds) {
		if (main.getDocumentPane().getCaret().getDot() != main.getDocumentPane().getCaret().getMark()) {
			main.getDocumentPane().getHighlighter().removeHighlight(currentHighlight);
			return;
		}
		
		if (currentHighlight != null)
			main.getDocumentPane().getHighlighter().removeHighlight(currentHighlight);
		try {
			System.out.printf("Moving highlight to %d to %d\n", bounds[0],bounds[1]);
			if ((selectedSentIndexRange[0] != currentCaretPosition || currentSentNum == 0) || deleting) { //if the user is not selecting a sentence, don't highlight it.
				if (main.getDocumentPane().getText().substring(bounds[0], bounds[0]+2).contains(newLine)) { // if the sentence is preceded by a newline, we need to modify this a bit
					int temp = 0;
					while (main.getDocumentPane().getText().substring(selectedSentIndexRange[0]+temp, selectedSentIndexRange[0]+1+temp).equals(newLine))
						temp++;

					if (selectedSentIndexRange[0]+temp <= currentCaretPosition) { //If the user is actually selecting the sentence
						currentHighlight = main.getDocumentPane().getHighlighter().addHighlight(bounds[0]+temp, bounds[1], painter);
					}
				} else {
					int temp = 0;
					//if (main.getDocumentPane().getText().substring(selectedSentIndexRange[0], selectedSentIndexRange[0]+1).equals(" "))
					//	temp++;
					while (main.getDocumentPane().getText().substring(selectedSentIndexRange[0]+temp, selectedSentIndexRange[0]+1+temp).equals(" ")) //we want to not highlight whitespace before the actual sentence.
						temp++;

					if (selectedSentIndexRange[0]+temp <= currentCaretPosition || firstRun) {
						currentHighlight = main.getDocumentPane().getHighlighter().addHighlight(bounds[0]+temp, bounds[1], painter);
					}
				}
			} 
		} catch (BadLocationException err) {
			err.printStackTrace();
		}
		synchronized(lock) {
			lock.notify();
		}
	}

	/**
	 * Calcualtes the selected sentence number (index in TaggedDocument taggedDoc), start of that sentence in the documentPane, and end of the sentence in the documentPane. 
	 * Returns all three values in an int array.
	 * @param currentCaretPosition the positions in the document to return sentence indices for
	 * @return a 2d int array such that each row is an array such that: index 0 is the sentence index, index 1 is the beginning of the sentence (w.r.t. the whole document in the editor), and index 2 is the end of the sentence.
	 * {sentenceNumber, startHighlight, endHighlight} (where start and end Highlight are the starting and ending indices of the selected sentence). The rows correspond to the order of the input indices
	 * 
	 * If 'currentCaretPosition' is past the end of the document (greater than the number of characters in the document), then "null" will be returned.
	 */
	public static int[][] calculateIndicesOfSentences(int ... positions){
		// get the lengths of each of the sentences
		int[] sentenceLengths = taggedDoc.getSentenceLengths();
		int numSents = sentenceLengths.length;
		int positionNumber;
		int numPositions = positions.length;
		int currentPosition;
		int[][] results = new int[numPositions][3];
		
		for (positionNumber = 0; positionNumber < numPositions; positionNumber++) {
			int i = 0;
			int lengthSoFar = 0;
			int[] lengthTriangle = new int[numSents]; // index '0' will be the length of sentence 0, index '1' will be the length of sentence '0' plus sentence '1', index '2' will be the lengths of the first three sentences added together, and so on. 
			int selectedSentence = 0;
			currentPosition = positions[positionNumber];
			if (currentPosition > 0) {
				while (lengthSoFar <= currentPosition && i < numSents) { //used to be i <= numSents, but since the sentence indexes are 0 based, it should be i < numSents
					lengthSoFar += sentenceLengths[i];
					lengthTriangle[i] = lengthSoFar;
					i++;
				}
				selectedSentence = i - 1;// after exiting the loop, 'i' will be one greater than we want it to be.
			}

			int startHighlight = 0;
			int endHighlight = 0;
			if (selectedSentence >= numSents)
				return null; // don't do anything.
			else if (selectedSentence <= 0)
				endHighlight = sentenceLengths[0];				
			else {
				startHighlight = lengthTriangle[selectedSentence-1]; // start highlighting JUST after the previous sentence stops
				endHighlight = lengthTriangle[selectedSentence]; // stop highlighting when the current sentence stops.
			}	
			results[positionNumber] = new int[]{selectedSentence, startHighlight, endHighlight};
		}
		
		//Checking to see if the user's deletion includes deleting a whole sentence at both the end of their selection and the beginning of their selection.
		//This is because we need to handle the deletion process different depending on which one is true or false.
		if (results.length > 1) {
			if ((results[1][0] - results[0][0]) >= 4 && (results[1][2] == oldStartSelection + (results[1][2] - results[1][1]) || results[1][2] == oldEndSelection + (results[1][2] - results[1][1])))
				wholeLastSentDeleted = true;
			if (results[1][0] - results[0][0] >= 4 && (results[0][2] == oldStartSelection + (results[0][2] - results[0][1]) || results[0][2] == oldEndSelection + (results[0][2] - results[0][1])))
				wholeBeginningSentDeleted = true;
		}
		
		return results; 
	}


	private static void displayEditInfo(DocumentEvent e) {
		javax.swing.text.Document document = (javax.swing.text.Document) e.getDocument();
		int changeLength = e.getLength();
		System.out.println(e.getType().toString() + ": " + changeLength + " character(s). Text length = " + document.getLength() + ".");
	}

	protected static void initListeners(final GUIMain main) {

		/***********************************************************************************************************************************************
		 *############################################################################################################*
		 *###########################################  BEGIN EDITING HANDLERS  ###########################################*
		 *############################################################################################################*
		 ***********************************************************************************************************************************************/	

		suggestionCalculator = new SuggestionCalculator();
		
		/*
		 * 
		 * xxx xxx xxx xxx xxx xxx xxx
		 * todo todo intercept keys with document thing
		 * xxx xxx xxx xxx xxx xxx xxx
		 * 
		 */

		main.getDocumentPane().addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				System.out.println("======================================================================================");
				if (ignoreNumActions > 0) {
					charsInserted = 0;
					charsWereRemoved = false;
					charsWereInserted = false;
					charsRemoved = 0;
					ignoreNumActions--;
				} else if (taggedDoc != null) { //main.documentPane.getText().length() != 0
					boolean setSelectionInfoAndHighlight = true;
					startSelection = e.getDot();
					endSelection = e.getMark();
					currentCaretPosition = startSelection;
					int[] currentSentSelectionInfo = null;
					caretPositionPriorToCharInsertion = currentCaretPosition - charsInserted;
					caretPositionPriorToCharRemoval = currentCaretPosition + charsRemoved;
					
					if (charsRemoved > 0) {	
						caretPositionPriorToAction = caretPositionPriorToCharRemoval;
						// update the EOSTracker, and from the value that it returns we can tell if sentences are being merged (EOS characters are being erased)
						
						/**
						 * We must subtract all the indices by 1 because the InputFilter indices refuses to work with anything other than - 1, and as such
						 * the indices here and in TaggedDocument must be adjustest as well.
						 */						
						
						if (skipDeletingEOSes) {
							skipDeletingEOSes = false;
						} else {
							EOSJustRemoved = taggedDoc.specialCharTracker.removeEOSesInRange( currentCaretPosition-1, caretPositionPriorToCharRemoval-1);
						}
						
						if (EOSJustRemoved) {
							try {
								// note that 'currentCaretPosition' will always be less than 'caretPositionPriorToCharRemoval' if characters were removed!
								int[][] activatedSentenceInfo = calculateIndicesOfSentences(currentCaretPosition, caretPositionPriorToCharRemoval);
								int i;
								int j = 0;
								leftSentInfo = activatedSentenceInfo[0];
								rightSentInfo = activatedSentenceInfo[1];

								if (rightSentInfo[0] != leftSentInfo[0]) {
									int numToDelete = rightSentInfo[0] - (leftSentInfo[0]+1); // add '1' because we don't want to count the lower bound (e.g. if midway through sentence '6' down to midway through sentence '3' was deleted, we want to delete "6 - (3+1) = 2" TaggedSentences. 
									int[] taggedSentsToDelete;
									
									// Now we list the indices of sentences that need to be removed, which are the ones between the left and right sentence (though not including either the left or the right sentence).
									if (wholeLastSentDeleted) {
										//We want to ignore the rightmost sentence from our deletion process since we didn't actually delete anything from it.
										taggedSentsToDelete = new int[numToDelete-1];
										for (i = (leftSentInfo[0] + 1); i < rightSentInfo[0]-1; i++) { 
											taggedSentsToDelete[j] = leftSentInfo[0] + 1;
											j++;
										}
									} else {
										//We want to include the rightmost sentence in our deletion process since we are partially deleting some of it.
										taggedSentsToDelete = new int[numToDelete];
										for (i = (leftSentInfo[0] + 1); i < rightSentInfo[0]; i++) { 
											taggedSentsToDelete[j] = leftSentInfo[0] + 1;
											j++;
										}
									}

									//First delete every sentence the user has deleted wholly (there's no extra concatenation or tricks, just delete). 
									taggedDoc.removeTaggedSentences(taggedSentsToDelete);
									System.out.println(NAME+"Ending whole sentence deletion, now handling left and right (if available) deletion");
									
									// Then read the remaining strings from "left" and "right" sentence:
										// for left: read from 'leftSentInfo[1]' (the beginning of the sentence) to 'currentCaretPosition' (where the "sentence" now ends)
										// for right: read from 'caretPositionPriorToCharRemoval' (where the "sentence" now begins) to 'rightSentInfo[2]' (the end of the sentence) 
									// Once we have the string, we call removeAndReplace, once for each sentence (String)
									String docText = main.getDocumentPane().getText();
									String leftSentCurrent = docText.substring(leftSentInfo[1],currentCaretPosition);
									taggedDoc.removeAndReplace(leftSentInfo[0], leftSentCurrent);
									//Needed so that we don't delete more than we should if that be the case
									//TODO integrate this better with wholeLastSentDeleted and wholeBeginningSentDeleted so it's cleaner, right now this is pretty sloppy and confusing
									if (TaggedDocument.userDeletedSentence) {
										rightSentInfo[0] = rightSentInfo[0]-1;
									}
									String rightSentCurrent = docText.substring((caretPositionPriorToCharRemoval-charsRemoved), (rightSentInfo[2]-charsRemoved));//we need to shift our indices over by the number of characters removed.
									
									if (wholeLastSentDeleted && wholeBeginningSentDeleted) {
										wholeLastSentDeleted = false;
										wholeBeginningSentDeleted = false;
										taggedDoc.removeAndReplace(leftSentInfo[0], "");
									} else if (wholeLastSentDeleted && !wholeBeginningSentDeleted) {
										wholeLastSentDeleted = false;
										taggedDoc.removeAndReplace(leftSentInfo[0]+1, "");
										taggedDoc.concatRemoveAndReplace(taggedDoc.getTaggedDocument().get(leftSentInfo[0]),leftSentInfo[0], taggedDoc.getTaggedDocument().get(leftSentInfo[0]+1), leftSentInfo[0]+1);
									} else {
										try {
											taggedDoc.removeAndReplace(leftSentInfo[0]+1, rightSentCurrent);
											taggedDoc.concatRemoveAndReplace(taggedDoc.getTaggedDocument().get(leftSentInfo[0]),leftSentInfo[0], taggedDoc.getTaggedDocument().get(leftSentInfo[0]+1), leftSentInfo[0]+1);
										} catch (Exception e1) {
											taggedDoc.removeAndReplace(leftSentInfo[0], rightSentCurrent);
										}
									}
																
									// Now that we have internally gotten rid of the parts of left and right sentence that no longer exist in the editor box, we merge those two sentences so that they become a single TaggedSentence.
									TaggedDocument.userDeletedSentence = false;
								}
							} catch (Exception e1) {
								JOptionPane.showOptionDialog(null,
										"Anonymouth has encountered an internal problem\nprocessing your most recent action.\n\nWe are aware of and working on the issue,\nand we apologize for any inconvenience.\nPlease re-process to fix the editor.",
										"Editor Failure",
										JOptionPane.DEFAULT_OPTION,
										JOptionPane.WARNING_MESSAGE,
										UIManager.getIcon("OptionPane.warningIcon"),
										null,
										null);
								return;
							}
							
							// now update the EOSTracker
							taggedDoc.specialCharTracker.shiftAllEOSChars(false, caretPositionPriorToCharRemoval, charsRemoved);
							
							// Then update the currentSentSelectionInfo, and fix variables
							currentSentSelectionInfo = calculateIndicesOfSentences(currentCaretPosition)[0];
							currentSentNum = currentSentSelectionInfo[0];
							selectedSentIndexRange[0] = currentSentSelectionInfo[1];
							selectedSentIndexRange[1] = currentSentSelectionInfo[2];
							
							// Now set the number of characters removed to zero because the action has been dealt with, and we don't want the statement further down to execute and screw up our indices. 
							charsRemoved = 0; 
							EOSJustRemoved = false;
						} else {
							// update the EOSTracker
							taggedDoc.specialCharTracker.shiftAllEOSChars(false, caretPositionPriorToAction, charsRemoved);
						}
					} else if (charsInserted > 0) {
						caretPositionPriorToAction = caretPositionPriorToCharInsertion;
						// update the EOSTracker. First shift the current EOS objects, and then create a new one 
						taggedDoc.specialCharTracker.shiftAllEOSChars(true, caretPositionPriorToAction, charsInserted);	
					} else {
						caretPositionPriorToAction = currentCaretPosition;
					}
					
					// Then update the selection information so that when we move the highlight, it highlights "both" sentences (well, what used to be both sentences, but is now a single sentence)
					try {
						//Try-catch in place just in case the user tried clicking on an area that does not contain sentences.
						currentSentSelectionInfo = calculateIndicesOfSentences(currentCaretPosition)[0];
					} catch (ArrayIndexOutOfBoundsException exception) {
						return;
					}
					
					if (currentSentSelectionInfo == null)
						return; // don't do anything.
					
					lastSentNum = currentSentNum;
					currentSentNum = currentSentSelectionInfo[0];

					boolean inRange = false;
					
					//check to see if the current caret location is within the selectedSentIndexRange ([0] is min, [1] is max)
					if ( caretPositionPriorToAction >= selectedSentIndexRange[0] && caretPositionPriorToAction < selectedSentIndexRange[1]) {
						inRange = true;
						// Caret is inside range of presently selected sentence.
						// update from previous caret
						if (charsInserted > 0 ) {// && lastSentNum != -1){
							selectedSentIndexRange[1] += charsInserted;
							charsInserted = ~-1; // puzzle: what does this mean? (scroll to bottom of file for answer) - AweM
							charsWereInserted = true;
							charsWereRemoved = false;
						} else if (charsRemoved > 0) {// && lastSentNum != -1){
							selectedSentIndexRange[1] -= charsRemoved;
							charsRemoved = 0;
							charsWereRemoved = true;
							charsWereInserted = false;
						}
					} else if (!firstRun) {
						/**
						 * Yet another thing that seems somewhat goofy but serves a distinct and important purpose. Since we're supposed to wait in InputFilter
						 * when the user types an EOS character since they may type more and we're not sure yet if they are actually done with the sentence, nothing
						 * will be removed, replaced, or updated until we have confirmation that it's the end of the sentence. This means that if they click/move away from the
						 * sentence after typing a period INSTEAD of finishing the sentence with a space or continuing the EOS characters, the sentence replacement will get
						 * all screwed up. This is to ensure that no matter what, when a sentence is created and we know it's a sentence it gets processed.
						 */
						if (changedCaret && InputFilter.isEOS) {
							InputFilter.isEOS = false;
							changedCaret = false;
							shouldUpdate = true;

							/**
							 * Exists for the sole purpose of pushing a sentence that has been edited and finished to the appropriate place in
							 * The Translation.java class so that it can be promptly translated. This will ONLY happen when the user has clicked
							 * away from the sentence they were editing to work on another one (the reason behind this being we don't want to be
							 * constantly pushing now sentences to be translated is the user's immediately going to replace them again, we only
							 * want to translate completed sentences).
							 */
							if (!originals.keySet().contains(main.getDocumentPane().getText().substring(selectedSentIndexRange[0],selectedSentIndexRange[1])))
								translate = true;
						}
					}
					
					// selectionInfo is an int array with 3 values: {selectedSentNum, startHighlight, endHighlight}
					
					// xxx todo xxx get rid of this check (if possible... BEI sets the selectedSentIndexRange)....
					if (firstRun) { //NOTE needed a way to make sure that the very first time a sentence is clicked (, we didn't break stuff... this may not be the best way...
						firstRun = false;
					} else {
						lastSelectedSentIndexRange[0] = selectedSentIndexRange[0];
						lastSelectedSentIndexRange[1] = selectedSentIndexRange[1];
						currentSentenceString = main.getDocumentPane().getText().substring(lastSelectedSentIndexRange[0],lastSelectedSentIndexRange[1]);

						if (!taggedDoc.getSentenceNumber(lastSentNum).getUntagged(false).equals(currentSentenceString)) {
							main.anonymityDrawingPanel.updateAnonymityBar();
							setSelectionInfoAndHighlight = false;
							GUIMain.saved = false;
						}
						
						if ((currentCaretPosition-1 != lastCaretLocation && !charsWereRemoved && charsWereInserted) || (currentCaretPosition != lastCaretLocation-1) && !charsWereInserted && charsWereRemoved) {
							charsWereInserted = false;
							charsWereRemoved = false;
							shouldUpdate = true;

							/**
							 * Exists for the sole purpose of pushing a sentence that has been edited and finished to the appropriate place in
							 * The Translation.java class so that it can be promptly translated. This will ONLY happen when the user has clicked
							 * away from the sentence they were editing to work on another one (the reason behind this being we don't want to be
							 * constantly pushing now sentences to be translated is the user's immediately going to replace them again, we only
							 * want to translate completed sentences).
							 */
							if (!originals.keySet().contains(main.getDocumentPane().getText().substring(selectedSentIndexRange[0],selectedSentIndexRange[1])))
								translate = true;
						}
					}
					
					if (setSelectionInfoAndHighlight) {
						currentSentSelectionInfo = calculateIndicesOfSentences(caretPositionPriorToAction)[0];
						selectedSentIndexRange[0] = currentSentSelectionInfo[1]; //start highlight
						selectedSentIndexRange[1] = currentSentSelectionInfo[2]; //end highlight
						moveHighlight(main,selectedSentIndexRange);
					}

					lastCaretLocation = currentCaretPosition;
					sentToTranslate = currentSentNum;
					if (!inRange) {
						if (shouldUpdate && !ignoreVersion) {
							backedUpTaggedDoc = new TaggedDocument(taggedDoc);
							main.versionControl.addVersion(backedUpTaggedDoc, oldStartSelection);
						}
						DriverTranslationsTab.showTranslations(taggedDoc.getSentenceNumber(sentToTranslate));
					}

					if (shouldUpdate) {
						shouldUpdate = false;
						GUIMain.saved = false;
						removeReplaceAndUpdate(main, lastSentNum, currentSentenceString, false);
					}
					
					oldSelectionInfo = currentSentSelectionInfo;
					oldStartSelection = startSelection;
					oldEndSelection = endSelection;
				}
			}
		});
		
		/**
		 * Key listener for the documentPane. Allows tracking the cursor while typing to make sure that indices of sentence start and ends 
		 */
		main.getDocumentPane().addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_RIGHT ||
						arg0.getKeyCode() == KeyEvent.VK_LEFT ||
						arg0.getKeyCode() == KeyEvent.VK_UP ||
						arg0.getKeyCode() == KeyEvent.VK_DOWN) {
					changedCaret = true;
					main.clipboard.setEnabled(false, false, true);
				}
				if (arg0.getKeyCode() == KeyEvent.VK_BACK_SPACE)
					deleting = true;
				else
					deleting = false;
//				keyJustPressed = true;
				lastKeyCaretPosition = thisKeyCaretPosition;
			}
			
			/*
			 * TODO: make the highlighter and sentences track when people type. think about copy and paste and cut and paste too.
			 */			
			@Override
			public void keyReleased(KeyEvent arg0) {  
				/* 	Code		|	key
				 * ----------------
				 * 		8			|	Backspace
				 *		10			|	Enter
				 *		9			|	Tab
				 *		27			|	Escape
 				 *		32			|	Space
 				 *
 				 * Codes 
				 */
				thisKeyCaretPosition = main.getDocumentPane().getCaretPosition(); // todo maybe we dont need to call for this.. all we might have to do is get the dot position from the CaretListener
				//System.out.println("Caret postion resitered at keyreleased:  "+currentCaretPosition);
				if (keyJustTyped == true) {
					keyJustTyped = false;
//					char keyChar = arg0.getKeyChar();
//					int keyCode = arg0.getKeyCode();
//					int keyLocation = arg0.getKeyLocation();
//					String keyText = KeyEvent.getKeyText(keyCode);
					//System.out.printf("key char: <%c> key code: <%d> key location: <%d> key text: <%s>\n",keyChar, keyCode, keyLocation, keyText);
					//System.out.printf("%c		%d		%d		%s\n",keyChar, keyCode, keyLocation, keyText);
					//System.out.println("Should start present features continuous present value update thread..");
					//BackendInterface.updatePresentFeatureNow(main, eits,theChief);
					//int caretPos =main.editorBox.getCaretPosition();
					//System.out.println("Old Caret Postion: "+oldCaretPosition+" and current CARET POSITION IS: "+currentCaretPosition);
					//Collections.sort(highlightedObjects);
					if (lastKeyCaretPosition < thisKeyCaretPosition) {
						// cursor has advanced 
						Iterator<HighlightMapper> hloi = highlightedObjects.iterator();
						boolean isGone;
						while (hloi.hasNext()) {
							isGone = false;
							HighlightMapper tempHm = hloi.next();
							
							if ((tempHm.getStart() <= thisKeyCaretPosition) && (lastKeyCaretPosition <= tempHm.getEnd())){
								//System.out.println("FOUND object... start at: "+tempHm.getStart()+" end at: "+tempHm.getEnd());
								main.getDocumentPane().getHighlighter().removeHighlight(tempHm.getHighlightedObject());
								isGone = true;
							}	
							
							if ((lastKeyCaretPosition <= tempHm.getStart() && !isGone))
								tempHm.increment(thisKeyCaretPosition - lastKeyCaretPosition);
						}
					} else if(lastKeyCaretPosition > thisKeyCaretPosition) {
						Iterator<HighlightMapper> hloi = highlightedObjects.iterator();
						boolean isGone;
						while (hloi.hasNext()) {
							isGone = false;
							HighlightMapper tempHm = hloi.next();
							
							if ((tempHm.getStart() <= thisKeyCaretPosition) && (thisKeyCaretPosition <= tempHm.getEnd())) {
								//System.out.println("FOUND object ... start at: "+tempHm.getStart()+" end at: "+tempHm.getEnd());
								main.getDocumentPane().getHighlighter().removeHighlight(tempHm.getHighlightedObject());
								isGone = true;
							}
							
							if ((lastKeyCaretPosition <= tempHm.getStart()) && !isGone)
								tempHm.decrement(lastKeyCaretPosition - thisKeyCaretPosition);
						}
					
					}
				}
			}
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				keyJustTyped = true;
			}
		});
		
		main.getDocumentPane().getDocument().addDocumentListener(new DocumentListener() {
		
			@Override
			public void insertUpdate(DocumentEvent e) {
				charsInserted = e.getLength();
				
				if (main.versionControl.isUndoEmpty() && GUIMain.processed && !ignoreVersion) {
					main.versionControl.addVersion(backedUpTaggedDoc, e.getOffset());
					backedUpTaggedDoc = new TaggedDocument(taggedDoc);
				}
				
				if (ignoreVersion || !GUIMain.processed) {
					backedUpTaggedDoc = new TaggedDocument(taggedDoc);
					return;
				}
				
				if (e.getLength() > 1) {
					main.versionControl.addVersion(backedUpTaggedDoc, e.getOffset());
					backedUpTaggedDoc = new TaggedDocument(taggedDoc);
				} else {
					if (InputFilter.shouldBackup) {
						main.versionControl.addVersion(backedUpTaggedDoc, e.getOffset()+1);
						backedUpTaggedDoc = new TaggedDocument(taggedDoc);
					}
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (InputFilter.ignoreDeletion)
					InputFilter.ignoreDeletion = false;
				else
					charsRemoved = e.getLength();
				
				if (main.versionControl.isUndoEmpty() && GUIMain.processed && !ignoreVersion) {
					main.versionControl.addVersion(backedUpTaggedDoc, e.getOffset());
					backedUpTaggedDoc = new TaggedDocument(taggedDoc);
				}
				
				if (ignoreVersion || !GUIMain.processed) {
					backedUpTaggedDoc = new TaggedDocument(taggedDoc);
					return;
				}
				
				if (e.getLength() > 1) {
					main.versionControl.addVersion(backedUpTaggedDoc, e.getOffset());
					backedUpTaggedDoc = new TaggedDocument(taggedDoc);
				} else {
					if (InputFilter.shouldBackup) {
						main.versionControl.addVersion(backedUpTaggedDoc, e.getOffset());
						backedUpTaggedDoc = new TaggedDocument(taggedDoc);
					}
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				DriverDocumentsTab.displayEditInfo(e);
			}
		});	
		
		main.getDocumentPane().addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent me) {
		
			}

			@Override
			public void mousePressed(MouseEvent me) {
				
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				changedCaret = true;
				deleting = false;
				
				if (main.getDocumentPane().getCaret().getDot() == main.getDocumentPane().getCaret().getMark())
					main.clipboard.setEnabled(false, false, true);
				else
					main.clipboard.setEnabled(true);
			}

			@Override
			public void mouseEntered(MouseEvent me) {
				
			}

			@Override
			public void mouseExited(MouseEvent me) {
				
			}
		});
		
/***********************************************************************************************************************************************
 *############################################################################################################*
 *###########################################   END EDITING HANDLERS   ########################################### *
 *############################################################################################################*
 ************************************************************************************************************************************************/
		
		/**
		 * ActionListener for process button (bar).
		 */
		main.processButton.addActionListener(new ActionListener() {
			@Override
			public synchronized void actionPerformed(ActionEvent event) {
				// ----- check if all requirements for processing are met
				String errorMessage = "";
				if (!main.mainDocReady())
					errorMessage += "Main document not provided.\n";
				if (!main.sampleDocsReady())
					errorMessage += "Sample documents not provided.\n";
				if (!main.trainDocsReady())
					errorMessage += "Train documents not provided.\n";
				if (!main.featuresAreReady())
					errorMessage += "Feature set not chosen.\n";
				if (!main.classifiersAreReady())
					errorMessage += "Classifier not chosen.\n";
				
				// ----- display error message if there are errors
				if (errorMessage != "") {
					JOptionPane.showMessageDialog(main, errorMessage, "Settings Error!",
						    JOptionPane.ERROR_MESSAGE);
				} else {
					main.leftTabPane.setSelectedIndex(0);
					// ----- confirm they want to process
					if (true) {// ---- can be a confirm dialog to make sure they want to process.
						setAllDocTabUseable(false, main);
						// ----- if this is the first run, do everything that needs to be ran the first time
						if (isFirstRun) {
							// ----- create the main document and add it to the appropriate array list.
							// ----- may not need the arraylist in the future since you only really can have one at a time
							TaggedDocument taggedDocument = new TaggedDocument();
							ConsolidationStation.toModifyTaggedDocs=new ArrayList<TaggedDocument>();
							ConsolidationStation.toModifyTaggedDocs.add(taggedDocument);
							taggedDoc = ConsolidationStation.toModifyTaggedDocs.get(0);

							Logger.logln(NAME+"Initial processing starting...");
							
							// initialize all arraylists needed for feature processing
							sizeOfCfd = main.cfd.numOfFeatureDrivers();
							featuresInCfd = new ArrayList<String>(sizeOfCfd);
							noCalcHistFeatures = new ArrayList<FeatureList>(sizeOfCfd);
							yesCalcHistFeatures = new ArrayList<FeatureList>(sizeOfCfd);
							
							for(int i = 0; i < sizeOfCfd; i++) {
								String theName = main.cfd.featureDriverAt(i).getName();
								
								// capitalize the name and replace all " " and "-" with "_"
								theName = theName.replaceAll("[ -]","_").toUpperCase(); 
								if(isCalcHist == false) {
									isCalcHist = main.cfd.featureDriverAt(i).isCalcHist();
									yesCalcHistFeatures.add(FeatureList.valueOf(theName));
								} else {
									// these values will go in suggestion list... PLUS any 	
									noCalcHistFeatures.add(FeatureList.valueOf(theName));
								}
								featuresInCfd.add(i,theName);
							}
							wizard = new DataAnalyzer(main.ps);
							magician = new DocumentMagician(false);
						} else {
							//TODO ASK ANDREW: Should we erase the user's "this is a single sentence" actions upon reprocessing? Only only when they reset the highlighter?
							taggedDoc.specialCharTracker.resetEOSCharacters();
							taggedDoc = new TaggedDocument(main.getDocumentPane().getText());
							
							Logger.logln(NAME+"Repeat processing starting....");
							resetAll(main);
						}

						main.getDocumentPane().getHighlighter().removeAllHighlights();
						highlightedObjects.clear();
						highlightedObjects.clear();
						okayToSelectSuggestion = false;
						Logger.logln(NAME+"calling backendInterface for preTargetSelectionProcessing");
						
						BackendInterface.preTargetSelectionProcessing(main,wizard,magician);
						
					}
				}
			}
		});

		saveAsTestDoc = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Logger.logln(NAME+"Save As document button clicked.");
				JFileChooser save = new JFileChooser();
				save.setSelectedFile(new File("anonymizedDoc.txt"));
				save.addChoosableFileFilter(new ExtFilter("txt files (*.txt)", "txt"));
				int answer = save.showSaveDialog(main);

				if (answer == JFileChooser.APPROVE_OPTION) {
					File f = save.getSelectedFile();
					String path = f.getAbsolutePath();
					if (!path.toLowerCase().endsWith(".txt"))
						path += ".txt";
					try {
						BufferedWriter bw = new BufferedWriter(new FileWriter(path));
						bw.write(main.getDocumentPane().getText());
						bw.flush();
						bw.close();
						Logger.log("Saved contents of current tab to "+path);

						GUIMain.saved = true;
					} catch (IOException exc) {
						Logger.logln(NAME+"Failed opening "+path+" for writing",LogOut.STDERR);
						Logger.logln(NAME+exc.toString(),LogOut.STDERR);
						JOptionPane.showMessageDialog(null,
								"Failed saving contents of current tab into:\n"+path,
								"Save Problem Set Failure",
								JOptionPane.ERROR_MESSAGE);
					}
				} else
		            Logger.logln(NAME+"Save As contents of current tab canceled");
			}
		};
		main.saveButton.addActionListener(saveAsTestDoc);
	}

	/**
	 * Resets everything to their default values, to be used before reprocessing
	 * @param main - An instance of GUIMain
	 */
	public static void resetAll(GUIMain main) {
		reset();
		GUIMain.GUITranslator.reset();	
		DriverTranslationsTab.reset();
		main.versionControl.reset();
		main.anonymityDrawingPanel.reset();
		main.resultsWindow.reset();
		GUIUpdateInterface.updateResultsPrepColor(main);
		main.elementsToRemove.removeAllElements();
		main.elementsToRemove.add(0, "Re-processing, please wait");
		main.elementsToAdd.removeAllElements();
		main.elementsToAdd.add(0, "Re-processing, please wait");
	}
	
	public static void reset() {
		currentCaretPosition = -1;
		startSelection = -1;
		endSelection = -1;
		thisKeyCaretPosition = -1;
		lastKeyCaretPosition = -1;
		keyJustTyped = false;
		noCalcHistFeatures.clear();
		yesCalcHistFeatures.clear();

		originals.clear();
		originalSents.clear();
		currentSentNum = 0;
		lastSentNum = -1;
		sentToTranslate = 0;
		selectedSentIndexRange = new int[]{-2,-2}; 
		lastSelectedSentIndexRange = new int[]{-3,-3};
		lastCaretLocation = -1;
		charsInserted = -1;
		charsRemoved = -1;
		currentSentenceString = "";
		ignoreNumActions = 0;
		caretPositionPriorToCharInsertion = 0;
		caretPositionPriorToCharRemoval = 0;
		caretPositionPriorToAction = 0;
		oldSelectionInfo = new int[3];
		wordsToRemove.clear();
	}
	
	public static void save(GUIMain main) {
		Logger.logln(NAME+"Save document button clicked.");

		String path = main.ps.getTestDocs().get(0).getFilePath();

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(path));
			bw.write(main.getDocumentPane().getText());
			bw.flush();
			bw.close();
			Logger.log("Saved contents of document to "+path);

			GUIMain.saved = true;
		} catch (IOException exc) {
			Logger.logln(NAME+"Failed opening "+path+" for writing",LogOut.STDERR);
			Logger.logln(NAME+exc.toString(),LogOut.STDERR);
			JOptionPane.showMessageDialog(null,
					"Failed saving contents of current tab into:\n"+path,
					"Save Problem Set Failure",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static int getSelection(JOptionPane oPane){
		Object selectedValue = oPane.getValue();

		if(selectedValue != null){
			Object options[] = oPane.getOptions();
			if (options == null){
				return ((Integer) selectedValue).intValue();
			}
			else{
				int i;
				int j;
				for(i=0, j= options.length; i<j;i++){
					if(options[i].equals(selectedValue))
						return i;
				}	
			}
		}
		return 0;
	}
} 

class TheHighlighter extends DefaultHighlighter.DefaultHighlightPainter{
	public TheHighlighter(Color color){
		super(color);
	}
}

class SuggestionCalculator {

	private final static String PUNCTUATION = "?!,.\"`'";
	protected static Highlighter editTracker;
	protected static ArrayList<String> topToRemove;
	protected static ArrayList<String> topToAdd;

	/*
	 * Highlights the sentence that is currently in the editor box in the main document
	 * no return
	 */
	protected static void placeSuggestions(GUIMain main) {
		//We must first clear any existing highlights the user has and remove all existing suggestions
		Highlighter highlight = main.getDocumentPane().getHighlighter();
		int highlightedObjectsSize = DriverDocumentsTab.highlightedObjects.size();

		for (int i = 0; i < highlightedObjectsSize; i++)
			highlight.removeHighlight(DriverDocumentsTab.highlightedObjects.get(i).getHighlightedObject());
		DriverDocumentsTab.highlightedObjects.clear();

		main.elementsToRemove.removeAllElements();
		main.elementsToAdd.removeAllElements();

		//Adding new suggestions
		topToRemove=ConsolidationStation.getPriorityWords(ConsolidationStation.toModifyTaggedDocs, true, .2);
		topToAdd=ConsolidationStation.getPriorityWords(ConsolidationStation.authorSampleTaggedDocs, false, .02);
		
		main.elementsToRemove.removeAllElements();

		int arrSize = topToRemove.size();
		int listIndex = 0;
		for (int i=0;i<arrSize;i++) {//loops through top to remove list
			if (!topToRemove.get(i).equals("''") && !topToRemove.get(i).equals("``")) {
				if (PUNCTUATION.contains(topToRemove.get(i).trim()))
					main.elementsToRemove.add(listIndex, "Reduce the number of " + topToRemove.get(i) + "'s you use");
				else
					main.elementsToRemove.add(listIndex, topToRemove.get(i));
				listIndex++;
			}		
		}

		main.elementsToRemovePane.clearSelection();

		main.elementsToAdd.removeAllElements();

		arrSize = topToAdd.size();
		for(int i=0;i<arrSize;i++)
			main.elementsToAdd.add(i, topToAdd.get(i));

		main.elementsToAddPane.clearSelection();
	}



		/**
		 * Finds the synonyms of the words to remove in the words to add list
		 */
//		protected static void findSynonyms(GUIMain main,String currentSent) {
//			String[] tempArr;
//			//addTracker = new DefaultHighlighter();
//			// TODO make new painter!!! (this one doesn't exist) anymore // painter3 = new DefaultHighlighter.DefaultHighlightPainter(new Color(0,0,255,128));
//			String setString,tempStr,synSetString = "";
//			main.addToSentencePane.setHighlighter(addTracker);
//			//addTracker.removeAllHighlights();
//
//			main.elementsToAddPane.repaint();
//
//			setString="";
//			int arrSize=topToAdd.size(), index;
//			main.elementsToAdd.removeAllElements();
//			
//			for(int i=0;i<arrSize;i++){//Sets the topToAddElements box
//				setString+=topToAdd.get(i)+"\n";
//				main.elementsToAdd.add(i, topToAdd.get(i));
//			}
//			main.elementsToAddPane.clearSelection();
//			main.elementsToAddPane.setText(setString);
//			synSetString="";
//			boolean inSent;
//			Scanner parser;
//			HashMap<String,Integer> indexMap=new HashMap<String,Integer>();
//			for(String str:topToRemove){
//				tempArr=DictionaryBinding.getSynonyms(str);
//				if(tempArr!=null){
//					//inSent=currentSent.contains(str);
//					inSent = DriverDocumentsTab.checkSentFor(currentSent,str);
//
//					if(inSent)
//						synSetString+=str+"=>";
//					for(int i=0;i<tempArr.length;i++){//looks through synonyms
//						tempStr=tempArr[i];
//						if(inSent){
//							synSetString+=tempStr+", ";
//							for(String addString:topToAdd){
//								if(addString.equalsIgnoreCase(tempStr)){
//									index=synSetString.indexOf(tempStr);
//									indexMap.put(tempStr, index);
//								}
//							}
//						}
//					}
//					if(inSent)
//						synSetString=synSetString.substring(0, synSetString.length()-2)+"\n";
//				}
//			}
//			Scanner sentParser=new Scanner(currentSent);
//			String wordToSearch, wordSynMatch;
//			HashMap<String,String>wordsWithSynonyms=new HashMap<String,String>();
//			boolean added=false;
//			synSetString="";
//			while(sentParser.hasNext()){//loops through every word in the sentence
//				wordToSearch=sentParser.next();
//				tempArr=DictionaryBinding.getSynonyms(wordToSearch);
//				wordSynMatch="";
//
//				if(!wordsWithSynonyms.containsKey(wordToSearch.toLowerCase().trim())){
//					if(tempArr!=null){
//						for(int i=0;i<tempArr.length;i++){//looks through synonyms
//							tempStr=tempArr[i];
//							wordSynMatch+=tempStr+" ";
//							added=false;
//							for(String addString:topToAdd){//loops through the toAdd list
//								if(addString.trim().equalsIgnoreCase(tempStr.trim())){//there is a match in topToAdd!
//									if(!synSetString.contains(wordToSearch))
//										synSetString+=wordToSearch+" => ";
//									else{
//										Logger.logln(NAME+"Did not add this again: "+wordToSearch);
//									}
//									synSetString=synSetString+addString+", ";
//									//index=synSetString.indexOf(tempStr);
//									//indexMap.put(tempStr, index);
//									added=true;
//									break;
//								}
//							}
//
//							if(added){
//								//do something if the word was added like print to the box.
//								synSetString=synSetString.substring(0, synSetString.length()-2)+"\n";
//							}
//						}
//						if(wordSynMatch.length()>2)
//							wordsWithSynonyms.put(wordToSearch.toLowerCase().trim(), wordSynMatch.substring(0, wordSynMatch.length()-1));
//						else
//							wordsWithSynonyms.put(wordToSearch.toLowerCase().trim(), "NO Synonyms");
//					}
//				}
//			}
//			String tempStrToAdd;
//			Word possibleToAdd;
//			double topAnon=0;
//			for(String wordToRem:topToRemove){//adds ALL the synonyms in the wordsToRemove
//				if(wordsWithSynonyms.containsKey(wordToRem)){
//					tempStr=wordsWithSynonyms.get(wordToRem);
//					tempStrToAdd="";
//					parser=new Scanner(tempStr);
//					topAnon=0;
//					while(parser.hasNext()){
//						possibleToAdd=new Word(parser.next().trim());
//						ConsolidationStation.setWordFeatures(possibleToAdd);
//						if(possibleToAdd.getAnonymityIndex()>topAnon){
//							tempStrToAdd=possibleToAdd.getUntagged()+", ";//changed for test
//							topAnon=possibleToAdd.getAnonymityIndex();
//						}
//					}
//					synSetString+=wordToRem+" => "+tempStrToAdd+"\n";
//				}
//			}
//			//main.addToSentencePane.setText(synSetString);
//			//main.addToSentencePane.setCaretPosition(0);
//
//			Iterator iter=indexMap.keySet().iterator();
//			String key;
//
//			while(iter.hasNext()){
//				key=(String) iter.next();
//				index=indexMap.get(key);
//				try {
//					addTracker.addHighlight(index, index+key.length(), painter3);
//				} catch (BadLocationException e) {
//					Logger.logln(NAME+"Problem highlighting the words To add list");
//					e.printStackTrace();
//				}
//			}
//
//			//SynonymReplaceTest.replaceWords(eits);
//		}
}

/*
 * Answer to puzzle:
 * The "~" is a bitwise "NOT". "-1" (in binary) is represented by all 1's. So, a bitwise 'NOT' makes it equivalent to '0':
 *  
 * ~-1 == 0
 */
