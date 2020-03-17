package z3;

import com.microsoft.z3.BoolExpr;
import org.junit.Before;
import org.smtlib.IExpr;

import java.util.stream.Stream;

public class BenchmarkJniTest {

//    private static final int TIMES = 12;
//    private static final int NUMBER_COLORS = 2;
//    private static final int SEED = 6066;//0.0844f;
//    private static final float EDGE_PERCENT = 0.99f;//20;
//    private static final int VERTICES = 1100;
    private GraphGen graphGen;
    private Scenario scenario;

    @Before
    public void setup(){
        scenario = new Scenario(1200, 4, 6066, 0.0844f, 99);
                //new Scenario(12, 2, 6066,0.99f, 1100);
        graphGen = new GraphGen(scenario.VERTICES, scenario.EDGE_PERCENT, scenario.SEED);
    }

    @org.junit.Test
    public void benchJni() {

        long start = System.currentTimeMillis();
        BenchmarkJni<BoolExpr> benchmark =
                new BenchmarkJni<>(ModelMaker::makeZ3, this.graphGen, scenario.NUMBER_COLORS);
        Stream.generate(() -> 0).limit(scenario.TIMES)
                .forEach(x -> benchmark.benchmark());
        long end = System.currentTimeMillis();
        System.out.println("Dur: " + (end-start) / scenario.TIMES);
    }


    @org.junit.Test
    public void benchJsmtlib() {
        long start = System.currentTimeMillis();
        BenchmarkJni<IExpr> benchmark =
                new BenchmarkJni<>(ModelMaker::makejSMTLIB, this.graphGen, scenario.NUMBER_COLORS);
        Stream.generate(() -> 0).limit(scenario.TIMES)
                .forEach(x -> benchmark.benchmark());
        long end = System.currentTimeMillis();
        System.out.println("Dur: " + (end-start) / scenario.TIMES);
    }


    public static class Scenario {
        public  final int TIMES;
        public  final int NUMBER_COLORS;
        public  final int SEED;
        public  final float EDGE_PERCENT;
        public  final int VERTICES;

        public Scenario(int times, int number_colors, int seed, float edge_percent, int vertices) {
            TIMES = times;
            NUMBER_COLORS = number_colors;
            SEED = seed;
            EDGE_PERCENT = edge_percent;
            VERTICES = vertices;
        }
    }
}