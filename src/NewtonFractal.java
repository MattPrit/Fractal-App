import java.awt.Color;

class NewtonFractal extends Fractal {

    // ========================================================
    // Constructor function.
    // ========================================================

    public NewtonFractal() {
        this(new Complex(), 800, 480, 1.0, 40, ColorStyle.THREECOLOUR);
    }

    public NewtonFractal(Complex centre, int imageWidth, int imageHeight, double zoomLevel, int maxIterations, ColorStyle col) {
        super(centre, imageWidth, imageHeight, zoomLevel, maxIterations);
        this.colorStyle = col;
        this.iterator = new NewtonIterator(maxIterations);
        setupFractal();
    }

    // ========================================================
    // Basic operations.
    // ========================================================

    private void setupColors() {
        this.colors = new Color[this.iterator.getOrder()+1];
        Color[] baseColors = new Color[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.PINK};
        for (int i = 0; i < this.iterator.getOrder(); i++) {
            this.colors[i] = baseColors[i % 8];
        }
        this.colors[this.iterator.getOrder()] = Color.BLACK;
    }

    public void setupFractal() {
        setupColors();
        super.setupFractal();
    }

    public void colorPixel(int x, int y, int ourColor, int numIterations) {

        if (this.colorStyle == ColorStyle.NO_SHADING) {
            fractalPixels[y * imageWidth + x] = colors[ourColor].getRGB();
        }
        else {
            fractalPixels[y*imageWidth+x] = colorMap[ourColor][numIterations-1].getRGB();
        }
    }


    // ========================================================
    // Tester function.
    // ========================================================

    public static void main(String[] args) {

        // Here is some example code which generates an image for P(z)=z^5-1
        // on square of complex plane with vertices -2-2i, -2+2i, 2-2i, 2+2i

        NewtonFractal f = new NewtonFractal(new Complex(), 500, 500, 1.0, 20, ColorStyle.TWOCOLOUR);

        Complex[] coeff = new Complex[]{new Complex(1), new Complex(1), new Complex(1), new Complex(1)};
        Polynomial p = new Polynomial(coeff);

        ((NewtonIterator)(f.iterator)).setF(p);
        f.setColorStyle(ColorStyle.TWOCOLOUR);

        long t1 = System.currentTimeMillis();
        f.setupFractal();
        long t2 = System.currentTimeMillis();
        f.createFractal();
        long t3 = System.currentTimeMillis();
        f.saveFractal("fractal-dark.png");
        long t4 = System.currentTimeMillis();
        Complex complex;
        //int rootColor, numIter;
        int [] result = new int[2];
        for (int i = 0; i < 500; i++) {
            for (int j = 0; j < 500; j++) {
                complex = f.pixelToComplex(i, j);
                result = f.iterator.iterate(complex);
                //rootColor = f.iterator.getError();
                //numIter = f.iterator.getNumIterations();
                f.colorPixel(i, j, result[0], result[1]);
            }
        }
        long t5 = System.currentTimeMillis();

        System.out.println("setup time: " + (t2-t1));
        System.out.println("create time: " + (t3-t2));
        System.out.println("save time: " + (t4-t3));
        System.out.println("iterate time: " + (t5-t4));

        // Test of the Polynomial toString() and NewtonFractal printRoots() methods using the above polynomial
        System.out.println("Polynomial: " + p);
        System.out.println("Roots: ");
        ((NewtonIterator)f.iterator).printRoots();
    }

}