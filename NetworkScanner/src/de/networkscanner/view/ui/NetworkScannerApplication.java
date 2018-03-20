package de.networkscanner.view.ui;

import java.io.IOException;

import de.networkscanner.view.controller.NetworkController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Aufbau der GUI des NetworkScanners
 * 
 * @author Alexander Voigt
 *
 */
public class NetworkScannerApplication extends Application {

	private VBox networkScanner;
	private AnchorPane portScannerTab;
	private AnchorPane ipScannerTab;
	private NetworkController networkController;

	@Override
	public void start(Stage primaryStage) {
		try {
			loadModules();
			initTabs();
			Scene scene = new Scene(networkScanner);
			primaryStage.setTitle("NetworkScanner");
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Laden aller benötigten Module für die GUI
	 */
	private void loadModules() {
		loadNetworkScanner();
		loadPortScanner();
		loadIpScanner();
	}

	/**
	 * Laden der GUI des allgemeinen Anwendungsfensters durch die entsprechende FXML Datei
	 */
	private void loadNetworkScanner() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(NetworkScannerApplication.class.getResource("/de/networkscanner/view/controller/NetworkScanner.fxml"));
			networkScanner = loader.load();
			networkController = loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Laden der GUI des PortScanners durch die entsprechende FXML Datei
	 */
	private void loadPortScanner() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(NetworkScannerApplication.class.getResource("/de/networkscanner/view/controller/PortScanner.fxml"));
			portScannerTab = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Laden der GUI des IpScanners durch die entsprechende FXML Datei
	 */
	private void loadIpScanner() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(NetworkScannerApplication.class.getResource("/de/networkscanner/view/controller/IpScanner.fxml"));
			ipScannerTab = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Füllen der Tabs im allgemeinen Anwendungsfenster mit der geladenen Port- und IpScanner-GUI
	 */
	private void initTabs() {
		networkController.setPortTab(portScannerTab);
		networkController.setIpTab(ipScannerTab);
	}

}