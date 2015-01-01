import java.util.TimerTask;


import com.sun.jna.Native;


// *************************************************
		// Get analogs and events 
		//  Here every timer tick
		//  events that are passed to this program(destID) will appear here
		//

	public class checkEPIC extends TimerTask {
		
		
		@Override  
	    public void run() {  
	        // The logic of task/job that is going to be executed.  
	        // Here we are going to print the following string value  
   
		eventStruct eventPtr = new eventStruct();
		
		eventPtr.nextRec = 0;
		char[] scrapBuffer;
		char[] analogs = null;
		
//		IntByReference analogsPtr = new IntByReference();
		
//		EPIC.epicIOdll.INSTANCE.__GetAnalogs(EPIC.handle,analogs);				//Get Output analogs
//				System.out.println("Analog[0]" + analogs[0]);											
											
			//get and process any events sent to this destID
			// events that will appear here:

			// nqw(cmd,data16,destID);
			//nqw32(cmd,data16,data16,destID);
			//events from devices with options(SEND_BUTTON_DLL);  use GetDeviceData to request a device's events
			// 
		
		while(EPIC.epicIOdll.INSTANCE.__GetEvent(EPIC.handle,eventPtr) >= 0)
		{
			String str = "cmd = " + eventPtr.cmd + ",Eventnr = " + eventPtr.data[0] +",data= " + (eventPtr.data[1]+(256*eventPtr.data[2]));
			scrapBuffer = str.toCharArray();
			System.out.println(scrapBuffer);
			switch (eventPtr.cmd)
			 {
			 	case	(byte) EPIC.EVENT16_0 :				//NQW(0-255,data16)
						String cmdStr = Byte.toString(eventPtr.data[0]);
						System.out.println("cmdStr: " + cmdStr);
						String found = mappings.getLineEPIC2PSX(cmdStr);

						if (!found.equals("")){
							String sbNoo = new String(found);
							int position = sbNoo.indexOf(",")+2;
							int length = sbNoo.length()-1;
							sbNoo=sbNoo.substring(position,length);
				 			switch (found)
				 			{
				 				case "Qs109":
				 				case "Qs110":
				 				case "Qs111":
				 					int value = eventPtr.data[1]+(256*eventPtr.data[2]);
				 					if (Math.abs(value)==1 || Math.abs(value)==8) {
				 						// small frequency knob
				 						sbNoo=sbNoo.concat("0,0," + value);
				 					}
				 					else {
				 						if (Math.abs(value)==2 || Math.abs(value)==16) {
				 							// high frequency knob
					 						sbNoo=sbNoo.concat("0," + (value/2) + ",0");
				 						}
				 						else {
				 							// hfsens knob
					 						sbNoo=sbNoo.concat((value/4) + ",0,0");
				 						}
				 					}	 						
				 					break;
				 				default:
						 			sbNoo=sbNoo.concat(Integer.toString(eventPtr.data[1]+(256*eventPtr.data[2])));
				 					break;
							}
		 					EPIC.sendToServer(sbNoo.toString());
		 					System.out.println("SendtoServer: " + sbNoo);
						}
			 			
						break;
			 	case	(byte) EPIC.EVENT16_1 :				//NQW(256-511,data16)
						switch(eventPtr.data[0])
						{
							case 0 :
							break;
						}
						break;
			 	case	(byte) EPIC.EVENT16_2 :				//NQW(512-767,data16)
						break;
			 	case	(byte) EPIC.EVENT16_3 :				//NQW(768-1023,data16)
						break;
			 	case	(byte) EPIC.EVENT_ON  :				//ENQUE(0-255,on)
						break;
			 	case	(byte) EPIC.EVENT_OFF :				//ENQUE(0-255,off)
						break;
			 	case	(byte) EPIC.EVENT32_0 :				//NQW(0-255,data32)
						break;
//			 	case	EPIC.DEVICE_BUTTON_ON:
//						switch(eventPtr.data[0])			//deviceID
//						{
//							case	10:						//device 10
//							if(eventPtr.data[1] < MAX_DEV10_ON_EVENTS)	(Dev10On[event.data[1]])();	
//							break;
//							case	4:	if(eventPtr.data[1] < MAX_SOUND_EVENTS)	(SoundOn[event.data[1]])();	
//							default  :
//							str = "DEVICE_BUTTON_ON event rcvd (Dev=" + eventPtr.getInt(1) + ",button=" + eventPtr.getInt(2);
//							scrapBuffer = str.toCharArray();
//							System.out.println(scrapBuffer);
//
//						}
//						break;
//			 	case  	EPIC.DEVICE_BUTTON_OFF:
//			 			switch(eventPtr.data[0])			//deviceID
//						{
//
//							case	10:
//							if(eventPtr.data[1] < MAX_DEV10_OFF_EVENTS)	(Dev10Off[event.data[1]])();	
//							break;
//							default :
//							str = "DEVICE_BUTTON_OFF event rcvd (Dev=" + eventPtr.getInt(1) + ",button=" + eventPtr.getInt(2);
//							scrapBuffer = str.toCharArray();
//							System.out.println(scrapBuffer);
//
//						}
//						break;

			 	default :
						//Unhandled event
			 			break;
			  }	
			}
		}
	}