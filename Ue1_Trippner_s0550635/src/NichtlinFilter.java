// BV Ue1 WS2015/16 Vorgabe
//
// Copyright (C) 2015 by Klaus Jung

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Random;

public class NichtlinFilter extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static final String author = "<Your Name>";		// TODO: type in your name here
	private static final String initialFilename = "lena_klein.png";
	private static final File openPath = new File(".");
	private static final int borderWidth = 5;
	private static final int maxWidth = 446;
	private static final int maxHeight = maxWidth;
	private static final int maxNoise = 30;	// in per cent
	
	private static JFrame frame;
	
	private ImageView srcView;			// source image view
	private ImageView dstView;			// filtered image view

	private int[] origPixels = null;
	
	private JLabel statusLine = new JLabel("     "); // to print some status text
	
	private JComboBox<String> noiseType;
	private JLabel noiseLabel;
	private JSlider noiseSlider;
	private JLabel noiseAmountLabel;
	private boolean addNoise = false;
	private double noiseFraction = 0.01;	// fraction for number of pixels to be modified by noise
	
	private JComboBox<String> filterType;
	

	public NichtlinFilter() {
        super(new BorderLayout(borderWidth, borderWidth));

        setBorder(BorderFactory.createEmptyBorder(borderWidth,borderWidth,borderWidth,borderWidth));
 
        // load the default image
        File input = new File(initialFilename);
        
        if(!input.canRead()) input = openFile(); // file not found, choose another image
        
        srcView = new ImageView(input);
        srcView.setMaxSize(new Dimension(maxWidth, maxHeight));
        
		// convert to grayscale
		makeGray(srcView);
		        
        // keep a copy of the grayscaled original image pixels
        origPixels = srcView.getPixels().clone();
       
		// create empty destination image of same size
		dstView = new ImageView(srcView.getImgWidth(), srcView.getImgHeight());
		dstView.setMaxSize(new Dimension(maxWidth, maxHeight));
		
        // control panel
        JPanel controls = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0,borderWidth,0,0);

		// load image button
        JButton load = new JButton("Open Image");
        load.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		loadFile(openFile());
        		// convert to grayscale
        		makeGray(srcView);  
                // keep a copy of the grayscaled original image pixels
                origPixels = srcView.getPixels().clone();
        		calculate(true);
        	}        	
        });
         
        // selector for the noise method
        String[] noiseNames = {"No Noise ", "Salt & Pepper "};
        
        noiseType = new JComboBox<String>(noiseNames);
        noiseType.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		addNoise = noiseType.getSelectedIndex() > 0;
        		noiseLabel.setEnabled(addNoise);
                noiseSlider.setEnabled(addNoise);
                noiseAmountLabel.setEnabled(addNoise);
        		calculate(true);
        	}
        });
        
        // amount of noise
        noiseLabel = new JLabel("Noise:");
        noiseAmountLabel = new JLabel("" + Math.round(noiseFraction * 100.0)  + " %");
        noiseSlider = new JSlider(JSlider.HORIZONTAL, 0, maxNoise, (int) Math.round(noiseFraction * 100.0));
        noiseSlider.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
        		noiseFraction = noiseSlider.getValue() / 100.0;
        		noiseAmountLabel.setText("" + Math.round(noiseFraction * 100.0) + " %");
        		calculate(true);
        	}
        });
        noiseLabel.setEnabled(addNoise);
        noiseSlider.setEnabled(addNoise);
        noiseAmountLabel.setEnabled(addNoise);

        // selector for filter
        String[] filterNames = {"No Filter", "Min Filter", "Max Filter", "Box Filter", "Median Filter"};
        filterType = new JComboBox<String>(filterNames);
        filterType.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		calculate(false);
        	}
        });
        
        controls.add(load, c);
        controls.add(noiseType, c);
        controls.add(noiseLabel, c);
        controls.add(noiseSlider, c);
        controls.add(noiseAmountLabel, c);
        controls.add(filterType, c);
        
        // images panel
        JPanel images = new JPanel(new GridLayout(1,2));
        images.add(srcView);
        images.add(dstView);
        
        // status panel
        JPanel status = new JPanel(new GridBagLayout());
        
        status.add(statusLine, c);
        
        add(controls, BorderLayout.NORTH);
        add(images, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
        
        calculate(true);
                       
	}
	
	private File openFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images (*.jpg, *.png, *.gif)", "jpg", "png", "gif");
        chooser.setFileFilter(filter);
        chooser.setCurrentDirectory(openPath);
        int ret = chooser.showOpenDialog(this);
        if(ret == JFileChooser.APPROVE_OPTION) return chooser.getSelectedFile();
        return null;		
	}
	
	private void loadFile(File file) {
		if(file != null) {
    		srcView.loadImage(file);
    		srcView.setMaxSize(new Dimension(maxWidth, maxHeight));
    		// create empty destination image of same size
    		dstView.resetToSize(srcView.getImgWidth(), srcView.getImgHeight());
    		frame.pack();
		}
		
	}
	
    
	private static void createAndShowGUI() {
		// create and setup the window
		frame = new JFrame("Nonlinear Filters - " + author);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JComponent newContentPane = new NichtlinFilter();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        // display the window.
        frame.pack();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        frame.setLocation((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2);
        frame.setVisible(true);
	}

	public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
	
	private void calculate(boolean createNoise) {
		long startTime = System.currentTimeMillis();
		
		if(createNoise) {
			// start with original image pixels
			srcView.setPixels(origPixels);
			// add noise
			if(addNoise) 
				makeNoise(srcView);
			// make changes visible
			srcView.applyChanges();
		}
		
		// apply filter
		filter();

		// make changes visible
		dstView.applyChanges();
		
		long time = System.currentTimeMillis() - startTime;
    	statusLine.setText("Processing Time = " + time + " ms");
	}

	
	private void makeGray(ImageView imgView) {
		int pixels[] = imgView.getPixels();
		
		// TODO: convert pixels to grayscale
		
		// loop over all pixels
		for(int i = 0; i < pixels.length; i++) {
			int r = (pixels[i]>>16)&0xff;
			int g = (pixels[i]>>8)&0xff;
			int b =  pixels[i] & 0xff;
			
			int grey = (r+g+g+b)/4;
			pixels[i] = (0xFF<<24) | (grey<<16) | (grey<< 8) | grey;
			
		}
	}
	
	private void makeNoise(ImageView imgView) {
		int pixels[] = imgView.getPixels();
		Random rn = new Random();
		boolean isBlack = false;
		
		for(int i = 0; i < pixels.length; i++) {	
			if(rn.nextInt(100) < (noiseFraction*100)){
				if(!isBlack){
					pixels[i]=0xffffff;	
				}
				else{
					pixels[i]=0x000000;	
					
				}
				isBlack = !isBlack;
			} 		
		// TODO: add noise to pixels
		}
	}
	
	
	private void filter() {
		int src[] = srcView.getPixels();
		int dst[] = dstView.getPixels();
		int width = srcView.getImgWidth();
		int height = srcView.getImgHeight();
		int filterIndex = filterType.getSelectedIndex();
		
		int[] kernel = new int[9];
		int grey = 0;
		
		int k1=0;
		int k2=0;
		int k3=0;
		int k4=0;
		int k5=0;
		int k6=0;
		int k7=0;
		int k8=0;
		int k9=0;
		
		for(int i =0; i< src.length-1; i++){
			boolean eckpunkt =false;
			boolean topline =false;
			boolean leftline =false;
			boolean rightline = false;
			boolean downline =false;
			boolean normal = false;
			if(i == 0 || i == width-1 || i== src.length-width || i == src.length-1) {eckpunkt = true;}
			else if(i < width)														{topline =true;}
			else if(i % width ==0)													{leftline =true;}
			else if(i % width ==349)												{rightline =true;}
			else if(i > src.length-width)											{downline =true;}
			else{ normal = true;}
			
			//Randbehandlung
			if	   (topline)	{k1=i-1;k2=i;k3=i+1;k4=i-1;k5=i;k6=i+1;k7=i+(width-1);k8=i+width;k9=i+(width+1);}
			else if(leftline)	{k1=i-width;k2=i-width;k3=i-(width-1);k4=i;k5=i;k6=i+1;k7=i+width;k8=i+width;k9=i+(width+1);}
			else if(rightline)	{k1=i-(width+1);k2=i-width;k3=i-width;k4=i-1;k5=i;k6=i;k7=i+(width-1);k8=i+width;k9=i+width;}
			else if(downline)	{k1=i-(width+1);k2=i-width;k3=i-(width-1);k4=i-1;k5=i;k6=i+1;k7=i-1;k8=i;k9=i+1;}
			else if(eckpunkt){
				if(i ==0)		{k1=i;k2=i;k3=i+1;k4=i;k5=i;k6=i+1;k7=i+width;k8=i+width;k9=i+(width+1);}
				else if(i == width-1){k1=i-1;k2=i;k3=i;k4=i-1;k5=i;k6=i;k7=i+(width-1);k8=i+width;k9=i+width;}
				else if(i == src.length-1){k1=i-(width+1); k2 =i-width;k3=i-width;k4=i-1;k5=i;k6=i;k7=i-1;k8=i;k9=i;}
				else 			{k1=i-width;k2=i-width;k3=i-(width-1);k4=i;k5=i;k6=i+1;k7=i;k8=i;k9=i+1;}
			}
			//Normal Fall (keine Randbehandlung nötig)
			else if(normal)		{k1=i-(width+1);k2=i-width;k3=i-(width-1);k4=i-1;k5=i;k6=i+1;k7=i+(width-1);k8=i+width;k9=i+width+1;}
			

			kernel[0]= (src[k1]>>16)& 0xff;
			kernel[1]= (src[k2]>>16)& 0xff;
			kernel[2]= (src[k3]>>16)& 0xff;
			kernel[3]= (src[k4]>>16)& 0xff;
			kernel[4]= (src[k5]>>16)& 0xff;
			kernel[5]= (src[k6]>>16)& 0xff;
			kernel[6]= (src[k7]>>16)& 0xff;
			kernel[7]= (src[k8]>>16)& 0xff;
			kernel[8]= (src[k9]>>16)& 0xff;

			
			Arrays.sort(kernel);
			switch(filterIndex){
			case 0:
				dst[i]=src[i];
				break;
			case 1: ;grey = kernel[0];
				dst[i] = (0xFF<<24) | (grey<<16) | (grey<< 8) | grey;
				break;
			case 2:	grey = kernel[8];
				dst[i] = (0xFF<<24) | (grey<<16) | (grey<< 8) | grey;
				break;
			case 3: 
				for(int k=0;k<kernel.length;k++){grey=grey+kernel[k];}
				grey= grey/9;
				dst[i] = (0xFF<<24) | (grey<<16) | (grey<< 8) | grey;
				break;
			case 4: grey = kernel[5];
				dst[i] = (0xFF<<24) | (grey<<16) | (grey<< 8) | grey;
				break;

			}
			
			
		}
		
		// TODO: implement filters 
		
	}


}

