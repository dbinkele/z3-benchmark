package z3;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import org.smtlib.IExpr;
import org.smtlib.IResponse;
import org.smtlib.SMT;
import org.smtlib.command.C_declare_fun;
import org.smtlib.impl.Sort;
import org.smtlib.solvers.Solver_z3_4_4;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public interface ModelMaker<B> {

    B mkBoolConst(String var1);

    B mkAnd(List<B> bs);

    B mkOr(List<B> bs);

    B mkNot(B b);


    Solverle<B> mkSolver();


    static ModelMaker<BoolExpr> makeZ3() {
        Context context = new Context(new HashMap<>());
        return new ModelMaker<BoolExpr>() {
            @Override
            public BoolExpr mkBoolConst(String var1) {
                return context.mkBoolConst(var1);
            }

            @Override
            public BoolExpr mkAnd(List<BoolExpr> boolExprs) {
                return context.mkAnd(boolExprs.toArray(new BoolExpr[]{}));
            }

            @Override
            public BoolExpr mkOr(List<BoolExpr> boolExprs) {
                return context.mkOr(boolExprs.toArray(new BoolExpr[]{}));
            }

            @Override
            public BoolExpr mkNot(BoolExpr boolExpr) {
                return context.mkNot(boolExpr);
            }

            @Override
            public Solverle<BoolExpr> mkSolver() {
                return Solverle.makeZ3(context.mkSolver());
            }
        };
    }

    static ModelMaker<IExpr> makejSMTLIB() {
        SMT smt = new SMT();
        IExpr.IFactory exprFactory = smt.smtConfig.exprFactory;
        Solver_z3_4_4 solver = new Solver_z3_4_4(smt.smtConfig, "/Library/z3-4.7.1-x64-osx-10.11.6/bin/z3");
        solver.start();
        solver.set_logic("QF_UF", null);
        return new ModelMaker<IExpr>() {
            @Override
            public IExpr mkBoolConst(String name) {
                IExpr.ISymbol sym = exprFactory.symbol(name);
                IResponse iResponseDeclare = solver.declare_fun(new C_declare_fun(sym, new LinkedList<>(), Sort.Bool()));
                //System.out.println("iResponseDeclare = " + iResponseDeclare);
                return sym;
            }

            @Override
            public IExpr mkAnd(List<IExpr> iExprs) {
                IExpr.IFcnExpr fcn = exprFactory.fcn(AND(), iExprs);
                //solver.assertExpr(fcn);
                return fcn;
            }

            @Override
            public IExpr mkOr(List<IExpr> iExprs) {
                IExpr.IFcnExpr fcn = exprFactory.fcn(OR(), iExprs);
               // solver.assertExpr(fcn);
                return fcn;
            }

            @Override
            public IExpr mkNot(IExpr iExpr) {
                IExpr.IFcnExpr fcn = exprFactory.fcn(NOT(), iExpr);
               // solver.assertExpr(fcn);
                return fcn;
            }

            @Override
            public Solverle<IExpr> mkSolver() {
                IResponse.IFactory responseFactory = smt.smtConfig.responseFactory;
                return Solverle.makejSMTLI(responseFactory.sat(), responseFactory.unsat(),
                        solver);
            }

            private IExpr.ISymbol NOT() {
                return exprFactory.symbol("not");
            }

            private IExpr.ISymbol AND() {
                return exprFactory.symbol("and");
            }

            private IExpr.ISymbol OR() {
                return exprFactory.symbol("or");
            }
        };
    }

}
