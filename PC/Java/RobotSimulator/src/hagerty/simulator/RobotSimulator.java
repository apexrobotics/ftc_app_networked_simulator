package hagerty.simulator;

import java.net.InetAddress;
import java.util.Collection;
import java.util.List;

import hagerty.gui.model.Brick;
import javafx.collections.ObservableList;

public class RobotSimulator  {

	static ModuleLister gModuleLister;
	static CoppeliaApiClient gCoppeliaApiClient;
	static public volatile boolean gThreadsAreRunning = true;
    static int gPhonePort;
    static InetAddress gPhoneIPAddress;
    static boolean simulatorStarted = false;
    static boolean visualizerStarted = false;

    private RobotSimulator() {

    }

    static public void startSimulator(hagerty.gui.MainApp mainApp) {

    	simulatorStarted = true;

		// Start the module info server
    	System.out.println("Starting Module Lister...");
        gModuleLister = new ModuleLister(mainApp);  // Runnable
        Thread moduleListerThread = new Thread(gModuleLister,"");
        moduleListerThread.start();
        
        // Start the individual threads for each module
        // Read the current list of modules from the GUI MainApp class
        List<Brick> brickList = mainApp.getBrickData();
        
        for (Brick temp : brickList) {
			System.out.println(temp.getAlias());
		}

    }

    static public boolean simulatorStarted() {
    	return simulatorStarted;
    }

    static public void startVisualizer(hagerty.gui.MainApp mainApp) {

    	visualizerStarted = true;

		// Start the module info server
    	System.out.println("Starting Visualizer...");
    	gCoppeliaApiClient = new CoppeliaApiClient(); // Runnable
    	Thread coppeliaThread = new Thread(gCoppeliaApiClient,"");
    	if (gCoppeliaApiClient.init()) {
    		coppeliaThread.start();
    	} else {
    		System.out.println("Initialization of Visualizer failed");
    	}
    }

    static public boolean visualizerStarted() {
    	return visualizerStarted;
    }
}
