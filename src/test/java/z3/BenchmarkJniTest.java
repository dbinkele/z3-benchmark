package z3;

import com.microsoft.z3.BoolExpr;
import org.smtlib.IExpr;

import java.util.stream.Stream;

public class BenchmarkJniTest {

    public static final int TIMES = 1200;

    @org.junit.Test
    public void benchJni() {

        long start = System.currentTimeMillis();
        BenchmarkJni<BoolExpr> benchmark = new BenchmarkJni<>(ModelMaker::makeZ3);
        Stream.generate(() -> 0).limit(TIMES)
                .forEach(x -> benchmark.benchmark());
        long end = System.currentTimeMillis();
        System.out.println("Dur: " + (end-start));
    }


    @org.junit.Test
    public void benchJsmtlib() {
        long start = System.currentTimeMillis();
        BenchmarkJni<IExpr> benchmark = new BenchmarkJni<>(ModelMaker::makejSMTLIB);
        Stream.generate(() -> 0).limit(TIMES)
                .forEach(x -> benchmark.benchmark());
        long end = System.currentTimeMillis();
        System.out.println("Dur: " + (end-start));
    }
}