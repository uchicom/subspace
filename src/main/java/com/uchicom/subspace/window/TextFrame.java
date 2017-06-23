// (c) 2017 uchicom
package com.uchicom.subspace.window;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class TextFrame extends JFrame {

	public TextFrame(String title, String text) {
		super(title);
		initComponents(text);
	}

	/**
	 * @param text
	 */
	private void initComponents(String text) {

		getContentPane().add(new JScrollPane(new JTextArea(text)));
		pack();
	}
}
