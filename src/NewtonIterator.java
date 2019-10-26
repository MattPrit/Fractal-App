import java.util.ArrayList;

public class NewtonIterator extends Iterator {

    public NewtonIterator(int maxIterations) {
        super(maxIterations);
        this.fp = f.derivative();
        this.setOrder(this.getF().degree());
        this.roots = new ArrayList<Complex>();
    }

    /**
     * The polynomial we wish to apply the Newton-Raphson method to.
     */
    private Polynomial f = new Polynomial(new Complex[]{new Complex(-1.0, 0.0), new Complex(),
            new Complex(), new Complex(), new Complex(), new Complex(1)});

    /**
     * The derivative of the the polynomial f.
     */
    private Polynomial fp;

    /**
     * A list of roots of the polynomial.
     */
    private ArrayList<Complex> roots;

    // ========================================================
    // Accessor methods.
    // ========================================================

    /**
     * Returns the polynomial associated with this object.
     */
    public Polynomial getF() {
        return this.f;
    }

    /**
     * Returns the derivative of the polynomial associated with this object.
     */
    public Polynomial getFp() {
        return this.fp;
    }

    // ========================================================
    // Mutator methods.
    // ========================================================

    /**
     * Sets the polynomial associated with this object
     * @param f the polynomial we to apply the N-R method to.
     */
    public void setF(Polynomial f) {
        this.roots.clear();
        this.f = f;
        this.fp = f.derivative();
        this.setOrder(f.degree());
    }

    // ========================================================
    // Basic operations.
    // ========================================================

    /**
     * Print out all of the roots found so far, which are contained in the
     * roots ArrayList.
     */
    public void printRoots() {
        for (Complex root : roots) {
            System.out.println(root);
        }
    }

    /**
     * Check to see if root is in the roots ArrayList (up to tolerance).
     *
     * @param root Root to find in this.roots.
     */
    public int findRoot(Complex root) {
        for (int i = 0; i < roots.size(); i++) {
            if (root.add(roots.get(i).minus()).abs2() < Iterator.TOL) {
                return i;
            }
        }
        return -1;
    }

    // ========================================================
    // Newton-Rapshon method
    // ========================================================

    /**
     * Given a complex number z0, apply Newton-Raphson to the polynomial f in
     * order to find a root within tolerance TOL.
     *
     * One of three things may occur:
     *
     *   - The root is found, in which case, set root to the end result of the
     *     algorithm, numIterations to the number of iterations required to
     *     reach it and err to 0.
     *   - At some point the derivative of f becomes zero. In this case, set err
     *     to -1 and return.
     *   - After maxIterations iterations the algorithm has not converged. In this
     *     case set err to -2 and return.
     *
     * @param z0  The initial starting point for the algorithm.
     */
    public int[] iterate(Complex z0) {
        int[] result = new int[2];
        result[0] = this.order; //Default value
        Complex z_n = new Complex(z0.getReal(), z0.getImag());
        Complex z_m = new Complex();
        for (int n=1; n<=this.maxIterations; n++) {
            result[1] = n;
            if (fp.evaluate(z_n).abs2() < TOL*TOL) {
                //err = -1;
                break;
            }
            z_m.setReal(z_n.getReal());
            z_m.setImag(z_n.getImag());
            z_n = z_m.add(f.evaluate(z_m).divide(fp.evaluate(z_m)).minus());
            if (z_n.add(z_m.minus()).abs2() < TOL*TOL) {

                if (findRoot(z_n) == -1) {
                    roots.add(z_n);
                    result[0] = roots.size() - 1;
                }else {
                    result[0] = findRoot(z_n);
                }
                break;
            }
        }
        return result;
    }

}
