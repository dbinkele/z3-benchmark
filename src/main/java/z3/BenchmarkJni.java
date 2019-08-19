package z3;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BenchmarkJni<B> {

    private GraphGen grapGen;
    private int numberColors;
    private Supplier<ModelMaker<B>> modelMakerSupplier;

    BenchmarkJni(Supplier<ModelMaker<B>> modelMakerSupplier, GraphGen grapGen, int numberColors) {
        this.modelMakerSupplier = modelMakerSupplier;
        this.grapGen = grapGen;

        this.numberColors = numberColors;
    }

    void benchmark() {
        ModelMaker<B> context = this.modelMakerSupplier.get();
        Solverle<B> solver = context.mkSolver();
        B boolExpr = make3ColouringSat(grapGen, context);

        int check = solver.check(boolExpr);
        System.out.println("check = " + check);
    }



    private B make3ColouringSat(Supplier<Graph<Integer, DefaultEdge>> grapGen, ModelMaker<B> context) {

        Graph<Integer, DefaultEdge> graph = grapGen.get();
        //System.out.println("vertices " + graph.vertexSet().size() + " edges " + graph.edgeSet().size());

        List<Map<Integer, B>> integerToColorVars = Stream.iterate(0, (x) -> x + 1).limit(this.numberColors)
                .map(x -> makeVertexToVar(context, graph, x))
                .collect(Collectors.toList());


        B andOfOrClauses = makeAndOfOrClauses(context, graph, integerToColorVars);

        List<B> colorNandClases = integerToColorVars.stream()
                .flatMap(theMap -> graph.edgeSet().stream()
                        .map(e -> toNandColorClause(e, graph, context, theMap)))
                .collect(Collectors.toList());

        if (colorNandClases.isEmpty())
            return andOfOrClauses;
        return context.mkAnd(Arrays.asList(andOfOrClauses, context.mkAnd(colorNandClases)));
    }

    private B toNandColorClause(DefaultEdge e, Graph<Integer, DefaultEdge> graph, ModelMaker<B> context, Map<Integer, B> theMap) {
        Integer edgeSource = graph.getEdgeSource(e);
        Integer edgeTarget = graph.getEdgeTarget(e);

        B sourceVar = theMap.get(edgeSource);
        B targetVar = theMap.get(edgeTarget);

        return context.mkNot(context.mkAnd(Arrays.asList(sourceVar, targetVar)));
    }

    private B makeAndOfOrClauses(ModelMaker<B> context, Graph<Integer, DefaultEdge> graph, List<Map<Integer, B>> integerToColorVars) {
        List<B> orClauses = graph.vertexSet().stream()
                .map(x -> toColorOrClause(integerToColorVars, x, context))
                .collect(Collectors.toList());

        return context.mkAnd(orClauses);
    }

    private B toColorOrClause(List<Map<Integer, B>> integerToColorVars, Integer x, ModelMaker<B> context) {
        return context.mkOr(
                integerToColorVars.stream()
                .map(theMap -> theMap.get(x))
                .collect(Collectors.toList()));
    }

    private Map<Integer, B> makeVertexToVar(ModelMaker<B> context, Graph<Integer, DefaultEdge> graph, Integer color) {
        return graph.vertexSet().stream()
                .collect(Collectors.toMap(x -> x, x -> context.mkBoolConst("v_"+ x.toString() + color)));
    }
}
