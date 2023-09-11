package com.desarrollo.myapplication.Configuracion;

import java.io.Serializable;

/**Class to save the application settings**/

public class DMRPrintSettings implements Serializable {

	private static final long serialVersionUID = 4762643389154364957L;

	private String printerIP;
	private int selectedPrinterPort;

	public String getPrinterIP() {
		return printerIP;
	}

	public void setPrinterIP(String printerIP) {
		this.printerIP = printerIP;
	}

	public int getSelectedPrinterPort() {
		return selectedPrinterPort;
	}

	public void setSelectedPrinterPort(int selectedPrinterPort) {
		this.selectedPrinterPort = selectedPrinterPort;
	}

	private String printerMAC;
	private int communicationMethod;
	private int selectedItemIndex;
	private String selectedPrintMode;//2018 PH
	private String communicationType;//2018
	private int configurado;//2018

	public int getConfigurado() {
		return configurado;
	}

	public void setConfigurado(int configurado) {
		this.configurado = configurado;
	}

	public String getIpwebservice() {
		return ipwebservice;
	}

	public void setIpwebservice(String ipwebservice) {
		this.ipwebservice = ipwebservice;
	}

	public String getWebservice() {
		return webservice;
	}

	public void setWebservice(String webservice) {
		this.webservice = webservice;
	}

	private String ipwebservice;
	private String webservice;

	public String getSuc() {
		return suc;
	}

	public void setSuc(String suc) {
		this.suc = suc;
	}

	private String suc;


	public String getCommunicationType() {
		return communicationType;
	}

	public void setCommunicationType(String communicationType) {
		this.communicationType = communicationType;
	}



	//2018 PH - Accessors for print mode
	public String getSelectedPrintMode() {
		return selectedPrintMode;
	}

	public void setSelectedPrintMode(String selectedPrintMode) {
		this.selectedPrintMode = selectedPrintMode;
	}

	//Accessors for selected item index;
	public int getSelectedItemIndex()
	{
		return selectedItemIndex;
	}
	public void setSelectedItemIndex(int value)
	{
		selectedItemIndex = value;
	}

	//Accessors for selected mode index;

	public String getPrinterMAC() {
		return printerMAC;
	}
	public void setPrinterMAC(String value) {
		printerMAC = value;
	}
	//Set and get printer's port


	public int getCommunicationMethod()
	{
		return communicationMethod;
	}
	public void setCommunicationMethod(int value)
	{
		communicationMethod = value;
	}
	//set and get folder path

	public DMRPrintSettings(String printerIP, int selectedPrinterPort, String printerMAC, int communicationMethod, int selectedItemIndex, String selectedPrintMode, String communicationType, int configurado, String ipwebservice, String webservice, String suc) {
		this.printerIP = printerIP;
		this.selectedPrinterPort = selectedPrinterPort;
		this.printerMAC = printerMAC;
		this.communicationMethod = communicationMethod;
		this.selectedItemIndex = selectedItemIndex;
		this.selectedPrintMode = selectedPrintMode;
		this.communicationType = communicationType;
		this.configurado = configurado;
		this.ipwebservice = ipwebservice;
		this.webservice = webservice;
		this.suc = suc;
	}

	//Constructor


}