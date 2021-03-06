package org.ftccommunity.gui.view;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.List;

import org.ftccommunity.gui.MainApp;
import org.ftccommunity.simulator.modules.BrickSimulator;

/**
 * Dialog to edit details of a Motor Controller.
 *
 * @author Hagerty High
 */
public class DebugWindowController {

    @FXML
    private Label brickDebugField;

    private Stage dialogStage;

    private MainApp mMainApp;

    private boolean mDone = false;


    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	startLiveDebug();
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        mMainApp = mainApp;
    }

    /**
     * Start a thread that queries the Brick Simulators and displays current values here.
     */
    public void startLiveDebug() {

        Task<Void> task = new Task<Void>() {
      	  @Override
      	  public Void call() throws Exception {
      	    int i = 0;
      	    while (!mDone) {
      	      final int finalI = i;
                Platform.runLater(() -> {
                    brickDebugField.setText("" + finalI);

                    // Read the current list of modules from the GUI MainApp class
                    List<BrickSimulator> brickList = mMainApp.getBrickData();
                    brickList.forEach(BrickSimulator::updateDebugGuiVbox);
                });
      	      i++;
      	      Thread.sleep(500);
      	    }
			return null;
      	  }
      	};
      	Thread th = new Thread(task);
      	th.setDaemon(true);
      	th.start();
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;

        // Set the dialog icon.
        this.dialogStage.getIcons().add(new Image("file:resources/images/edit.png"));
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
    	mDone = true;
        dialogStage.close();
    }

}