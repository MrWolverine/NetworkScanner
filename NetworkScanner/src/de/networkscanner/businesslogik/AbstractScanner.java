package de.networkscanner.businesslogik;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

/**
 * Anlegen der ben�tigten Properties und Flags, Implementierung der
 * Abbrechen-Funktionalit�ten
 * 
 * @author Alexander Voigt
 *
 */
public abstract class AbstractScanner implements Runnable {

	private boolean cancelRequested = false;
	public DoubleProperty processProperty;
	public StringProperty txtAreaProperty;
	public BooleanProperty runningProperty;

	/**
	 * Vorzeitiges Abbrechen des Threads durch setzen einer Flag, auf die in den
	 * Scannern nach jedem Durchlauf gepr�ft wird
	 */
	public void cancel() {
		cancelRequested = true;
	}

	/**
	 * Getter f�r die Flag <i>cancelRequested</i>
	 * 
	 * @return true, wenn das Abbrechen des Scanners von der Oberfl�che verlangt
	 *         wurde
	 */
	protected boolean isCancelRequested() {
		return cancelRequested;
	}

}
