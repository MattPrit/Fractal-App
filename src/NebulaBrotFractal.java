import java.awt.*;
import java.util.ArrayList;

public class NebulaBrotFractal extends BhuddabrotFractal{

    private double redIterCoeff, redPointCoeff, greenIterCoeff, greenPointCoeff, blueIterCoeff, bluePointCoeff;

    public NebulaBrotFractal(Complex centre, int imageWidth, int imageHeight, double zoomLevel, int maxIterations, ColorStyle col, int numRandomPoints) {
        super(centre,imageWidth, imageHeight, zoomLevel, maxIterations, col, numRandomPoints);
        this.redIterCoeff = 1;
        this.redPointCoeff = 1.0/3;
        this.blueIterCoeff = 0.3;
        this.bluePointCoeff = 1.0/3;
        this.greenIterCoeff = 0.7;
        this.greenPointCoeff = 1.0/3;
    }

    public void createFractal() {

        setupFractal();

        long t1 = System.currentTimeMillis();

        int[] redChannel = new int[this.fractalPixels.length];
        int[] greenChannel = new int[this.fractalPixels.length];
        int[] blueChannel = new int[this.fractalPixels.length];

        int numIterations, pixelIndex;
        Complex c, z_n;
        ArrayList<Complex> escapingPoints = new ArrayList<>();

        for (Complex z: randomStartingPoints) {
            if (iterator.iterate(z)[0] == 1) { escapingPoints.add(z); }
        }

        randomStartingPoints = null;

        int k = (escapingPoints.size() / 4);

        for (int i=0; i < escapingPoints.size(); i++) {

            c = escapingPoints.get(i);

            numIterations = 1;
            z_n = new Complex(c.getReal(), c.getImag());

            while (numIterations < maxIterations) {

                if (isPointInImage(z_n)) {

                    pixelIndex = getPixelIndex(z_n);
                    iterationCounts[pixelIndex] ++;

                    if (i < k) {

                        if (numIterations < redIterCoeff*maxIterations) { redChannel[pixelIndex] ++; }

                    }else {

                        if (i < 2*k) {

                            if (numIterations < greenIterCoeff*maxIterations) { greenChannel[pixelIndex] ++; }

                        }else {

                            if (numIterations < blueIterCoeff*maxIterations) { blueChannel[pixelIndex] ++; }
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

        escapingPoints.clear();

        int maxPixelIter = 0;
        int maxRedPixelIter = 0;
        int maxGreenPixelIter = 0;
        int maxBluePixelIter = 0;

        for (int i = 0; i < fractalPixels.length; i++) {

            if (redChannel[i] > maxRedPixelIter) {
                maxRedPixelIter = redChannel[i];
            }

            if (greenChannel[i] > maxGreenPixelIter) {
                maxGreenPixelIter = greenChannel[i];
            }

            if (blueChannel[i] > maxBluePixelIter) {
                maxBluePixelIter = blueChannel[i];
            }

        }

        int maxRGBPixelIter = maxRedPixelIter;

        if (maxGreenPixelIter > maxRGBPixelIter) {

            if (maxGreenPixelIter > maxBluePixelIter){
                maxRGBPixelIter = maxGreenPixelIter;
            }else {
                maxRGBPixelIter = maxBluePixelIter;
            }

        }else {
            if (maxBluePixelIter > maxRGBPixelIter) {
                maxRGBPixelIter = maxBluePixelIter;
            }
        }

        for (int i: iterationCounts) {
            if (i > maxPixelIter){
                maxPixelIter = i;
            }
        }

        System.out.println("Max pixel iterations (r,g,b,total): (" + maxRedPixelIter + ", " + maxGreenPixelIter + ", " + maxBluePixelIter + ", " +maxRGBPixelIter +")");

        float r, g, b;

        for (int i = 0; i < fractalPixels.length; i++) {

            r = (float)redChannel[i] / (float)maxRGBPixelIter;
            g = (float)greenChannel[i] / (float)maxRGBPixelIter;
            b = (float)blueChannel[i] / (float)maxRGBPixelIter;

            try {

                fractalPixels[i] = new Color(r, g, b).getRGB();

            }catch(IllegalArgumentException e) {

                System.out.println(i);
                System.out.println("(" + r + ", " + g + ", " + b + ")\n");

            }
        }

        System.out.println("Create time: " + (System.currentTimeMillis()-t1));

    }

}
