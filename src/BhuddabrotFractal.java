import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BhuddabrotFractal extends Fractal {

    private int numRandomPoints;

    protected Complex[] randomStartingPoints;


    /**
     * Constructor function which initialises instance variables and
     * sets up for the creation of fractalImage
     */
    public BhuddabrotFractal() {
        this(new Complex(), 800, 480, 1.0, 60, ColorStyle.TWOCOLOUR, 500000);
    }

    /**
     * Constructor function which initialises instance variables and
     * sets up for the creation of fractalImage
     * @param centre        The centre of the square to image.
     * @param imageWidth    The width of the image to be produced.
     * @param imageHeight   The height of the image to be produced
     * @param zoomLevel     Determines the zoom level of image to be produced
     * @param maxIterations The maximum number of iterations per pixel when determining how to classify the complex number it represents
     * @param numRandomPoints The number of random points the BhuddaBrot algorithm starts with
     */
    public BhuddabrotFractal(Complex centre, int imageWidth, int imageHeight, double zoomLevel, int maxIterations, ColorStyle col, int numRandomPoints) {
        super(centre, imageWidth, imageHeight, zoomLevel, maxIterations);
        this.numRandomPoints = numRandomPoints;
        this.colorStyle = col;
        this.colors = new Color[2];
        this.colors[0] = Color.WHITE;
        this.colors[1] = Color.BLACK;
        this.iterator = new MandelbrotIterator(maxIterations);
        setOrder(1);
        this.colorStyle = ColorStyle.TWOCOLOUR;
    }

    public int getNumRandomPoints() {
        return numRandomPoints;
    }

    public void setNumRandomPoints(int n) {
        this.numRandomPoints = n;
    }

    /**
     * Checks whether a given complex number lies within the region of the complex plane represented
     * by our image.
     *
     * @param c The complex number under test
     * @return TRUE if c does lie in our imaged region, FALSE if not.
     */
    protected boolean isPointInImage(Complex c) {

        if ((c.getReal() - origin.getReal()) < -0.5* dz || (imageWidth-0.5)* dz < (c.getReal() - origin.getReal())){
            return false;
        }

        return !((origin.getImag() - c.getImag()) < -0.5 * dz) && !((imageHeight - 0.5) * dz < (origin.getImag() - c.getImag()));
    }

    /**
     * Given a complex number c (determined to lie within the region represented by our image), returns the
     * corresponding pixel - i.e. the corresponding index in the fractalPixels array.
     *
     * @param c A complex number in the region of our image
     * @return  The corresponding pixel index
     */
    protected int getPixelIndex(Complex c) {

        double z = (c.getReal() - origin.getReal()) / dz;
        int x = (int) Math.round(z);
        int y = (int) Math.round((origin.getImag() - c.getImag()) / dz);

        return y*imageWidth+x;
    }

    public void setupFractal() {

        long t1 = System.currentTimeMillis();

        this.dz = referenceHeight / ((this.imageHeight - 1) * this.zoomLevel);
        this.origin.setImag(this.centre.getImag() + (this.dz * (0.5 * this.imageHeight - 0.5)));
        this.origin.setReal(this.centre.getReal() - (this.dz * (0.5 * this.imageWidth - 0.5)));

        randomStartingPoints = new Complex[this.numRandomPoints];
        double randArg, randAbs;

        for (int i = 0; i < numRandomPoints; i++) {
            randArg = Math.random() * 2 * Math.PI;
            randAbs = Math.random() * 2;
            randomStartingPoints[i] = (new Complex(Math.cos(randArg), Math.sin(randArg))).multiply(randAbs);
        }

        fractalImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        fractalPixels = ((DataBufferInt) fractalImage.getRaster().getDataBuffer()).getData();
        g2 = fractalImage.createGraphics();

        iterationCounts = new int[imageWidth*imageHeight];
        numPixelsPerIteration = new int[this.iterator.getMaxIterations()+1];

        System.out.println("Setup time: " + (System.currentTimeMillis()-t1));
    }

    public void createFractal() {

        setupFractal();

        long t1 = System.currentTimeMillis();

        int numIterations, pixelIndex;
        Complex z_n;
        ArrayList<Complex> escapingPoints = new ArrayList<>();

        // Filter out points that don't escape:
        for (Complex c: randomStartingPoints) {
            if (iterator.iterate(c)[0] == 1) { escapingPoints.add(c); }
        }

        randomStartingPoints = null;

        System.out.println("Check points time: " + (System.currentTimeMillis()-t1));

        long t2 = System.currentTimeMillis();

        // Track the escape of those that do escape:
        for (Complex c: escapingPoints) {

            numIterations = 1;
            z_n = new Complex(c.getReal(), c.getImag());

            while (numIterations < maxIterations) {

                if (isPointInImage(z_n)) {

                    pixelIndex = getPixelIndex(z_n);
                    iterationCounts[pixelIndex] += 1;

                }

                if (z_n.abs2() > 4) {
                    break;
                }

                numIterations ++;
                z_n = z_n.multiply(z_n).add(c);
            }
        }

        escapingPoints.clear();

        System.out.println("Path tracing time: " + (System.currentTimeMillis()-t2));

        long t3 = System.currentTimeMillis();

        colorFractal();

        System.out.println("Color time: " + (System.currentTimeMillis()-t3));
        System.out.println("Create time: " + (System.currentTimeMillis()-t1));

    }

    public void colorFractal() {

        int index, numIter;
        int maxPixelIter = 0;

        for (int i: iterationCounts) {
            if (i > maxPixelIter){
                maxPixelIter = i;
            }
        }

        int minPixelIter = maxPixelIter;

        for (int i: iterationCounts) {
            if (i < minPixelIter) {
                minPixelIter = i;
            }
        }

        setupColorMap(this.iterator.getOrder()+1, maxPixelIter+1);

        System.out.println("Max iteration for a pixel = " + maxPixelIter);
        System.out.println("Min iteration for a pixel = " + minPixelIter);

        for (int x=0; x<imageWidth; x++) {
            for (int y=0; y<imageHeight; y++) {

                index = y*imageWidth+x;
                numIter = iterationCounts[index];
                colorPixel(x, y, 1, numIter+1);

            }
        }

    }

    public static void main(String args[]) {

        // Produces a nebulabrot image 'manually' using 3 BhuddaBrotFractals

        Fractal f = new BhuddabrotFractal(new Complex(-0.4), 1080,1920, 1.0, 1000, ColorStyle.TWOCOLOUR, 10000000);
        Fractal f2 = new BhuddabrotFractal(new Complex(-0.4), 1080,1920, 1.0, 750, ColorStyle.TWOCOLOUR, 7500000);
        Fractal f3 = new BhuddabrotFractal(new Complex(-0.4), 1080,1920, 1.0, 500, ColorStyle.TWOCOLOUR, 5000000);

        f.setColour(0, Color.RED);
        f2.setColour(0, Color.GREEN);
        f3.setColour(0, Color.BLUE);

        f.setupFractal();
        f.createFractal();

        f2.setupFractal();
        f2.createFractal();

        f3.setupFractal();
        f3.createFractal();

        int[] fp = f.fractalPixels;
        int[] fp2 = f2.fractalPixels;
        int[] fp3 = f3.fractalPixels;

        for (int i = 0; i < f.fractalPixels.length; i++) {

            float red = (1f/255f) * new Color(fp[i]).getRed();
            float green = (1f/255f) * new Color(fp2[i]).getGreen();
            float blue = (1f/255f) * new Color(fp3[i]).getBlue();

            fp[i] = new Color(red, green, blue).getRGB();
        }

        f.saveFractal("TESTTest.png");

    }

}
