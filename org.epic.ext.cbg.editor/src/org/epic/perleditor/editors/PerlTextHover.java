package org.epic.perleditor.editors;



import org.eclipse.swt.graphics.Point;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import java.util.ResourceBundle;

import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;

/**
 * Example implementation for an <code>ITextHover</code> which hovers over Java code.
 */
public class PerlTextHover implements ITextHover {
	
	private static int MAX_INFO_LENGTH = 80;

	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		String textRes = null;

		if (hoverRegion != null) {
			try {
				if (hoverRegion.getLength() > -1) {
					String text =
						textViewer.getDocument().get(
							hoverRegion.getOffset(),
							hoverRegion.getLength());

					try {
						ResourceBundle rb =
							ResourceBundle.getBundle(
								"org.epic.perleditor.editors.quickreference");

						if (text.length() > 0 && rb.getString(text) != null) {
							textRes = splitMessage(rb.getString(text));
					

						}
						else {		
						     //If no keyword description was found try to show marker info
						     IAnnotationHover markerAnnotation = new PerlAnnotationHover();
						     int line = textViewer.getDocument().getLineOfOffset(hoverRegion.getOffset());
						     textRes = markerAnnotation.getHoverInfo((ISourceViewer) textViewer, line);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} catch (BadLocationException x) {
			}
		}

		if (textRes != null) {
			return textRes;
		} else {
			return null;
		}
	}

	/* 
	 * Method declared on ITextHover
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		Point selection = textViewer.getSelectedRange();
		if (selection.x <= offset && offset < selection.x + selection.y)
			return new Region(selection.x, selection.y);
		return new Region(offset, 0);
	}
	
	
	private String splitMessage(String message) {
		String result = "";
		
		if(message.length() <= MAX_INFO_LENGTH) {
			return message;
		}
		
		
		String tmpStr;
		
		// Index of \n
		int crIndex = message.indexOf("\n");
		
		if(crIndex != -1) {
			tmpStr = message.substring(0, crIndex);
		}
		else {
			tmpStr = new String(message);
		}
		
		while(tmpStr.length() > MAX_INFO_LENGTH) {
			
			int spacepos = tmpStr.indexOf(" ", MAX_INFO_LENGTH);
			
			if(spacepos != -1) {
				result += tmpStr.substring(0, spacepos) + "\n";
				tmpStr = tmpStr.substring(spacepos);
			}
			else {
				result += tmpStr.substring(0, MAX_INFO_LENGTH) + "\n";
				tmpStr = tmpStr.substring(MAX_INFO_LENGTH);
			}
			
			
		    	
		}
		
		result += tmpStr;
		
		if(crIndex != -1) {
			result += message.substring(crIndex);
		}
		
		return result;
	}
}