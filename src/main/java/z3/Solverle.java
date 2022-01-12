package z3;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Solver;
import org.smtlib.IExpr;
import org.smtlib.IResponse;
import org.smtlib.solvers.Solver_z3_4_3;

public interface Solverle<B> {

    int check(B boolExpr);


    static Solverle<BoolExpr> makeZ3(Solver solver
    ) {
        return boolExpr -> {
            solver.add(boolExpr);
            return solver.check().toInt();
        };
    }




    static Solverle<IExpr> makejSMTLI(IResponse sat, IResponse unsat, final Solver_z3_4_3 solver) {
        return boolExpr -> {
            IResponse iResponse = solver.assertExpr(boolExpr);
        //    System.out.println("iResponse = " + iResponse);
            IResponse res = solver.check_sat();
          //  System.out.println("res = " + res);
            return res.equals(sat) ? 1 : res.equals(unsat) ? -1 : 0;
        };
    }
}
