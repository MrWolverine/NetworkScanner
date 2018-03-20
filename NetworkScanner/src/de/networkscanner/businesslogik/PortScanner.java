package de.networkscanner.businesslogik;

import java.net.InetSocketAddress;
import java.net.Socket;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Die Logik des PortScanners.<br>
 * Wird als Ziel eines Threads über {@link Thread#start()} mit den
 * entsprechenden Parametern gestartet
 * 
 * @author Alexander Voigt
 * 
 */
public class PortScanner extends AbstractScanner {

	private int startPort;
	private int endPort;

	/**
	 * Initiieren mit Start- und Endports und setzen der Properties, um während der
	 * Laufzeit Updates auf der Oberfläche anzuzeigen
	 * 
	 * @param startPort
	 *            der Port, ab dem gescannt werden soll
	 * @param endPort
	 *            der Port, bis zu dem gescannt werden soll
	 */
	public PortScanner(int startPort, int endPort) {
		this.startPort = startPort;
		this.endPort = endPort;
		processProperty = new SimpleDoubleProperty(0);
		txtAreaProperty = new SimpleStringProperty("");
		runningProperty = new SimpleBooleanProperty(true);
	}

	@Override
	public void run() {
		scanPorts();
	}

	/**
	 * Über die angegebenen Ports wird iteriert und versucht, eine Verbindung zum
	 * localhost über den jeweiligen Port herzustellen
	 * 
	 */
	private void scanPorts() {
		if (startPort >= 0 && startPort <= 65535 && endPort >= 0 && endPort <= 65535) {
			if (startPort <= endPort) {
				txtAreaProperty.set("");
				runningProperty.set(false);
				for (int port = startPort; port <= endPort; port++) {
					try {
						Socket socket = new Socket();
						socket.connect(new InetSocketAddress("localhost", port), 1);
						socket.close();
						txtAreaProperty.set("Port " + port + " ist offen\n" + txtAreaProperty.get());

					} catch (Exception ex) {
						// Port ist nicht offen
					}
					processProperty.set((double) (port - startPort) / (double) (endPort - startPort));
					if (isCancelRequested()) {
						processProperty.set(0);
						txtAreaProperty.set("PortScan bei Port " + port + " abgebrochen\n" + txtAreaProperty.get());
						runningProperty.set(true);
						break;
					}

				}
				txtAreaProperty.set(
						"PortScan von Port " + startPort + " bis " + endPort + " beendet\n" + txtAreaProperty.get());
				runningProperty.set(true);
			} else {
				txtAreaProperty.set("ERROR\n Startport ist größer als Endport");
			}
		} else {
			txtAreaProperty.set("ERROR\n Ports müssen zwischen 0 und 65535 liegen");
		}
	}
}
