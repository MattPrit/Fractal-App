public abstract class Iterator {

    /**
     * The maximum number of iterations that should be used when applying
     * our fractal-generating iterative process.
     */
    int maxIterations;

    /**
     * The tolerance that should be used throughout this project.
     * Other classes can access this tolerance
     * by using Iterator.TOL.
     */
    static final double TOL = 1.0e-10;

    /**
     * The number of iterations required for z_n value to go beyond point of no return.
     */
    int numIterations = maxIterations;

    /**
     * An integer that signifies outcomes that may occur from the iterative
     * process.
     */
    int err;


    int order;

    double power;

    Complex c, a;


    // ========================================================
    // Constructor functions.
    // ========================================================

    /**
     * Class constructor.
     */
    Iterator(int maxIterations) {
        this.maxIterations = maxIterations;
        this.order = new Double(this.getPower()).intValue();
    }

    // ========================================================
    // Accessor  and mutator methods.
    // ========================================================

    /**
     * Returns the current value of the err instance variable.
     */
    public int getError() {
        return this.err;
    }

    /**
     * Returns the current value of the numIterations instance variable.
     */
    public int getNumIterations() {
        return this.numIterations;
    }

    /**
     * Returns the value of the maxIterations instance variable.
     */
    public int getMaxIterations() {
        return this.maxIterations;
    }

    /**
     * Sets the value of the maxIterations instance variable.
     */
    public void setMaxIterations(int n) {
        this.maxIterations = n;
    }


    protected void setOrder(int order) {this.order = order;}

    protected int getOrder() {
        return this.order;
    }

    protected void setPower(double power) {this.power = power;}

    protected double getPower() {return this.power;}

    protected void setC(Complex c) {this.c = c;}

    protected Complex getC() {
        return this.c;
    }

    void setA(Complex a) {
        this.a = a;
    }

    Complex getA() {
        return this.a;
    }


    /**
     * Performs iterative process at a point in the complex plane,
     * determines property of fractal at that point
     * @param p complex number of point in question
     */
    abstract int[] iterate(Complex p);

}
