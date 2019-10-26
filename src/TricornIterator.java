public class TricornIterator extends Iterator {

    public TricornIterator (int maxIterations) {
        super(maxIterations);
        this.setPower(2);
    }

    @Override
    public int[] iterate(Complex p) {
        int[] result = new int[2];
        result[1] = 1;
        Complex z_n = new Complex(0,0);
        Complex z_n_temp = new Complex();
        Complex c = new Complex(p.getReal(), p.getImag());

        if (p.abs2() <= (double)1/16) {
        }
        else for (int n = 1; n <= this.maxIterations; n++) {
            result[1] = n;
            this.numIterations = n;

            if (z_n.abs2() > 4) {
                result[0] = 1;
                break;
            }
            z_n_temp.setReal(z_n.getReal());
            z_n_temp.setImag(z_n.getImag());
            z_n = z_n.pow(this.power).conjugate().add(c);

            if (z_n_temp.add(z_n.minus()).abs2() <= TOL * TOL) {
                break;
            }
        }
        return result;
    }

}
