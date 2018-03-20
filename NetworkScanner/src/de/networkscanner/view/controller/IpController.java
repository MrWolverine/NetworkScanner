package de.networkscanner.view.controller;

import de.networkscanner.businesslogik.IpScanner;
import de.networkscanner.view.ui.InformationWindow;
import de.networkscanner.view.ui.IpRestrictedTextField;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;

/**
 * Controller-Klasse für die Oberfläche des IPScanners
 * 
 * @author Alexander Voigt
 *
 */
public class IpController {

	@FXML
	private RadioButton singleScanRBtn;
	@FXML
	private RadioButton subnetScanRBtn;
	@FXML
	private RadioButton rangeScanRBtn;
	@FXML
	private IpRestrictedTextField startIpTxt;
	@FXML
	private IpRestrictedTextField endIpTxt;
	@FXML
	private Button scanBtn;
	@FXML
	private Button cancelBtn;
	@FXML
	private Button subnetBtn;
	@FXML
	private TextArea resultsTxtArea;
	@FXML
	private ProgressBar progressBar;

	@FXML
	private final ToggleGroup ipScanBtns = new ToggleGroup();

	private String startIp = "";
	private String endIp = "";

	private Thread thread = null;
	private IpScanner ipScanner = null;

	private String[] subnetBorders = new String[2];

	/**
	 * Controller für die RadioButton zum Auswählen des Scanmodus<br>
	 * Textfelder sind ausgeblendet, wenn der RadioButton für den kompletten Scan
	 * ausgewählt ist und werden eingeblendet, sobald der Bereichsscan ausgewählt
	 * wird.
	 */
	@FXML
	private void handleIpRadioButtons() {
		if (singleScanRBtn.isSelected()) {
			scanBtn.disableProperty().unbind();
			scanBtn.disableProperty().bind(Bindings.isEmpty(startIpTxt.textProperty()));
			startIpTxt.setDisable(false);
			endIpTxt.setDisable(true);
		} else if (rangeScanRBtn.isSelected()) {
			scanBtn.disableProperty().unbind();
			scanBtn.disableProperty()
					.bind(Bindings.isEmpty(startIpTxt.textProperty()).or(Bindings.isEmpty(endIpTxt.textProperty())));
			startIpTxt.setDisable(false);
			endIpTxt.setDisable(false);
		} else {
			scanBtn.disableProperty().unbind();
			scanBtn.setDisable(false);
			startIpTxt.setDisable(true);
			endIpTxt.setDisable(true);
		}
	}

	/**
	 * Controller für den "Scan starten" Button <br>
	 * Je nach ausgewählter Scanart wird eine bestimmte IP festgelegt und der Thread
	 * gestartet, sofern nicht bereits ein Thread läuft.
	 */
	@FXML
	private void handleIpScanButton() {
		if (singleScanRBtn.isSelected()) {
			startIp = startIpTxt.getText();
		} else if (rangeScanRBtn.isSelected()) {
			startIp = startIpTxt.getText();
			endIp = endIpTxt.getText();
		}
		// Überprüfen, ob bereits ein Thread läuft, um eine Dopplung zu vermeiden, bevor
		// ein neuer Thread gestartet wird
		if (thread != null) {
			if (!thread.isAlive()) {
				startIpThread();
			} else {
				InformationWindow.createInformationWindow("Scan läuft bereits", "Es läuft bereits ein Scan. "
						+ "Dieser muss zuerst beendet oder abgebrochen werden, bevor ein weiterer gestartet werden kann.");
			}
		} else {
			startIpThread();
		}
		startIp = "";
		endIp = "";
	}

	/**
	 * Controller für den "Abbrechen" Button <br>
	 * Sollte ein Thread laufen, wird im Portscanner cancel() aufgerufen, wodurch
	 * der Thread von innen gestoppt wird.
	 */
	@FXML
	private void handleIpCancelButton() {
		if (thread != null) {
			if (thread.isAlive()) {
				ipScanner.cancel();
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Controller für den "Subnetz anzeigen" Button <br>
	 * Die erste und letzte IP des Subnetzes werden in einem separaten Fenster
	 * angezeigt
	 */
	@FXML
	private void handleSubnetButton() {
		ipScanner = new IpScanner();
		subnetBorders = ipScanner.getSubnetBorders();

		InformationWindow.createInformationWindow("Subnetz",
				"Das Subnetz umfasst die Adressen von " + subnetBorders[0] + " bis " + subnetBorders[1]);
	}

	/**
	 * Starten eines Threads für den Ipscan<br>
	 * Es wird ein neuer IpScanner initialisiert und als Thread gestartet.<br>
	 * Außerdem werden Porperties an Variablen von Ipscanner gebunden, sodass
	 * Ausgaben auf der Oberfläche während der Threadlaufzeit möglich sind
	 */
	private void startIpThread() {
		if (startIp == "" && endIp == "") {
			ipScanner = new IpScanner();
		} else if (startIp != "" && endIp != "") {
			ipScanner = new IpScanner(startIp, endIp);
		} else {
			ipScanner = new IpScanner(startIp);
		}
		thread = new Thread(ipScanner);
		progressBar.progressProperty().bind(ipScanner.processProperty);
		resultsTxtArea.textProperty().bind(ipScanner.txtAreaProperty);
		cancelBtn.disableProperty().bind(ipScanner.runningProperty);
		thread.start();
	}
}
