package weekend5;

public enum CustomerLevel {

    VIP("vip 등급 고객", 0.2, 1.0),
    GOLD("gold 등급 고객", 0.1, 0.5),
    SILVER("silver 등급 고객", 0.0, 0.0);

    private final String koreanName;
    private final double sneakersDiscountRate;
    private final double deliveryDiscountRate;

    CustomerLevel(String koreanName, double sneakersDiscountRate, double deliveryDiscountRate) {
        this.koreanName = koreanName;
        this.sneakersDiscountRate = sneakersDiscountRate;
        this.deliveryDiscountRate = deliveryDiscountRate;
    }

    public String getKoreanName() {
        return koreanName;
    }

    @Override
    public String toString() { return name(); }
    public double getSneakersDiscountRate() { return sneakersDiscountRate; }
    public double getDeliveryDiscountRate() { return deliveryDiscountRate; }
}
