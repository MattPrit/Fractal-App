import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

enum FractalMode {MANDELBROT, JULIA, NEWTON, BURNINGSHIP, MULTIBROT, TRICORN, BHUDDABROT}

public class FractalApp {

    private Fractal fractal;
    private JFrame appFrame;
    private FractalMode fractalMode;
    private SettingsWindow settingsWindow;
    private ColorWindow colorWindow;
    private FractalViewer fractalViewer;
    private FractalMenu fractalMenu;
    private BhuddaWindow bhuddaWindow;

    public FractalApp() {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                appFrame = new JFrame("Fractal App");
                appFrame.setIconImage(new ImageIcon(this.getClass().getResource("/resources/images/Iconv2.png")).getImage());
                appFrame.setSize(800, 480);
                appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                fractalMenu = new FractalMenu();
                fractalViewer = new FractalViewer();
                settingsWindow = new SettingsWindow();
                colorWindow = new ColorWindow();
                bhuddaWindow = new BhuddaWindow();

                appFrame.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentMoved(ComponentEvent e) {
                        super.componentMoved(e);
                        settingsWindow.setLocation(appFrame.getX()+appFrame.getWidth()-settingsWindow.getWidth()-7, appFrame.getY()+66);

                    }
                });

                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }

                appFrame.setVisible(true);

            }
        });

        // System.setProperty("sun.java2d.opengl", "true");
    }

    public static void main(String[] args) {
        new FractalApp();
    }

    private class FractalMenu extends JLayeredPane {

        private FractalMenu() {
            super();
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            setup();
        }

        private void setup() {
            setSize(appFrame.getSize());

            JPanel backgroundFrame = new JPanel();
            ImageIcon backgroundImage = new ImageIcon(getClass().getResource("resources/images/background1.png"));
            JLabel menuBackground = new JLabel(backgroundImage);
            backgroundFrame.setBounds(0, -4, backgroundImage.getIconWidth(), backgroundImage.getIconHeight());
            backgroundFrame.add(menuBackground);

            JPanel fractalSelectionBox = new JPanel();
            fractalSelectionBox.setBorder(BorderFactory.createTitledBorder("Choose fractal type"));
            fractalSelectionBox.setSize(appFrame.getSize());
            fractalSelectionBox.setOpaque(false);
            int x = (appFrame.getWidth() - 420) / 2;
            int y = (appFrame.getHeight() - 420) / 2;
            fractalSelectionBox.setBounds(x,y,400,400);
            fractalSelectionBox.setLayout(new GridLayout(0, 1));

            // Create fractal selection buttons and populate fractalSelectionBox via enumeration of FractalMode type
            for (FractalMode f:FractalMode.values()) {
                JButton newButton = new JButton(f.name().substring(0, 1) + f.name().substring(1).toLowerCase());
                newButton.addActionListener((e) -> {
                    fractalMode = f;
                    System.out.println(fractalMode.toString());
                    appFrame.remove(this);
                    fractalViewer.setup();
                    colorWindow.setup();
                    appFrame.revalidate();
                });
                fractalSelectionBox.add(newButton);
            }

            add(backgroundFrame, Integer.valueOf(0));
            add(new JPanel(), Integer.valueOf(1));
            add(fractalSelectionBox, Integer.valueOf(1));
            setVisible(true);

            appFrame.add(this);
            appFrame.revalidate();
            appFrame.repaint();
        }
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

                int h = fractalViewer.fractalFrame.getBounds().height;
                double d = 4.0 / (h * fractal.getZoomLevel());

                double dX = -1 * (d * (endX - startX));
                double dY = 1 * (d * (endY - startY));

                double newX = fractal.getCentre().getReal() + dX;
                double newY = fractal.getCentre().getImag() + dY;

                fractal.setCentre(newX, newY);
                settingsWindow.update();
                fractalViewer.updateFractal();
                fractalViewer.refresh();

            } else {

                if (fractalMode == FractalMode.BURNINGSHIP) {
                    dy = -1 * dy;
                }

                fractal.translateFractal(dx, dy);
                fractalViewer.refresh();
                settingsWindow.update();
                appFrame.revalidate();
            }

        }

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
    }

    private class FractalViewer extends JPanel {

        private JLabel centerLabel, zoomLabel;
        private JTextField juliaPowInput, juliaReCInput, juliaImCInput, multiPowInput;
        private JPanel topPanel, fractalFrame, bottomPanel, extraPanel;

        private FractalViewer() {

            super();

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
                System.out.println("Error loading system look and feel for fractal viewer");
                e.printStackTrace();
            }

            setLayout(new BorderLayout());

            this.centerLabel = new JLabel();
            this.zoomLabel = new JLabel();
            this.juliaPowInput = new JTextField("2.0",4);
            this.juliaReCInput = new JTextField("-0.835",4);
            this.juliaImCInput = new JTextField("0.2321",4);
            this.multiPowInput = new JTextField("2.3568",4);
            this.fractalFrame = new JPanel();
            this.topPanel = new JPanel();
            this.bottomPanel = new JPanel();
            this.extraPanel = new JPanel();

            JPanel buttonPanel = new JPanel();
            JLabel centerLabel = new JLabel("Centre: " + new Complex().toShortString());
            JLabel zoomLabel = new JLabel("Zoom level:      1.00");
            JButton homeButton = new JButton("Home");
            JButton resetButton = new JButton("Reset");
            JButton saveButton = new JButton("Save");
            JButton zoomInButton = new JButton("+");
            JButton zoomOutButton = new JButton("-");
            JToggleButton settingsButton = new JToggleButton("Advanced");
            JToggleButton colorButton = new JToggleButton("Colors");

            homeButton.addActionListener((e) -> {
                appFrame.remove(fractalViewer);
                settingsWindow.visible = false;
                settingsButton.setSelected(false);
                settingsWindow.update();
                colorWindow.visible = false;
                colorButton.setSelected(false);
                colorWindow.update();
                appFrame.add(fractalMenu);
                appFrame.revalidate();
                appFrame.repaint();
            });

            resetButton.addActionListener((e) -> {
                appFrame.remove(fractalViewer);
                setup();
                appFrame.revalidate();
                appFrame.repaint();
                settingsWindow.update();
            });

            saveButton.addActionListener((e) -> fractal.saveFractal("testFractal.png"));

            settingsButton.addActionListener((e) -> {
                colorWindow.visible = false;
                colorButton.setSelected(false);
                colorWindow.update();
                settingsWindow.toggleVisible();
                settingsWindow.update();
            });

            colorButton.addActionListener((e -> {
                settingsWindow.visible = false;
                settingsButton.setSelected(false);
                settingsWindow.update();
                colorWindow.toggleVisible();
                colorWindow.update();
            }));

            zoomInButton.addActionListener((e) -> {
                fractal.setZoomLevel(fractal.getZoomLevel() * 1.5);
                settingsWindow.update();
                this.updateFractal();
                this.refresh();
            });

            zoomOutButton.addActionListener((e) -> {
                fractal.setZoomLevel(fractal.getZoomLevel() * (1 / 1.5));
                settingsWindow.update();
                this.updateFractal();
                this.refresh();
            });

            fractalFrame.addMouseWheelListener((e) -> {
                if(e.getWheelRotation() < 0) {
                    fractal.setZoomLevel(fractal.getZoomLevel() * (1 / 1.1));
                }
                if(e.getWheelRotation() > 0) {
                    fractal.setZoomLevel(fractal.getZoomLevel() * 1.1);
                }
                this.updateFractal();
                this.refresh();
            });

            fractalFrame.addMouseListener(new fractalMouseListener());

            this.topPanel.add(homeButton);
            this.topPanel.add(resetButton);
            this.topPanel.add(saveButton);
            this.topPanel.add(settingsButton);
            this.topPanel.add(colorButton);
            this.topPanel.add(this.extraPanel);

            buttonPanel.add(zoomInButton);
            buttonPanel.add(zoomOutButton);

            this.bottomPanel.setLayout(new GridLayout(0,4));
            this.bottomPanel.add(centerLabel);
            this.bottomPanel.add(zoomLabel);
            this.bottomPanel.add(buttonPanel);

            add(BorderLayout.NORTH, this.topPanel);
            add(BorderLayout.CENTER, this.fractalFrame);
            add(BorderLayout.SOUTH, this.bottomPanel);

        }

        private void setup() {

            this.topPanel.remove(this.extraPanel);
            this.extraPanel.removeAll();

            JLabel juliaPowLabel = new JLabel("Enter power: ");
            JLabel juliaReCLabel = new JLabel("Enter Re(c): ");
            JLabel juliaImCLabel = new JLabel("Enter Im(c): ");
            JLabel multiPowLabel = new JLabel("Enter power: ");
            JButton updateButton = new JButton("Update");
            JToggleButton newtonButton = new JToggleButton("Set polynomial");
            JToggleButton bhuddaButton = new JToggleButton("Options");

            updateButton.addActionListener((e) -> {
                this.updateFractal();
            });

            newtonButton.addActionListener((e) -> {
                settingsWindow.visible = false;
                //settingsButton.setSelected(false);
                settingsWindow.update();
                colorWindow.visible = false;
                //colorButton.setSelected(false);
                colorWindow.update();
                new NewtonControlWindow();
            });

            bhuddaButton.addActionListener((e) -> {
                settingsWindow.visible = false;
                //settingsButton.setSelected(false);
                settingsWindow.update();
                colorWindow.visible = false;
                //colorButton.setSelected(false);
                colorWindow.update();
                bhuddaWindow.toggleVisible();
                bhuddaWindow.update();
            });

            switch (fractalMode) {

                case MANDELBROT: fractal = new MandelbrotFractal(new Complex(0, 0), 80, 48, 1.0, 1000, ColorStyle.RAINBOW);
                    //this.fractal.setColorMode(ColorMode.HISTOGRAM);
                    break;
                case JULIA: fractal = new JuliaFractal(new Complex(0, 0), 800, 480, 1.0, 150, ColorStyle.TWOCOLOUR, 2, new Complex(-0.835, 0.2321));
                    extraPanel.add(juliaPowLabel);
                    extraPanel.add(this.juliaPowInput);
                    extraPanel.add(juliaReCLabel);
                    extraPanel.add(this.juliaReCInput);
                    extraPanel.add(juliaImCLabel);
                    extraPanel.add(this.juliaImCInput);
                    extraPanel.add(updateButton);
                    break;
                case NEWTON: fractal = new NewtonFractal();
                    newtonButton.setText(((NewtonIterator) fractal.iterator).getF().toString());
                    extraPanel.add(newtonButton);
                    break;
                case BURNINGSHIP: fractal = new BurningShipFractal(new Complex(-1.7, 0.0), 800, 480, 25, 150, ColorStyle.THREECOLOUR);
                    break;
                case MULTIBROT: fractal = new MultibrotFractal(new Complex(), 800, 480, 1.0, 100, ColorStyle.RAINBOW, 2.3568);
                    extraPanel.add(multiPowLabel);
                    extraPanel.add(this.multiPowInput);
                    extraPanel.add(updateButton);
                    break;
                case TRICORN: fractal = new TricornFractal(new Complex(), 800, 480, 1.0, 40);
                    break;
                case BHUDDABROT: fractal = new BhuddabrotFractal(new Complex(-0.4), 800,480, 1.0, 100, ColorStyle.TWOCOLOUR, 1000000); //BhuddabrotFractal();
                    extraPanel.add(bhuddaButton);
                    break;
            }

            this.topPanel.add(this.extraPanel);
            System.out.println(this.extraPanel);

            appFrame.getContentPane().add(BorderLayout.CENTER, this);
            appFrame.setVisible(true);
            System.out.println(fractalFrame.getWidth() + " " + fractalFrame.getHeight());

            updateFractal();

        }

        private void updateFractal() {

            Rectangle r = fractalFrame.getBounds();
            fractal.setImageDimensions(r.width, r.height);

            if (fractalMode == FractalMode.JULIA) {
                double cReal = Double.parseDouble(juliaReCInput.getText().trim());
                double cImag = Double.parseDouble(juliaImCInput.getText().trim());
                double pow = Double.parseDouble(juliaPowInput.getText().trim());
                ((JuliaFractal) fractal).iterator.setC(new Complex(cReal, cImag));
                ((JuliaFractal) fractal).iterator.setPower(pow);
            }

            if (fractalMode == FractalMode.MULTIBROT) {
                double pow = Double.parseDouble(multiPowInput.getText().trim());
                ((MultibrotFractal) fractal).iterator.setPower(pow);
            }

            if (fractalMode == FractalMode.BHUDDABROT) {
                ((BhuddabrotFractal)fractal).createFractal2();
            } else {
                fractal.createFractal();
            }

            refresh();

        }

        private void refresh() {

            this.bottomPanel.remove(0);
            this.bottomPanel.remove(0);

            this.centerLabel.setText("Centre: " + fractal.getCentre().toShortString());
            this.zoomLabel.setText(String.format("Zoom level: %10.2f", fractal.getZoomLevel()));

            this.bottomPanel.add(centerLabel, 0);
            this.bottomPanel.add(zoomLabel, 1);
            //this.bottomPanel.repaint();
            this.topPanel.repaint();
            //this.bottomPanel.revalidate();
            this.topPanel.revalidate();

            BufferedImage fractalImage = fractal.getFractalImage();
            this.fractalFrame.removeAll();
            this.fractalFrame.add(new JLabel(new ImageIcon(fractalImage)));

            revalidate();
            repaint();

            appFrame.revalidate();
            appFrame.repaint();

        }

    }

    private class SettingsWindow extends JFrame {

        private JLabel xInputLabel, yInputLabel, zoomLabel, iterationLabel;
        private JTextField xInput, yInput, zoomInput;
        private JToggleButton multithreadButton, multisamplingButton;
        private JButton applyButton;
        private JSlider slider;
        private boolean visible;

        private SettingsWindow() {

            super("Advanced settings");
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(0,1));
            panel.setVisible(true);
            setSize(300, 350);
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            setUndecorated(true);
            setResizable(false);

            this.xInputLabel = new JLabel("Enter centre x:");
            this.xInput = new JTextField();
            this.yInputLabel = new JLabel("Enter centre y:");
            this.yInput = new JTextField(4);
            this.zoomLabel = new JLabel("Enter zoom level:");
            this.zoomInput = new JTextField(4);
            this.iterationLabel = new JLabel();
            this.slider = new JSlider(JSlider.HORIZONTAL, 10, 2000, 100);
            this.multithreadButton = new JToggleButton("Toggle multithreaded rendering");
            this.multisamplingButton = new JToggleButton("Toggle supersampling");
            this.applyButton = new JButton("Apply");
            this.visible = false;

            this.xInputLabel.setHorizontalAlignment(0);
            this.yInputLabel.setHorizontalAlignment(0);
            this.zoomLabel.setHorizontalAlignment(0);
            this.iterationLabel.setHorizontalAlignment(0);

            JPanel xPanel = new JPanel();
            JPanel yPanel = new JPanel();
            JPanel zoomPanel = new JPanel();
            JPanel iterationPanel = new JPanel();

            xPanel.setLayout(new GridLayout(1, 0));
            yPanel.setLayout(new GridLayout(1, 0));
            zoomPanel.setLayout(new GridLayout(1, 0));
            iterationPanel.setLayout(new GridLayout(1, 0));

            slider.addChangeListener(l -> {
                this.iterationLabel.setText("Maximum iterations: " + slider.getValue());
            });

            applyButton.addActionListener(l -> {
                fractal.setCentre(Double.parseDouble(this.xInput.getText()), Double.parseDouble(this.yInput.getText()));
                fractal.setZoomLevel(Double.parseDouble(this.zoomInput.getText()));
                fractal.setMaxIterations(this.slider.getValue());
                if (fractal.getMultithreadingEnabled() != this.multithreadButton.isSelected()) {
                    fractal.toggleMultithreadingEnabled();
                }
                if (fractal.getSupersamplingEnabled() != this.multisamplingButton.isSelected()) {
                    fractal.toggleMultiSamplingEnabled();
                }
                fractalViewer.updateFractal();
                this.update();
            });

            xPanel.add(this.xInputLabel);
            xPanel.add(this.xInput);
            yPanel.add(this.yInputLabel);
            yPanel.add(this.yInput);
            zoomPanel.add(this.zoomLabel);
            zoomPanel.add(this.zoomInput);
            iterationPanel.add(this.iterationLabel);
            iterationPanel.add(this.slider);

            panel.add(xPanel);
            panel.add(yPanel);
            panel.add(zoomPanel);
            panel.add(iterationPanel);

            panel.add(multithreadButton);
            panel.add(multisamplingButton);
            panel.add(this.applyButton);

            add(panel);

        }

        private void toggleVisible() {
            this.visible = !this.visible;
        }

        private void update() {

            this.setLocation(appFrame.getX()+appFrame.getWidth()-this.getWidth()-7, appFrame.getY()+66);
            setAlwaysOnTop(true);

            this.xInput.setText(String.valueOf(fractal.getCentre().getReal()));
            this.yInput.setText(String.valueOf(fractal.getCentre().getImag()));
            this.zoomInput.setText(String.valueOf(fractal.getZoomLevel()));
            this.iterationLabel.setText("Maximum iterations: " + fractal.iterator.getMaxIterations());
            this.slider.setValue(fractal.getMaxIterations());
            this.multithreadButton.setSelected(fractal.getMultithreadingEnabled());
            this.multisamplingButton.setSelected(fractal.getSupersamplingEnabled());
            this.setVisible(this.visible);

        }

    }

    private class ColorWindow extends JFrame {

        private boolean visible;
        private JDialog dialogBox;
        private ColorStyle selectedColorstyle;
        private Color[] selectedColors;
        private JPanel colorSelectionBox, styleSelectionBox;

        private ColorWindow() {

            super("Color options");
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(0,1));
            panel.setVisible(true);
            setSize(300, 350);
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            setUndecorated(true);
            setResizable(false);

            this.visible = false;
            this.dialogBox = new JDialog();
            this.styleSelectionBox = new JPanel();
            this.colorSelectionBox = new JPanel();

            this.styleSelectionBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Select fractal color style:"));
            this.colorSelectionBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),"Select fractal colors"));

            JButton applyButton = new JButton("Apply");
            applyButton.addActionListener(l -> {
                fractal.colors = this.selectedColors.clone();
                fractal.setColorStyle(this.selectedColorstyle);
                fractal.colorFractal();
                fractalViewer.refresh();
                this.update();
            });

            panel.add(this.styleSelectionBox);
            panel.add(this.colorSelectionBox);
            panel.add(applyButton);
            add(panel);

        }

        private void setup() {

            this.selectedColorstyle = fractal.getColorStyle();

            this.styleSelectionBox.removeAll();
            this.styleSelectionBox.setLayout(new GridLayout(0, 1));

            ButtonGroup colourModeButtonGroup = new ButtonGroup();

            for (ColorStyle colorStyle : ColorStyle.values()) {
                JRadioButton newButton = new JRadioButton(colorStyle.name().substring(0, 1) + colorStyle.name().substring(1).toLowerCase());
                newButton.addActionListener((e) -> this.selectedColorstyle = colorStyle);
                if (fractal.getColorStyle() == colorStyle) {
                    newButton.setSelected(true);
                }
                colourModeButtonGroup.add(newButton);
                this.styleSelectionBox.add(newButton);
            }

            this.selectedColors = fractal.colors.clone();
            int nCols = selectedColors.length;

            this.colorSelectionBox.removeAll();
            this.colorSelectionBox.setLayout(new GridLayout(2, (int)(nCols/2)));

            for (int i=0; i<nCols; i++) {
                JButton colourButton = new JButton("Col " + String.valueOf(i + 1));
                colourButton.setBackground(selectedColors[i]);
                colourButton.setBorder(null);

                int finalI = i;
                colourButton.addActionListener((e) -> {
                    Color selection = JColorChooser.showDialog(this.dialogBox, "Pick a colour", selectedColors[finalI]);
                    if (selection != null) {
                        selectedColors[finalI] = selection;
                    }
                    colourButton.setBackground(selectedColors[finalI]);
                });

                colorSelectionBox.add(colourButton);
            }

        }

        private void toggleVisible() {
            this.visible = !this.visible;
        }

        private void update() {
            this.setLocation(appFrame.getX()+appFrame.getWidth()-this.getWidth()-7, appFrame.getY()+66);
            this.setVisible(this.visible);
        }

    }

    private class BhuddaWindow extends JFrame{

        private boolean visible;
        private JPanel pointBox, pointCoeffBox, iterCoeffBox;

        private boolean nebulaModeSelected;
        private JTextField rPointCoeffField, gPointCoeffField, bPointCoeffField;
        private JSlider pointSlider, rIterSlider, gIterSlider, bIterSlider;
        private JLabel pointLabel, rIterLabel, gIterLabel, bIterLabel, rPointLabel, gPointLabel, bPointLabel;
        private JToggleButton nebulaModeToggleButton;

        private BhuddaWindow() {

            super("BhuddaBrot options");
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(0,1));
            panel.setVisible(true);
            setSize(300, 350);
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            setUndecorated(true);
            setResizable(false);

            this.visible = false;

            this.pointBox = new JPanel(new GridLayout(0, 2));
            this.pointBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Set number of starting points:"));
            this.pointLabel = new JLabel("10000000");
            this.pointSlider = new JSlider(JSlider.HORIZONTAL, (int)1e4, (int)1e7, (int)1e5);
            this.pointSlider.addChangeListener(l -> {
                this.pointLabel.setText(String.valueOf(pointSlider.getValue()));
            });
            this.pointBox.add(pointLabel);
            this.pointBox.add(pointSlider);


            this.nebulaModeToggleButton = new JToggleButton("Enable nebula mode");
            this.nebulaModeToggleButton.addActionListener(l -> {
                this.nebulaModeSelected = !this.nebulaModeSelected;
                this.setNebulaInputEnabled();
            });


            JPanel pointCoeffPanel = new JPanel(new GridLayout(3,0));
            JPanel spacerPanel1 = new JPanel();
            JPanel spacerPanel2 = new JPanel();
            this.pointCoeffBox = new JPanel(new GridLayout(0,5));
            spacerPanel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),"Set ratio of points in each channel (R:G:B):"));
            this.rPointCoeffField = new JTextField("1000", 4);
            this.gPointCoeffField = new JTextField("1000", 4);
            this.bPointCoeffField = new JTextField("1000", 4);
            JLabel spacerLabel1 = new JLabel(":");
            JLabel spacerLabel2 = new JLabel(":");
            spacerLabel1.setHorizontalAlignment(0);
            spacerLabel2.setHorizontalAlignment(0);
            this.pointCoeffBox.add(rPointCoeffField);
            this.pointCoeffBox.add(spacerLabel1);
            this.pointCoeffBox.add(gPointCoeffField);
            this.pointCoeffBox.add(spacerLabel2);
            this.pointCoeffBox.add(bPointCoeffField);
            pointCoeffPanel.add(spacerPanel1);
            pointCoeffPanel.add(pointCoeffBox);
            pointCoeffPanel.add(spacerPanel2);


            this.iterCoeffBox = new JPanel();
            this.iterCoeffBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Set channel max iteration factors:"));
            this.iterCoeffBox.setLayout(new GridLayout(3, 0));

            JPanel rSliderBox = new JPanel(new GridLayout(0, 2));
            this.rIterLabel = new JLabel("R: 1.000");
            this.rIterLabel.setHorizontalAlignment(0);
            this.rIterSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 500);
            this.rIterSlider.addChangeListener((e -> {
                this.rIterLabel.setText(String.format("R: %4.3f", (double)(rIterSlider.getValue()/1000.0)));
            }));
            rSliderBox.add(this.rIterLabel);
            rSliderBox.add(this.rIterSlider);

            JPanel gSliderBox = new JPanel(new GridLayout(0, 2));
            this.gIterLabel = new JLabel("G: 1.000");
            this.gIterLabel.setHorizontalAlignment(0);
            this.gIterSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 500);
            this.gIterSlider.addChangeListener((e -> {
                this.gIterLabel.setText(String.format("G: %4.3f", (double)(gIterSlider.getValue())/1000.0));
            }));
            gSliderBox.add(this.gIterLabel);
            gSliderBox.add(this.gIterSlider);

            JPanel bSliderBox = new JPanel(new GridLayout(0, 2));
            this.bIterLabel = new JLabel("B: 1.000");
            this.bIterLabel.setHorizontalAlignment(0);
            this.bIterSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 500);
            this.bIterSlider.addChangeListener((e -> {
                this.bIterLabel.setText(String.format("B: %4.3f", (double)(bIterSlider.getValue())/1000.0));
            }));
            bSliderBox.add(this.bIterLabel);
            bSliderBox.add(this.bIterSlider);

            this.iterCoeffBox.add(rSliderBox);
            this.iterCoeffBox.add(gSliderBox);
            this.iterCoeffBox.add(bSliderBox);



            JButton applyButton = new JButton("Apply");
            applyButton.addActionListener(l -> {
                ((BhuddabrotFractal) fractal).setNumRandomPoints(pointSlider.getValue());
                ((BhuddabrotFractal) fractal).setPointCoeff(0, Double.parseDouble(this.rPointCoeffField.getText().trim()));
                ((BhuddabrotFractal) fractal).setPointCoeff(1, Double.parseDouble(this.gPointCoeffField.getText().trim()));
                ((BhuddabrotFractal) fractal).setPointCoeff(2, Double.parseDouble(this.bPointCoeffField.getText().trim()));
                ((BhuddabrotFractal) fractal).setIterCoeff(0, ((double)this.rIterSlider.getValue())/1000.0);
                ((BhuddabrotFractal) fractal).setIterCoeff(1, ((double)this.gIterSlider.getValue())/1000.0);
                ((BhuddabrotFractal) fractal).setIterCoeff(2, ((double)this.bIterSlider.getValue())/1000.0);
                if (this.nebulaModeSelected != ((BhuddabrotFractal)fractal).getNebulaEnabled()) {
                    ((BhuddabrotFractal) fractal).toggleNebulaEnabled();
                }
                fractalViewer.updateFractal();
                fractalViewer.refresh();
                this.update();
            });

            panel.add(this.pointBox);
            panel.add(this.nebulaModeToggleButton);
            panel.add(pointCoeffPanel);
            panel.add(this.iterCoeffBox);
            panel.add(applyButton);
            add(panel);

        }

        private void setup() {

        }

        private void toggleVisible() {
            this.visible = !this.visible;
        }

        private void update() {
            this.setLocation(appFrame.getX()+appFrame.getWidth()-this.getWidth()-7, appFrame.getY()+66);
            this.setVisible(this.visible);

            this.nebulaModeSelected = ((BhuddabrotFractal)fractal).getNebulaEnabled();
            this.pointSlider.setValue(((BhuddabrotFractal)fractal).getNumRandomPoints());
            this.rPointCoeffField.setText(String.valueOf(((BhuddabrotFractal)fractal).getPointCoeff(0)));
            this.gPointCoeffField.setText(String.valueOf(((BhuddabrotFractal)fractal).getPointCoeff(1)));
            this.bPointCoeffField.setText(String.valueOf(((BhuddabrotFractal)fractal).getPointCoeff(2)));
            this.rIterSlider.setValue((int)(((BhuddabrotFractal)fractal).getIterCoeff(0) * 1000));
            this.gIterSlider.setValue((int)(((BhuddabrotFractal)fractal).getIterCoeff(1) * 1000));
            this.bIterSlider.setValue((int)(((BhuddabrotFractal)fractal).getIterCoeff(2) * 1000));

            this.setNebulaInputEnabled();
        }

        private void setNebulaInputEnabled() {
            this.rPointCoeffField.setEnabled(this.nebulaModeSelected);
            this.gPointCoeffField.setEnabled(this.nebulaModeSelected);
            this.bPointCoeffField.setEnabled(this.nebulaModeSelected);
            this.rIterSlider.setEnabled(this.nebulaModeSelected);
            this.gIterSlider.setEnabled(this.nebulaModeSelected);
            this.bIterSlider.setEnabled(this.nebulaModeSelected);
        }

    }

    private class NewtonControlWindow {

        private JFrame newtonControlFrame;
        private JPanel newtonControlPanel;
        private int currentDegreeValue;
        private JTextField[][] coeffInputs;

        private NewtonControlWindow() {
            currentDegreeValue = ((NewtonIterator)fractal.iterator).getF().degree();

            newtonControlFrame = new JFrame("Change polynomial");
            newtonControlFrame.setSize(400, 400);
            newtonControlFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            newtonControlFrame.setResizable(false);
            newtonControlFrame.setVisible(true);

            newtonControlPanel = new JPanel();
            newtonControlPanel.add(this.getCoeffInputPane());

            newtonControlFrame.add(newtonControlPanel);
        }

        private JPanel getCoeffInputPane() {

            JPanel coeffInputPane = new JPanel();
            coeffInputPane.setLayout(new GridLayout(0, 1));
            coeffInputs = new JTextField[9][2];

            JLabel degreeLabel = new JLabel("Current polynomial: " + ((NewtonIterator)fractal.iterator).getF().toString());
            coeffInputPane.add(degreeLabel);

            for (int i = 0; i <= 8; i++) {
                if (i <= currentDegreeValue) {
                    coeffInputs[i][0] = new JTextField(String.valueOf(((NewtonIterator)fractal.iterator).getF().coeff[i].getReal()), 4);
                    coeffInputs[i][1] = new JTextField(String.valueOf(((NewtonIterator)fractal.iterator).getF().coeff[i].getImag()), 4);
                } else {
                    coeffInputs[i][0] = new JTextField("0", 4);
                    coeffInputs[i][1] = new JTextField("0", 4);
                }
                JPanel panel = new JPanel();
                JLabel label = new JLabel("z^" + i + " coefficient : ");
                JLabel realLabel = new JLabel(" Re: ");
                JLabel imagLabel = new JLabel(" Im: ");
                panel.add(label);
                panel.add(realLabel);
                panel.add(coeffInputs[i][0]);
                panel.add(imagLabel);
                panel.add(coeffInputs[i][1]);
                coeffInputPane.add(panel);
            }

            JButton setCoeffsButton = new JButton("Set");
            setCoeffsButton.addActionListener(e -> {
                Complex[] newCoeffs = new Complex[9];

                for (int i = 0; i <= 8; i++) {
                    double x = Double.parseDouble(coeffInputs[i][0].getText().trim());
                    double y = Double.parseDouble(coeffInputs[i][1].getText().trim());
                    newCoeffs[i] = new Complex(x, y);
                }

                try {
                    ((NewtonIterator)(fractal.iterator)).setOrder((new Polynomial(newCoeffs)).degree());
                    ((NewtonIterator)(fractal.iterator)).setF(new Polynomial(newCoeffs));
                } catch (NegativeArraySizeException ex) {
                    ((NewtonIterator)(fractal.iterator)).setF(new Polynomial());
                }

                fractal.setupFractal();
                fractalViewer.updateFractal();
                newtonControlFrame.dispose();
            });

            coeffInputPane.add(setCoeffsButton);

            return coeffInputPane;
        }

    }

}