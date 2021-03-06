package com.github.nitrogen2oxygen.savefilesync.server;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class DataServer implements java.io.Serializable {
    private static final long serialVersionUID = -4315689195751908240L;

    public DataServer() {
    }

    /* Used to ge the server type of the data server */
    public abstract ServerType getServerType();

    /* Gets the name of the host server */
    public abstract String getHostName();

    /* Sets all the data necessary for the server */
    public abstract void setData(HashMap<String, String> args);

    /* Gets the data in the same form its able to be set in */
    public abstract HashMap<String, String> getData();

    /* Lists the names of all saves on the server */
    public abstract List<String> getSaveNames();

    /* Gets the raw data of the ZIP file from a given name */
    public abstract byte[] getSaveData(String name);

    /* Uploads a zip file with the save file contents */
    public abstract void uploadSaveData(String name, byte[] data) throws Exception;

    /* Verifies if the server is working properly */
    public abstract boolean verifyServer();

    /* Gets the hash of a given save */
    public abstract String getSaveHash(String name);
}
