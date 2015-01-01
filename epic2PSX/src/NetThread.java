/* AddonExampleNetThread.java is a class used by AddonExample.java
 * See AddonExample.java for more details.
 */

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

class NetThread extends Thread {

	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter ou = null;
	private boolean remoteExit;
	private String targetHost;
	private int targetPort;

	NetThread(String h, int p) {
		targetHost = h;
		targetPort = p;
	}

	public void run() {

		try {
			socket = new Socket(targetHost, targetPort);
			ou = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.out.println(e);
			return;
		} catch (IOException e) {
			System.out.println(e);
			return;
		}

		// *********************************************************************
		// Reader:

		try {

			String message;
			char qCategory;
			int qIndex;
			int intVal=0;
			short val1=0;
			short val2=0;

			String valStr1,valStr2;
			int parseMark;

			while (true) {

				if ((message = in.readLine()) != null) {
					try {
						if (message.charAt(0) == 'Q') {
							parseMark = message.indexOf('=');
							try {
								qIndex = Integer.parseInt(message.substring(2, parseMark));
								qCategory = message.charAt(1);
								parseMark++;
								if (qCategory == 's') {
									// Check if variable needed for interfacing with Hardware
									String map = mappings.getLinePSX2EPIC("Q" + qCategory + qIndex);
									if (!map.equals("")) {
										// OK, so variable needed for interfacing with hardware
										System.out.println("Map: " + map);

										String sbNoo = new String(map);
										int position = sbNoo.indexOf(",")+4;
										int length = sbNoo.length()-1;
										sbNoo=sbNoo.substring(position,length);				 			
										int PHnumb = Integer.parseInt(sbNoo);
										switch (qIndex) {
											case 458:
												// RcpDispL: An RcpDisp string is always 15 characters long. 
												// The last one is a semicolon. The first 7 characters represent the active
												// display, the next 7 characters are for the standby display.
												
												System.out.println("message: " + message);
												// Get the 2 strings for the 2 displays

												valStr1 = message.replace("Qs458=","");
												valStr2=valStr1;
												
												if (valStr1.trim().equals(";")) {
													// The display is off, so Qproc call because no parameters needed
													EPIC.SendQP(EPIC.handle,1);
													break;
												} else {
													valStr1 = valStr1.substring(0, 7);
													valStr1 = valStr1.replace(".", "");
													valStr1=valStr1.trim();
													if (valStr1.equals("DATA")) valStr1 = "137000";
												
													valStr2 = valStr2.substring(7, 14);
													valStr2 = valStr2.replace(".", "");
													// Strip the spaces
													valStr2=valStr2.trim();
													if (valStr2.equals("DATA")) valStr2 = "137000";
													if (valStr2.startsWith("SEN")) {
														valStr2 = valStr2.substring(3);
														valStr2 = valStr2.trim();
													}
												
													System.out.println("valStr1: " + valStr1);
													System.out.println("valStr2: " + valStr2);

													System.out.println("Lengte: " + valStr1.length());
													if (valStr1.length() == 6) {
														PHnumb=5;
														val1 = (short) (Integer.parseInt(valStr1) /10);
													} else {
														PHnumb=4;
														val1 = (short) (Integer.parseInt(valStr1));
													}
													System.out.println("val1: " + val1);

													if (valStr2.length() == 6) {
														PHnumb=5;
														val2 = (short) (Integer.parseInt(valStr2) /10);
													} else {
														if (valStr2.length() < 5) {
															PHnumb=6;
														} else {
															PHnumb=4;
														}
														val2 = (short) (Integer.parseInt(valStr2));
													}																			

													System.out.println("val2: " + val2);
													EPIC.SendPH(EPIC.handle,PHnumb,val1,val2);
													break;
												}
											default:
												System.out.println("message: " + message);
												break;
											}									
										}
									}
									else if ((qCategory == 'i') || (qCategory == 'h')) {
										String map = mappings.getLinePSX2EPIC("Q" + qCategory + qIndex);

										if (!map.equals("")){
											System.out.println("Map: " + map);
											intVal = Byte.parseByte(message.substring(parseMark).trim());
										
											String sbNoo = new String(map);
											int position = sbNoo.indexOf(",")+4;
											int length = sbNoo.length()-1;
											sbNoo=sbNoo.substring(position,length);				 			
										int PHnumb = Integer.parseInt(sbNoo);
										byte[] data = ByteBuffer.allocate(4).putInt(intVal).array();
										System.out.println("sendPHInt(" + PHnumb + ","+ data[0] +  ","+ data[1] + ","+ data[2] + ","+ data[3] + ")" );

										EPIC.epicIOdll.INSTANCE.__SendPH(EPIC.handle,PHnumb,data[0],data[1],data[2],data[3]);

										System.out.println("sendPHInt(" + EPIC.handle + "," + PHnumb + "," + data[0] + "," + data[1] + "," + data[2] + "," + data[3] + ")");
									}	
								}

							} catch (NumberFormatException nfe) {
								nfe.printStackTrace();
							}

						} else if (message.charAt(0) == 'L') {

							// Lexicon at net connect - Ignore
							
						} else if (message.substring(0, 3).equals("id=")) {

							try {
								System.out.println("Connection OK. Our client id: " + message);
							} catch (NumberFormatException nfe) {
								nfe.printStackTrace();
							}

						} else if (message.length() > 8
								&& message.substring(0, 8).equals("version=")) {
							// Check version agreement if required
						} else if (message.equals("load1")) {
							// Situation loading phase 1 (paused and reading variables)
						} else if (message.equals("load2")) {
							// Situation loading phase 2 (reading model options)
						} else if (message.equals("load3")) {
							// Situation loading phase 3 (unpaused)
						} else if (message.equals("exit")) {
							remoteExit = true;
							break;
						} else if (message.startsWith("metar=")) {
							// METAR feeder status message
						}
					} catch (StringIndexOutOfBoundsException sioobe) {
						sioobe.printStackTrace();
					}
				}
			}

		} catch (IOException e) {
		}
		finalJobs();
	}

	void finalJobs() {
		try {
			if (!remoteExit && ou != null) {
				ou.println("exit");
				try {
					sleep(20);
				} catch (InterruptedException e) {
				}
				ou.close();
			}
			if (in != null)
				in.close();
			if (socket != null)
				socket.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	void send(String s) {
		if (ou != null) {
			ou.println(s);
			if (ou.checkError())
				finalJobs();
		}
	}

}