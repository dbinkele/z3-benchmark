package z3;

import com.microsoft.z3.BoolExpr;
import org.smtlib.IExpr;

import java.util.stream.Stream;

public class BenchmarkJniTest {

    private static final int TIMES = 12;
    private static final int NUMBER_COLORS = 2;
    private static final int SEED = 6066;//0.0844f;
    private static final float EDGE_PERCENT = 0.99f;//20;
    private static final int VERTICES = 1100;

    @org.junit.Test
    public void benchJni() {

        long start = System.currentTimeMillis();
        BenchmarkJni<BoolExpr> benchmark =
                new BenchmarkJni<>(ModelMaker::makeZ3, new GraphGen(VERTICES, EDGE_PERCENT, SEED), NUMBER_COLORS);
        Stream.generate(() -> 0).limit(TIMES)
                .forEach(x -> benchmark.benchmark());
        long end = System.currentTimeMillis();
        System.out.println("Dur: " + (end-start) / TIMES);
    }


    @org.junit.Test
    public void benchJsmtlib() {
        long start = System.currentTimeMillis();
        BenchmarkJni<IExpr> benchmark =
                new BenchmarkJni<>(ModelMaker::makejSMTLIB, new GraphGen(VERTICES, EDGE_PERCENT, SEED), NUMBER_COLORS);
        Stream.generate(() -> 0).limit(TIMES)
                .forEach(x -> benchmark.benchmark());
        long end = System.currentTimeMillis();
        System.out.println("Dur: " + (end-start) / TIMES);
    }
}