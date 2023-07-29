package weekend5;

import java.util.*;

public class OrderSneakersSituationV2 {
    private static List<Customer> customers = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        final long TODAY_START_SALES_AMOUNT = 0;

        StaffSingleton staff = StaffSingleton.INSTANCE;
        DeliveryManagerSingleton deliveryManager = DeliveryManagerSingleton.INSTANCE;

        staff.readFileAndSetSneakerInfoMap();
        staff.readFileAndSetSneakersStockMap();
        deliveryManager.readFileAndSetDeliveryInfoMap();


        // 고객 대기 등록을 받습니다.
        System.out.println("안녕하세요~, \"고객등급,이름,배송선호 여부,예산,운동화 모델명\" 입력해주세요");

        while (scanner.hasNext()) {
            String response = scanner.nextLine();

            if (response.equals("끝")) {
                break;
            }

            try {
                String[] responseArray = response.split(",");
                CustomerLevel customerLevel = CustomerLevel.valueOf(responseArray[0]);
                String customerName = responseArray[1];
                boolean isCustomerLikeDelivery = Boolean.parseBoolean(responseArray[2]);
                Long cache = Long.parseLong(responseArray[3]);
                String sneakersModelName = responseArray[4];

                // 고객 대기 목록 명단에 고객 객체 넣어야합니다.
                customers.add(new Customer(customerLevel, customerName, isCustomerLikeDelivery, cache, sneakersModelName));
                System.out.println("고객 대기 리스트 등록 완료되었습니다. " + customers);

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("입력 형식이 잘못 되어 고객 등록 넘어갑니다.");
            }
        }
        // 이후 작업 이어서 진행 해주세요.

