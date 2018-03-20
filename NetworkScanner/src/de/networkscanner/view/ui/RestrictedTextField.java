package de.networkscanner.view.ui;

import java.awt.Toolkit;

import javafx.scene.control.TextField;

/**
 * Textfeld mit der M�glichkeit, die Eingabe des Nutzers individuell
 * einzuschr�nken
 * 
 * @author Alexander Voigt
 *
 */
public class RestrictedTextField extends TextField {

	private int maxLength;
	private String restrictions;

	/**
	 * Setzt die maximale Zeichenanzal f�r das Eingabefeld
	 * 
	 * @param maxLength
	 *            Maximale Zeichenanzahl
	 */

	protected void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * Setzt die Einschr�nkungen des Eingabefelds �ber eine Regular Expression
	 * 
	 * @param restrictions
	 *            Einschr�nkungen (Regex)
	 */
	protected void setRestrictions(String restrictions) {
		this.restrictions = restrictions;
	}

	@Override
	public void replaceText(int start, int end, String text) {
		if (text == "") {
			super.replaceText(start, end, text);
		} else if (text.matches(restrictions) && getText().length() < maxLength) {
			super.replaceText(start, end, text);
		} else {
			// Alarmsound, wenn gegen die maximale L�nge oder die Einschr�nkungen versto�en
			// wird
			Toolkit.getDefaultToolkit().beep();
		}
	}

	@Override
	public void replaceSelection(String text) {
		if (text == "") {
			super.replaceSelection(text);
		} else if (text.matches(restrictions) && getText().length() <= maxLength) {
			if (text.length() > maxLength - getText().length()) {
				text = text.substring(0, maxLength - getText().length());
			}
			super.replaceSelection(text);
		} else {
			// Alarmsound, wenn gegen die maximale L�nge oder die Einschr�nkungen versto�en
			// wird
			Toolkit.getDefaultToolkit().beep();
		}
	}
}
