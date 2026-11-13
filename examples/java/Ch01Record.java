import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** 01장 검증: record의 자동 생성 멤버, 얕은 불변성, List.copyOf의 동작. */
public class Ch01Record {
    record BookingRequest(String bookingId, Long customerId, int ticketCount, BigDecimal unitPrice) {}
    record Leaky(String orderId, List<String> items) {}
    record Defensive(String orderId, List<StringBuilder> items) {
        Defensive { items = (items == null) ? List.of() : List.copyOf(items); }
    }

    public static void main(String[] args) throws Exception {
        var a = new BookingRequest("BK-101", 1L, 2, BigDecimal.valueOf(50000));
        var b = new BookingRequest("BK-101", 1L, 2, BigDecimal.valueOf(50000));
        check("toString 형식", a.toString().equals("BookingRequest[bookingId=BK-101, customerId=1, ticketCount=2, unitPrice=50000]"));
        check("값 기반 equals/hashCode", a.equals(b) && a.hashCode() == b.hashCode() && a != b);

        List<String> src = new ArrayList<>(List.of("ITEM-1"));
        var leaky = new Leaky("ORD-01", src);
        src.add("ITEM-2");
        check("방어적 복사가 없으면 외부 변경이 record 안으로 샌다", leaky.items().size() == 2);

        var sb = new StringBuilder("ITEM-1");
        var defensive = new Defensive("ORD-01", new ArrayList<>(List.of(sb)));
        check("List.copyOf 결과는 수정 불가", throwsType(UnsupportedOperationException.class, () -> defensive.items().add(new StringBuilder())));
        sb.append("-CHANGED");
        check("List.copyOf는 얕은 복사: 요소 객체는 여전히 변경 가능", defensive.items().get(0).toString().equals("ITEM-1-CHANGED"));
        check("List.copyOf는 null 요소를 거부", throwsType(NullPointerException.class, () -> List.copyOf(Arrays.asList("a", null))));

        var field = BookingRequest.class.getDeclaredField("ticketCount");
        field.setAccessible(true);
        check("record 필드는 리플렉션으로도 수정 불가", throwsType(IllegalAccessException.class, () -> field.setInt(a, 99)));
        System.out.println("Ch01Record OK");
    }

    interface Action { void run() throws Exception; }
    static boolean throwsType(Class<? extends Throwable> type, Action action) {
        try { action.run(); return false; } catch (Throwable t) { return type.isInstance(t); }
    }
    static void check(String name, boolean ok) {
        if (!ok) throw new AssertionError("실패: " + name);
        System.out.println("  ok  " + name);
    }
}
