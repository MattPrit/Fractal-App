import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;

enum ColorStyle {NO_SHADING, RAINBOW, THREECOLOUR, TWOCOLOUR}

enum ColorMode {HISTOGRAM, ESCAPE_TIME}

public abstract class Fractal {

    /**
     * The maximum number of iterations when generating the fractalImage
     */
    int maxIterations;

    /**
     * The top-left corner of the rectangle in the complex plane to examine.
     */
    Complex origin = new Complex();

    /**
     * The complex number corresponding to the centre of our imaged region
     */
    Complex centre;

    /**
     * A reference height in the complex plane for use in construction
     */
    static final double referenceHeight = 4.0;

    /**
     * An array holding the base colours of the image.
     */
    Color[] colors;

    /**
     * A ColorStyle type indicating what type of color map used when coloring our image
     */
    ColorStyle colorStyle;

    /**
     * A ColorMode type indicating which algorithm should be used to color pixels of the image
     */
    ColorMode colorMode;

    /**
     * An array holding colours of the image for continuous colouring modes
     */
    Color[][] colorMap;

    /**
     * A standard Java object which allows us to store a simple image in
     * memory. This will be set up by setupFractal -- you do not need to worry
     * about it!
     */
    BufferedImage fractalImage;

    /**
     * This object is another standard Java object which allows us to perform
     * basic graphical operations (drawing lines, rectangles, pixels, etc) on
     * the BufferedImage. This will be set up by setupFractal -- you do not
     * need to worry about it!
     */
    Graphics2D g2;

    /**
     * Defines the width (in pixels) of the BufferedImage and hence the
     * resulting image.
     */
    int imageWidth;

    /**
     * Defines the height (in pixels) of the BufferedImage and hence the
     * resulting image.
     */
    int imageHeight;

    /**
     * The distance in the complex plane corresponding to separation of the centres of two adjacent pixels
     */
    double dz;

    double zoomLevel;

    int order;

    Complex c;

    Iterator iterator;

    int [] fractalPixels;

    int [] iterationCounts;
    int [] pixelColorNums; // Rename pixelColorIDs?
    float [] pixelHues;
    int [] numPixelsPerIteration;

    /**
     * Constructor function which initialises instance variables and
     * sets up for the creation of fractalImage
     *
     * @param centre  The centre of the square to image.
     * @param imageWidth  The width of the image to be produced.
     * @param imageHeight  The height of the image to be produced
     * @param zoomLevel  Determines the zoom level of image to be produced
     * @param maxIterations The maximum number of iterations per pixel when determining how to classify the complex number it represents
     */
    public Fractal(Complex centre, int imageWidth, int imageHeight, double zoomLevel, int maxIterations) {
        this.maxIterations = maxIterations;
        this.imageHeight   = imageHeight;
        this.imageWidth    = imageWidth;
        this.zoomLevel     = zoomLevel;
        this.centre        = centre;

        this.numThreads = Runtime.getRuntime().availableProcessors();

        this.colorMode = ColorMode.ESCAPE_TIME;
    }

    // ========================================================
    // Accessor and mutator methods
    // ========================================================

