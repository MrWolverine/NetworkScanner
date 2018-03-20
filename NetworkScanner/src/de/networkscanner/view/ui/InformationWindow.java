package de.networkscanner.view.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Informationsfenster zum Darstellen von Informationen in einem zusätzlichen
 * Fenster
 * 
 * @author Alexander Voigt
 *
 */
public class InformationWindow {

	/**
	 * Erstellen und Aufrufen eines Informationsfenster
	 * 
	 * @param title
	 *            Titel des Informationsfensters
	 * @param text
	 *            Text des Informationsfensters
	 */
	public static void createInformationWindow(String title, String text) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(text);
		alert.showAndWait();
	}
}
