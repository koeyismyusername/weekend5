package weekend5;

public class DeliveryInfo {
    private String modelName;
    private int deliveryHours;
    private long cost;

    public DeliveryInfo(String modelName, int deliveryHours, long cost) {
        this.modelName = modelName;
        this.deliveryHours = deliveryHours;
        this.cost = cost;
    }

    public String getModelName() { return modelName; }
    public int getDeliveryHours() { return deliveryHours;}
    public long getCost() { return cost; }
}
