public class JuliaIterator extends Iterator {

    public JuliaIterator(double power, Complex c, int maxIterations) {
        super(maxIterations);
        this.power = power;
        this.c = c;
    }

    @Override
    public int[] iterate(Complex p) {
        int[] result = new int[2];

        Complex z_n = new Complex(p.getReal(), p.getImag());
        result[1] = 1;

        if (this.power == Math.floor(power)) {

            while (result[1] < this.maxIterations && z_n.abs2() < 4) {

                for (int k=1; k<this.power; k++) {
                    z_n = z_n.multiply(z_n);
                }

                z_n = z_n.add(this.getC());

                result[1] ++;
            }
            if (result[1] == this.maxIterations) {
                result[0] = 0;
            }
            else {
                result[0] = 1;
            }

        }
        else {
            while (result[1] < this.maxIterations && z_n.abs2() < 4) {

                z_n = z_n.pow(this.getPower()).add(this.getC());

                result[1] ++;
            }
            if (result[1] == this.maxIterations) {
                result[0] = 0;
            }
            else {
                result[0] = 1;
            }
        }

        return result;
    }

    public static void main(String[] args) {

        JuliaIterator j = new JuliaIterator(2, new Complex(0.279), 10);
        j.iterate(new Complex(-0.279));
        System.out.println(j.err);
    }
}
