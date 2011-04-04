// Copyright (c) 2009, 2011 by Patrick Juola.   All rights reserved.  All unauthorized use prohibited.  
/**
 **/
package com.jgaap.eventDrivers;

import com.jgaap.generics.Document;
import com.jgaap.generics.Event;
import com.jgaap.generics.EventDriver;
import com.jgaap.generics.EventSet;
import javax.swing.*;


/**
 * NullEventSet : no eventification, returns entire document as a single event, prints arguments
 * 
 * Deprecated, because ordered canonicizers means we can print
 * the documents with the null canonicizer
 */
@Deprecated
public class NullEventDriver extends EventDriver {
    @Override
    public String displayName(){
    	return "Null Event Set";
    }
    
    @Override
    public String tooltipText(){
    	return "Null events for debugging previous sets";
    }
    
    @Override
    public boolean showInGUI(){
    	return false;
    }

    @Override
    public GroupLayout getGUILayout(JPanel panel){
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(panel);
        return layout;
    }

    @Override
    public EventSet createEventSet(Document doc) {
        EventSet es = new EventSet(doc.getAuthor());

        // again, use logging facilities when they exist
        //System.out.println(ds.getDocument(0).getAuthor());
        //for (int i = 0; i < ds.documentCount(); i++) {

            // again, use logging facilities when they exist
	    // System.out.println("--- Document #" + i + " --- ");
	    // System.out.println(ds.getDocument(i).stringify());

            es.addEvent(new Event(doc.stringify()));
        //}

        return es;
    }

}
