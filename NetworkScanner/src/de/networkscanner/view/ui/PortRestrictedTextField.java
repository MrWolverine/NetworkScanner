package de.networkscanner.view.ui;

/**
 * Auf die Eingabe von Port-Nummern ausgelegtes Textfeld
 * 
 * @author Alexander Voigt
 *
 */
public class PortRestrictedTextField extends RestrictedTextField {

	private final static int MAX_LENGTH = 5;
	private final static String RESTRICTIONS = "[0-9]";

	public PortRestrictedTextField() {
		super.setMaxLength(MAX_LENGTH);
		super.setRestrictions(RESTRICTIONS);
	}

}
