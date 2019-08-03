package com.github.dtanjp.cachepacker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
/**
 * DisplayPanel.java
 * Description: Displays the image/text contents from a file
 * 
 * @author David Tan
 **/
public class DisplayPanel extends JPanel {

	/** Serial version UID **/
	private static final long serialVersionUID = 824981394635483028L;

	/** Constructor **/
	protected DisplayPanel() {
		setBounds(30, 30, 500, 500);
		setLayout(null);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		text = new JTextArea();
		text.setBounds(0, 0, 500, 500);
		text.setLineWrap(true);
		text.setAutoscrolls(true);
		text.setEditable(false);
		text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		text.setVisible(false);
		add(text);
		
		scroll = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setAutoscrolls(true);
		scroll.setWheelScrollingEnabled(true);
		scroll.setBounds(0, 0, 500, 500);
		scroll.setVisible(false);
		add(scroll);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		super.setBackground(Color.LIGHT_GRAY);
		if(image != null) {
			try {
				if(!keepRatio) {//Fill + stretch screen
					Image img = ImageIO.read(new ByteArrayInputStream(image));
					int width = img.getWidth(null);
					int height = img.getHeight(null);
					if(width > 500) {
						img = (img.getScaledInstance(500, height, Image.SCALE_SMOOTH));
						width = 500;
					}
					if(height > 500) {
						img = (img.getScaledInstance(width, 500, Image.SCALE_SMOOTH));
						height = 500;
					}
					int x = width < 500 ? 250-(width/2) : 0;
					int y = height < 500 ? 250-(height/2) : 0;
					g.drawImage(img, x, y, null);
				} else {//Preserve ratio aspect
					if(ratioImage == null) return;
					int width = ratioImage.getWidth(null);
					int height = ratioImage.getHeight(null);
					int x = width < 500 ? 250-(width/2) : 0;
					int y = height < 500 ? 250-(height/2) : 0;
					g.drawImage(ratioImage, x, y, null);
				}
			} catch (IOException e) {
			}
		}
	}
	
	public void setImage(byte[] data) {
		if(data == null) ratioImage = null;
		image = data;
		if(data == null) return;
		try {
			Image img = ImageIO.read(new ByteArrayInputStream(image));
			int width = img.getWidth(null);
			int height = img.getHeight(null);
			if(width > 500 || height > 500) {
				int original_width = width;
			    int original_height = height;
			    int new_width = width;
			    int new_height = height;

			    // first check if we need to scale width
			    if (original_width > 500) {
			        new_width = 500;
			        new_height = (new_width * original_height) / original_width;
			    }

			    if (new_height > 500) {
			        new_height = 500;
			        new_width = (new_height * original_width) / original_height;
			    }
			    width = new_width;
			    height = new_height;
			}
			ratioImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		} catch (IOException e) {}
	}
	
	/** Variables **/
	private byte[] image = null;
	private Image ratioImage = null;
	public JTextArea text = null;
	public JScrollPane scroll = null;
	public boolean keepRatio = true;
}
