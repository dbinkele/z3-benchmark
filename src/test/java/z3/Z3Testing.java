package z3;


import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FPExpr;
import com.microsoft.z3.FPNum;
import com.microsoft.z3.FPRMSort;
import com.microsoft.z3.FPSort;
import com.microsoft.z3.Model;
import com.microsoft.z3.RatNum;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Z3Testing {

//    static {
//        Path path = Paths.get(".").normalize().toAbsolutePath();
//        String base = path.toString() + "/lib/";
//        try {
//            System.load(base + "z3java");
//        } catch (UnsatisfiedLinkError var1) {
//            System.load(base + "libz3java");
//        }
//
//    }

    @Test
    public void testing() {
        Context ctx = new Context();

        ArithExpr ratNum = (ArithExpr) ctx.mkNumeral("42.03475", ctx.getRealSort());
        ArithExpr ratNum1 = (ArithExpr) ctx.mkNumeral("34.5", ctx.getRealSort());
        ArithExpr x1 = (ArithExpr) ctx.mkConst("x", ctx.getRealSort());

        BoolExpr boolExpr = ctx.mkGe(x1, ratNum);
        Solver solver = ctx.mkSolver();
//        Params p = ctx.mkParams();
//        p.add("rational_to_decimal", true);
//        p.add("precision", 8);
//        solver.setParameters(p);

        Status status = solver.check(boolExpr);
        System.out.println("---> " + status.toInt());
        Model model = solver.getModel();
        RatNum eval_x1 = (RatNum) model.evaluate(x1, false);
        String val = eval_x1.toDecimalString(7);
        BigDecimal bigDecimal = new BigDecimal(((double) eval_x1.getNumerator().getInt() / eval_x1.getDenominator().getInt()));
        BigInteger bigIntDenominator = eval_x1.getBigIntDenominator();
        BigDecimal divisor = new BigDecimal(bigIntDenominator);
        BigDecimal divide = new BigDecimal(eval_x1.getBigIntNumerator()).divide(divisor, MathContext.DECIMAL32);
        int z = 0;

    }

    @Test
    public void floatingPointExample1() {
        Context ctx = new Context();

        System.out.println("FloatingPointExample1");
        //Log.append("FloatingPointExample1");

        FPSort s = ctx.mkFPSortDouble();
        System.out.println("Sort: " + s);

        FPNum x = (FPNum) ctx.mkNumeral("-1e1", s); /* -1 * 10^1 = -10 */
        FPNum y = (FPNum) ctx.mkNumeral("-10", s); /* -10 */
        FPNum z = (FPNum) ctx.mkNumeral("-1.25p3", s); /* -1.25 * 2^3 = -1.25 * 8 = -10 */
        System.out.println("x=" + x.toString() +
                "; y=" + y.toString() +
                "; z=" + z.toString());

        BoolExpr a = ctx.mkAnd(ctx.mkFPEq(x, y), ctx.mkFPEq(y, z));
        check(ctx, ctx.mkNot(a), Status.UNSATISFIABLE);

        /* nothing is equal to NaN according to floating-point
         * equality, so NaN == k should be unsatisfiable. */
        FPExpr k = (FPExpr) ctx.mkConst("x", s);
        FPExpr nan = ctx.mkFPNaN(s);

        /* solver that runs the default tactic for QF_FP. */
        Solver slvr = ctx.mkSolver("QF_FP");
        slvr.add(ctx.mkFPEq(nan, k));
        if (slvr.check() != Status.UNSATISFIABLE)
            throw new IllegalStateException();
        System.out.println("OK, unsat:" + System.getProperty("line.separator") + slvr);

        /* NaN is equal to NaN according to normal equality. */
        slvr = ctx.mkSolver("QF_FP");
        slvr.add(ctx.mkEq(nan, nan));
        if (slvr.check() != Status.SATISFIABLE)
            throw new IllegalStateException();
        System.out.println("OK, sat:" + System.getProperty("line.separator") + slvr);

        /* Let's prove -1e1 * -1.25e3 == +100 */
        x = (FPNum) ctx.mkNumeral("-1e1", s);
        y = (FPNum) ctx.mkNumeral("-1.25p3", s);
        FPExpr x_plus_y = (FPExpr) ctx.mkConst("x_plus_y", s);
        FPNum r = (FPNum) ctx.mkNumeral("100", s);
        slvr = ctx.mkSolver("QF_FP");

        slvr.add(ctx.mkEq(x_plus_y, ctx.mkFPMul(ctx.mkFPRoundNearestTiesToAway(), x, y)));
        slvr.add(ctx.mkNot(ctx.mkFPEq(x_plus_y, r)));
        if (slvr.check() != Status.UNSATISFIABLE)
            throw new IllegalStateException();
        System.out.println("OK, unsat:" + System.getProperty("line.separator") + slvr);
    }

    @Test
    public void doubleTest() {
        Context ctx = new Context();

        System.out.println("FloatingPointExample2");

        FPSort double_sort = ctx.mkFPSort(11, 53);
        FPRMSort rm_sort = ctx.mkFPRoundingModeSort();

        FPExpr y = (FPExpr) ctx.mkConst(ctx.mkSymbol("y"), double_sort);
        FPExpr fp_val = ctx.mkFP(2, double_sort);
        FPNum co = (FPNum) ctx.mkNumeral("-0.2", double_sort); /* -10 */

        BoolExpr c1 = ctx.mkEq(y, co);

        /* Generic solver */
        Solver s = ctx.mkSolver();
        s.add(c1);

        if (s.check() != Status.SATISFIABLE)
            throw new IllegalStateException();

        System.out.println("OK, model: " + s.getModel().toString());
        Expr evaluate = s.getModel().evaluate(y, true);

        System.out.println("OK, model: " + s.getModel().toString());
        int zz = 0;

    }

    @Test
    public void consequences(){
        Context context = new Context();
        BoolExpr a = context.mkBoolConst("A");
        BoolExpr b = context.mkBoolConst("B");

        BoolExpr implies = context.mkImplies(a, b);


    }

    private Model check(Context ctx, BoolExpr f, Status sat) {
        Solver s = ctx.mkSolver();
        s.add(f);
        if (s.check() != sat)
            throw new IllegalStateException();
        if (sat == Status.SATISFIABLE)
            return s.getModel();
        else
            return null;
    }
}
