package com.github.nitrogen2oxygen.savefilesync.server;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class WebDavDataServer extends DataServer {

    private static final long serialVersionUID = -2218215115068183298L;
    private String username;
    private String password;
    private String uri;

    public WebDavDataServer() {
        super();
    }

    @Override
    public ServerType getServerType() {
        return ServerType.WEBDAV;
    }

    @Override
    public String getHostName() {
        try {
            if (uri.length() == 0) return null;
            return new URL(uri).getHost();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public void setData(HashMap<String, String> args) {
        uri = args.get("uri");
        username = args.get("username");
        password = args.get("password");
    }

    @Override
    public HashMap<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uri", uri);
        data.put("username", username);
        data.put("password", password);
        return data;
    }

    @Override
    public List<String> getSaveNames()  {
        return new ArrayList<>();
    }

    @Override
    public byte[] getSaveData(String name) {
        try {
            HttpURLConnection connection = (HttpURLConnection) getSaveURL(name + ".zip").openConnection();
            connection.setRequestMethod("GET");
            if (username != null) connection.setRequestProperty("Authorization", getAuthorization());

            /* Handle request */
            InputStream stream = connection.getInputStream();
            return IOUtils.toByteArray(stream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void uploadSaveData(String name, byte[] data) throws Exception {
            HttpURLConnection connection = (HttpURLConnection) getSaveURL(name + ".zip").openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            if (username != null) connection.setRequestProperty("Authorization", getAuthorization());

            /* Handle request */
            OutputStream stream = connection.getOutputStream();
            stream.write(data);
            stream.close();
            connection.getInputStream();
    }

    @Override
    public boolean verifyServer() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(uri).openConnection();
            connection.setRequestMethod("HEAD");
            if (username != null) connection.setRequestProperty("Authorization", getAuthorization());

            /* Handle response code */
            int code = connection.getResponseCode();
            return code < 300;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getSaveHash(String name) {
        return null;
    }

    private URL getSaveURL(String fileName) throws IOException, URISyntaxException {
        URI base = new URL(this.uri).toURI();
        String path = base.getPath() + "/" + fileName.replace(" ", "%20"); // Replace the space first to bypass stupid java using a +
        return base.resolve(path).toURL();
    }

    private String getAuthorization() {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedAuth);
    }
}
