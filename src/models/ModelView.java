package models;
import java.util.HashMap;

public class ModelView {
    private String url ;
    private HashMap <String, Object> data ;

    public void AddObject(String key, Object value) {
    data.put(key, value);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }
}
