package de.networkscanner.view.controller;

import de.networkscanner.businesslogik.PortScanner;
import de.networkscanner.view.ui.InformationWindow;
import de.networkscanner.view.ui.PortRestrictedTextField;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;

/**
 * Controller-Klasse für die Oberfläche des PortScanners
 * 
 * @author Alexander Voigt
 *
 */
public class PortController {

	@FXML
	private RadioButton completeScanRBtn;
	@FXML
	private RadioButton partialScanRBtn;
	@FXML
	private PortRestrictedTextField startPortTxt;
	@FXML
	private PortRestrictedTextField endPortTxt;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Button scanBtn;
	@FXML
	private Button cancelBtn;
	@FXML
	private TextArea resultsTxtArea;

	@FXML
	private final ToggleGroup portScanBtns = new ToggleGroup();

	private int startPortInteger = 0;
	private int endPortInteger = 0;

	private Thread thread = null;
	private PortScanner portScanner = null;

	/**
	 * Controller für die RadioButton zum Auswählen des Scanmodus<br>
	 * Textfelder sind ausgeblendet, wenn der RadioButton für den kompletten Scan
	 * ausgewählt ist und werden eingeblendet, sobald der Bereichsscan ausgewählt
	 * wird.
	 */
	@FXML
	private void handlePortRadioButtons() {
		if (partialScanRBtn.isSelected()) {
			scanBtn.disableProperty().bind(
					Bindings.isEmpty(startPortTxt.textProperty()).or(Bindings.isEmpty(endPortTxt.textProperty())));
			startPortTxt.setDisable(false);
			endPortTxt.setDisable(false);
		} else {
			scanBtn.disableProperty().unbind();
			scanBtn.setDisable(false);
			startPortTxt.setDisable(true);
			endPortTxt.setDisable(true);
		}
	}

	/**
	 * Controller für den "Scan starten" Button <br>
	 * Je nach ausgewählter Scanart werden Start- und Endport festgelegt und der
	 * Thread gestartet, sofern nicht bereits ein Thread läuft.
	 */
	@FXML
	private void handlePortScanButton() {
		if (partialScanRBtn.isSelected()) {
			startPortInteger = Integer.parseInt(startPortTxt.getText());
			endPortInteger = Integer.parseInt(endPortTxt.getText());
		} else {
			startPortInteger = 0;
			endPortInteger = 65535;
		}
		// Überprüfen, ob bereits ein Thread läuft, um eine Dopplung zu vermeiden, bevor
		// ein neuer Thread gestartet wird
		if (thread != null) {
			if (!thread.isAlive()) {
				startPortThread();
			} else {
				InformationWindow.createInformationWindow("Scan läuft bereits", "Es läuft bereits ein Scan. "
						+ "Dieser muss zuerst beendet oder abgebrochen werden, bevor ein weiterer gestartet werden kann.");
			}
		} else {
			startPortThread();
		}
	}

	/**
	 * Controller für den "Abbrechen" Button <br>
	 * Sollte ein Thread laufen, wird im Portscanner cancel() aufgerufen, wodurch
	 * der Thread von innen gestoppt wird.
	 */
	@FXML
	private void handlePortCancelButton() {
		if (thread != null) {
			if (thread.isAlive()) {
				portScanner.cancel();
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Starten eines Threads für den Portscan<br>
	 * Es wird ein neuer Portscanner mit Start- und Endport initialisiert und als
	 * Thread gestartet.<br>
	 * Außerdem werden Porperties an Variablen von Portscanner gebunden, sodass
	 * Ausgaben auf der Oberfläche während der Threadlaufzeit möglich sind
	 */
	private void startPortThread() {
		portScanner = new PortScanner(startPortInteger, endPortInteger);
		thread = new Thread(portScanner);
		progressBar.progressProperty().bind(portScanner.processProperty);
		resultsTxtArea.textProperty().bind(portScanner.txtAreaProperty);
		cancelBtn.disableProperty().bind(portScanner.runningProperty);
		thread.start();
	}

}
