package weekend5;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum StaffSingleton {
    INSTANCE(0);
    private static final long TODAY_START_SALES_AMOUNT = 0;
    private Map<String, SneakersInfo> sneakersInfoMap;
    private Map<String, Long> sneakersStockMap;

    private List<SaleInfo> saleInfoList;
    private long salesAmount;

    StaffSingleton(long salesAmount){
        this.sneakersInfoMap = new HashMap<>();
        this.sneakersStockMap = new HashMap<>();
        this.saleInfoList = new ArrayList<>();
        this.salesAmount = salesAmount;
    }

    public void readFileAndSetSneakerInfoMap() {
        try (BufferedReader fis = new BufferedReader(new FileReader("src/mission/inputs/nike-sneaker-characters.txt"))) {

            String line;
            while (true) {
                line = fis.readLine();
                if (line == null) break;

                String[] strArray = line.split("\\|");
                String modelName = strArray[0];
                long price = Long.parseLong(strArray[1]);
                String[] features = strArray[2].split(",");

                SneakersInfo sneakersInfo = new SneakersInfo(modelName, price, features);
                sneakersInfoMap.put(modelName, sneakersInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("직원: 운동화 정보 다음과 같이 숙지하였습니다. " + this.sneakersInfoMap);
    }

    public void readFileAndSetSneakersStockMap() {
        try (BufferedReader fis = new BufferedReader(new FileReader("src/mission/inputs/nike-sneaker-stocks.txt"))) {

            String line;
            while (true) {
                line = fis.readLine();
                if (line == null) break;

                String[] strArray = line.split("\\|");
                String modelName = strArray[0];
                long stock = Long.parseLong(strArray[1]);

                sneakersStockMap.put(modelName, stock);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("직원: 운동화 재고 정보 다음과 같이 숙지하였습니다. " + this.sneakersStockMap);
    }

    public SneakersInfo getSheakersInfoByName(String shoesName) {
        return sneakersInfoMap.get(shoesName);
    }

    public boolean hasSneakersStock(String sneakersModelName) {
        return sneakersStockMap.get(sneakersModelName) > 0 ? true : false;
    }

    public void saleSneakers(Customer customer, SneakersInfo sneakersInfo, long payment, boolean useDelivery) {
        customer.pay(this, payment);

        // 5-1-4: '매장 직원'은 '자신의 매출전표'에 '판매|{운동화 모델명}|{운동화 가격}|{손님 등급}|{손님 이름}|{현재시각}'을 입력합니다.
        String identity = useDelivery ? "DELIVERY" : "SALE";
        String sneakersModelName = sneakersInfo.getModelName();
        String customerLevel = customer.getLevel().name();
        String customerName = customer.getName();
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try (
                BufferedWriter bw = new BufferedWriter(new FileWriter("src/mission/outputs/staff-sales.txt", true))
        ) {
            bw.append(String.format("%s|%s|%d|%s|%s|%s", identity, sneakersModelName, payment, customerLevel, customerName, dateTime));
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void receivePayment(long payment) {
        System.out.println(String.format("직원: 현금 %d원 확인했습니다.", payment));
        salesAmount += payment;
    }

    public void giveSneakers(Customer customer, SneakersInfo sneakersInfo) {
        // 5-1-6: '매장 직원'은 '운동화 재고현황'에 해당 모델 수량 출고를 숙지합니다.
        String sneakersModelName = sneakersInfo.getModelName();
        long stock = sneakersStockMap.get(sneakersModelName);
        stock -= 1;
        if (stock < 0) stock = 0;

        sneakersStockMap.put(sneakersModelName, stock);
        customer.receiveSneakers(sneakersInfo);
    }

    public void refoundPayment(Customer customer, SneakersInfo sneakersInfo, long payment) {
        // 7-6-1-1: '매장 직원'은 신발 가격을 다시 매상에서 빼고, '고객'은 환불 받습니다.
        salesAmount -= payment;
        customer.receiveRefound(payment);

        String sneakersModelName = sneakersInfo.getModelName();
        String customerLevel = customer.getLevel().name();
        String customerName = customer.getName();
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // 7-6-1-2: '매장 직원'은 '자신의 매출전표'에 '환불|{운동화 모델명}|{운동화 가격}|{손님 등급}|{손님 이름}|{현재시각}'을 입력합니다.
        try (
                BufferedWriter bw = new BufferedWriter(new FileWriter("src/mission/outputs/staff-sales.txt", true))
        ) {
            bw.append(String.format("REFOUND|%s|%d|%s|%s|%s", sneakersModelName, payment, customerLevel, customerName, dateTime));
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long calculateSneakersPayment(CustomerLevel level, long sneakersPrice) {
        double discountRate = level.getSneakersDiscountRate();
        return Math.round(sneakersPrice * (1 - discountRate));
    }

    public long calculateDeliveryPayment(long deliveryCost, CustomerLevel level) {
        return Math.round(deliveryCost * (1 - level.getDeliveryDiscountRate()));
    }

    public void saveTodaySales() {
        // 8-1. '매장 직원'은 'today-sales.txt'에 금일 매상 'staff | {자신의 매상}'으로 기록합니다.
        try (
                BufferedWriter bw = new BufferedWriter(new FileWriter("src/mission/outputs/today-sales.txt", true))
        ) {
            bw.append(String.format("staff|%d", salesAmount));
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveSneakersStocks() {
        try (
                BufferedWriter bw = new BufferedWriter(new FileWriter("src/mission/outputs/nike-sneaker-stocks-2.txt", true))
        ) {
            sneakersStockMap.forEach((sneakersModelName, stock)->{
                try {
                    bw.append(String.format("%s|%d", sneakersModelName, stock));
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