    public int getMaxIterations() {
        return this.iterator.getMaxIterations();}

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
        this.iterator.setMaxIterations(maxIterations);
    }

    public Complex getCentre() {
        return this.centre;}

    public void setCentre(Complex centre) {
        this.centre.setReal(centre.getReal());
        this.centre.setImag(centre.getImag());
    }

    public void setCentre(double x, double y) {
        this.centre.setReal(x);
        this.centre.setImag(y);
    }

    protected void setOrder(int order) {
        this.iterator.setOrder(order);
    }

    protected int getOrder() {
        return this.iterator.getOrder();
    }

    protected void setC(Complex c) {
        this.iterator.setC(c);
    }

    Complex getC() {
        return this.iterator.getC();
    }

    public Color getColour(int i) {
        return this.colors[i];
    }

    public void setColour(int i, Color colour) {
        this.colors[i] = colour;
    }

    ColorStyle getColorStyle() {
        return this.colorStyle;
    }

    void setColorStyle(ColorStyle colorStyle) {
        this.colorStyle = colorStyle;
    }

    ColorMode getColorMode() {
        return this.colorMode;
    }

    void setColorMode(ColorMode colorMode) {
        this.colorMode = colorMode;
    }

    public BufferedImage getFractalImage() {
        return this.fractalImage;
    }

    public void setImageDimensions(int imageWidth, int imageHeight) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public double getZoomLevel() {
        return this.zoomLevel;
    }

    public void setZoomLevel(double zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    // ========================================================
    // Basic operations.
    // ========================================================

    /**
     * Convert from pixel indices (i,j) to the complex number (origin.real +
     * i*dz, origin.imag - j*dz).
     *
     * @param i  x-axis co-ordinate of the pixel located at (i,j)
     * @param j  y-axis co-ordinate of the pixel located at (i,j)
     */
    public Complex pixelToComplex(int i, int j) {
        double real = origin.getReal() + i * dz;
        double imaginary = origin.getImag() - j * dz;
        return new Complex(real, imaginary);
    }

    public void setupColorMap(int numColors, int numSteps) {

        this.colorMap = new Color[numColors][numSteps];
        float[][] components = new float[numColors][3];

        for (int i = 0; i < numColors; i++) {
            components[i] = this.colors[i].getRGBComponents(null);
        }

        for (int j = 0; j < numColors; j++) {

            if (this.colorStyle == ColorStyle.RAINBOW) {
                for (int k = 0; k < numSteps; k++) {
                    float hue = (float) ((k % 360.0) / 360.0 + 0.05); // 0.3595801134881463, 0.6153338949563218, 37876.75244106352
                    this.colorMap[j][k] = Color.getHSBColor(hue, 1, 1);
                }
            }

            if (this.colorStyle == ColorStyle.TWOCOLOUR) {
                for (int k = 0; k < numSteps; k++) {
                    float f = (float) Math.sin(Math.PI / 2 * k / numSteps);
                    float red = (1 - f) * components[1][0] + f * components[0][0];
                    float green = (1 - f) * components[1][1] + (f * components[0][1]);
                    float blue = (1 - f) * components[1][2] + (f * components[0][2]);
                    this.colorMap[j][k] = new Color(red, green, blue);
                }
            }
            if (this.colorStyle == ColorStyle.THREECOLOUR){
                for (int k = 0; k < numSteps; k++) {
                    float f = (float) Math.sin(Math.PI / 2 * k / numSteps);
                    float red = ((1 - f) * components[j][0] + (f * components[numColors - 1][0]));
                    float green = ((1 - f) * components[j][1] + (f * components[numColors - 1][1]));
                    float blue = ((1 - f) * components[j][2] + (f * components[numColors - 1][2]));
                    this.colorMap[j][k] = new Color(red, green, blue);
                }
            }
        }
    }

    /**
     * Sets up all the fractalImage image. Called within class constructor function
     */
    public void setupFractal() {

        long t1 = System.currentTimeMillis();

        this.dz = referenceHeight / ((this.imageHeight - 1) * this.zoomLevel);
        this.origin.setImag(this.centre.getImag() + (this.dz * (0.5 * this.imageHeight - 0.5)));
        this.origin.setReal(this.centre.getReal() - (this.dz * (0.5 * this.imageWidth - 0.5)));

        fractalImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        fractalPixels = ((DataBufferInt) fractalImage.getRaster().getDataBuffer()).getData();
        g2      = fractalImage.createGraphics();

        iterationCounts = new int[imageWidth*imageHeight];
        pixelColorNums = new int[imageWidth*imageHeight]; // Rename pixelColorIDs?
        pixelHues = new float[imageWidth*imageHeight];
        numPixelsPerIteration = new int[this.iterator.getMaxIterations()+1];
        for (int i = 0; i < this.iterator.getMaxIterations(); i++) { numPixelsPerIteration[i] = 0; }
        System.out.println("Setup time: " + (System.currentTimeMillis()-t1));
    }

    /**
     * Generate the fractalImage image.
     */
    public void createFractal() {

        setupFractal();

        long t0 = System.currentTimeMillis();

        if (this.multithreadingEnabled) {
            multithreadedTestCreate();
        }else{
            renderRect(0, 0, imageWidth, imageHeight);
            colorFractal();
            //g2.drawImage(fractalImage,0,0, imageWidth, imageHeight, null);
        }

        if (this.supersamplingEnabled) {
            supersampleTest();
        }

        long t1 = System.currentTimeMillis();
        System.out.println("Create time: " + (t1-t0));

    }

    public void translateFractal(int dX, int dY) {

        long t1 = System.currentTimeMillis();

        int[] tempIterationCounts = iterationCounts.clone();
        int[] tempColorNums = pixelColorNums.clone();
        int[] tempFractalPixles = fractalPixels.clone();

        // TODO: change to transfer fractalPixels as well

        this.centre = this.centre.add(new Complex(-1 * dX * this.dz, dY * this.dz));
        this.origin = this.origin.add(new Complex((-1) * dX * this.dz, dY * this.dz));

        Complex complex;
        int ourColor, numIter, xAltered, yAltered, pixelIndex, newPixelIndex;
        int pixelResult[];

        // TODO: fix this to allow for renderRect() usage.
        /*for (int x = -dX; x < imageWidth-dX; x++) {
            for (int y = -dY; y < imageHeight-dY; y++) {
                pixelIndex = y * this.imageWidth + x;
                newPixelIndex = (y + dY) * this.imageWidth + (x + dX);
                try {
                    iterationCounts[newPixelIndex] = tempIterationCounts[pixelIndex];
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println(x + ", " + y + ", " + newPixelIndex);
                }
                iterationCounts[newPixelIndex] = tempIterationCounts[pixelIndex];
                pixelColorNums[newPixelIndex] = tempColorNums[pixelIndex];
                fractalPixels[newPixelIndex] = tempFractalPixles[pixelIndex];
                pixelHues[newPixelIndex] = 0;
            }
        }*/

        for (int x=0; x<imageWidth; x++) {
            for (int y=0; y<imageHeight; y++) {

                if ((x+dX<imageWidth) && (y+dY<imageHeight) && (x+dX>(-1)) && (y+dY>(-1))) {
                    pixelIndex = y * this.imageWidth + x;
                    newPixelIndex = (y + dY) * this.imageWidth + (x + dX);
                    iterationCounts[newPixelIndex] = tempIterationCounts[pixelIndex];
                    pixelColorNums[newPixelIndex] = tempColorNums[pixelIndex];
                    fractalPixels[newPixelIndex] = tempFractalPixles[pixelIndex];
                    pixelHues[newPixelIndex] = 0;
                    continue;
                }

                /*
                Alter x and y coordinates such that for each pixel that leaves the screen after translation we
                render the corresponding 'new' pixel that enters on the opposite side of the image.
                */
                xAltered = imageWidth - (x+1);
                yAltered = imageHeight - (y+1);

                complex = pixelToComplex(xAltered, yAltered);

                pixelResult = this.iterator.iterate(complex); //TODO: change this stuff to a call of renderRect().

                numIter = pixelResult[1];
                pixelIndex = yAltered*imageWidth+xAltered;

                iterationCounts[pixelIndex] = numIter;
                pixelColorNums[pixelIndex] = pixelResult[0];
                pixelHues[pixelIndex] = 0;
                numPixelsPerIteration[numIter] ++; //TODO: fix this counter in the above code for translation to allow for histogram colouring.

            }
        }

        colorFractal();
        System.out.println("Translate time: " + (System.currentTimeMillis()-t1));

    }

    /**
     * Colors all the pixels of the fractal image
     */
    public void colorFractal() {

        colorRect(0, 0, imageWidth, imageHeight);

    }

    /**
     * Colors a pixel in the image.
     *
     * @param pixelIndex The index of the pixel to be coloured in fractalPixels.
     * @param ourColor   An integer indicating a base colour in this.colors.
     * @param numIterations    Number of iterations at this point.
     */
    public void colorPixel(int pixelIndex, int ourColor, int numIterations) {

        if (this.colorStyle == ColorStyle.NO_SHADING)
            fractalPixels[pixelIndex] = colors[ourColor].getRGB();
        else {
            if (ourColor == 0){
                fractalPixels[pixelIndex] = colors[0].getRGB();
            }if (ourColor != 0){
                Color color = colorMap[ourColor][numIterations-1];

                try {
                    int col = color.getRGB();
                    fractalPixels[pixelIndex] = col;
                }catch (NullPointerException e) {
                    System.out.println(fractalPixels[pixelIndex]);
                    System.out.println("Nullpointer! (x,y): "+pixelIndex+" ourColor: " + ourColor + " numiterations: " + numIterations);
                }

            }
        }

    }

    /**
     * Colors a pixel in the image.
     *
     * @param x          x-axis co-ordinate of the pixel located at (i,j)
     * @param y          y-axis co-ordinate of the pixel located at (i,j)
     * @param ourColor   An integer indicating a base colour in this.colors.
     * @param numIterations    Number of iterations at this point.
     */
    public void colorPixel(int x, int y, int ourColor, int numIterations) {

        int pixelIndex = y * imageWidth + x;
        colorPixel(pixelIndex, ourColor, numIterations);

    }

    /**
     * Colors a pixel in the image according to a given HSB hue value.
     *
     * @param x   x-axis co-ordinate of the pixel located at (i,j)
     * @param y   y-axis co-ordinate of the pixel located at (i,j)
     * @param hue A float indicating the HSB hue value with which to color the pixel.
     */
    public void colorPixel(int x, int y, float hue) {
        g2.setColor(Color.getHSBColor(hue, 1, 1));
        g2.fillRect(x, y, 1, 1);
    }

    /**
     * Saves the fractalImage image to a file.
     *
     * @param fileName  The filename to save the image as. Should end in .png.
     */
    void saveFractal(String fileName) {
        try {
            File outputFile = new File(fileName);
            ImageIO.write(fractalImage, "png", outputFile);
        } catch (IOException e) {
            System.out.println("I got an error trying to save! Maybe you're out of space?");
        }
    }


    /**
     * Renders a sub rectangle of the fractal image.
     * @param x the x coordinate of the top left corner of the rectangle within the image
     * @param y the x coordinate of the top left corner of the rectangle within the image
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     */
    public void renderRect(int x, int y, int width, int height) {

        Complex complex;
        int index;
        int[] iterateData;

        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {

                complex = pixelToComplex(i, j);
                index = j*imageWidth+i;

                iterateData = this.iterator.iterate(complex);

                iterationCounts[index] = iterateData[1];
                pixelColorNums[index] = iterateData[0];
                pixelHues[index] = 0;
                numPixelsPerIteration[iterateData[1]] ++;

                if (i ==1 || j ==1 || i == (imageWidth-1) || j == (imageWidth-1)) {
                    // superSamplePixel();
                }

            }
        }

        colorRect(x, y, width, height);

    }

    /**
     * Colors a sub rectangle of the fractal image.
     * @param x the x coordinate of the top left corner of the rectangle within the image
     * @param y the x coordinate of the top left corner of the rectangle within the image
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     */
    public void colorRect(int x, int y, int width, int height) {

        this.setupColorMap(this.getOrder() + 1, this.getMaxIterations());
        int ourColor, numIter;

        switch (this.colorMode) {

            case ESCAPE_TIME:
                for (int i=x; i < x+width; i++) {
                    for (int j = y; j < y+height; j++) {

                        numIter = iterationCounts[j*imageWidth+i];
                        ourColor = pixelColorNums[j*imageWidth+i];
                        if (numIter==0 && ourColor==0) {System.out.println("Black line at y: " + y + "; height: " + imageHeight);}
                        try {
                            colorPixel(i , j, ourColor, numIter);
                        }catch (ArrayIndexOutOfBoundsException e) {
                            //System.out.println(ourColor);
                        }
                    }
                }
                break;

            case HISTOGRAM:
                int total = 0;
                for (int i = 0; i < this.getMaxIterations(); i++) { total += numPixelsPerIteration[i];}

                for (int i=x; i < x+width; i++) {
                    for (int j = y; j < y+height; j++) {

                        numIter = iterationCounts[j*imageWidth+i];
                        ourColor = pixelColorNums[j*imageWidth+i];

                        if (ourColor == 0) {
                            colorPixel(x , y, ourColor, numIter);
                        }

                        else {
                            for (int k = 0; k < numIter; k++) {
                                pixelHues[j*imageWidth+i] += (float) numPixelsPerIteration[k] / total;
                            }
                            colorPixel(i, j, pixelHues[j*imageWidth+i]);
                        }
                    }
                }

                break;
        }

    }


    private int numThreads;
    private boolean multithreadingEnabled = true;
    private boolean supersamplingEnabled = true;

    public void setNumThreads(int n) {
        this.numThreads = n;
    }

    public int getNumThreads() {
        if (!multithreadingEnabled) {
            return 1;
        }else {
            return this.numThreads;
        }
    }

    public void toggleMultithreadingEnabled() {
        this.multithreadingEnabled = !multithreadingEnabled;
    }

    public void toggleMultiSamplingEnabled() {
        this.supersamplingEnabled = !supersamplingEnabled;
    }

    private void multithreadedTestCreate() {

        MultithreadedRenderer renderer = new MultithreadedRenderer();
        try {
            renderer.render();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    void supersampleTest(){

        long t0 = System.currentTimeMillis();
        long iterateTime = 0;

        int[] iterationCountsCopy = iterationCounts.clone();
        int[][] neighbourPixelInfo = new int[4][2];
        int subPixelIterationTotal, neighbourPixelIterationTotal;
        Complex[] samples = new Complex[4];
        Complex c;
        double spd = dz * 0.25; // Sub-pixel distance
        int changedPixels = 0; // Count number of pixels altered by this process

        for (int i = 1; i < imageWidth-1; i++) {
            for (int j = 1; j < imageHeight-1; j++) {

                if (i ==1 || j ==1 || i == (imageWidth-1) || j == (imageWidth-1)) {
                    continue;
                }

                neighbourPixelInfo[0][0] = pixelColorNums[(j-1)*imageWidth + i];
                neighbourPixelInfo[1][0] = pixelColorNums[j*imageWidth + (i-1)];
                neighbourPixelInfo[2][0] = pixelColorNums[j*imageWidth + (i+1)];
                neighbourPixelInfo[3][0] = pixelColorNums[(j+1)*imageWidth + i];

                // If pixel is surrounded on all 4 sides by pixels all of color ID 0 (e.g. indicating in MandelBrot set)
                // we do nothing, since this pixel should definitely be a solid color
                if (neighbourPixelInfo[0][0] == 0
                        && neighbourPixelInfo[0][0] == neighbourPixelInfo[1][0]
                        && neighbourPixelInfo[1][0] == neighbourPixelInfo[2][0]
                        && neighbourPixelInfo[2][0] == neighbourPixelInfo[3][0]){
                    continue;
                }

                neighbourPixelInfo[0][1] = iterationCountsCopy[(j-1)*imageWidth + i];
                neighbourPixelInfo[1][1] = iterationCountsCopy[j*imageWidth + (i-1)];
                neighbourPixelInfo[2][1] = iterationCountsCopy[j*imageWidth + (i+1)];
                neighbourPixelInfo[3][1] = iterationCountsCopy[(j+1)*imageWidth + i];

                neighbourPixelIterationTotal = 0;

                for (int k = 0; k < 4; k++) {
                    neighbourPixelIterationTotal += neighbourPixelInfo[k][1];
                }

                // Check local variance in iteration counts & current pixel (i.e. comparing color shades);
                // if local variance is below threshold the following process would make little difference,
                // so we move on to the next pixel.
                if ((float)(Math.abs(neighbourPixelIterationTotal - 4 * iterationCountsCopy[j*imageWidth+i])) < 0.1f * (float)iterationCountsCopy[j*imageWidth+i]) {
                    continue;
                }

                subPixelIterationTotal = iterationCountsCopy[j*imageWidth+i];

                c = pixelToComplex(i, j);
                samples[0] = c.add(new Complex(-spd, spd));
                samples[1] = c.add(new Complex(spd, spd));
                samples[2] = c.add(new Complex(-spd, -spd));
                samples[3] = c.add(new Complex(spd, -spd));

                long t1 = System.currentTimeMillis();

                for (int k = 0; k < 4; k++) {
                    subPixelIterationTotal += iterator.iterate(samples[k])[1];
                }

                iterateTime += (System.currentTimeMillis()-t1);

                iterationCounts[j*imageWidth+i] = Math.round((float) subPixelIterationTotal / 5f);
                changedPixels ++;

            }
        }

        colorFractal();
        iterationCounts = iterationCountsCopy.clone();
        System.out.println("AA time: " + (System.currentTimeMillis()-t0));
        System.out.println("AA iterate time: " + iterateTime);
        System.out.println("Changed pixels: " + changedPixels);

    }

    public class MultithreadedRenderer {

        int[] regionYCoords;

        public void render() throws InterruptedException {
            System.out.println("Rendering with " + numThreads + " threads...");

            regionYCoords = new int[numThreads+1];
            regionYCoords[0] = 0;
            regionYCoords[numThreads] = imageHeight;
            for (int i = 1; i < numThreads; i++) {
                regionYCoords[i] = i*(imageHeight/numThreads);
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

            //colorFractal();
        }

        private Runnable createRenderThread(final int i) {
            return () -> renderRegion(i);
        }

        public void renderRegion(int regionIndex) {
            int y = regionYCoords[regionIndex];
            int height = regionYCoords[regionIndex+1] - y;
            renderRect(0, y, imageWidth, height);
        }

        public void supersampleRegion(int regionIndex) {
            int y = regionYCoords[regionIndex];
            int height = regionYCoords[regionIndex+1] - y;
        }

    }

}
