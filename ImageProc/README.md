<html>
<head>
<title>
<h1>Image Processing R&D using OpenCV and JavaFx</h1>
</title>
</head>
<body>
<img src="../screenshots/s1.png" alt="UI width="550" height="350"/>

<h2>What is this repository for?</h2>


<ul>
  <li>This repository is for R&D on Image processing.</li>
  <li>Technologies: - Java 9, JavaFX & OpenCV</li>
  <li>Version 1.0</li>
</ul>  



<h2>How do I get set up?</h2>

<ul>
  <li>IDE: Intellij idea</li>
   <li>Make sure you have installed Java 9[Java 8 is OK. But recommend Java 9  because some features of java media only works for Java 9]</li>
     <li>Install JavaFX plugin for IntelliJ Idea</li>
  <li>Add external dependency </li>
  <ul>
  <li>  eg: Opencv-3.2.0.jar (Can be found in /opencv-root/build/bin/)</li>
  </ul>
  <li>Set VM Options in Run Configurations</li>
    <ul>
  <li> -Djava.library.path=/home/chathura/softcopy/opencv-3.2.0/build/lib</li>
  </ul>
    <li>Set path to SceneBuilder in settings -> Language & Frameworks -> javaFx</li>
  <ul>
    <li>       eg: /opt/SceneBuilder/SceneBuilder</li>
  </ul>
    <li>Set Main class in Run Configurations</li>
  <ul>
    <li> sample.Main</li>
  </ul>
  <li>Now you can run the project</li>
</ul>  



<h2>Tested requirements</h2>
<ul>
  <li>General measurement functions</li>
  <li>Save multiple calibration values</li>
  <li>Calibration value setting for measurement</li>
  <li>Real time fps display</li>
  <li>Interval shooting (time lapse)</li>
  <li>Binarization of real-time video</li>
  <li>Count function (number count by mouse click) </li>
  <li>Compatible with multiple cameras </li>
</ul>  

<h2>Who do I talk to?</h2>
<ul>
  <li>Repo owner and developer: hSenid Mobile(BeyondM)</li>
 </ul>
 
 <h2>Componenets</h2>
 <ul>
 
  <li>Fps Tracker</li>
    <ul>
      <li>use data/anim folder</li>
    </ul>
     <li>Object Detection</li>
    <ul>
      <li>For Ball detector>>use data/BallDetection folder</li>
    </ul>
     <ul>
      <li>For Circle detector>>use data/CircleDetector folder</li>
    </ul>
     <ul>
      <li>For Line detector>>use data/Line folder</li>
    </ul>
     <li>CameraCalibration</li>
    <ul>
      <li>use data/Chess folder</li>
    </ul>
       <li>Video binarization</li>
    <ul>
      <li>use data/Anim folder</li>
    </ul>
       <li>Triangle,Rectangle,Square,Polygon,Circle & Hexogon detection</li>
    <ul>
      <li>use data/Shapes folder</li>
    </ul>
      <li>Arc detection</li>
    <ul>
      <li>use data/arc folder</li>
    </ul>
         <li>Inter-Circle-Distance detection</li>
    <ul>
      <li>use data/inter-circle folder</li>
    </ul>
         <li>Mouse Click Count</li>
    <ul>
      <li>do not want images</li>
    </ul>
    
    
 </ul>
  
  
  <h2>Credits</h2>
<ul>
  <li><a href="https://github.com/opencv-java">OpenCV with JavaFx</a></li>
  <li><a href="https://docs.opencv.org/master/d4/d94/tutorial_camera_calibration.html">Camera calibration With OpenCV</a></li>
  <li><a href="https://docs.opencv.org/java/3.0.0/constant-values.html">Constant Field Values</a></li>
  <li><a href="http://laxmaredy.blogspot.com/2014/06/blog-post_6263.html">Shapes Detection in image using OpenCV - Java</a></li>
  <li><a href="https://github.com/michaeltroger/shape-detection">GitHub - michaeltroger/shape-detection: Augmented Reality simple</a></li>
  <li><a href="https://www.pyimagesearch.com/2016/02/08/opencv-shape-detection/">PyImageSearch - OpenCV shape detection</a></li>
  <li><a href="https://www.programcreek.com/java-api-examples/index.php?class=org.opencv.imgproc.Imgproc&method=boundingRect">Imgproc.boundingRect() </a></li>
  <li><a href="https://docs.opencv.org/trunk/d1/d32/tutorial_py_contour_properties.html">Contour Properties </a></li>
  <li><a href="https://docs.opencv.org/trunk/d4/d70/tutorial_hough_circle.html">Hough Circle Transform</a> </li>
  <li><a href="https://docs.opencv.org/2.4/doc/tutorials/imgproc/imgtrans/hough_lines/hough_lines.html">Hough Line Transform</a></li>
  <li>**most important  OPENCV Q&A ONLINE FORUM</li>
</ul> 
</body>
</html>