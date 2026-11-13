import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** 03장 검증: 지연 평가와 단락, 스트림 재사용 예외, toList의 수정 불가. */
public class Ch03Stream {
    record Customer(String email, boolean subscribed) {}

    public static void main(String[] args) {
        AtomicInteger visited = new AtomicInteger();
        Stream<Integer> pipeline = Stream.iterate(1, i -> i + 1).peek(i -> visited.incrementAndGet()).filter(i -> i % 2 == 0);
        check("종단 연산 전에는 아무것도 실행되지 않는다", visited.get() == 0);
        List<Integer> firstFive = pipeline.limit(5).toList();
        check("limit(5)는 필요한 만큼만 순회한다", firstFive.equals(List.of(2, 4, 6, 8, 10)) && visited.get() == 10);

        Stream<String> once = Stream.of("a", "b");
        once.count();
        boolean reused = false;
        try { once.toList(); } catch (IllegalStateException e) { reused = true; }
        check("소비된 스트림을 다시 쓰면 IllegalStateException", reused);

        boolean unmodifiable = false;
        try { Stream.of("a").toList().add("b"); } catch (UnsupportedOperationException e) { unmodifiable = true; }
        check("Stream.toList() 결과는 수정 불가", unmodifiable);
        check("Stream.toList()는 null 요소를 허용", Stream.of("a", null).toList().size() == 2);
        check("Collectors.toList()는 현재 구현에서 ArrayList", Stream.of("a").collect(Collectors.toList()) instanceof ArrayList);

        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("a@example.com", true));
        customers.add(null);
        boolean npe = false;
        try { customers.stream().filter(Customer::subscribed).map(Customer::email).toList(); } catch (NullPointerException e) { npe = true; }
        check("null 요소에 메서드 참조 filter를 걸면 NPE (null 검사를 빼면 안 된다)", npe);
        System.out.println("Ch03Stream OK");
    }

    static void check(String name, boolean ok) {
        if (!ok) throw new AssertionError("실패: " + name);
        System.out.println("  ok  " + name);
    }
}
