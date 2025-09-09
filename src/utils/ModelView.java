package utils;

import java.util.HashMap;
import java.util.Map;

public class ModelView {
    private String url;
    private Map<String, Object> data;
    
    public ModelView() {
        this.data = new HashMap<>();
    }
    
    public ModelView(String url) {
        this.url = url;
        this.data = new HashMap<>();
    }
    
    // Setters et getters
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    
    // Méthode pour ajouter des données
    public void add(String key, Object value) {
        this.data.put(key, value);
    }
    
    // Méthode pour récupérer une donnée
    public Object get(String key) {
        return this.data.get(key);
    }
}