// (c) 2017 uchicom
package com.uchicom.subspace.window;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class ImageFrame extends JFrame {

	private BufferedImage image;
	public ImageFrame(String title, BufferedImage image) {
		super(title);
		this.image = image;
		this.setPreferredSize(new Dimension(640, 480));
	}
	@Override
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, this);
	}
}
