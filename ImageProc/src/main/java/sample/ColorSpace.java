
package sample;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import util.CVLoader;
import util.ImgWindow;

import java.util.ArrayList;
import java.util.List;

public class ColorSpace {
	public static void main (String[] args) {
//		CVLoader.load();
		Mat orig = Imgcodecs.imread("data/topdown-6.jpg");
		Mat hsv = new Mat();
		Imgproc.cvtColor(orig, hsv, Imgproc.COLOR_BGR2YCrCb);
		
		List<Mat> channels = new ArrayList<Mat>();
		for(int i = 0; i < hsv.channels(); i++) {
			Mat channel = new Mat();
			channels.add(channel);
		}
		Core.split(hsv, channels);
		
		for(Mat channel: channels) {
			ImgWindow.newWindow(channel);
		}
	}
	
	public static Mat getChannel(Mat img, int channelIdx) {
		List<Mat> channels = new ArrayList<Mat>();
		for(int i = 0; i < img.channels(); i++) {
			Mat channel = new Mat();
			channels.add(channel);
		}
		Core.split(img, channels);
		return channels.get(channelIdx);
	}
	
	public static Mat getChannel(Mat orig, int colorSpace, int channelIdx) {
		Mat hsv = new Mat();
		Imgproc.cvtColor(orig, hsv, colorSpace);
		List<Mat> channels = new ArrayList<Mat>();
		for(int i = 0; i < hsv.channels(); i++) {
			Mat channel = new Mat();
			channels.add(channel);
		}
		Core.split(hsv, channels);
		return channels.get(channelIdx);
	}
}
