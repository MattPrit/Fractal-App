import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

enum FractalMode {MANDELBROT, JULIA, NEWTON, BURNINGSHIP, MULTIBROT, TRICORN, BHUDDABROT}

public class FractalApp {
    private Fractal fractal;
    private JPanel pictureFrame, fractalControlPanel, topPanel;
    private JLayeredPane mainMenu, fractalViewer;
    private JTextField xInput, yInput, zoomInput, powerInput, cRealInput, cImagInput;
    private JFrame appFrame;
    private FractalMode fractalMode;

    public FractalApp() {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                appFrame = new JFrame("Fractal App");
                appFrame.setIconImage(new ImageIcon(getClass().getResource("/Resources/Images/Iconv2.png")).getImage());
                appFrame.setSize(800, 480);
                appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }

                mainMenu = getMainMenu();

                appFrame.getContentPane().add(BorderLayout.CENTER, mainMenu);
                appFrame.setVisible(true);

            }
        });

        // System.setProperty("sun.java2d.opengl", "true");
    }

    public static void main(String[] args) {
        new FractalApp();
    }

    private JLayeredPane getMainMenu() {
        JLayeredPane mainMenu = new JLayeredPane();
        mainMenu.setSize(this.appFrame.getSize());

        JPanel backgroundFrame = new JPanel();
        ImageIcon backgroundImage = new ImageIcon(getClass().getResource("/Resources/Images/background1.png"));
        JLabel menuBackground = new JLabel(backgroundImage);
        backgroundFrame.setBounds(0, -4, backgroundImage.getIconWidth(), backgroundImage.getIconHeight());
        backgroundFrame.add(menuBackground);

        JPanel fractalSelectionBox = new JPanel();
        fractalSelectionBox.setBorder(BorderFactory.createTitledBorder("Choose fractal type"));
        fractalSelectionBox.setSize(this.appFrame.getSize());
        fractalSelectionBox.setOpaque(false);
        int x = (appFrame.getWidth() - 420) / 2;
        int y = (appFrame.getHeight() - 420) / 2;
        fractalSelectionBox.setBounds(x,y,400,400);
        fractalSelectionBox.setLayout(new GridLayout(0, 1));

        // Create fractal selection buttons and populate fractalSelectionBox via enumeration of FractalMode type
        for (FractalMode f:FractalMode.values()) {
            JButton newButton = new JButton(f.name().substring(0, 1) + f.name().substring(1).toLowerCase());
            newButton.addActionListener((e) -> {
                this.fractalMode = f;
                System.out.println(this.fractalMode.toString());
                this.appFrame.remove(this.mainMenu);
                setupFractalViewer();
                this.appFrame.revalidate();
                this.getUpdateFractalButton().doClick();
            });
            fractalSelectionBox.add(newButton);
        }

        mainMenu.add(backgroundFrame, Integer.valueOf(0));
        mainMenu.add(new JPanel(), Integer.valueOf(1));
        mainMenu.add(fractalSelectionBox, Integer.valueOf(1));
        mainMenu.setVisible(true);

        return  mainMenu;
    }

    private void setupFractalViewer() {

        switch (this.fractalMode) {

            case MANDELBROT: this.fractal = new MandelbrotFractal(new Complex(0, 0), 80, 48, 1.0, 1000, ColorStyle.RAINBOW);
                //this.fractal.setColorMode(ColorMode.HISTOGRAM);
                break;
            case JULIA: this.fractal = new JuliaFractal(new Complex(0, 0), 800, 480, 1.0, 150, ColorStyle.TWOCOLOUR, 2, new Complex(-0.835, 0.2321));
                break;
            case NEWTON: this.fractal = new NewtonFractal();
                break;
            case BURNINGSHIP: this.fractal = new BurningShipFractal(new Complex(-1.7, 0.0), 800, 480, 25, 150, ColorStyle.THREECOLOUR);
                break;
            case MULTIBROT: this.fractal = new MultibrotFractal(new Complex(), 800, 480, 1.0, 100, ColorStyle.RAINBOW, 2.3568);
                break;
            case TRICORN: this.fractal = new TricornFractal(new Complex(), 800, 480, 1.0, 40);
                break;
            case BHUDDABROT: this.fractal = new NebulaBrotFractal(new Complex(-0.4), 800,480, 1.0, 100, ColorStyle.TWOCOLOUR, 100000); //BhuddabrotFractal();
                break;
        }

        // Create and display default fractal image
        this.fractal.setImageDimensions(this.appFrame.getWidth(), this.appFrame.getHeight());
        this.fractal.setupFractal();

        this.topPanel = this.getTopPanel();
        this.pictureFrame = this.getPictureFrame();
        this.fractalControlPanel = this.getFractalControlPanel();

        appFrame.getContentPane().add(BorderLayout.NORTH, topPanel);
        appFrame.getContentPane().add(BorderLayout.CENTER, pictureFrame);
        appFrame.getContentPane().add(BorderLayout.SOUTH, fractalControlPanel);
    }

    private JPanel getTopPanel() {
        JPanel topPanel = new JPanel();
        JButton homeButton = new JButton("Home");
        JButton resetButton = new JButton("Reset");
        JButton saveButton = new JButton("Save");

        homeButton.addActionListener((e) -> {
            this.appFrame.remove(this.pictureFrame);
            this.appFrame.remove(this.topPanel);
            this.appFrame.remove(this.fractalControlPanel);
            this.mainMenu = this.getMainMenu();
            this.appFrame.add(this.mainMenu);
            this.appFrame.revalidate();
            this.appFrame.repaint();
        });

        resetButton.addActionListener((e) -> {
            this.appFrame.remove(this.pictureFrame);
            this.appFrame.remove(this.topPanel);
            this.appFrame.remove(this.fractalControlPanel);
            this.setupFractalViewer();
            this.appFrame.revalidate();
            this.appFrame.repaint();
            this.getUpdateFractalButton().doClick();
        });

        saveButton.addActionListener((e) -> this.fractal.saveFractal("testFractal.png"));

        JPanel buttonHolder = new JPanel();

        buttonHolder.add(homeButton);
        buttonHolder.add(resetButton);
        buttonHolder.add(saveButton);

        topPanel.add(buttonHolder);

        if (this.fractalMode == FractalMode.JULIA) {
            topPanel.add(BorderLayout.AFTER_LAST_LINE, this.getJuliaPanel());
        }

        if (this.fractalMode == FractalMode.MULTIBROT) {
            topPanel.add(BorderLayout.AFTER_LAST_LINE, this.getMultibrotPanel());
        }

        if (this.fractalMode == FractalMode.NEWTON) {
            topPanel.add(BorderLayout.AFTER_LAST_LINE, this.getNewtonPanel());
        }

        return topPanel;
    }

    private JPanel getPictureFrame() {
        JPanel pictureFrame = new JPanel();

        pictureFrame.add(new JLabel(new ImageIcon(this.fractal.getFractalImage())));

        pictureFrame.addMouseWheelListener((e) -> {
            if(e.getWheelRotation() < 0) {
                this.fractal.setZoomLevel(this.fractal.getZoomLevel() * (1 / 1.1));
            }
            if(e.getWheelRotation() > 0) {
                this.fractal.setZoomLevel(this.fractal.getZoomLevel() * 1.1);
            }
            this.zoomInput.setText(String.valueOf(this.fractal.getZoomLevel()));
            this.getUpdateFractalButton().doClick();
        });

        pictureFrame.addMouseListener(new fractalMouseListener());

        return pictureFrame;
    }

    private class fractalMouseListener implements MouseListener {

        int startX, startY;

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {
            startX = e.getX();
            startY = e.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            int endX = e.getX();
            int endY = e.getY();

            int dx = endX - startX;
            int dy = endY - startY;

            if (fractalMode == FractalMode.BHUDDABROT){

                int h = pictureFrame.getBounds().height;
                double d = 4.0 / (h * fractal.getZoomLevel());

                double dX = -1 * (d * (endX - startX));
                double dY = 1 * (d * (endY - startY));

                double newX = fractal.getCentre().getReal() + dX;
                double newY = fractal.getCentre().getImag() + dY;

                fractal.setCentre(newX, newY);
                xInput.setText(String.valueOf(newX));
                yInput.setText(String.valueOf(newY));
                getUpdateFractalButton().doClick();

            }else {

                if (fractalMode == FractalMode.BURNINGSHIP) {
                    dy = -1 * dy;
                }

                fractal.translateFractal(dx, dy);
                BufferedImage fractalImage = fractal.getFractalImage();
                pictureFrame.removeAll();
                pictureFrame.add(new JLabel(new ImageIcon(fractalImage)));
                appFrame.revalidate();
                xInput.setText(String.valueOf(fractal.getCentre().getReal()));
                yInput.setText(String.valueOf(fractal.getCentre().getImag()));
            }

        }

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
    }

    private JPanel getJuliaPanel() {
        JPanel juliaPanel = new JPanel();

        JLabel powerInputLabel = new JLabel("Enter power:");
        this.powerInput = new JTextField(4);
        JLabel cRealInputLabel = new JLabel("Enter Re(c):");
        this.cRealInput = new JTextField(4);
        JLabel cImagInputLabel = new JLabel("Enter Im(c):");
        this.cImagInput = new JTextField(4);

        powerInput.setText(String.valueOf(this.fractal.iterator.getPower()));
        cRealInput.setText(String.valueOf(this.fractal.iterator.getC().getReal()));
        cImagInput.setText(String.valueOf(this.fractal.iterator.getC().getImag()));

        juliaPanel.add(powerInputLabel);
        juliaPanel.add(powerInput);
        juliaPanel.add(cRealInputLabel);
        juliaPanel.add(cRealInput);
        juliaPanel.add(cImagInputLabel);
        juliaPanel.add(cImagInput);

        return juliaPanel;
    }

    private JPanel getMultibrotPanel() {
        JPanel multibrotPanel = new JPanel();
        JLabel powerInputLabel = new JLabel("Enter power:");
        this.powerInput = new JTextField(4);
        powerInput.setText(String.valueOf(this.fractal.iterator.getPower()));
        multibrotPanel.add(powerInputLabel);
        multibrotPanel.add(powerInput);

        return multibrotPanel;
    }

    private JPanel getNewtonPanel() {
        JPanel newtonPanel = new JPanel();

        String fractalPolyString = ((NewtonIterator)((NewtonFractal)fractal).iterator).getF().toString();
        JButton newtonButton = new JButton(fractalPolyString);

        newtonButton.addActionListener(
                (e) -> new NewtonControlWindow()
        );

        newtonPanel.add(newtonButton);

        return newtonPanel;
    }

    private JPanel getFractalControlPanel() {

        JLabel zoomLabel = new JLabel("Zoom:");
        JButton zoomInButton = new JButton("+");
        JButton zoomOutButton = new JButton("-");

        JLabel xInputLabel = new JLabel("Enter centre x:");
        this.xInput = new JTextField(4);
        JLabel yInputLabel = new JLabel("Enter centre y:");
        this.yInput = new JTextField(4);

        this.zoomInput = new JTextField(4);

        this.xInput.setText(String.valueOf(this.fractal.getCentre().getReal()));
        this.yInput.setText(String.valueOf(this.fractal.getCentre().getImag()));
        this.zoomInput.setText(String.valueOf(this.fractal.getZoomLevel()));

        zoomInButton.addActionListener((e) -> {
            this.fractal.setZoomLevel(this.fractal.getZoomLevel() * 1.5);
            this.zoomInput.setText(String.valueOf(this.fractal.getZoomLevel()));
            this.getUpdateFractalButton().doClick();
        });

        zoomOutButton.addActionListener((e) -> {
            this.fractal.setZoomLevel(this.fractal.getZoomLevel() * (1 / 1.5));
            this.zoomInput.setText(String.valueOf(this.fractal.getZoomLevel()));
            this.getUpdateFractalButton().doClick();
        });

        // Create fractal control panel
        this.fractalControlPanel = new JPanel();
        fractalControlPanel.add(xInputLabel);
        fractalControlPanel.add(xInput);
        fractalControlPanel.add(yInputLabel);
        fractalControlPanel.add(yInput);
        fractalControlPanel.add(this.getUpdateFractalButton());
        fractalControlPanel.add(zoomLabel);
        fractalControlPanel.add(zoomInButton);
        fractalControlPanel.add(zoomOutButton);
        fractalControlPanel.add(zoomInput);
        fractalControlPanel.add(this.getIterationSlider());

        ButtonGroup colourModeButtonGroup = new ButtonGroup();

        // Create fractal color buttons and populate colorModeButtonGroup via enumeration of ColorStyle type
        for (ColorStyle colorStyle : ColorStyle.values()) {
            JRadioButton newButton = new JRadioButton(colorStyle.name().substring(0, 1) + colorStyle.name().substring(1).toLowerCase());
            newButton.addActionListener((e) -> {
                this.fractal.setColorStyle(colorStyle);
                this.getRepaintFractalButton().doClick();
            });
            if (this.fractal.getColorStyle() == colorStyle) {
                newButton.setSelected(true);
            }
            colourModeButtonGroup.add(newButton);
            fractalControlPanel.add(newButton);
        }

        // Add colour selection buttons
        for (int j=0; j<this.fractal.getOrder()+1; j++) {
            fractalControlPanel.add(this.getColourButton(j));
        }

        return fractalControlPanel;
    }

    private JButton getUpdateFractalButton() {
        JButton updateFractalButton = new JButton("Update Fractal");

        updateFractalButton.addActionListener((e) -> {

            Rectangle r = pictureFrame.getBounds();
            this.fractal.setImageDimensions(r.width, r.height);
            double centreX = Double.parseDouble(xInput.getText().trim());
            double centreY = Double.parseDouble(yInput.getText().trim());

            if (this.fractalMode == FractalMode.JULIA) {
                double cReal = Double.parseDouble(cRealInput.getText().trim());
                double cImag = Double.parseDouble(cImagInput.getText().trim());
                double pow = Double.parseDouble(powerInput.getText().trim());
                this.fractal.iterator.setC(new Complex(cReal, cImag));
                this.fractal.iterator.setPower(pow);
            }

            if (this.fractalMode == FractalMode.MULTIBROT) {
                double pow = Double.parseDouble(powerInput.getText().trim());
                this.fractal.iterator.setPower(pow);
            }

            this.fractal.setCentre(centreX, centreY);
            this.fractal.setZoomLevel(Double.parseDouble(zoomInput.getText().trim()));

            if (this.fractalMode == FractalMode.BHUDDABROT) {
                this.fractal.toggleMultiSamplingEnabled();
            }

            this.fractal.createFractal();

            BufferedImage fractalImage = this.fractal.getFractalImage();
            pictureFrame.removeAll();
            pictureFrame.add(new JLabel(new ImageIcon(fractalImage)));
            appFrame.revalidate();
        });

        return updateFractalButton;
    }

    private JButton getRepaintFractalButton() {
        JButton repaintFractalButton = new JButton("Repaint Fractal");

        repaintFractalButton.addActionListener((e) -> {
            this.fractal.colorFractal();
            BufferedImage fractalImage = this.fractal.getFractalImage();
            pictureFrame.removeAll();
            pictureFrame.add(new JLabel(new ImageIcon(fractalImage)));
            appFrame.revalidate();
        });

        return repaintFractalButton;
    }

    private JSlider getIterationSlider(){
        JSlider iterationSlider = new JSlider(JSlider.HORIZONTAL, 10, 5 * this.fractal.getMaxIterations(), this.fractal.getMaxIterations());

        iterationSlider.addChangeListener((e) -> {
            try {
                this.fractal.iterator.setMaxIterations(iterationSlider.getValue());
            }catch (NullPointerException n) {
                this.fractal.setMaxIterations(iterationSlider.getValue());
            }
            this.getUpdateFractalButton().doClick();
        });

        return iterationSlider;
    }

    private JButton getColourButton(int i){
        JButton colourButton = new JButton("Col " + String.valueOf(i + 1));
        colourButton.setBackground(this.fractal.getColour(i));
        colourButton.setMargin(new Insets(0,0,0,0));

        colourButton.addActionListener((e) -> {
            ColourWindow cw = new ColourWindow(this.fractal.getColour(i));
            this.fractal.setColour(i, cw.getColour());
            colourButton.setBackground(this.fractal.getColour(i));
            this.getRepaintFractalButton().doClick();
        });

        return colourButton;
    }

    private class FractalControlWindow {

        private JFrame controlFrame;
        private JPanel controlPanel;

        private FractalControlWindow() {
            this.controlFrame = new JFrame("Fractal Control Window");
            controlFrame.setSize(400, 400);
            controlFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            controlFrame.setResizable(false);
            controlFrame.setVisible(true);

            controlPanel = new JPanel();
            controlPanel.setLayout(new GridLayout(0,1));
            controlPanel.setVisible(true);

            JLabel zoomLabel = new JLabel("Zoom:");
            JButton zoomInButton = new JButton("+");
            JButton zoomOutButton = new JButton("-");

            JLabel xInputLabel = new JLabel("Enter centre x:");
            JLabel yInputLabel = new JLabel("Enter centre y:");

            // Create fractal control panel
            controlPanel.add(xInputLabel);
            controlPanel.add(xInput);
            controlPanel.add(yInputLabel);
            controlPanel.add(yInput);
            controlPanel.add(getUpdateFractalButton());
            controlPanel.add(zoomLabel);
            controlPanel.add(zoomInButton);
            controlPanel.add(zoomOutButton);
            controlPanel.add(zoomInput);
            controlPanel.add(getIterationSlider());

            controlFrame.add(controlPanel);
        }
    }

    private class NewtonControlWindow {

        private JFrame newtonControlFrame;
        private JPanel newtonControlPanel;
        private int currentDegreeValue;
        //private JScrollPane coeffInputPane;
        private JTextField[][] coeffInputs;

        private NewtonControlWindow() {
            currentDegreeValue = fractal.iterator.getOrder();

            newtonControlFrame = new JFrame("Newton Control Panel");
            newtonControlFrame.setSize(400, 400);
            newtonControlFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            newtonControlFrame.setResizable(false);
            newtonControlFrame.setVisible(true);

            newtonControlPanel = new JPanel();
            newtonControlPanel.add(this.getStartPanel());

            newtonControlFrame.add(newtonControlPanel);
        }

        private JPanel getStartPanel() {

            JPanel startPanel = new JPanel();
            JLabel polyDegreeInputLabel = new JLabel("Enter polynomial degree: ");
            JButton polyDegreeSetButton = new JButton("OK");
            JTextField polyDegreeField = new JTextField(2);
            polyDegreeField.setText(String.valueOf(currentDegreeValue));
            polyDegreeSetButton.addActionListener(e -> {
                System.out.println(currentDegreeValue);
                currentDegreeValue = Integer.valueOf(polyDegreeField.getText());
                System.out.println(currentDegreeValue);
                newtonControlPanel.removeAll();
                newtonControlPanel.repaint();
                newtonControlFrame.revalidate();
                newtonControlPanel.add(BorderLayout.NORTH, this.getDegreeInputPanel());
                newtonControlPanel.add(BorderLayout.CENTER, this.getCoeffInputPane());
                newtonControlPanel.add(BorderLayout.SOUTH, this.getBottomPanel());
                newtonControlFrame.revalidate();
            });
            startPanel.add(polyDegreeInputLabel);
            startPanel.add(polyDegreeField);
            startPanel.add(polyDegreeSetButton);

            return startPanel;
        }

        private JPanel getDegreeInputPanel() {
            JPanel degreeInputPanel = new JPanel();
            JLabel degreeLabel = new JLabel("Polynomial degree: ");
            degreeInputPanel.add(degreeLabel);
            JButton degreeButton = new JButton(String.valueOf(this.currentDegreeValue));

            degreeButton.addActionListener(e -> {
                newtonControlPanel.removeAll();
                newtonControlPanel.repaint();
                newtonControlFrame.revalidate();
                newtonControlPanel.add(this.getStartPanel());
                newtonControlFrame.revalidate();
            });

            degreeInputPanel.add(degreeButton);
            return degreeInputPanel;
        }

        private JPanel getCoeffInputPane() {

            JPanel coeffInputPane = new JPanel();
            coeffInputPane.setLayout(new GridLayout(0, 1));
            coeffInputs = new JTextField[currentDegreeValue+1][2];

            for (int i = 0; i <= currentDegreeValue; i++) {
                JPanel panel = new JPanel();
                JLabel label = new JLabel("z^" + i + " coefficient : ");
                JLabel realLabel = new JLabel(" Re: ");
                coeffInputs[i][0] = new JTextField("0", 4);
                JLabel imagLabel = new JLabel(" Im: ");
                coeffInputs[i][1] = new JTextField("0", 4);
                panel.add(label);
                panel.add(realLabel);
                panel.add(coeffInputs[i][0]);
                panel.add(imagLabel);
                panel.add(coeffInputs[i][1]);
                coeffInputPane.add(panel);
            }

            JButton setCoeffsButton = new JButton("Set");
            setCoeffsButton.addActionListener(e -> {
                Complex[] newCoeffs = new Complex[currentDegreeValue+1];

                for (int i = 0; i <= currentDegreeValue; i++) {
                    double x = Double.parseDouble(coeffInputs[i][0].getText().trim());
                    double y = Double.parseDouble(coeffInputs[i][1].getText().trim());
                    newCoeffs[i] = new Complex(x, y);
                }

                ((NewtonIterator)(fractal.iterator)).setF(new Polynomial(newCoeffs));
                fractal.setupFractal();
            });

            coeffInputPane.add(setCoeffsButton);

            return coeffInputPane;
        }

        private JPanel getBottomPanel() {
            JPanel bottomPanel = new JPanel();
            JButton doneButton = new JButton("Done");
            doneButton.addActionListener(e -> newtonControlFrame.dispose());
            bottomPanel.add(doneButton);
            return  bottomPanel;
        }

    }
}

