// BV Ue04 WS2015/16 Vorgabe
//
// Copyright (C) 2015 by Klaus Jung

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;

public class ImagePrädiktoren extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static final String author = "Keoma Trippner";		// TODO: type in your name here*
	private static final String initialFilename = "BBT.jpg";
	private static final File openPath = new File(".");
	private static final int border = 10;
	private static final int maxWidth = 910; 
	private static final int maxHeight = 910; 
	private static final int graySteps = 256;
	private StatsView statsView = new StatsView();	// statistics values view
	
	private static JFrame frame;
	
	private ImageView imgView;						// image view
	private ImageView fehlerView;
	private ImageView ergebnisView;
	private JSlider brightnessSlider;				// brightness Slider
	private int methodChoose;
	
	// TODO: add an array to hold the histogram of the loaded image
	
	// TODO: add an array that holds the ARGB-Pixels of the originally loaded image*
	private int[] ARGBPixels;
	private int[] FehlerPixels;
	private int[] ErgebnisPixels;
	private double[] HistoARGB;
	private double[] HistoFehler;
	private double[] HistoErgebnis;
	private double EntroARGB;
	private double EntroFehler;
	private double EntroErgebnis;
	private double MSE;
	
	// TODO: add a contrast slider*
	private JSlider quantSlider;
	private JComboBox<String> method;
	
	
	private JLabel statusLine;				// to print some status text
	
	/**
	 * Constructor. Constructs the layout of the GUI components and loads the initial image.
	 */
	public ImagePrädiktoren() {
        super(new BorderLayout(border, border));
        
        // load the default image
        File input = new File(initialFilename);
        methodChoose = 0;
        HistoARGB = new double[256];
        HistoFehler = new double[256];
        HistoErgebnis = new double[256];
       
        
        if(!input.canRead()) input = openFile(); // file not found, choose another image
        
        imgView = new ImageView(input);
        imgView.setMaxSize(new Dimension(maxWidth, maxHeight));
        
        fehlerView = new ImageView(input);
        fehlerView.setMaxSize(new Dimension(maxWidth, maxHeight));
        
        ergebnisView = new ImageView(input);
        ergebnisView.setMaxSize(new Dimension(maxWidth, maxHeight));
        
        
        // TODO: initialize the original ARGB-Pixel array from the loaded image*
        ErgebnisPixels = ergebnisView.getPixels();
        FehlerPixels= fehlerView.getPixels();
        ARGBPixels = imgView.getPixels();
       
		// load image button
        JButton load = new JButton("Open Image");
        load.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		File input = openFile();
        		if(input != null) {
        			imgView.loadImage(input);
        			imgView.setMaxSize(new Dimension(maxWidth, maxHeight));
        			
        			
        			ErgebnisPixels = imgView.getPixels();
        	        ARGBPixels = ErgebnisPixels.clone();
        	       
        	        // TODO: initialize the original ARGB-Pixel array from the newly loaded image*
        			
        			frame.pack();
	                processImage();
        		}
        	}        	
        });
        
 String[] methodNames = {"A", "B", "C", "A+B-C", "A+B/2", "adaptiv"};
        
        method = new JComboBox<String>(methodNames);
        method.setSelectedIndex(0);		// set initial method
        method.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		methodChoose= method.getSelectedIndex();
        		processImage();
        	}});
        

        
        // some status text
        statusLine = new JLabel(" ");
        
        // top view controls
        JPanel topControls = new JPanel(new GridBagLayout());
        topControls.add(load);
        topControls.add(method);
        
        // center view
        JPanel centerControls = new JPanel();
        JPanel rightControls = new JPanel();
        rightControls.setLayout(new BoxLayout(rightControls, BoxLayout.Y_AXIS));
        centerControls.add(imgView);
        centerControls.add(fehlerView);
        centerControls.add(ergebnisView);
        centerControls.add(rightControls);
        
        // bottom view controls
        JPanel botControls = new JPanel();
        botControls.setLayout(new BoxLayout(botControls, BoxLayout.Y_AXIS));
        


        quantSlider =new JSlider(1,128,1);
        TitledBorder quantBorder = BorderFactory.createTitledBorder("Quantisierung");
        quantBorder.setTitleColor(Color.GREEN);
        quantSlider.setBorder(quantBorder);
        quantSlider.addChangeListener(new ChangeListener(){
        	public void stateChanged(ChangeEvent e){
        		processImage();
        	}
        });
        // TODO: setup contrast slider*
        
        botControls.add(quantSlider);
        statusLine.setAlignmentX(Component.CENTER_ALIGNMENT);
        botControls.add(statusLine);

        // add to main panel
        add(topControls, BorderLayout.NORTH);
        add(centerControls, BorderLayout.CENTER);
        add(botControls, BorderLayout.SOUTH);
        botControls.add(statsView);

               
        // add border to main panel
        setBorder(BorderFactory.createEmptyBorder(border,border,border,border));
        
        // perform the initial rotation
        processImage();
	}
	

	/**
	 * Set up and show the main frame.
	 */
	private static void createAndShowGUI() {
		// create and setup the window
		frame = new JFrame("Image Analysis - " + author);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JComponent contentPane = new ImagePrädiktoren();
        contentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(contentPane);

        // display the window
        frame.pack();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        frame.setLocation((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2);
        frame.setVisible(true);
	}

	/**
	 * Main method. 
	 * @param args - ignored. No arguments are used by this application.
	 */
	public static void main(String[] args) {
        // schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
	
	
	/**
	 * Open file dialog used to select a new image.
	 * @return The selected file object or null on cancel.
	 */
	private File openFile() {
		// file open dialog
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images (*.jpg, *.png, *.gif)", "jpg", "png", "gif");
        chooser.setFileFilter(filter);
        chooser.setCurrentDirectory(openPath);
        int ret = chooser.showOpenDialog(this);
        if(ret == JFileChooser.APPROVE_OPTION) return chooser.getSelectedFile();
        return null;		
	}
	
    
	
    /**
     * Update image with new brightness and contrast values.
     * Update histogram, histogram view and statistics view.
     */
    protected void processImage() {
    	
		long startTime = System.currentTimeMillis();
		int height = imgView.getImgHeight();
		int width = imgView.getImgWidth();
		
		// TODO: add your processing code here
		int fehler;
		int prediction;
		EntroARGB =0;
	    EntroFehler =0;
	    EntroErgebnis =0;
	    MSE =0;
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				//Ausgangsbild berechnen
    			int pos = y*width+x ;    			
    			double grau = greyFromArray(pos,ARGBPixels);
    			HistoARGB[(int) grau]++;
    			ARGBPixels[pos]= 0xFF000000 | ((int)grau << 16) | ((int)grau << 8) | (int)grau;
    			
    			//FehlerView berechnen
    			fehler =  calkPrediction(pos,ARGBPixels)- (int)grau;
    			if(fehler+128>255){HistoFehler[255]++;}
    			else if(fehler+128<0){HistoFehler[0]++;}
    			else HistoFehler[(fehler+128)]++;

    			FehlerPixels[pos]= 0xFF000000 | ((fehler+128) << 16) | ((fehler+128) << 8) | fehler+128;
    			
    			prediction = decode(pos,fehler);
    			HistoErgebnis[prediction]++;
    			ErgebnisPixels[pos]= 0xFF000000 | (prediction << 16) | (prediction << 8) | prediction;
    			MSE += Math.pow( grau-prediction,2 );  
    		}
    	}
		for(int i= 0;i<255;i++){
			HistoARGB[i] = HistoARGB[i]/ARGBPixels.length;
			HistoFehler[i] = HistoFehler[i]/ARGBPixels.length;
			HistoErgebnis[i] = HistoErgebnis[i]/ARGBPixels.length;
			
			EntroARGB-= HistoARGB[i]* lb(HistoARGB[i]);
			EntroFehler-= HistoFehler[i]* lb(HistoFehler[i]);
			EntroErgebnis-= HistoErgebnis[i]* lb(HistoErgebnis[i]);
			MSE = MSE/ARGBPixels.length;
			statsView.update(EntroARGB, EntroFehler, EntroErgebnis, MSE);
		}
		
		//if(auto)auto =false;
		imgView.applyChanges();
		fehlerView.applyChanges();
		ergebnisView.applyChanges();
		
		// show processing time
		long time = System.currentTimeMillis() - startTime;
		statusLine.setText("Processing time = " + time + " ms.");
    }
    
    private int decode(int pos, int fehler){
    	int rightColor;
    	
			 int prediction = calkPrediction(pos,ErgebnisPixels); 
			 rightColor = fehler + prediction;
		
		
    	return rightColor;
    }
    
    private int calkPrediction(int pos, int[] array){
		int width = imgView.getImgWidth();
		int a;
		int b;
		int c;
		if(pos-1>-1) a = greyFromArray(pos-1,array);
		else a=128;
		if(pos-(width-1)>-1 )b = greyFromArray(pos-(width-1),array);
		else b=128;
		if(pos>=width && pos%width != 0 )c = greyFromArray(pos-width,array);
		else c=128; 
		
		switch(methodChoose) {
		case 0:
			if(pos-1>-1){return a;}
	    	else{return 128;}
		case 1:
			if(pos-(width-1)>-1 ){return b;}
	    	else{return 128;}
		case 2:
			if(pos>=width && pos%width != 0 ){return c;}
	    	else{return 128;}
		case 3:
			if(pos>=width && pos%width != 0 ){return a+b-c;}
	    	else{return 128;}
		case 4:
			if(pos>=width && pos%width != 0 ){return (a+b)/2;}
	    	else{return 128;}
		case 5:
			if(pos>=width && pos%width != 0 ){
				if(a-c<b-c)return b;
				else return a;
				}
			
	    	else{return 128;}
		default:
			return 128;
		}
		
    	
    }
    
    protected int greyFromArray(int  pos, int[] array){
    	int r = (array[pos] >> 16) & 0xff;
		int g = (array[pos] >>  8) & 0xff;
		int b = array[pos]       & 0xff;
		double grau = (r+g+g+b)/4; 
		return (int) grau;
    }

    private static double lb( double x )
	{
	  return Math.log( x ) / Math.log( 2.0 );
	}

    
 

}

