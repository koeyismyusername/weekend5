package weekend5;

public class SneakersInfo {
    private String modelName;
    private long price;
    private String[] features;
    public SneakersInfo (String modelName, long price, String[] features) {
        this.modelName = modelName;
        this.price = price;
        this.features = features.clone();
    }

    public String getModelName() { return modelName; }
    public long getPrice() { return price; }
    public String[] getFeatures() { return features; }
}
