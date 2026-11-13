import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** 07장 검증: putIfAbsent의 반환값과 원자성, computeIfPresent의 제거, 재귀 갱신 예외. */
public class Ch07Concurrency {
    public static void main(String[] args) throws Exception {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        check("putIfAbsent: 키가 없었으면 null 반환", map.putIfAbsent("A1", "u1") == null);
        check("putIfAbsent: 키가 있었으면 기존 값 반환", "u1".equals(map.putIfAbsent("A1", "u2")));
        map.computeIfPresent("A1", (k, v) -> v.equals("u1") ? null : v);
        check("computeIfPresent가 null을 반환하면 엔트리 제거", !map.containsKey("A1"));

        ExecutorService pool = Executors.newFixedThreadPool(8);
        boolean alwaysOneWinner = true;
        for (int round = 0; round < 5000 && alwaysOneWinner; round++) {
            ConcurrentHashMap<String, String> seats = new ConcurrentHashMap<>();
            CountDownLatch start = new CountDownLatch(1);
            List<Future<Boolean>> results = new ArrayList<>();
            for (int t = 0; t < 4; t++) {
                String user = "u" + t;
                results.add(pool.submit(() -> { start.await(); return seats.putIfAbsent("A1", user) == null; }));
            }
            start.countDown();
            int winners = 0;
            for (Future<Boolean> f : results) if (f.get()) winners++;
            alwaysOneWinner = winners == 1;
        }
        pool.shutdown();
        check("putIfAbsent로는 동시에 시도해도 승자가 항상 1명", alwaysOneWinner);

        ConcurrentHashMap<String, Integer> recursive = new ConcurrentHashMap<>();
        boolean rejected = false;
        try { recursive.computeIfAbsent("a", k -> recursive.computeIfAbsent("a", k2 -> 1)); } catch (IllegalStateException e) { rejected = true; }
        check("computeIfAbsent 안에서 같은 키를 다시 갱신하면 IllegalStateException", rejected);
        System.out.println("Ch07Concurrency OK");
    }

    static void check(String name, boolean ok) {
        if (!ok) throw new AssertionError("실패: " + name);
        System.out.println("  ok  " + name);
    }
}
