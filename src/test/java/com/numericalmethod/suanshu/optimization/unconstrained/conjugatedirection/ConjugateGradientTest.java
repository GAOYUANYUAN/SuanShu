/*
 * Copyright (c) Numerical Method Inc.
 * http://www.numericalmethod.com/
 * 
 * THIS SOFTWARE IS LICENSED, NOT SOLD.
 * 
 * YOU MAY USE THIS SOFTWARE ONLY AS DESCRIBED IN THE LICENSE.
 * IF YOU ARE NOT AWARE OF AND/OR DO NOT AGREE TO THE TERMS OF THE LICENSE,
 * DO NOT USE THIS SOFTWARE.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITH NO WARRANTY WHATSOEVER,
 * EITHER EXPRESS OR IMPLIED, INCLUDING, WITHOUT LIMITATION,
 * ANY WARRANTIES OF ACCURACY, ACCESSIBILITY, COMPLETENESS,
 * FITNESS FOR A PARTICULAR PURPOSE, MERCHANTABILITY, NON-INFRINGEMENT, 
 * TITLE AND USEFULNESS.
 * 
 * IN NO EVENT AND UNDER NO LEGAL THEORY,
 * WHETHER IN ACTION, CONTRACT, NEGLIGENCE, TORT, OR OTHERWISE,
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIMS, DAMAGES OR OTHER LIABILITIES,
 * ARISING AS A RESULT OF USING OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.numericalmethod.suanshu.optimization.unconstrained.conjugatedirection;

import com.numericalmethod.suanshu.analysis.function.matrix.RntoMatrix;
import com.numericalmethod.suanshu.vector.doubles.Vector;
import com.numericalmethod.suanshu.matrix.doubles.Matrix;
import com.numericalmethod.suanshu.analysis.function.rn2r1.RealScalarFunction;
import com.numericalmethod.suanshu.analysis.function.rn2rm.RealVectorFunction;
import com.numericalmethod.suanshu.matrix.doubles.matrixtype.dense.DenseMatrix;
import com.numericalmethod.suanshu.optimization.problem.C2OptimProblemImpl;
import com.numericalmethod.suanshu.optimization.problem.IterativeMinimizer;
import com.numericalmethod.suanshu.vector.doubles.dense.DenseVector;
import org.junit.Test;
import static org.junit.Assert.*;
import static java.lang.Math.*;

/**
 *
 * @author Haksun Li
 */
public class ConjugateGradientTest {

    /**
     * The Himmelblau function:
     * f(x) = (x1^2 + x2 - 11)^2 + (x1 + x2^2 - 7)^2
     */
    @Test
    public void test_0010() throws Exception {
        RealScalarFunction f = new RealScalarFunction() {

            public Double evaluate(Vector x) {
                double x1 = x.get(1);
                double x2 = x.get(2);

                double result = pow(x1 * x1 + x2 - 11, 2);
                result += pow(x1 + x2 * x2 - 7, 2);

                return result;
            }

            public int dimensionOfDomain() {
                return 2;
            }

            public int dimensionOfRange() {
                return 1;
            }
        };

        RealVectorFunction g = new RealVectorFunction() {

            public Vector evaluate(Vector x) {
                double x1 = x.get(1);
                double x2 = x.get(2);

                double w1 = x1 * x1 + x2 - 11;
                double w2 = x1 + x2 * x2 - 7;

                double[] result = new double[2];
                result[0] = 4 * w1 * x1 + 2 * w2;
                result[1] = 2 * w1 + 4 * w2 * x2;
                return new DenseVector(result);
            }

            public int dimensionOfDomain() {
                return 2;
            }

            public int dimensionOfRange() {
                return 2;
            }
        };

        RntoMatrix H = new RntoMatrix() {

            public Matrix evaluate(Vector x) {
                double x1 = x.get(1);
                double x2 = x.get(2);

                Matrix result = new DenseMatrix(2, 2);
                result.set(1, 1, 12 * x1 * x1 + 4 * x2 - 42);
                result.set(1, 2, 4 * (x1 + x2));
                result.set(2, 1, result.get(1, 2));
                result.set(2, 2, 4 * x1 + 12 * x2 * x2 - 26);

                return result;
            }

            public int dimensionOfDomain() {
                return 2;
            }

            public int dimensionOfRange() {
                return 1;
            }
        };

        ConjugateGradient optim = new ConjugateGradient(1e-6, 40000);
        IterativeMinimizer<Vector> soln = optim.solve(new C2OptimProblemImpl(f, g, H));
        Vector xmin = soln.search(new DenseVector(new double[]{6, 6}));
//        System.out.println(xmin);

        Vector ans = new DenseVector(new double[]{2.99999919816496, 2.00000098867083});//from matlab
        double fans = f.evaluate(ans);
//        System.out.println(ans);
//        System.out.println(fans);

        assertEquals(0.0, xmin.minus(ans).norm(), 1e-14);
        double fxmin = f.evaluate(xmin);
        assertEquals(0.0, abs(fans - fxmin), 1e-15);
//        System.out.println(f.evaluate(xmin.toArray()));
    }

