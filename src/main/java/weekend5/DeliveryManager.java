package weekend5;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class DeliveryManager {
    private Map<String, DeliveryInfo> deliveryInfoMap;
    private long salesAmount;

    public DeliveryManager(long salesAmount) {
        this.salesAmount = salesAmount;
        this.deliveryInfoMap = new HashMap<>();
    }
    public void readFileAndSetDeliveryInfoMap(){
        try(BufferedReader fis = new BufferedReader(new FileReader("src/mission/inputs/nike-sneaker-delivery-infos.txt"))){

            String line;
            while(true){
                line = fis.readLine();
                if(line == null) break;

                String[] strArray = line.split("\\|");
                String modelName = strArray[0];
                int deliveryHours = Integer.parseInt(strArray[1]);
                long cost = Long.parseLong(strArray[2]);

                DeliveryInfo deliveryInfo = new DeliveryInfo(modelName, deliveryHours, cost);
                deliveryInfoMap.put(modelName, deliveryInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("배송 담당자: 배달 정보 다음과 같이 숙지하였습니다. " + this.deliveryInfoMap);
    }


    public DeliveryInfo getDeliveryInfoBySneakersModelName(String sneakersModelName) {
        return deliveryInfoMap.get(sneakersModelName);
    }

    public void delivery(Customer customer, SneakersInfo sneakersInfo, long payment) {
        // 배송 담당자: Sneaker 배송 완료되었습니다.
        System.out.println("배송 담당자: Sneakers 배송 완료되었습니다.");

        if (payment <= 0) return;

        // 7-6-2-3: '고객'은 '배송 담당자' 에게 배송료를 지불합니다.
        // 배송 담당자: 고객님 배송 결제 도와드리겠습니다. 20000 입니다.
        System.out.println(String.format("배송 담당자: 고객님 배송 결제 도와드리겠습니다. %d원입니다.", payment));
        customer.pay(this, payment);

        String sneakersModelName = sneakersInfo.getModelName();
        String customerLevel = customer.getLevel().name();
        String customerName = customer.getName();
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // TODO: 7-6-2-5: '배송 담당자'은 '자신의 배송기록표'에 '배송완료|{운동화 모델명}|{배송료 가격}|{손님 등급}|{손님 이름}|{현재시각}'을 입력합니다.
        try (
                BufferedWriter bw = new BufferedWriter(new FileWriter("src/mission/outputs/delivery-manager-record.txt", true))
        ) {
            bw.append(String.format("DELIVERY_DONE|%s|%d|%s|%s|%s", sneakersModelName, payment, customerLevel, customerName, dateTime));
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recivePayment(long payment) {
        // 7-6-2-4: '배송 담당자'는 '자신의 매상'에 돈을 더합니다.
        salesAmount += payment;
        System.out.println(String.format("배송 담당자: 현금 %d원 확인했습니다.", payment));
    }

    public void saveTodaySales() {
        // TODO: 8-2. '배송 담당자'는 'today-sales.txt'에 금일 매상을 'delivery-manager | {자신의 매상}'으로 기록합니다.
        try (
                BufferedWriter bw = new BufferedWriter(new FileWriter("src/mission/outputs/today-sales.txt", true))
        ) {
            bw.append(String.format("delivery-manager|%d", salesAmount));
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
