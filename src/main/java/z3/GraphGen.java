package z3;

import org.jgrapht.Graph;
import org.jgrapht.generate.GnmRandomGraphGenerator;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.util.HashMap;
import java.util.Random;
import java.util.function.Supplier;

public class GraphGen implements Supplier<Graph<Integer, DefaultEdge>>{


    private final Random rand;
    private final int vertices;
    private final float edgePercent;

    public GraphGen(int vertices, float edgePercent, int seed) {
        this.vertices = vertices;
        this.edgePercent = edgePercent;
        this.rand = new Random(seed);
    }


    private Graph<Integer, DefaultEdge> randomGraph(int n, float percent) {
        int edges = (int) ((n * (n - 1) / 2) * percent);
        final int[] i = {0};
        Supplier<Integer> vs = () -> i[0]++;
        Graph<Integer, DefaultEdge> graph = new DefaultUndirectedGraph<>(vs, DefaultEdge::new, false);
        GraphGenerator<Integer, DefaultEdge, Integer> grapGenerator = new GnmRandomGraphGenerator<>(n, edges, this.rand.nextInt());
        grapGenerator.generateGraph(graph, new HashMap<>());
        return graph;
    }

    @Override
    public Graph<Integer, DefaultEdge> get() {
        return randomGraph(this.vertices, this.edgePercent);
    }
}
