package sample;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class Calibration {
	/** background image used for background subtraction **/
	private Mat backgroundImage;
	/** threshold for background subtraction **/
	private int subtractionThreshold = 30;
	/** blur size in pixels for hough transform **/
	private int blurSize = 7;
	/** morph size in pixels for hough transform **/
	private int morphSize = 5;
	/** ball radius in pixels **/
	private float ballRadius = 35;
	/** 4 corner points of the play area in camera space **/
	private List<Point> playAreaCameraSpace = new ArrayList<Point>();
	/** 4 corner points of the play area in logical/world space **/
	private List<Point> playArea = new ArrayList<Point>();
	/** 4 corner points of a rectangle projected by the projector in camera space **/
	private List<Point> projectorCameraSpace = new ArrayList<Point>();
	/** transforms camera to play area space **/
	private Mat cameraToPlayArea;
	/** transforms play area to camera space **/
	private Mat playAreaToCamera;
	/** transforms play area to projector space **/
	private Mat playAreaToProjector;
	
	public Calibration(int playAreaWidth, int playAreaHeight) {
		playArea.add(new Point(0, 0));
		playArea.add(new Point(playAreaWidth, 0));
		playArea.add(new Point(playAreaWidth, playAreaHeight));
		playArea.add(new Point(0, playAreaHeight));
	}

	public List<Point> getPlayAreaCameraSpace () {
		return playAreaCameraSpace;
	}

	public List<Point> getPlayArea () {
		return playArea;
	}

	public List<Point> getProjectorCameraSpace () {
		return projectorCameraSpace;
	}

	public Mat getBackgroundImage () {
		return backgroundImage;
	}

	public void setBackgroundImage (Mat backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public int getSubtractionThreshold () {
		return subtractionThreshold;
	}

	public void setSubtractionThreshold (int subtractionThreshold) {
		this.subtractionThreshold = subtractionThreshold;
	}

	public int getBlurSize () {
		return blurSize;
	}

	public void setBlurSize (int blurSize) {
		this.blurSize = blurSize;
	}

	public int getMorphSize () {
		return morphSize;
	}

	public void setMorphSize (int morphSize) {
		this.morphSize = morphSize;
	}

	public float getBallRadius () {
		return ballRadius;
	}

	public void setBallRadius (float ballRadius) {
		this.ballRadius = ballRadius;
	}

	public Mat getCameraToPlayArea () {
		return cameraToPlayArea;
	}

	public void setCameraToPlayArea (Mat cameraToPlayArea) {
		this.cameraToPlayArea = cameraToPlayArea;
	}

	public Mat getPlayAreaToCamera () {
		return playAreaToCamera;
	}

	public void setPlayAreaToCamera (Mat playAreaToCamera) {
		this.playAreaToCamera = playAreaToCamera;
	}

	public Mat getPlayAreaToProjector () {
		return playAreaToProjector;
	}

	public void setPlayAreaToProjector (Mat playAreaToProjector) {
		this.playAreaToProjector = playAreaToProjector;
	}

	public void setPlayAreaCameraSpace (List<Point> playAreaCameraSpace) {
		this.playAreaCameraSpace = playAreaCameraSpace;
	}

	public void setPlayArea (List<Point> playArea) {
		this.playArea = playArea;
	}

	public void setProjectorCameraSpace (List<Point> projectorCameraSpace) {
		this.projectorCameraSpace = projectorCameraSpace;
	}
}
