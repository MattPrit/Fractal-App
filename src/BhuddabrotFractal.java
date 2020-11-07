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

    protected int[][] channels;

    private int[][][] newChannels;

    boolean nebulaModeEnabled;

    private double[] iterCoeffs, pointCoeffs;

    private int[] channelBorders, channelIterations;


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
        this.randomStartingPoints = new Complex[this.numRandomPoints];
        this.nebulaModeEnabled = false;
        this.numThreads = Runtime.getRuntime().availableProcessors();

        this.iterCoeffs = new double[]{0.9, 0.3, 0.1};
        this.pointCoeffs = new double[]{0.5, 0.4, 0.5};
        this.channelBorders = new int[3];
        this.channelIterations = new int[3];
    }

    public int getNumRandomPoints() {
        return numRandomPoints;
    }

    public void setNumRandomPoints(int n) {
        this.numRandomPoints = n;
    }

    public void setPointCoeff(int channelIndex, double coeff) {
        this.pointCoeffs[channelIndex] = coeff;
    }

    public void setIterCoeff(int channelIndex, double coeff) {
        this.iterCoeffs[channelIndex] = coeff;
    }

    public double getPointCoeff(int channelIndex) {
        return this.pointCoeffs[channelIndex];
    }

    public double getIterCoeff(int channelIndex) {
        return this.iterCoeffs[channelIndex];
    }

    public boolean getNebulaEnabled() {
        return this.nebulaModeEnabled;
    }

    public void toggleNebulaEnabled() {
        this.nebulaModeEnabled = !this.nebulaModeEnabled;
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

        //randomStartingPoints = new Complex[this.numRandomPoints];
        double randArg, randAbs;

        int replaceCount = 0;

        if (this.numRandomPoints != this.randomStartingPoints.length) {
            this.randomStartingPoints = new Complex[numRandomPoints];
        }

        for (int i = 0; i < numRandomPoints; i++) {
            if (randomStartingPoints[i] == null) {
                randArg = Math.random() * 2 * Math.PI;
                randAbs = Math.random() * 1.75 + 0.25;
                randomStartingPoints[i] = (new Complex(Math.cos(randArg), Math.sin(randArg))).multiply(randAbs);
                replaceCount ++;
            }
        }

        System.out.println("\nReplaced: " + replaceCount);

        fractalImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        fractalPixels = ((DataBufferInt) fractalImage.getRaster().getDataBuffer()).getData();
        g2 = fractalImage.createGraphics();

        iterationCounts = new int[imageWidth*imageHeight];
        numPixelsPerIteration = new int[this.iterator.getMaxIterations()+1];

        channelBorders[0] = 0;
        channelBorders[1] = (int)(numRandomPoints * (pointCoeffs[0] / (pointCoeffs[0] + pointCoeffs[1] + pointCoeffs[2])));
        channelBorders[2] = (int)(numRandomPoints * ((pointCoeffs[0] + pointCoeffs[1]) / (pointCoeffs[0] + pointCoeffs[1] + pointCoeffs[2])));

        channelIterations[0] = (int)(iterCoeffs[0] * this.iterator.getMaxIterations());
        channelIterations[1] = (int)(iterCoeffs[1] * this.iterator.getMaxIterations());
        channelIterations[2] = (int)(iterCoeffs[2] * this.iterator.getMaxIterations());

        System.out.println("channel iterations: " + channelIterations[0] + " " + channelIterations[1] + " " + channelIterations[2]);
        System.out.println("channel points: " + channelBorders[0] + " " + channelBorders[1] + " " + channelBorders[2]);

        System.out.println("Setup time: " + (System.currentTimeMillis()-t1));
    }

    public void createFractal() {

        setupFractal();
        newChannels = new int [numThreads][3][fractalPixels.length];

        long t1 = System.currentTimeMillis();

        int numIterations, pixelIndex;
        Complex z_n;
        ArrayList<Complex> escapingPoints = new ArrayList<>();

        // Filter out points that don't escape:
        for (Complex c: randomStartingPoints) {
            if (iterator.iterate(c)[0] == 1) { escapingPoints.add(c); }
        }

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

        if (!nebulaModeEnabled){
            colorFractal();
        }

        newChannels = null;

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

        for (index=0; index<fractalPixels.length; index++) {
            numIter = iterationCounts[index];
            colorPixel(index, 1, numIter+1);
        }

    }

    int getChannelCoeff (int pointIndex) {

        if (pointIndex < channelBorders[1]) {
            return 0;
        } else if (pointIndex < channelBorders[2]) {
            return 1;
        } else {
            return 2;
        }

    }

    void createFractal2() {

        setupFractal();

        long t1 = System.currentTimeMillis();

        newChannels = new int[numThreads][3][fractalPixels.length];

        MultithreadedBhuddabrotRenderer bhuddabrotRenderer = new MultithreadedBhuddabrotRenderer();

        try {
            bhuddabrotRenderer.render();
        } catch (InterruptedException e) {
            System.out.println("Interrupted exception when trying to render");
            e.printStackTrace();
        }

        // Maximum overall number of passes though a pixel
        int maxPixelIter = 0;

        // Maximum number of passes though a pixel during red, green, and blue channel iterations
        int maxRedPixelIter = 0;
        int maxGreenPixelIter = 0;
        int maxBluePixelIter = 0;

        for (int i = 0; i < fractalPixels.length; i++) {

            for (int j=1; j<numThreads; j++) {
                newChannels[0][0][i] += newChannels[j][0][i];
                newChannels[0][1][i] += newChannels[j][1][i];
                newChannels[0][2][i] += newChannels[j][2][i];
            }

            maxRedPixelIter = Math.max(newChannels[0][0][i], maxRedPixelIter);
            maxGreenPixelIter = Math.max(newChannels[0][1][i], maxGreenPixelIter);
            maxBluePixelIter = Math.max(newChannels[0][2][i], maxBluePixelIter);

            iterationCounts[i] += (newChannels[0][0][i] + newChannels[0][1][i] + newChannels[0][2][i]);
            maxPixelIter = Math.max(iterationCounts[i], maxPixelIter);
        }

        int maxRGBPixelIter = Math.max(maxRedPixelIter, Math.max(maxGreenPixelIter, maxBluePixelIter));

        System.out.println("Max pixel iterations (r,g,b,total): (" + maxRedPixelIter + ", " + maxGreenPixelIter + ", " + maxBluePixelIter + ", " + maxPixelIter +")");

        if (!nebulaModeEnabled) {
            colorFractal();
        } else {

            float r, g, b;

            for (int i = 0; i < fractalPixels.length; i++) {
                r = (float)newChannels[0][0][i] / (float)maxRGBPixelIter;
                g = (float)newChannels[0][1][i] / (float)maxRGBPixelIter;
                b = (float)newChannels[0][2][i] / (float)maxRGBPixelIter;
                fractalPixels[i] = new Color(r, g, b).getRGB();
            }

        }

        newChannels = null;

        System.out.println("Create time: " + (System.currentTimeMillis()-t1));

    }

    void renderChannel(int threadNum, int start, int numPoints) {

        int numIterations, pixelIndex, channelCoeff;
        Complex c, z_n;
        boolean neverEntersImage;

        int [] result;

        int neverEntersCount = 0;
        int escapeCount = 0;

        for (int i=start; i<start+numPoints; i++) {

            neverEntersImage = true;
            channelCoeff = getChannelCoeff(i); //int) (3 * (float)i / (float)(numRandomPoints+1));

            result = ((MandelbrotIterator)iterator).iterate(randomStartingPoints[i], channelIterations[channelCoeff]);

            if (result[0] == 0) {
                randomStartingPoints[i] = null;
            } else {
                c = randomStartingPoints[i];
                escapeCount ++;
                numIterations = 1;
                z_n = new Complex(c.getReal(), c.getImag());

                while (numIterations < maxIterations) {

                    if (isPointInImage(z_n)) {

                        neverEntersImage = false;
                        pixelIndex = getPixelIndex(z_n);

                        try {
                            newChannels[threadNum][channelCoeff][pixelIndex] ++;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Array index exception i="+i+" coeff="+channelCoeff);
                        }

                    }

                    if (z_n.abs2() > 4) {
                        break;
                    }

                    numIterations ++;
                    z_n = z_n.multiply(z_n).add(c);
                }

                if (neverEntersImage) {
                    randomStartingPoints[i] = null;
                    neverEntersCount ++;
                }
            }

        }

        System.out.println("No escaping points ("+threadNum+"): " + escapeCount);
        System.out.println("No never-entering points ("+threadNum+"): " + neverEntersCount);

    }

    class MultithreadedBhuddabrotRenderer extends MultithreadedRenderer {

        /*
        In this case the sectionBorders array becomes
         */

        public void render() throws InterruptedException {

            //numThreads=3;
            System.out.println("Rendering ("+imageWidth+"x"+imageHeight+") of " +getNumRandomPoints() + " points with " + numThreads + " threads...");

            sectionBorders = new int[numThreads+1];
            sectionBorders[0] = 0;
            sectionBorders[numThreads] = getNumRandomPoints();
            for (int i = 1; i < numThreads; i++) {
                sectionBorders[i] = i*(getNumRandomPoints()/numThreads);
            }

            ExecutorService renderPool = Executors.newFixedThreadPool(numThreads);

            for(int i=0; i<numThreads; i++) {
                renderPool.execute(createRenderThread(i));
            }

            renderPool.shutdown();

            try {
                renderPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void renderSection(int sectionIndex) {
            int start = sectionBorders[sectionIndex];
            int length = sectionBorders[sectionIndex+1] - start;
            renderChannel(sectionIndex, start, length);
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
