package de.networkscanner.view.ui;

/**
 * Auf die Eingabe von IP-Adressen ausgelegtes Textfeld
 * 
 * @author Alexander Voigt
 *
 */
public class IpRestrictedTextField extends RestrictedTextField {

	private final static int MAX_LENGTH = 15;
	private final static String RESTRICTIONS = "[0-9]|\\.";

	public IpRestrictedTextField() {
		super.setMaxLength(MAX_LENGTH);
		super.setRestrictions(RESTRICTIONS);
	}
}
