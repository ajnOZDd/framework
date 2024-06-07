public class ModelView {
    private String url;
    private HashMap<String, Object> data;

    public ModelView(String url) {
        this.url = url;
        this.data = new HashMap<>();
    }

    public void addObject(String name, Object value) {
        this.data.put(name, value);
    }

    public String getUrl() {
        return this.url;
    }

    public HashMap<String, Object> getData() {
        return this.data;
    }
}
