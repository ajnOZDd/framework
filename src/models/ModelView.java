package models;

import java.util.HashMap;

public class ModelView {
    private String url;
    private HashMap<String, Object> data;

    public ModelView(String url) {
        this.url = url;
        this.data = new HashMap<>();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void addData(String key, Object value) {
        this.data.put(key, value);
    }

    public HashMap<String, Object> getData() {
        return data;
    }
}