// BV Ue04 WS2015/16 Vorgabe Hilfsklasse StatsView
//
// Copyright (C) 2014 by Klaus Jung

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;


public class StatsView extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final String[] names = { "Entropie 1. Bild:", "Entropie 2. Bild:", "Entropie 3. Bild:", "MSE:"}; // TODO: enter proper names
	private static final int rows = names.length;
	private static final int border = 2;
	private static final int columns = 2;
	
	private JLabel[] infoLabel = new JLabel[rows];
	private JLabel[] valueLabel = new JLabel[rows];
	
	
	
	
	
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
	

	
	
	private void setValue(int column, double value) {
		valueLabel[column].setText(String.format(Locale.US, "%.2f", value));
	}
	

	
	public boolean update(double e1, double e2, double e3, double error) {
		
		
		setValue(0,e1);
		setValue(1,e2);
		setValue(2,e3);//Durchschnitt
		setValue(3,error);

		// TODO: calculate and display statistic values
		

		return true;
	}
	

}
