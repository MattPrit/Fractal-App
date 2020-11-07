import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NebulaBrotFractal extends BhuddabrotFractal{

    private double redIterCoeff, redPointCoeff, greenIterCoeff, greenPointCoeff, blueIterCoeff, bluePointCoeff;


    public NebulaBrotFractal(Complex centre, int imageWidth, int imageHeight, double zoomLevel, int maxIterations, ColorStyle col, int numRandomPoints) {
        super(centre,imageWidth, imageHeight, zoomLevel, maxIterations, col, numRandomPoints);
        this.redIterCoeff = 1;
        this.redPointCoeff = 1.0/3;
        this.blueIterCoeff = 0.7;
        this.bluePointCoeff = 1.0/3;
        this.greenIterCoeff = 0.7;
        this.greenPointCoeff = 1.0/3;
    }

    public void createFractal() {

        setupFractal();

        long t1 = System.currentTimeMillis();

        /*
        Setup array for 3 color channels of fractal image
        channels[0] = red channel, channels[1] = green channel, channels[2] = blue channel
         */
        channels = new int[3][this.fractalPixels.length];

        int numIterations, pixelIndex;
        Complex c, z_n;
        ArrayList<Complex> escapingPoints = new ArrayList<>();
        boolean neverEntersImage;
        int k = randomStartingPoints.length / 3;//(escapingPoints.size() / 4);
        int neverEntersCount = 0;

        for (int i = 0; i < randomStartingPoints.length; i++) {

            neverEntersImage = true;

            /*
            //Complex z = randomStartingPoints[i];
            if (iterator.iterate(randomStartingPoints[i])[0] == 1) {
                escapingPoints.add(randomStartingPoints[i]);
            } else {
                randomStartingPoints[i] = null;
            }
             */

            if (iterator.iterate(randomStartingPoints[i])[0] == 0) {
                randomStartingPoints[i] = null;
            } else {
                c = randomStartingPoints[i];
                escapingPoints.add(c);
                numIterations = 1;
                z_n = new Complex(c.getReal(), c.getImag());

                while (numIterations < maxIterations) {

                    if (isPointInImage(z_n)) {

                        neverEntersImage = false;

                        pixelIndex = getPixelIndex(z_n);
                        iterationCounts[pixelIndex] ++;

                        if (i < k) {

                            if (numIterations < redIterCoeff*maxIterations) {
                                channels[0][pixelIndex] += 1;
                            }

                        }else {

                            if (i < 2*k) {

                                if (numIterations < greenIterCoeff*maxIterations) {
                                    channels[1][pixelIndex] += 1;
                                }

                            }else {

                                if (numIterations < blueIterCoeff*maxIterations) {
                                    channels[2][pixelIndex] += 1;
                                }
                            }

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

        //randomStartingPoints = null;

        /*
        for (int i=0; i < escapingPoints.size(); i++) {

            c = escapingPoints.get(i);

            numIterations = 1;
            z_n = new Complex(c.getReal(), c.getImag());

            neverEntersImage = true;

            while (numIterations < maxIterations) {

                if (isPointInImage(z_n)) {

                    neverEntersImage = false;

                    pixelIndex = getPixelIndex(z_n);
                    iterationCounts[pixelIndex] ++;

                    if (i < k) {

                        if (numIterations < redIterCoeff*maxIterations) {
                            channels[0][pixelIndex] += 1;
                        }

                    }else {

                        if (i < 2*k) {

                            if (numIterations < greenIterCoeff*maxIterations) {
                                channels[1][pixelIndex] += 1;
                            }

                        }else {

                            if (numIterations < blueIterCoeff*maxIterations) {
                                channels[2][pixelIndex] += 1;
                            }
                        }

                    }

                }

                if (z_n.abs2() > 4) {
                    break;
                }

                numIterations ++;
                z_n = z_n.multiply(z_n).add(c);
            }
        }
         */

        System.out.println("No. escaping points: " + escapingPoints.size());
        System.out.println("No. never-entering points: " + neverEntersCount);

        escapingPoints.clear();

        // Maximum overall number of passes though a pixel
        int maxPixelIter = 0;

        // Maximum number of passes though a pixel during red, green, and blue channel iterations
        int maxRedPixelIter = 0;
        int maxGreenPixelIter = 0;
        int maxBluePixelIter = 0;

        for (int i = 0; i < fractalPixels.length; i++) {
            maxRedPixelIter = Math.max(channels[0][i], maxRedPixelIter);
            maxGreenPixelIter = Math.max(channels[1][i], maxGreenPixelIter);
            maxBluePixelIter = Math.max(channels[2][i], maxBluePixelIter);
        }

        int maxRGBPixelIter = Math.max(maxRedPixelIter, Math.max(maxGreenPixelIter, maxBluePixelIter));

        for (int i: iterationCounts) {
            maxPixelIter = Math.max(i, maxPixelIter);
        }

        System.out.println("Max pixel iterations (r,g,b,total): (" + maxRedPixelIter + ", " + maxGreenPixelIter + ", " + maxBluePixelIter + ", " +maxPixelIter +")");

        float r, g, b;

        for (int i = 0; i < fractalPixels.length; i++) {
            r = (float)channels[0][i] / (float)maxRGBPixelIter;
            g = (float)channels[1][i] / (float)maxRGBPixelIter;
            b = (float)channels[2][i] / (float)maxRGBPixelIter;
            fractalPixels[i] = new Color(r, g, b).getRGB();
        }

        System.out.println("Create time: " + (System.currentTimeMillis()-t1));

    }

    public void createFractal2() {

        setupFractal();

        long t1 = System.currentTimeMillis();

        /*
        Setup array for 3 color channels of fractal image
        channels[0] = red channel, channels[1] = green channel, channels[2] = blue channel
         */
        channels = new int[3][this.fractalPixels.length];

        /*
        int k = (getNumRandomPoints() / 4);

        renderChannel(0, 0, k);
        renderChannel(1, k, k);
        renderChannel(2,2*k, 2*k);
         */

        MultithreadedNebulaRenderer fractalRenderer = new MultithreadedNebulaRenderer();
        try {
            fractalRenderer.render();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //randomStartingPoints = null;

        // Maximum overall number of passes though a pixel
        int maxPixelIter = 0;

        // Maximum number of passes though a pixel during red, green, and blue channel iterations
        int maxRedPixelIter = 0;
        int maxGreenPixelIter = 0;
        int maxBluePixelIter = 0;

        for (int i = 0; i < fractalPixels.length; i++) {
            maxRedPixelIter = Math.max(channels[0][i], maxRedPixelIter);
            maxGreenPixelIter = Math.max(channels[1][i], maxGreenPixelIter);
            maxBluePixelIter = Math.max(channels[2][i], maxBluePixelIter);

            iterationCounts[i] += (channels[0][i] + channels[1][i] + channels[2][i]);
            maxPixelIter = Math.max(iterationCounts[i], maxPixelIter);
        }

        int maxRGBPixelIter = Math.max(maxRedPixelIter, Math.max(maxGreenPixelIter, maxBluePixelIter));

        System.out.println("Max pixel iterations (r,g,b,total): (" + maxRedPixelIter + ", " + maxGreenPixelIter + ", " + maxBluePixelIter + ", " + maxPixelIter +")");

        float r, g, b;

        for (int i = 0; i < fractalPixels.length; i++) {
            r = (float)channels[0][i] / (float)maxRGBPixelIter;
            g = (float)channels[1][i] / (float)maxRGBPixelIter;
            b = (float)channels[2][i] / (float)maxRGBPixelIter;
            fractalPixels[i] = new Color(r, g, b).getRGB();
        }

        System.out.println("Create time: " + (System.currentTimeMillis()-t1));

    }

    void renderChannel(int channelCoeff, int start, int numPoints) {

        int numIterations, pixelIndex;
        Complex c, z_n;
        boolean neverEntersImage;

        int [] result;

        int neverEntersCount = 0;
        int escapeCount = 0;

        for (int i=start; i<start+numPoints; i++) {

            neverEntersImage = true;

            result = iterator.iterate(randomStartingPoints[i]);

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
                        channels[channelCoeff][pixelIndex] ++;

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

        System.out.println("No escaping points("+channelCoeff+"): " + escapeCount);
        System.out.println("No never-entering points("+channelCoeff+"): " + neverEntersCount);

    }

    class MultithreadedNebulaRenderer extends MultithreadedRenderer {

        /*
        In this case the sectionBorders array becomes
         */

        public void render() throws InterruptedException {

            numThreads=3;
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
            int y = sectionBorders[sectionIndex];
            int height = sectionBorders[sectionIndex+1] - y;
            renderChannel(sectionIndex, y, height);
        }

    }

    public static void main(String[] args) {

        /*NebulaBrotFractal f = new NebulaBrotFractal(new Complex(-1.757, 0), 500, 500, 57.665, 100, ColorStyle.RAINBOW, 10000000);

        f.createFractal();
        f.saveFractal("Old nebula create.png");

        f.createFractal2();
        f.saveFractal("New nebula create.png");*/

        BhuddabrotFractal f2 = new BhuddabrotFractal(new Complex(), 500, 500, 1.0, 100, ColorStyle.RAINBOW, 10000000);
        f2.createFractal2();
        f2.saveFractal("New Bhudda create.png");
        f2.createFractal();
        f2.saveFractal("Old Bhudda create.png");
        f2.nebulaModeEnabled = true;
        f2.createFractal2();
        f2.saveFractal("New Bhudda create - nebula.png");

    }

}