        ListIterator lit = customers.listIterator();
        while (lit.hasNext()) {
            Customer customer = (Customer) lit.next();
            final String customerName = customer.getName();
            final String sneakersModelName = customer.getSneakersModelName();
            boolean useDelivery = false;

            // '고객'은 '매장 직원'에게 "{운동화 모델명}에 대해 알려주세요"라고 물어봅니다.
            System.out.println(String.format("%s: %s에 대해 알려주세요.", customerName, sneakersModelName));

            // '매장 직원'은 'Nike 운동화 모델 특성표' 확인하고, 해당 운동화의 특성과 가격을 자세히 설명해줍니다.
            // 만약 '고객'이 매장에 없는 "{운동화 모델명}"을 주문하면, '예외'가 발생하고 '고객'은 '다음에 올게요'라고 하고 자신의 상태를 말하고 해당 고객의 상황은 종료됩니다.
            // 직원: 이  Air Max 270 운동화는[트렌디함] 의 특징들과 180000의 가격을 가지고 있습니다
            SneakersInfo sneakersInfo = staff.getSheakersInfoByName(sneakersModelName);
            if (sneakersInfo == null) {
                System.out.println("직원: 손님 해당 모델명은 현재 없습니다.");
                customer.leave();
                continue;
            }

            final String[] features = sneakersInfo.getFeatures();
            final long sneakersPrice = sneakersInfo.getPrice();
            System.out.println(String.format("직원: 이 %s 운동화는 %s 의 특징들과 %d의 가격을 가지고 있습니다.", sneakersModelName, Arrays.toString(features), sneakersPrice));

            customer.requestSneakersDisCount();
            final long sneakersPayment = staff.calculateSneakersPayment(customer.getLevel(), sneakersPrice);

            // 4. '고객'은 '매장 직원'에게 가격을 얻어낸 후, 자신의 결제 가능 여부를 확인한다.
            // 4-1. 만약 예산 부족으로 결제 가능하지 않다면, '고객'은 '다음에 올게요'라고 하고 자신의 상태를 말하고 해당 고객의 상황은 종료됩니다.
            if (!customer.canPay(sneakersPayment)) {
                customer.leave();
                continue;
            }

            // 5. '고객'이 결제가 가능하다면 '매장 직원'은 Nike 매장의 '운동화의 재고 현황'를 확인합니다.
            // 5-1.  만약 "{운동화 모델명}" 대한 재고가 있는 경우, '매장 직원'은 해당 모델 재고가 있음을 말합니다.
            if (!staff.hasSneakersStock(sneakersModelName)) {
                // 6. '매장 직원'은 '고객'에게 "{운동화 모델명}" 대한 재고 부족을 안내하고 물품배송 요청 여부를 안내합니다.
                System.out.println("이런.. 매장에 재고가 없네요, 배송으로 안내 드리겠습니다.");

                // 6-1: 만약 '고객'의 배송 주문을 선호하지 않는 사람일 경우, '고객'은 '다음에 올게요'라고 하고 자신의 상태를 말하고 해당 고객의 상황은 종료됩니다.
                if (!customer.getIsLikeDelivery()) {
                    customer.leave();
                    continue;
                }

                useDelivery = true;
            }

            // 손님: 네 좋네요, Air Max 270 주문 계속 진행할게요.
            customer.accept();

            // 직원: 고객님 Air Max 270 주문 도와드리겠습니다. 가격은 180000 입니다.
            System.out.println(String.format("직원: 고객님 %s 주문 도와드리겠습니다. 가격은 %d원입니다.", sneakersModelName, sneakersPayment));

            // 5-1-1: '매장 직원'은 '고객'에게 신발가격 결제를 안내합니다.
            staff.saleSneakers(customer, sneakersInfo, sneakersPayment, useDelivery);

            // 7-4: '매장 직원'은 운동화를 배송하기 위해 '배송 담당자'에게 "{운동화 모델명}" 배송 요청합니다.
            // 7-5: '배송 담당자'는 해당 '{운동화 모델명}'에 해당하는 택배 패키지의 '배송 예정소요 일자'와 '배송료'를 '매장직원'에게 응답 해줍니다.
            if (!staff.hasSneakersStock(sneakersModelName)) {
                DeliveryInfo deliveryInfo = deliveryManager.getDeliveryInfoBySneakersModelName(sneakersModelName);
                long deliveryHours = deliveryInfo.getDeliveryHours();
                long deliveryCost = deliveryInfo.getCost();

                // 7-6: '매장 직원'은 '배송 예정 소요일자'와 '배송료'를 말해주고, '배송료'을 '고객'에게 전달합니다.
                // 직원: 고객님 배송은 5 시간 걸릴 예정이고, 배송 금액은 20000 소요되십니다.
                System.out.println(String.format("직원: 고객님 배송은 %d시간 걸릴 예정이고, 배송 금액은 %d원 소요되십니다.", deliveryHours, deliveryCost));

                customer.requestDeliveryDiscount();
                long deliveryPayment = staff.calculateDeliveryPayment(deliveryCost, customer.getLevel());
                // 직원: 현재 예상 배송료는 25000 입니다.
                System.out.println(String.format("직원: 현재 예상 배송료는 %d원입니다.", deliveryPayment));

                // 7-6-1: 만약 배송료 + 신발가격이 고객의 잔고보다 많은 경우
                if (!customer.canPay(deliveryPayment)) {
                    // 7-6-1-0: '고객'은 기분이 나빠지고, 신발 가격을 환불 요청합니다.
                    customer.setFeeling("나쁨");
                    staff.refoundPayment(customer, sneakersInfo, sneakersPayment);

                    // 7-6-1-3: '고객'은 '다음에 올게요'라고 하고 자신의 상태를 말하고 상황이 종료됩니다.
                    customer.leave();
                    continue;
                }

                // 7-6-2: 만약 배송료 + 신발가격이 고객의 잔고보다 많지 않은 경우
                // 7-6-2-0: '고객'은 주문 계속 진행합니다.
                customer.accept();

                // 7-6-2-2: '배송 담당자'는 '고객'에게 택배 패키지를 전달합니다.
                deliveryManager.delivery(customer, sneakersInfo, deliveryPayment);

                // 7-6-2-6: '고객'은 택배 패키지의 운동화를 신고, 운동화의 특징을 경험하며 기분이 좋아집니다. 자신의 상태를 말하고 해당 고객의 상황은 종료됩니다.
                customer.setFeeling("좋음");
                continue;
            }

            // 5-1-5: '매장 직원'은 운동화를 찾아 '고객'에게 전달합니다.
            staff.giveSneakers(customer, sneakersInfo);
        }

        // '매장 직원'과 '배송 담당자'는 'Nike 운동화' 금일 정산을 진행합니다.
        staff.saveTodaySales();
        deliveryManager.saveTodaySales();

        // 9. '매장 직원'이 정산 후 남은 재고 현황을 기록합니다.
        staff.saveSneakersStocks();
    }
}