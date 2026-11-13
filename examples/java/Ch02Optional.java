import java.util.NoSuchElementException;
import java.util.Optional;

/** 02장 검증: orElse는 즉시 평가, orElseGet은 지연 평가, 빈 Optional의 get은 예외. */
public class Ch02Optional {
    static int calls = 0;
    static String createDefault() { calls++; return "default"; }

    public static void main(String[] args) {
        Optional<String> present = Optional.of("value");

        calls = 0;
        present.orElse(createDefault());
        check("orElse의 인자는 값이 있어도 실행된다", calls == 1);

        calls = 0;
        present.orElseGet(Ch02Optional::createDefault);
        check("orElseGet의 Supplier는 값이 있으면 실행되지 않는다", calls == 0);

        calls = 0;
        Optional.<String>empty().orElseGet(Ch02Optional::createDefault);
        check("orElseGet의 Supplier는 비어 있을 때 실행된다", calls == 1);

        boolean thrown = false;
        try { Optional.empty().get(); } catch (NoSuchElementException e) { thrown = true; }
        check("빈 Optional의 get은 NoSuchElementException", thrown);

        Optional<String> nullOptional = null;
        boolean npe = false;
        try { nullOptional.isPresent(); } catch (NullPointerException e) { npe = true; }
        check("Optional 변수 자체가 null일 수 있다 (컴파일러가 막지 않는다)", npe);
        System.out.println("Ch02Optional OK");
    }

    static void check(String name, boolean ok) {
        if (!ok) throw new AssertionError("실패: " + name);
        System.out.println("  ok  " + name);
    }
}
