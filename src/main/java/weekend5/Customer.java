package weekend5;

import java.util.Arrays;

public class Customer {

    private CustomerLevel level;
    private String name;
    private boolean isLikeDelivery;
    private Long cache;
    private String sneakersModelName;

    private String feeling;

    public Customer(CustomerLevel level, String name, boolean isLikeDelivery, Long cache, String sneakersModelName) {
        this.level = level;
        this.name = name;
        this.isLikeDelivery = isLikeDelivery;
        this.cache = cache;
        this.sneakersModelName = sneakersModelName;
        this.feeling = "보통";
    }

    public CustomerLevel getLevel() { return level; }
    public String getName() { return name; }
    public boolean getIsLikeDelivery() { return isLikeDelivery; }
    public Long getCache() { return cache; }
    public String getSneakersModelName() { return sneakersModelName; }
    public void setFeeling(String feeling) { this.feeling = feeling; }

    public void printStatus() {
        System.out.println(String.format("기분이 %s이고, 잔액 %d원 남았습니다.", feeling, cache));
    }

    public boolean canPay(long payment) { return cache >= payment; }
    public long pay(Staff staff, long payment) {
        cache -= payment;
        staff.receivePayment(payment);
        return payment;
    }
    public long pay(DeliveryManager deliveryManager, long payment) {
        cache -= payment;
        deliveryManager.recivePayment(payment);
        return payment;
    }

    public void requestSneakersDisCount() {
        switch (level) {
            case VIP:
            case GOLD:
                System.out.println(String.format("%s: 저 %s 등급이어서 운동화 할인 %.1f 되는 걸로 알고 있습니다.", name, level.name(), level.getSneakersDiscountRate()));
            default:
                break;
        }
    }

    public void receiveSneakers(SneakersInfo sneakersInfo) {
        feeling = "좋음";

        // 손님: 이 신발은 [트렌디함] 의 특징이 느껴지네요, 기분이 좋음 이고, 잔액 40000 남았습니다.
        System.out.print(String.format("%s: 이 신발은 %s의 특징이 느껴지네요, ", name, Arrays.toString(sneakersInfo.getFeatures())));
        printStatus();
    }
    public void accept() {
        System.out.println(String.format("%s: 네 좋네요, %s 주문 계속 진행할게요.", name, sneakersModelName));
    }

    public void leave() {
        System.out.printf("%s: 다음에 올게요. ", name);
        printStatus();
    }

    public void receiveRefound(long payment) {
        cache += payment;
    }

    public void requestDeliveryDiscount() {
        switch (level) {
            case VIP:
            case GOLD:
                System.out.println(String.format("%s: 저 %s 등급이어서 배송 할인 %.1f 되는 걸로 알고 있습니다.", name, level.name(), level.getDeliveryDiscountRate()));
            default:
                break;
        }
    }
}
