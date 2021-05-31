package org.processmining.filterbook.cells;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.filterbook.notebook.Notebook;
import org.processmining.framework.util.ui.widgets.ProMTextArea;
import org.processmining.framework.util.ui.widgets.ProMTextField;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class TextCell extends Cell implements KeyListener, ActionListener {

	/*
	 * The current widget for this text cell. 
	 */
	private JComponent widget;
	/*
	 * Handle to the text area in the current widget.
	 */
	private ProMTextArea textWidget;
	/*
	 * Text displayed in the text area.
	 */
	private String text;

	/*
	 * Button displaying the cell's name.
	 */
	private JButton labelButton;
	/*
	 * Field allowing user to edit the cell's name.
	 */
	private ProMTextField labelField;

	/**
	 * Construct a text cell with given context, notebook, and name.
	 * @param context The given context
	 * @param notebook The given notebook
	 * @param name The given name
	 */
	public TextCell(UIPluginContext context, Notebook notebook, String name) {
		super(context, notebook, name);
		// No widget yet.
		widget = null;
		textWidget = null;
		// Initially, the text is empty.
		text = "";
	}

	public void updated() {
		// Nothing to do.
	}

	public JComponent getWidget(boolean doReset) {
		if (doReset) {
			// Reset the current widget.
			widget = null;
		}
		if (widget != null) {
			// We have a current widget, return that one.
			return widget;
		}
		// Create a new widget.
		widget = new JPanel();
		double size[][] = { { 230, TableLayoutConstants.FILL, 230 }, { 30, TableLayoutConstants.FILL } };
		widget.setLayout(new TableLayout(size));

		// Create a button to display the name of the text cell.
		labelButton = new JButton("<html><h1>" + getName() + "</h1></html>");
		labelButton.setBorderPainted( false );
		labelButton.setBackground( new Color(240, 240, 240) );
		labelButton.setHorizontalAlignment(JLabel.CENTER);
		labelButton.addActionListener(this);
		// Create a text field to allow the user to edit the name of the text cell.
		labelField = new ProMTextField(getName());
		labelField.getTextField().addActionListener(this);
		labelField.getTextField().setHorizontalAlignment(JLabel.CENTER);
		// Add the button displaying the name. Selecting the button will display the text field.
		widget.add(labelButton, "1, 0");

		// Set the text.
		setText(text);
		
		widget.setPreferredSize(new Dimension(600, 300));
		return widget;
	}

	/**
	 * Gets the text in the text area.
	 * @return The text in the text area.
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Sets the text in the text area.
	 * @param text The text to set in the text area.
	 */
	public void setText(String text) {
		this.text = text;
		if (textWidget == null) {
			// No text widget yet, construct one.
			textWidget = new ProMTextArea();
			textWidget.setText(text);
//			textWidget.getTextArea().setBackground(Color.WHITE);
//			textWidget.getTextArea().setForeground(Color.BLACK);
			// Increase the font size a bit, as the default font is really small.
			textWidget.getTextArea().setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 16));
			textWidget.getTextArea().addKeyListener(this);
		} else {
			textWidget.setText(text);
		}
		widget.add(textWidget, "0, 1, 2, 1");
		widget.revalidate();
		widget.repaint();
	}

	public void keyPressed(KeyEvent arg0) {
//		text = textWidget.getText();
	}

	public void keyReleased(KeyEvent arg0) {
		// Update the text.
		text = textWidget.getText();
	}

	public void keyTyped(KeyEvent arg0) {
//		text = textWidget.getText();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == labelButton) {
			// User selected the button displaying the name of the text cell. Display text filed instead.
			widget.remove(labelButton);
			widget.add(labelField, "1, 0");
			widget.revalidate();
			widget.repaint();
		} else if (e.getSource() == labelField.getTextField()) {
			// User has finished editing the name. Restore the button.
			labelButton.setText("<html><h1>" + labelField.getText() + "</h1></html>");
			widget.remove(labelField);
			widget.add(labelButton, "1, 0");
			widget.revalidate();
			widget.repaint();
		}		
	}
	
	public void update() {
		// Ignore
	}

	/**
	 * Get a template for this text cell.
	 */
	public CellTemplate getTemplate() {
		TextCellTemplate textCellTemplate = new TextCellTemplate();
		// Template contains the name and the text of the text cell.
		textCellTemplate.setName(getName());
		textCellTemplate.setText(getText());
		return textCellTemplate;
	}
}
