import java.util.Objects;

/** 06장 검증: 문자열과 래퍼 객체의 == 비교, 캐시 범위, 삼항 연산자 언박싱. */
public class Ch06Equality {
    public static void main(String[] args) {
        String dynamic = new String("CONFIRMED");
        check("리터럴끼리는 == 가 true (상수 풀)", "CONFIRMED" == "CONFIRMED".intern());
        check("동적으로 만든 문자열은 == 가 false", dynamic != "CONFIRMED");
        check("equals는 내용 비교", "CONFIRMED".equals(dynamic));
        check("리터럴을 앞에 두면 null에도 안전", !"CONFIRMED".equals(null));

        Long l1 = 127L, l2 = 127L, l3 = 128L, l4 = 128L;
        check("Long 127은 캐시되어 == 가 true", l1 == l2);
        check("Long 128은 == 가 false", l3 != l4);
        check("Objects.equals는 값 비교", Objects.equals(l3, l4));

        Integer nothing = null;
        boolean condition = false;
        boolean npe = false;
        try { Integer r = condition ? 0 : nothing; } catch (NullPointerException e) { npe = true; }
        check("삼항 연산자에 int와 Integer가 섞이면 null 언박싱 NPE", npe);
        Integer safe = condition ? Integer.valueOf(0) : nothing;
        check("양쪽을 Integer로 맞추면 NPE 없음", safe == null);
        System.out.println("Ch06Equality OK");
    }

    static void check(String name, boolean ok) {
        if (!ok) throw new AssertionError("실패: " + name);
        System.out.println("  ok  " + name);
    }
}
