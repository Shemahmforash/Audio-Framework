package com.icdif.audio.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * An older version of the Plot class that hadn't listeners to the closing of the window
 */

/**
 * A simple class that allows to plot float[] arrays to a swing window. The
 * first function to plot that is given to this class will set the minimum and
 * maximum height values. I'm not that good with Swing so i might have done a
 * couple of stupid things in here :)
 * 
 * @author wanderer
 * 
 */
public class PlotOld {
	/** the frame **/
	private JFrame frame;

	/** the scroll pane **/
	private JScrollPane scrollPane;

	/** the image gui component **/
	private JPanel panel;

	/** the image **/
	private BufferedImage image;

	/** the last scaling factor to normalize samples **/
	private float scalingFactor = 1;

	/**
	 * Whether the plot was cleared, if true we have to recalculate the scaling
	 * factor
	 **/
	private boolean cleared = true;

	/** current marker position and color **/
	private int markerPosition = 0;
	private Color markerColor = Color.white;

	/**
	 * Instantiates a new Plot with the given title and dimensions.
	 * 
	 * @param title
	 *            The title.
	 * @param width
	 *            The width of the plot in pixels.
	 * @param height
	 *            The height of the plot in pixels.
	 */
	public PlotOld(final String title, final int width, final int height) {
		image = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);

