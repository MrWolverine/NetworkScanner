package de.networkscanner.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

/**
 * Controller-Klasse für das Hauptfenster des NetworkScanners
 * 
 * @author Alexander Voigt
 *
 */
public class NetworkController {

	@FXML
	private TabPane tabPane;
	@FXML
	private Tab ipScannerTab;
	@FXML
	private Tab portScannerTab;

	/**
	 * Setzt den Inhalt des PortScanner-Tabs in der UI
	 * 
	 * @param portTab
	 *            Die AnchorPane, die angezeigt werden soll
	 */
	public void setPortTab(AnchorPane portTab) {
		portScannerTab.setContent(portTab);
	}

	/**
	 * Setzt den Inhalt des IpScanner-Tabs in der UI
	 * 
	 * @param ipTab
	 *            Die AnchorPane, die angezeigt werden soll
	 */
	public void setIpTab(AnchorPane ipTab) {
		ipScannerTab.setContent(ipTab);
	}

}
