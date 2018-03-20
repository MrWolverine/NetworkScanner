package de.networkscanner.businesslogik;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.util.SubnetUtils;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Die Logik des IpScanners. <br>
 * Wird als Ziel eines Threads mit {@link Thread#start()} gestartet
 * 
 * @author Alexander Voigt
 *
 */
public class IpScanner extends AbstractScanner {

	private String ip = "";
	private String startIp = "";
	private String endIp = "";
	private String localIp = "";
	private String scanOption;

	private List<String> scanAddresses = new ArrayList<String>();

	/* @formatter:off */
	private static final String IP_ADDRESS_PATTERN = 
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." 
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	/* @formatter:on */

	private Pattern pattern;
	private Matcher matcher;

	/**
	 * Konstruktor für den kompletten Subnetzscan
	 */
	public IpScanner() {
		initIpProperties();
		scanOption = "complete";
	}

	/**
	 * Konstruktor für den Einzelscan
	 * 
	 * @param ip
	 *            die zu scannende IP-Adresse
	 */
	public IpScanner(String ip) {
		this.ip = ip;
		initIpProperties();
		scanOption = "single";
	}

	/**
	 * Konstruktor für den Bereichsscan
	 * 
	 * @param startIp
	 *            die IP-Adresse, ab der gescannt werden soll
	 * @param endIp
	 *            die IP-Adresse, bis zu der gescannt werden soll
	 */
	public IpScanner(String startIp, String endIp) {
		this.startIp = startIp;
		this.endIp = endIp;
		initIpProperties();
		scanOption = "partial";
	}

	/**
	 * Initialisieren der benötigten Porperties und setzen der lokalen IP-Adresse
	 * und Subnetzmaske in CIDR Notation
	 */
	private void initIpProperties() {
		processProperty = new SimpleDoubleProperty(0);
		txtAreaProperty = new SimpleStringProperty("");
		runningProperty = new SimpleBooleanProperty(true);
		setIpSubnetmask();
	}

	@Override
	public void run() {
		if (checkRequirements()) {
			scanIps(scanAddresses);
		}
	}

	/**
	 * Überprüfen der von der Scanart abhängigen Bedingungen auf Vollständigkeit und
	 * Richtigkeit.
	 * 
	 * @return true, wenn alle Bedingungen für eine Verarbeitung der IP's erfüllt
	 *         sind
	 */
	private boolean checkRequirements() {
		if (checkNetworkConnections()) {

			SubnetUtils subnetUtils = new SubnetUtils(localIp);
			String[] subnetAddresses = subnetUtils.getInfo().getAllAddresses();

			switch (scanOption) {
			case "single":
				if (validateFormat(ip)) {
					List<String> allAddresses = Arrays.asList(subnetAddresses);
					if (!allAddresses.contains(ip)) {
						txtAreaProperty.set("IP-Adresse liegt nicht im Subnetz\n" + "Das Subnetz umfasst "
								+ allAddresses.get(0) + " bis " + allAddresses.get(allAddresses.size() - 1));
						return false;
					} else {
						scanAddresses.add(ip);
					}
				} else {
					txtAreaProperty.set("IP-Adress-Format falsch");
					return false;
				}
				break;
			case "partial":
				if (validateFormat(startIp) && validateFormat(endIp)) {
					List<String> allAddresses = Arrays.asList(subnetAddresses);
					if (!partOfListInOrder(allAddresses, startIp, endIp)) {
						txtAreaProperty.set("Die IP's liegen nicht im Subnetz oder sind in der falschen Reihenfolge\n"
								+ "Das Subnetz umfasst " + allAddresses.get(0) + " bis "
								+ allAddresses.get(allAddresses.size() - 1));
						return false;
					} else {
						int i = allAddresses.indexOf(startIp);
						do {
							scanAddresses.add(allAddresses.get(i));
							i++;
						} while (!(allAddresses.get(i - 1).equals(endIp)));
					}
				} else {
					txtAreaProperty.set("IP-Adress-Format falsch");
					return false;
				}
				break;
			case "complete":
				scanAddresses = Arrays.asList(subnetAddresses);
				break;
			default:
				txtAreaProperty.set("Interner Fehler");
				return false;
			}
			return true;

		} else {
			txtAreaProperty.set("Keine Netzwerkverbindungen gefunden");
			return false;
		}
	}

	/**
	 * Es wird über die Liste von zu scannenden IP's iteriert und jeweils ein Ping
	 * an diese IP versendet. Wird innerhalb von 1000ms geantwortet, zählt diese
	 * Adresse als vorhanden.
	 * 
	 * @param scanAddresses
	 *            die Liste der zu scannenden IP's
	 */
	private void scanIps(List<String> scanAddresses) {
		try {
			txtAreaProperty.set("");
			runningProperty.set(false);

			for (String currentAddress : scanAddresses) {
				InetAddress address = InetAddress.getByName(currentAddress);
				if (address.isReachable(1000)) {
					txtAreaProperty.set("Die Adresse " + address.getHostAddress() + " ist im Netzwerk vorhanden\n"
							+ txtAreaProperty.get());
				}
				processProperty
						.set((double) scanAddresses.indexOf(currentAddress) / (double) (scanAddresses.size() - 1));
				if (isCancelRequested()) {
					processProperty.set(0);
					txtAreaProperty.set("IPScan bei " + currentAddress + " abgebrochen\n" + txtAreaProperty.get());
					runningProperty.set(true);
					break;
				}
			}
			txtAreaProperty.set("IPScan von " + scanAddresses.get(0) + " bis "
					+ scanAddresses.get(scanAddresses.size() - 1) + " beendet\n" + txtAreaProperty.get());
			runningProperty.set(true);
		} catch (UnknownHostException uhex) {
			txtAreaProperty.set("IP-Adressen sind nicht valide");
			printStackTraceToTxtArea(uhex);
		} catch (Exception ex) {
			txtAreaProperty
					.set("IpScan weist einen interenen Fehler auf. Bitte erneut probieren oder Admin ansprechen");
			printStackTraceToTxtArea(ex);
		}
	}

	/**
	 * Setzt die Variable <i>localIp</i> auf die lokale IP-Adresse inklusive
	 * Subnetzmaske in CIDR Schreibweise. </br>
	 * Bsp.: 192.168.0.10/25
	 */
	private void setIpSubnetmask() {
		localIp = getLocalIpAddress() + "/" + getSubnetmask();
	}

	// TODO Netzwerkadapter einstellbar machen
	/**
	 * Gibt die lokale IP-Adresse zurück
	 * 
	 * @return die lokale IP-Adresse
	 */
	private String getLocalIpAddress() {
		String localIp = "";
		try {
			localIp = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException uhex) {
			txtAreaProperty.set("Lokale IP-Adresse konnte nicht gefunden werden");
			printStackTraceToTxtArea(uhex);
		}
		return localIp;
	}

	/**
	 * Gibt die Subnetzmaske des Netzwerkes in CIDR Schreibweise zurück
	 * 
	 * @return Subnetzmaske in CIDR Schreibweise
	 */
	private short getSubnetmask() {
		short cidr = 0;
		try {
			InetAddress localhost = Inet4Address.getLocalHost();
			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localhost);
			cidr = networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength();
		} catch (Exception ex) {
			printStackTraceToTxtArea(ex);
		}
		return cidr;
	}

	/**
	 * Überprüfung des Formats der IP-Adresse durch Regex (Regular Expression)
	 *
	 * @param ip
	 *            Die zu überprüfende IP-Adresse
	 * @return true bei Erfüllung der Regex
	 */
	private boolean validateFormat(String ip) {
		pattern = Pattern.compile(IP_ADDRESS_PATTERN);
		matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	/**
	 * Überprüft, ob die Strings in der richtigen Reihenfolge in der Liste enthalten
	 * sind
	 * 
	 * @param addressList
	 *            die zu überprüfende Liste
	 * @param startIp
	 *            der String, der die StartIP enthält
	 * @param endIp
	 *            der String, der die EndIp enthält
	 * @return true, wenn die Reihenfolge eingehalten ist und beide Strings
	 *         enthalten sind
	 */
	private boolean partOfListInOrder(List<String> addressList, String startIp, String endIp) {
		boolean ergebnis = false;
		if (addressList.contains(startIp) && addressList.contains(endIp)
				&& (addressList.indexOf(startIp) < addressList.indexOf(endIp))) {
			ergebnis = true;
		}
		return ergebnis;
	}

	/**
	 * Ausgabe der Exception und des StackTraces in die <i>txtAreaProperty</i>
	 * 
	 * @param ex
	 *            die auszugebende Exception
	 */
	private void printStackTraceToTxtArea(Exception ex) {
		StringWriter stackTraceWriter = new StringWriter();
		ex.printStackTrace(new PrintWriter(stackTraceWriter));
		txtAreaProperty.set(txtAreaProperty.get() + "\n" + stackTraceWriter.toString());
	}

	/**
	 * Schreibt die Anfangs- und End-IP des Subnetzes in einen Array und gibt diesen
	 * zurück.
	 * 
	 * @return String-Array, der die Anfangs- und End-IP des Subnetzes enthält
	 */
	public String[] getSubnetBorders() {
		String[] subnetBorders = new String[2];
		if (localIp == "") {
			setIpSubnetmask();
		}
		SubnetUtils subnetUtils = new SubnetUtils(localIp);
		String[] subnetAddresses = subnetUtils.getInfo().getAllAddresses();
		subnetBorders[0] = subnetAddresses[0];
		subnetBorders[1] = subnetAddresses[subnetAddresses.length - 1];

		return subnetBorders;
	}

	/**
	 * Überprüfen aller Netzwerkadapter und der damit verbundenen IP-Adressen, ob es
	 * sich dabei um Loopback-Adressen handelt. So wird überprüft, ob eine
	 * Netzwerkverbindung besteht oder nicht.
	 * 
	 * @return true, wenn es mindestens eine Netzwerkverbindung gibt
	 */
	private boolean checkNetworkConnections() {
		try {
			for (Enumeration<NetworkInterface> enumNI = NetworkInterface.getNetworkInterfaces(); enumNI
					.hasMoreElements();) {
				NetworkInterface nInterf = enumNI.nextElement();
				for (Enumeration<InetAddress> enumIpAddress = nInterf.getInetAddresses(); enumIpAddress
						.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddress.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return true;
					}
				}
			}
		} catch (SocketException se) {
			printStackTraceToTxtArea(se);
		}
		return false;
	}

}