		try {
			// The invokeAndWait() method is just like invokeLater(), except
			// that invokeAndWait() doesn't return until the event-dispatching
			// thread has executed the specified code
			SwingUtilities.invokeAndWait(new Runnable() {
				// it starts the swing, by initializing the frame
				@Override
				public void run() {
					frame = new JFrame(title);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setPreferredSize(new Dimension(width
							+ frame.getInsets().left + frame.getInsets().right,
							frame.getInsets().top + frame.getInsets().bottom
									+ height));
					BufferedImage img = new BufferedImage(width, height,
							BufferedImage.TYPE_4BYTE_ABGR);
					Graphics2D g = (Graphics2D) img.getGraphics();
					g.setColor(Color.black);
					g.fillRect(0, 0, width, height);
					g.dispose();
					image = img;
					panel = new JPanel() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

						@Override
						public void paintComponent(Graphics g) {
							super.paintComponent(g);
							// the image will be update from another method, so
							// it needs to be in sync
							synchronized (image) {
								g.drawImage(image, 0, 0, null);
								g.setColor(markerColor);
								g.drawLine(markerPosition, 0, markerPosition,
										image.getHeight());
							}

							// pauses the thread
							Thread.yield();

							frame.repaint();
						}

						@Override
						public void update(Graphics g) {
							paint(g);
						}

						public Dimension getPreferredSize() {
							return new Dimension(image.getWidth(), image
									.getHeight());
						}
					};
					// panel.setPreferredSize( new Dimension( width, height ) );
					scrollPane = new JScrollPane(panel);
					frame.getContentPane().add(scrollPane);
					frame.pack();
					frame.setVisible(true);

				}
			});
		} catch (Exception ex) {
			// doh...
		}
	}

	/**
	 * It clears the plot, i.e., paints the original rectangle with the
	 * background color and sets the attribute cleared to true
	 */
	public void clear() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				Graphics2D g = image.createGraphics();
				g.setColor(Color.black);
				g.fillRect(0, 0, image.getWidth(), image.getHeight());
				g.dispose();
				cleared = true;
			}
		});
	}

	/**
	 * This plots the values passed, with the Window defined as parameter. It
	 * updates the image initialized in the constructor.
	 * 
	 * @param samples
	 *            the samples to plot
	 * @param samplesPerPixel
	 *            The number of samples in each pixel
	 * @param color
	 *            The color of the line
	 */
	public void plot(float[] samples, final float samplesPerPixel,
			final Color color) {
		synchronized (image) {
			/*
			 * if the size of the image is smaller than the ammount of values
			 * (converted to pixels), then we draw a new rectangule to serve as
			 * background
			 */
			if (image.getWidth() < samples.length / samplesPerPixel) {
				image = new BufferedImage(
						(int) (samples.length / samplesPerPixel), frame
								.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
				Graphics2D g = image.createGraphics();
				g.setColor(Color.black);
				g.fillRect(0, 0, image.getWidth(), image.getHeight());
				g.dispose();
				panel.setSize(image.getWidth(), image.getHeight());
			}

			/*
			 * when the plot is cleared, we calculate the maximum and minimum of
			 * all the values passed, i.e., the scaling factor
			 */
			if (cleared) {
				float min = 0;
				float max = 0;
				for (int i = 0; i < samples.length; i++) {
					min = Math.min(samples[i], min);
					max = Math.max(samples[i], max);
				}
				scalingFactor = max - min;
				cleared = false;
			}

			// it starts the class that allows to draw the line graph
			Graphics2D graph = image.createGraphics();
			graph.setColor(color);

			// the values
			float lastValue = (samples[0] / scalingFactor) * image.getHeight()
					/ 3 + image.getHeight() / 2;
			for (int i = 1; i < samples.length; i++) {
				float value = (samples[i] / scalingFactor) * image.getHeight()
						/ 3 + image.getHeight() / 2;
				graph.drawLine((int) ((i - 1) / samplesPerPixel), image
						.getHeight()
						- (int) lastValue, (int) (i / samplesPerPixel), image
						.getHeight()
						- (int) value);
				lastValue = value;
			}
			graph.dispose();
		}
	}

	/**
	 * This plots the samples, with the Window defined as parameter
	 * 
	 * @param samples
	 *            the samples to plot
	 * @param samplesPerPixel
	 *            The number of samples in each pixel
	 * @param color
	 *            The color of the line
	 */
	public void plot(ArrayList<Float> samples, final float samplesPerPixel,
			final Color color) {
		synchronized (image) {
			if (image.getWidth() < samples.size() / samplesPerPixel) {
				image = new BufferedImage(
						(int) (samples.size() / samplesPerPixel), frame
								.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
				Graphics2D g = image.createGraphics();
				g.setColor(Color.black);
				g.fillRect(0, 0, image.getWidth(), image.getHeight());
				g.dispose();
				panel.setSize(image.getWidth(), image.getHeight());
			}

			if (cleared) {
				float min = 0;
				float max = 0;
				for (int i = 0; i < samples.size(); i++) {
					min = Math.min(samples.get(i), min);
					max = Math.max(samples.get(i), max);
				}
				scalingFactor = max - min;
				cleared = false;
			}

			Graphics2D g = image.createGraphics();
			g.setColor(color);
			float lastValue = (samples.get(0) / scalingFactor)
					* image.getHeight() / 3 + image.getHeight() / 2;
			for (int i = 1; i < samples.size(); i++) {
				float value = (samples.get(i) / scalingFactor)
						* image.getHeight() / 3 + image.getHeight() / 2;
				g.drawLine((int) ((i - 1) / samplesPerPixel), image.getHeight()
						- (int) lastValue, (int) (i / samplesPerPixel), image
						.getHeight()
						- (int) value);
				lastValue = value;
			}
			g.dispose();
		}
	}

	/**
	 * This plots the samples, with the Window defined as parameter. It can used
	 * the scale of the last plot and a vertical offset.
	 * 
	 * @param samples
	 *            the samples to plot
	 * @param samplesPerPixel
	 *            The number of samples in each pixel
	 * @param verticalOffset
	 *            The vertical offSet (in order to plot several frequencies in
	 *            the same image)
	 * @param useLastScale
	 *            boolean that tells to user or not the last scale
	 * @param color
	 *            The color of the line
	 */
	public void plot(float[] samples, final float samplesPerPixel,
			final float verticalOffset, final boolean useLastScale,
			final Color color) {
		synchronized (image) {
			if (image.getWidth() < samples.length / samplesPerPixel) {
				image = new BufferedImage(
						(int) (samples.length / samplesPerPixel), frame
								.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
				Graphics2D g = image.createGraphics();
				g.setColor(Color.black);
				g.fillRect(0, 0, image.getWidth(), image.getHeight());
				g.dispose();
				panel.setSize(image.getWidth(), image.getHeight());
			}

			if (!useLastScale) {
				float min = 0;
				float max = 0;
				for (int i = 0; i < samples.length; i++) {
					min = Math.min(samples[i], min);
					max = Math.max(samples[i], max);
				}
				scalingFactor = max - min;
			}

			Graphics2D g = image.createGraphics();
			g.setColor(color);
			float lastValue = (samples[0] / scalingFactor) * image.getHeight()
					/ 3 + image.getHeight() / 2 - verticalOffset
					* image.getHeight() / 3;
			for (int i = 1; i < samples.length; i++) {
				float value = (samples[i] / scalingFactor) * image.getHeight()
						/ 3 + image.getHeight() / 2 - verticalOffset
						* image.getHeight() / 3;
				g.drawLine((int) ((i - 1) / samplesPerPixel), image.getHeight()
						- (int) lastValue, (int) (i / samplesPerPixel), image
						.getHeight()
						- (int) value);
				lastValue = value;
			}
			g.dispose();
		}
	}

	/**
	 * This plots the samples, with the Window defined as parameter. It can used
	 * the scale of the last plot and a vertical offset.
	 * 
	 * @param samples
	 *            the samples to plot
	 * @param samplesPerPixel
	 *            The number of samples in each pixel
	 * @param verticalOffset
	 *            The vertical offSet (in order to plot several frequencies in
	 *            the same image)
	 * @param useLastScale
	 *            boolean that tells to user or not the last scale
	 * @param color
	 *            The color of the line
	 */
	public void plot(ArrayList<Float> samples, final float samplesPerPixel,
			final float verticalOffset, final boolean useLastScale,
			final Color color) {
		synchronized (image) {
			if (image.getWidth() < samples.size() / samplesPerPixel) {
				image = new BufferedImage(
						(int) (samples.size() / samplesPerPixel), frame
								.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
				Graphics2D g = image.createGraphics();
				g.setColor(Color.black);
				g.fillRect(0, 0, image.getWidth(), image.getHeight());
				g.dispose();
				panel.setSize(image.getWidth(), image.getHeight());
			}

			if (!useLastScale) {
				float min = 0;
				float max = 0;
				for (int i = 0; i < samples.size(); i++) {
					min = Math.min(samples.get(i), min);
					max = Math.max(samples.get(i), max);
				}
				scalingFactor = max - min;
			}

			Graphics2D g = image.createGraphics();
			g.setColor(color);
			float lastValue = (samples.get(0) / scalingFactor)
					* image.getHeight() / 3 + image.getHeight() / 2
					- verticalOffset * image.getHeight() / 3;
			for (int i = 1; i < samples.size(); i++) {
				float value = (samples.get(i) / scalingFactor)
						* image.getHeight() / 3 + image.getHeight() / 2
						- verticalOffset * image.getHeight() / 3;
				g.drawLine((int) ((i - 1) / samplesPerPixel), image.getHeight()
						- (int) lastValue, (int) (i / samplesPerPixel), image
						.getHeight()
						- (int) value);
				lastValue = value;
			}
			g.dispose();
		}
	}

	/**
	 * Sets a marker (vertical line)
	 * 
	 * @param x
	 *            the position to set the marker
	 * @param color
	 *            the colour of the marker
	 */
	public void setMarker(int x, Color color) {
		this.markerPosition = x;
		this.markerColor = color;
	}

	/**
	 * Calculates and returns the value converted to pixels in order to be
	 * plotted
	 * 
	 * @param img
	 *            Buffered image where to plot
	 * @param value
	 *            The value to be plotted
	 * @param offset
	 *            The vertical offset (it's useful to draw several graphics in
	 *            the same image)
	 * @return the value converted to pixels
	 */
	private float calculateScaledValue(BufferedImage img, final float value,
			final float offset) {

		float scaled = (value / scalingFactor) * img.getHeight() / 3
				+ img.getHeight() / 2 - offset * img.getHeight() / 3;

		return scaled;

	}
}
