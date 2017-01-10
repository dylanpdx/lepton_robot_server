package lepton;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Lepton {

	public static int getrgb(int red, int green, int blue) {
		int rgb = red;
		rgb = (rgb << 8) + green;
		rgb = (rgb << 8) + blue;
		return rgb;
	}

	public static void main(String[] args) {
		Thread pi1 = new Thread(){
			@Override
			public void run(){
				server(1024,"pi1");
			}
		};
		
		Thread pi2 = new Thread(){
			@Override
			public void run(){
				server(1025,"pi2");
			}
		};
		
		pi1.start();
		pi2.start();
		
		JFrame f = new JFrame();
		f.setLayout(new BorderLayout());
		ImageIcon pii1 = new ImageIcon("E:\\workspace\\lepton\\pi1.png");
		ImageIcon pii2 = new ImageIcon("E:\\workspace\\lepton\\pi2.png");
		
		JLabel pilab1 = new JLabel(pii1);
		JLabel pilab2 = new JLabel(pii2);
		pilab1.setVisible(true);
		pilab2.setBounds(10, 10, 400, 400);
		pilab2.setVisible(true);
		
		f.add(pilab1, BorderLayout.EAST);
		f.add(pilab2, BorderLayout.WEST);
		
		f.repaint();
		f.setVisible(true);
		
		while (true){
			try {
				pii1.setImage(ImageIO.read(new File("E:\\workspace\\lepton\\pi1.png")));
				pii2.setImage(ImageIO.read(new File("E:\\workspace\\lepton\\pi2.png")));
			}catch (Exception iio){
				// image is being written to, so we can't read from it :(
			}
			f.repaint();
		}

	}
	
	
	public static void server(int port,String filename){
		try {
			ServerSocket s = new ServerSocket(port);
			while (true) {
				Socket sock = s.accept(); // accept connection (only does so
											// once)
				System.out.println("Accepted connection!");
				InputStream in = sock.getInputStream(); // input stream
				OutputStream out = sock.getOutputStream(); // output stream
				String fdata = "";
				BufferedReader b = new BufferedReader(new InputStreamReader(in));
				while (true) {
					
					fdata = b.readLine();
					//System.out.println("owo "+fdata);
						
					
					fdata = fdata.replace("]", "],");
					fdata = fdata.replace("],]", "]]");
					while (fdata.contains("],]")) {
						fdata = fdata.replace("],]", "]]");
					}

					fdata = "{\"data\":" + fdata.substring(0, fdata.length() - 1) + "}";

					//System.out.println(fdata);
					JSONObject j = new JSONObject(fdata);
					JSONArray arr = j.getJSONArray("data"); // array of
															// arrays????????
					//System.out.println("Vertical size: " + arr.length());
					//System.out.println("horiz size: " + arr.getJSONArray(0).length());

					BufferedImage bb = new BufferedImage(80, 60, BufferedImage.TYPE_INT_RGB);

					for (int x = 0; x < arr.length(); x++) {
						JSONArray arry = arr.getJSONArray(x);
						for (int y = 0; y < arry.length(); y++) {
							int pixel = arry.getJSONArray(y).getInt(0);
							pixel = getrgb(pixel, pixel, pixel);
							//System.out.println("Setting x" + x + " y" + y + " to " + pixel);
							bb.setRGB(y, x, pixel);
						}
					}
					File outf = new File(filename+".png");
					ImageIO.write(bb, "png", outf);
					System.out.println("done!!!"+filename);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
