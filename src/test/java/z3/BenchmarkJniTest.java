package z3;

import com.microsoft.z3.BoolExpr;
import org.smtlib.IExpr;

import java.util.stream.Stream;

public class BenchmarkJniTest {

    @org.junit.Test
    public void benchJni() {

        BenchmarkJni<BoolExpr> benchmark = new BenchmarkJni<>(ModelMaker::makeZ3);
        Stream.generate(() -> 0).limit(20)
                .forEach(x -> benchmark.benchmark());
    }


    @org.junit.Test
    public void benchJsmtlib() {

        BenchmarkJni<IExpr> benchmark = new BenchmarkJni<>(ModelMaker::makejSMTLIB);
        Stream.generate(() -> 0).limit(1)
                .forEach(x -> benchmark.benchmark());
    }
}