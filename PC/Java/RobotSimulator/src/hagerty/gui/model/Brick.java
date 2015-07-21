package hagerty.gui.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class for a Motor Controller, called "Brick" to avoid confusion with "Controller"
 *
 * @author Hagerty High
 */
public abstract class Brick {


    private final StringProperty alias;
    private IntegerProperty mPort;
    private final StringProperty serial;



    /**
     * Default constructor.
     */
    public Brick() {
        this.alias = new SimpleStringProperty("");
        mPort = new SimpleIntegerProperty(6000);
        serial = new SimpleStringProperty("");
    }


    public String getAlias() {
        return alias.get();
    }

    public Integer getPort() {
    	return mPort.get();
    }

    public String getSerial() {
        return serial.get();
    }

    public void setAlias(String alias) {
        this.alias.set(alias);
    }

    public void setPort(Integer port) {
    	mPort.set(port);
    }

    public void setSerial(String serial) {
        this.serial.set(serial);
    }

    public StringProperty aliasProperty() {
        return alias;
    }

    public StringProperty serialProperty() {
        return serial;
    }

    private int listStuff() {
    	return 0;
    }
}