// BV Ue04 WS2015/16 Vorgabe Hilfsklasse StatsView
//
// Copyright (C) 2014 by Klaus Jung

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;


public class StatsView extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final String[] names = { "Minimal Wert:", "Maximal Wert:", "Durchschnitts Wert:", "Varianz Wert:", "Median :", "Entropie:" }; // TODO: enter proper names
	private static final int rows = names.length;
	private static final int border = 2;
	private static final int columns = 2;
	private static final int graySteps = 256;
	
	private JLabel[] infoLabel = new JLabel[rows];
	private JLabel[] valueLabel = new JLabel[rows];
	
	private int[] histogram = null;
	public double avg;
	public int median;
	public  int min;
	public int max;
	
	
	
	public StatsView() {
		super(new GridLayout(rows, columns, border, border));
		TitledBorder titBorder = BorderFactory.createTitledBorder("Statistics");
		titBorder.setTitleColor(Color.GRAY);
		setBorder(titBorder);
		for(int i = 0; i < rows; i++) {
			infoLabel[i] = new JLabel(names[i]);
			valueLabel[i] = new JLabel("-----");
			add(infoLabel[i]);
			add(valueLabel[i]);
		}
	}
	
	private void setValue(int column, int value) {
		valueLabel[column].setText("" + value);
	}
	
	public double getAVG(){
		return avg;
	}
	
	public static double lb( double x )
	{
	  return Math.log( x ) / Math.log( 2.0 );
	}
	
	private void setValue(int column, double value) {
		valueLabel[column].setText(String.format(Locale.US, "%.2f", value));
	}
	
	public boolean setHistogram(int[] histogram) {
		if(histogram == null || histogram.length != graySteps) {
			return false;
		}
		this.histogram = histogram;
		update();
		return true;
	}
	
	public boolean update() {
		if(histogram == null) {
			return false;
		}
		avg = 0;
		min = 999999;
		max = 0;
		boolean checkedminJet = false;
		double var = 0;
		double sumavg	= 0;
		int allePixel = 0;
		double sumvar= 0;
		median =0;
		
			for (int i = 0; i < histogram.length; i++) {
				if (histogram[i]!=0&& !checkedminJet){min = i; checkedminJet=true;}// Min
				if (histogram[i]!=0){max = i;}// max
				sumavg += histogram[i]*i; // avg
				allePixel += histogram[i];// N
			}
			
		avg = sumavg/allePixel; //avg
		double psum = 0;		// Entropie
		
		int[] histoclone = histogram.clone();// Median
		Arrays.sort(histoclone);
		int mediansum = 0; 
		boolean medianIsSet =false;
		
		for(int i = 0; i < histogram.length; i++){
			
			if(mediansum < allePixel/2)
			mediansum += histogram[i];// Median
			else if(!medianIsSet){
				median = i;
				medianIsSet = true;
			}
			
			double p = histogram[i]/(double)allePixel; // Entropie
			if(p != 0)
				psum -= p* lb(p);
		
			sumvar += histogram[i]*Math.pow((i-avg),2.0);	// Varianz
		}
		
		var = sumvar/allePixel;
		setValue(0,min);
		setValue(1,max);
		setValue(2,avg);//Durchschnitt
		setValue(3,var);//Varianz
		setValue(4,median);//(unter)Median ???
		setValue(5,psum);//Entropie ???


		// TODO: calculate and display statistic values
		

		return true;
	}
	

}