    /**
     * Use numerical Hessian.
     *
     * The Himmelblau function:
     * f(x) = (x1^2 + x2 - 11)^2 + (x1 + x2^2 - 7)^2
     */
    @Test
    public void test_0020() throws Exception {
        RealScalarFunction f = new RealScalarFunction() {

            public Double evaluate(Vector x) {
                double x1 = x.get(1);
                double x2 = x.get(2);

                double result = pow(x1 * x1 + x2 - 11, 2);
                result += pow(x1 + x2 * x2 - 7, 2);

                return result;
            }

            public int dimensionOfDomain() {
                return 2;
            }

            public int dimensionOfRange() {
                return 1;
            }
        };

        RealVectorFunction g = new RealVectorFunction() {

            public Vector evaluate(Vector x) {
                double x1 = x.get(1);
                double x2 = x.get(2);

                double w1 = x1 * x1 + x2 - 11;
                double w2 = x1 + x2 * x2 - 7;

                double[] result = new double[2];
                result[0] = 4 * w1 * x1 + 2 * w2;
                result[1] = 2 * w1 + 4 * w2 * x2;
                return new DenseVector(result);
            }

            public int dimensionOfDomain() {
                return 2;
            }

            public int dimensionOfRange() {
                return 2;
            }
        };

        ConjugateGradient optim = new ConjugateGradient(1e-6, 40000);
        IterativeMinimizer<Vector> soln = optim.solve(new C2OptimProblemImpl(f, g));
        Vector xmin = soln.search(new DenseVector(new double[]{6, 6}));

        Vector ans = new DenseVector(new double[]{2.99999919816496, 2.00000098867083});//from matlab
        double fans = f.evaluate(ans);

        assertEquals(0.0, xmin.minus(ans).norm(), 1e-11);//less precision
        double fxmin = f.evaluate(xmin);
        assertEquals(0.0, abs(fans - fxmin), 1e-15);
    }

    /**
     * Use numerical Hessian and gradient.
     *
     * The Himmelblau function:
     * f(x) = (x1^2 + x2 - 11)^2 + (x1 + x2^2 - 7)^2
     */
    @Test
    public void test_0030() throws Exception {
        RealScalarFunction f = new RealScalarFunction() {

            public Double evaluate(Vector x) {
                double x1 = x.get(1);
                double x2 = x.get(2);

                double result = pow(x1 * x1 + x2 - 11, 2);
                result += pow(x1 + x2 * x2 - 7, 2);

                return result;
            }

            public int dimensionOfDomain() {
                return 2;
            }

            public int dimensionOfRange() {
                return 1;
            }
        };

        ConjugateGradient optim = new ConjugateGradient(1e-6, 40000);
        IterativeMinimizer<Vector> soln = optim.solve(new C2OptimProblemImpl(f));
        Vector xmin = soln.search(new DenseVector(new double[]{6, 6}));

        Vector ans = new DenseVector(new double[]{2.99999919816496, 2.00000098867083});//from matlab
        double fans = f.evaluate(ans);

        assertEquals(0.0, xmin.minus(ans).norm(), 1e-11);//less precision
        double fxmin = f.evaluate(xmin);
        assertEquals(0.0, abs(fans - fxmin), 1e-15);
    }

    /**
     * The global minimizer is at x = [0,0,0,0].
     */
    @Test
    public void test_0050() throws Exception {
        RealScalarFunction f = new RealScalarFunction() {

            public Double evaluate(Vector x) {
                double x1 = x.get(1);
                double x2 = x.get(2);
                double x3 = x.get(3);
                double x4 = x.get(4);

                double result = pow(x1 - 4 * x2, 4);
                result += 12 * pow(x3 - x4, 4);
                result += 3 * pow(x2 - 10 * x3, 2);
                result += 55 * pow(x1 - 2 * x4, 2);

                return result;
            }

            public int dimensionOfDomain() {
                return 4;
            }

            public int dimensionOfRange() {
                return 1;
            }
        };

        RealVectorFunction g = new RealVectorFunction() {

            public Vector evaluate(Vector x) {
                double x1 = x.get(1);
                double x2 = x.get(2);
                double x3 = x.get(3);
                double x4 = x.get(4);

                double[] result = new double[4];
                result[0] = 4 * pow(x1 - 4 * x2, 3) + 110 * (x1 - 2 * x4);
                result[1] = -16 * pow(x1 - 4 * x2, 3) + 6 * (x2 - 10 * x3);
                result[2] = 48 * pow(x3 - x4, 3) - 60 * (x2 - 10 * x3);
                result[3] = -48 * pow(x3 - x4, 3) - 220 * (x1 - 2 * x4);
                return new DenseVector(result);
            }

            public int dimensionOfDomain() {
                return 4;
            }

            public int dimensionOfRange() {
                return 4;
            }
        };

        ConjugateGradient optim = new ConjugateGradient(1e-6, 40000);
        IterativeMinimizer<Vector> soln = optim.solve(new C2OptimProblemImpl(f, g));
        Vector xmin = soln.search(new DenseVector(new double[]{1, -1, -1, 1}));
//        System.out.println(xmin);
        double fxmin = f.evaluate(xmin);
//        System.out.println(fxmin);
        assertEquals(0.0, fxmin, 1e-9);

//        Vector ans = new DenseVector(new double[]{0.04841813, 0.01704776, 0.00170602, 0.02420804});
//        System.out.println(ans);
//        System.out.println(f.evaluate(ans.toArray()));

        soln = optim.solve(new C2OptimProblemImpl(f, g));
        xmin = soln.search(new DenseVector(new double[]{2, 10, -15, 17}));
//        System.out.println(xmin);
        fxmin = f.evaluate(xmin);
//        System.out.println(fxmin);
        assertEquals(0.0, fxmin, 1e-9);
    }
}
