package org.ftccommunity.simulator;


import org.ftccommunity.gui.MainApp;
import org.ftccommunity.simulator.data.AnalogSimData;
import org.ftccommunity.simulator.data.MotorSimData;
import org.ftccommunity.simulator.data.NullSimData;
import org.ftccommunity.simulator.modules.*;
import org.ftccommunity.simulator.modules.devices.Device;
import org.ftccommunity.simulator.modules.devices.LegoLightSensorDevice;
import org.ftccommunity.simulator.modules.devices.NullDevice;
import org.ftccommunity.simulator.modules.devices.TetrixMotorControllerDevice;
import org.ftccommunity.simulator.modules.devices.TetrixServoControllerDevice;
import org.ftccommunity.simulator.modules.devices.USBMotorControllerDevice;

import javafx.collections.ObservableList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Logger;

public class BrickListGenerator implements Runnable {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    DatagramSocket mServerSocket;

    byte[] mReceiveData = new byte[1024];
    byte[] mSendData = new byte[1024];

    MainApp mMainApp;

    public BrickListGenerator(MainApp mainApp) {
        mMainApp = mainApp;

        try {
        	mServerSocket = new DatagramSocket(7000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
    	byte[] packet;

    	try {
            while (!Thread.currentThread().isInterrupted()) {
                packet = receivePacketFromPhone();
                handleIncomingPacket(packet, false);
            }
            // Catch unhandled exceptions and cleanup
    	} catch (Exception e) {
    		e.printStackTrace();
    		close();
    	}
    }

    public void close() {
    	try {
    		mServerSocket.close();
    	} catch (Exception ex) {
    		System.out.println("An error occurred while closing!");
    		ex.printStackTrace();
    	}
    }


    private byte[] receivePacketFromPhone() {
    	DatagramPacket receivePacket = new DatagramPacket(mReceiveData, mReceiveData.length);
    	try {
    		mServerSocket.receive(receivePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

    	// Get the port and address of the sender from the incoming packet and set some global variables
    	// to be used when we reply back.
    	// TODO: do we need to set this every time?
        RobotSimulator.GetPhoneConnectionDetails(receivePacket.getPort(), receivePacket.getAddress());

    	// Make a copy of the packet.  Not sure if we need to do this.  Might not hold on to it for long.
        // byte[] myPacket = new byte[receivePacket.getLength()];
        // System.arraycopy(receivePacket.getData(), 0, myPacket, 0, receivePacket.getLength());
        return receivePacket.getData();
    }

    private void sendPacketToPhone(byte[] sendData) {
    	try {
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                    RobotSimulator.getPhoneIPAddress(), RobotSimulator.getPhonePort());
            mServerSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleIncomingPacket(byte[] data, boolean wait)
    {
    	if (data[0] == '?') { // infoCmd
    		sendPacketToPhone(getXmlModuleList(mMainApp.getBrickData()));
        }
    }

    private byte[] getXmlModuleList(ObservableList<BrickSimulator> mBrickList) {
    	try {
	    	JAXBContext context = JAXBContext.newInstance(
	    			BrickListWrapper.class,
            		LegacyBrickSimulator.class,
            		MotorBrickSimulator.class,
            		ServoBrickSimulator.class,
            		Device.class,
            		NullDevice.class,
            		LegoLightSensorDevice.class,
            		TetrixMotorControllerDevice.class,
            		TetrixServoControllerDevice.class,
            		USBMotorControllerDevice.class,
            		MotorSimData.class,
            		AnalogSimData.class,
            		NullSimData.class
	    	);
	        Marshaller m = context.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	        // Wrapping our controller data.
	        BrickListWrapper wrapper = new BrickListWrapper();
	        wrapper.setBricks(mBrickList);

	        // Marshalling to generate XML stream.
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			m.marshal(wrapper, outputStream);
			return outputStream.toByteArray();
		} catch (JAXBException e) {
			e.printStackTrace();
            throw new AssertionError("JAXB should not be throwing", e.getCause());
        }

    }

}




