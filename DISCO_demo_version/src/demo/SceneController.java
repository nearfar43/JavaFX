package demo;

/*
 * author: Jeffrey Cheng
 * 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.Callable;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.animation.PathTransition.OrientationType;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Point2D;


public class SceneController implements Initializable{
	
	@FXML
	private VBox mainBox;
	
	@FXML
	private Pane mapPane;

	@FXML
	private TextArea textArea;

	@FXML
	private StackPane circle;

	@FXML
	private Circle blueSensorCircle;

	@FXML
	private Circle blueAreaCircle;

	@FXML
	private Circle targetCircle;

	@FXML
	private Label blueLabel;

	@FXML
	private Label label1;

	@FXML
	private Label scoreLabel;

	@FXML
	private Label bonusLabel;

	@FXML
	private Label plusLabel;

	@FXML
	private Slider areaSlider;

	@FXML
	private AnchorPane mainPane;

	@FXML
	private Rectangle rect;

	@FXML
	private SVGPath hourHand;

	@FXML
	private StackPane blueAirPlane;

	@FXML
	private StackPane blueSearchArea;

	@FXML
	private Button buttonSearch;

	@FXML
	private Text text;
	
	@FXML
	private TextField confidentInput;

	@FXML
	private ProgressBar scoreBar;

	@FXML
	private Circle scoreCircle;

	@FXML
	private Button goButton;

	private Timeline timer;
	private Timeline timer2;
	private Timeline replayTimer;
	private int startAngleO;
	private Rotate rotate;
	private PathTransition cPT;
	private PathTransition cPT2;
	private PathTransition moveTransition;
	private PathTransition autoSearchTransition;
	private boolean go;
	private double currentDistance;
	private double currentDistance2;
	private double closestDistance;
	private double closestDistance2;
	private double currentScore;
	private double currentScore2;
	private double lineSecond;
	private double circleSecond;
	private Path linePath;
	private Path circlePath;
	private int count = 0;
	private boolean replay;
	private boolean targetFound = false;
	private int targetSpeed;
	private double targetDistance; 


	//parameters 
	private ArrayList<Double> userSelectX;
	private ArrayList<Double> userSelectY;
	private ArrayList<Double> radiusSelected;
	private ArrayList<Double> targetStartX;
	private ArrayList<Double> targetStartY;
	private ArrayList<Double> targetLastVisibleX;
	private ArrayList<Double> targetLastVisibleY;
	private ArrayList<Double> targetFinalX;
	private ArrayList<Double> targetFinalY;
	private ArrayList<Double> aircraftStartX;
	private ArrayList<Double> aircraftStartY;

	private double detectDistance;
	private double aircraftSpeed;
	private double totalTime;
	private double visibleTime;
	private double invisibleTime;
	private double probAir;
	private double probTarget;
	private double awardPoint;
	private double deductedPoint;
	private double confidentScore;
	private double replayCount;

	private String pId;
	private File saveFile;
	private File tFile;
	private File sFile;

	private long userStartTime;
	private long userMoveTime;
	private long userFindTime;
	
	int count2;
	int count3;

	
	final Delta dragDelta = new Delta();
	final Delta releaseDelta = new Delta();


	class Delta { 

		double x; 
		double y; 
	}
	
	
	public SceneController(String pId, File sFile, File tFile) throws IOException {
		
		this.pId = pId;
		this.sFile = sFile;
		this.tFile = tFile;

		readFileScenario(sFile);
		readFileTrial(tFile);	
		writeFileScenario(pId, sFile, tFile);

		
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
		initialUI();
		gameProcess();


	}



	public void readFileScenario(File file) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(file));

		String line;

		try {
			while ((line = br.readLine()) != null) {

				if (line.startsWith("/") == false && line.trim().length() != 0) {

					String[] para = line.split(",");

					line.trim();

					System.out.println(Arrays.toString(para));

					try {						
						detectDistance = Double.parseDouble(para[0]);						
						aircraftSpeed = Double.parseDouble(para[1]);
						totalTime = Double.parseDouble(para[2]);
						visibleTime = Double.parseDouble(para[3]);
						probAir = Double.parseDouble(para[4]);
						probTarget = Double.parseDouble(para[5]);
						awardPoint = Double.parseDouble(para[6]);
						deductedPoint = Double.parseDouble(para[7]);
						replayCount = Double.parseDouble(para[8]);
						
						invisibleTime = totalTime - visibleTime;
						


					} catch(NumberFormatException e) {

					}
					
					System.out.println(invisibleTime);

				}

			}

			br.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}


	public void readFileTrial(File file) throws IOException {

		targetStartX = new ArrayList<Double>();
		targetStartY = new ArrayList<Double>();
		targetLastVisibleX = new ArrayList<Double>();
		targetLastVisibleY = new ArrayList<Double>();
		targetFinalX = new ArrayList<Double>();
		targetFinalY = new ArrayList<Double>();
		aircraftStartX = new ArrayList<Double>();
		aircraftStartY = new ArrayList<Double>();		

		try {

			BufferedReader br = new BufferedReader(new FileReader(file));

			String line = "";

			while ((line = br.readLine()) != null) {

				if (line.startsWith("/") == false && line.trim().length() != 0) {

					line.trim();

					String[] para = line.split(",");

					System.out.println(Arrays.toString(para));

					try {

						targetStartX.add(Double.parseDouble(para[0]));
						targetStartY.add(Double.parseDouble(para[1]));
						targetLastVisibleX.add(Double.parseDouble(para[2]));
						targetLastVisibleY.add(Double.parseDouble(para[3]));
						targetFinalX.add(Double.parseDouble(para[4]));
						targetFinalY.add(Double.parseDouble(para[5]));
						aircraftStartX.add(Double.parseDouble(para[6]));
						aircraftStartY.add(Double.parseDouble(para[7]));						


					}catch(NumberFormatException e) {

					}


				}

			}

			br.close();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




	}


	public void writeFileScenario(String fileName, File sFile, File tFile) throws IOException {


		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".txt", true));
		//BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MMM/dd");
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

		Calendar cal = Calendar.getInstance();


		String output = "Paritcipant ID: " + pId + ", " + "Date: " + sdfDate.format(cal.getTime()) + ", " + "Time: " + sdfTime.format(cal.getTime()) + "," + "Scenario: " + sFile.getName() + "," + 
		"Trial: " + tFile.getName() + "\n";


		writer.write(output);

		writer.close();
		
		BufferedWriter writer2 = new BufferedWriter(new FileWriter(fileName + ".csv", true));
		
		String header = "ParticipantID" + "," + 
				"Trial" + "," + 
				"Location of target initially X" + "," + 
				"Location of target initially Y" + "," + 
				"Location of target disappears X" + "," + 
				"Location of target disappears Y" + "," + 
				"Location of target finally X" + "," + 
				"Location of target finally Y" + "," + 
				"Length of time thinking(second)" + "," + 
				"Location selected by participant X" + "," + 
				"Location selected by participant Y" + "," + 
				"Radius selected by participant" + "," + 
				"Target found" + "," +  
				"Time duration until target is found" + "," + 
				"Angle of variances after target is invisible" + "," + 
				"Speed of variances after target is invisible" + "," + 
				"Confident score" + "," + 
				"Closest distance X between target and selected point" + "," +  
				"Optimal point X" + "," + 
				"Optimal point Y" + "," +  
				"Distance of their deviation from optimal" + "," +  
				"Lead Distance of the search" + "," +  
				"Closest distance between target and aircraft sensor" + "," +  
				"Probability of detection by vehicle" + "," + 
				"Probability of being found by target" + "\n";
		
		writer2.write(header);
		writer2.close();
	}


	public void writeFileTrial(String fileName) throws IOException {
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".csv", true));
			
		String timeFound = "";

		if (targetFound == true) {

			timeFound = String.valueOf(userFindTime);

		}
		else {
			timeFound = "null";

		}

		double targetDistance = Math.sqrt(Math.pow(targetFinalX.get(count -1) - targetLastVisibleX.get(count-1), 2) + Math.pow(targetFinalY.get(count - 1) - targetLastVisibleY.get(count - 1), 2));

		double changeSpeed = targetDistance / invisibleTime;

		float angle = (float) Math.toDegrees(Math.atan2(targetFinalY.get(count - 1) - targetLastVisibleY.get(count - 1), targetFinalX.get(count - 1) - targetLastVisibleX.get(count - 1)));

		if (angle < 0) {

			angle += 360;
		}


		double distanceToOptimal = getTargetOptimalDistance(count - 1);

		double optimalAngle = getOptimalAngle(targetLastVisibleX.get(count - 1), targetLastVisibleY.get(count - 1), targetStartX.get(count - 1), targetStartY.get(count - 1));

		double optimalX = targetLastVisibleX.get(count - 1) + distanceToOptimal * Math.cos(Math.toRadians(optimalAngle));
		double optimalY = targetLastVisibleY.get(count - 1) + distanceToOptimal * Math.sin(Math.toRadians(optimalAngle));
		System.out.println(optimalX);
		System.out.println(optimalY);

		double distanceBetweenOptimalAndSelected = Math.sqrt(Math.pow(optimalX - userSelectX.get(0), 2) + Math.pow(optimalY - userSelectY.get(0), 2));

		double distanceBetweenStartAndSelected = Math.sqrt(Math.pow(userSelectX.get(0) - aircraftStartX.get(count - 1), 2) + Math.pow(userSelectY.get(0) - aircraftStartY.get(count - 1), 2));

		double leadDistance = distanceBetweenStartAndSelected - distanceBetweenOptimalAndSelected ;
					
		System.out.println(distanceBetweenOptimalAndSelected);
		System.out.println(leadDistance);
		
		
		String output = pId + "," + 
						String.valueOf(count) + "," +
						targetStartX.get(count - 1) + "," + 
						targetStartY.get(count - 1) + "," + 
						targetLastVisibleX.get(count - 1) + "," + 
						targetLastVisibleY.get(count - 1) + "," + 
						targetFinalX.get(count - 1) + "," + 
						targetFinalY.get(count - 1) + "," + 
						(userMoveTime - userStartTime) / 1000 + "," + 
						userSelectX.get(0) + "," + 
						userSelectY.get(0) + "," + 
						radiusSelected.get(0) + "," + 
						targetFound + "," + 
						timeFound + "," + 
						String.format("%.2f", angle) + "," + 
						String.format("%.2f", changeSpeed) + "," + 
						confidentScore + "," + 
						String.format("%.2f", closestDistance2) + "," + 
						String.format("%.2f", optimalX) + "," + 
						String.format("%.2f", optimalY) + "," + 
						String.format("%.2f", distanceBetweenOptimalAndSelected) + "," +
						String.format("%.2f", leadDistance) + "," + 
						String.format("%.2f", closestDistance) + "," +
						probAir + "," +
						probTarget + "\n";
		writer.write(output);
		
		writer.close();
		

	}



	//////initial UI//////
	public void initialUI() {

		if (count >= targetFinalX.size()) {

			System.exit(0);
		}

		//initialize the timers 
		timer = new Timeline();
		timer2 = new Timeline();
		replayTimer = new Timeline();
		hourHand.getTransforms().clear();
		rotate = new Rotate(0, 100, 100);
		startAngleO = 0;
		hourHand.getTransforms().add(rotate);
		rotate.setAngle(startAngleO);

		//initialize the path transactions...
		cPT = new PathTransition();	
		cPT2 = new PathTransition();
		moveTransition = new PathTransition();
		autoSearchTransition = new PathTransition();

		//circle initialize
		setPositionFixed(circle, circle.getLayoutX() + circle.getTranslateX(), circle.getLayoutY() + circle.getTranslateY());	
		circle.setLayoutX(targetStartX.get(count));
		circle.setLayoutY(targetStartY.get(count));

		//airCraft initialize
		setPositionFixed(blueAirPlane, blueAirPlane.getLayoutX() + blueAirPlane.getTranslateX(), blueAirPlane.getLayoutY() + blueAirPlane.getTranslateY());
		blueAirPlane.setLayoutX(aircraftStartX.get(count));
		blueAirPlane.setLayoutY(aircraftStartY.get(count));
		blueSearchArea.setLayoutX(aircraftStartX.get(count));
		blueSearchArea.setLayoutY(aircraftStartY.get(count));

		///show the airplane
		blueAirPlane.setVisible(true);
		blueAirPlane.setRotate(0);
		blueSearchArea.setVisible(true);

		//disable the '+' and hide the searching area circle
		blueSearchArea.setDisable(true);
		blueAreaCircle.setVisible(false);


		//re design area slider as tickUnit 25
		areaSlider.setMajorTickUnit(25);
		areaSlider.setMinorTickCount(0);
		areaSlider.setSnapToTicks(true);
		areaSlider.setMin(detectDistance);
		areaSlider.setMax(200);
		areaSlider.setValue(50);
		areaSlider.setValueChanging(false);

		blueAreaCircle.radiusProperty().bindBidirectional(areaSlider.valueProperty());
		
	


		//initialize score
		currentScore = 101;
		currentScore2 = 101;

		//initialize array list 
		userSelectX = new ArrayList<Double>();
		userSelectY = new ArrayList<Double>();

		radiusSelected = new ArrayList<Double>();

		scoreLabel.setVisible(false);
		bonusLabel.setVisible(false);
		plusLabel.setVisible(false);

		goButton.setDisable(true);
		confidentInput.clear();
		confidentInput.setDisable(true);		
		scoreBar.setVisible(true);

		go = true;
		replay = false;
		targetFound = false;
		text.setVisible(false);
		targetCircle.setFill(Color.BLACK);

		hourHand.setStroke(Color.BLACK);
		hourHand.setFill(Color.BLACK);

		/////sensor circle size
		blueSensorCircle.setRadius(detectDistance);

		///
		Bounds targetBound = circle.localToScene(circle.getBoundsInLocal());
		Bounds sensorBound = blueAirPlane.localToScene(blueAirPlane.getBoundsInLocal());
		Point2D targetPoint = new Point2D((targetBound.getMinX() + targetBound.getMaxX()) / 2, (targetBound.getMinY() + targetBound.getMaxY()) / 2);
		Point2D sensorPoint = new Point2D((sensorBound.getMinX() + targetBound.getMaxY()) / 2, (sensorBound.getMinY() + sensorBound.getMaxY()) / 2);
		closestDistance = targetPoint.distance(sensorPoint);
		
		count2 = count+1;
		///11, 12, 13, 14
		//targetSpeed = 11 + (int)(Math.random() * ((14 - 11) + 1));
		//targetDistance = targetSpeed * invisibleTime;

		//Random r = new Random();

		//int index = r.nextInt(8);

		//targetDestinationX = 224 + targetDistance * Math.cos(Math.toRadians(angleArray[index]));
		//targetDestinationY = 232 + targetDistance * Math.sin(Math.toRadians(angleArray[index]));



	}
	// end initialUI()

	public void gameProcess() {


		//disable button click when game start...
		areaSlider.setDisable(true);



		//Keep updating the distance between target and airCraft			
		DoubleBinding distance = Bindings.createDoubleBinding(() -> {

			//Point2D targetPoint = new Point2D(circle.getLayoutX() + circle.getTranslateX(), circle.getLayoutY() + circle.getTranslateY());
			//Point2D sensorPoint = new Point2D(blueAirPlane.getLayoutX() + blueAirPlane.getTranslateX(), blueAirPlane.getLayoutY() + blueAirPlane.getTranslateY());
			Bounds targetBound = circle.localToScene(circle.getBoundsInLocal());
			Bounds sensorBound = blueAirPlane.localToScene(blueAirPlane.getBoundsInLocal());
			Point2D targetPoint = new Point2D((targetBound.getMinX() + targetBound.getMaxX()) / 2, (targetBound.getMinY() + targetBound.getMaxY()) / 2);
			Point2D sensorPoint = new Point2D((sensorBound.getMinX() + targetBound.getMaxY()) / 2, (sensorBound.getMinY() + sensorBound.getMaxY()) /2);

			//distanceList.addAll(targetPoint.distance(sensorPoint));

			currentDistance = targetPoint.distance(sensorPoint);

			if (currentDistance < closestDistance) {

				closestDistance = currentDistance;

			}


			return closestDistance;

		},blueAirPlane.translateXProperty(), blueAirPlane.translateYProperty(), circle.translateXProperty(), circle.translateYProperty(), blueAirPlane.layoutXProperty(), blueAirPlane.layoutYProperty());


		text.textProperty().bind(distance.asString("Distance: %f"));			


		//Keep updating scores
		IntegerBinding score = Bindings.createIntegerBinding(() -> {


			currentScore = currentScore - (deductedPoint / 100);

			return (int) currentScore;

		}, timer2.currentTimeProperty());

		DoubleBinding score2 = Bindings.createDoubleBinding(() -> {

			currentScore2 = currentScore / 100;

			return currentScore2;

		}, timer2.currentTimeProperty());

		//
		scoreLabel.textProperty().bind(score.asString());
		scoreBar.progressProperty().bind(score2);
		//scoreCircle.radiusProperty().bind(score2);




		//Clock animation timeline
		targetRun();

		phase1(timer);

		
		goButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				try {
					confidentScore = Integer.parseInt(confidentInput.getText());
				}
				
				catch (Exception e){
					
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Exception Input");
					alert.setContentText("confident number is an integer between 1 to 100");
					alert.show();
					
					return;

				}
				
				if (confidentScore > 100 || confidentScore < 1) {
					
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Exception Input");
					alert.setContentText("confident number is an integer between 1 to 100");
					alert.show();
					
					return;
				}
							
				if (go == true && replay == false) {
					
					//store selected location 
					userSelectX.add(releaseDelta.x);
					userSelectY.add(releaseDelta.y);
					
					//store selected radius
					radiusSelected.add(areaSlider.getValue());
					
					blueSearchArea.setDisable(true);
					goButton.setDisable(true);
					//disable slider
					areaSlider.setDisable(true);
					//disable confident input
					confidentInput.setDisable(true);

					lineMovePathTransition();	



				}

			}

		});
		 

		mapPane.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {


				blueSearchArea.setOnMousePressed(new EventHandler<MouseEvent>() {
					@Override public void handle(MouseEvent mouseEvent) {
						// record a delta distance for the drag and drop operation.
						dragDelta.x = blueSearchArea.getLayoutX() - mouseEvent.getSceneX();
						dragDelta.y = blueSearchArea.getLayoutY() - mouseEvent.getSceneY();
						blueSearchArea.setCursor(Cursor.MOVE);


						////drag position.....
						blueSearchArea.setOnMouseDragged(new EventHandler<MouseEvent>() {
							@Override public void handle(MouseEvent mouseEvent) {
								blueSearchArea.setLayoutX(mouseEvent.getSceneX() + dragDelta.x);
								blueSearchArea.setLayoutY(mouseEvent.getSceneY() + dragDelta.y);

							}


						});///// mouse dragged
					}

				});/////mouse pressed


				blueSearchArea.setOnMouseReleased(new EventHandler<MouseEvent>() {
					@Override public void handle(MouseEvent mouseEvent) {
						blueSearchArea.setCursor(Cursor.HAND);

						releaseDelta.x = blueSearchArea.getLayoutX();
						releaseDelta.y = blueSearchArea.getLayoutY();


						if ( releaseDelta.x > mapPane.getWidth() || releaseDelta.y > mapPane.getHeight() || releaseDelta.x < 0 || releaseDelta.y < 0) {

							textArea.appendText("outside the map" + "\n" + "Please re-select your location" + "\n");

							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Error Location Selected");
							alert.setContentText("Please set your '+' in the map");
							alert.show();
							setPositionFixed(blueSearchArea, blueSearchArea.getLayoutX() + blueSearchArea.getTranslateX(),blueSearchArea.getLayoutY() + blueSearchArea.getTranslateY());


							return;
						}

						else {

							//message for user to choose area slider
							textArea.appendText("Please drag the area slider..." + "\n");

							//enable area slider
							areaSlider.setDisable(false);
							blueAreaCircle.setVisible(true);

							
							areaSlider.setOnMouseClicked(new EventHandler<MouseEvent>() {

								@Override
								public void handle(MouseEvent event) {
									areaSlider.setValueChanging(true);
								}
								
								
							});
							
				
							
							//search area size
							areaSlider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {

								@Override
								public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
									blueAreaCircle.radiusProperty().setValue(Double.valueOf((double) areaSlider.getValue()));
									
									if (oldValue) {
										confidentInput.setDisable(false);
										goButton.setDisable(false);
									}

									else {
										confidentInput.setDisable(false);
										goButton.setDisable(false);
									}
									
									/*
									if (oldValue) {
															
										TextInputDialog dialog = new TextInputDialog("");
										dialog.setTitle("Confident Score");
										dialog.setHeaderText("A confident number for your decision of searching location and area");
										dialog.setContentText("Please enter a number from 1 - 100");

										Alert alert = new Alert(AlertType.ERROR);

										dialog.setOnHidden(e -> {

											try {
												confidentScore = Integer.parseInt(dialog.getResult());
												//goButton.setDisable(false);
												radiusSelected.add(areaSlider.getValue());

												if (go == true && replay == false) {

													lineMovePathTransition();														
													//disable slider
													areaSlider.setDisable(true);

												}
											}
											catch (Exception ex) {

												alert.setTitle("Exception Input");
												alert.setContentText("confident number is an integer between 1 to 100");
												//goButton.setDisable(true);
												alert.show();

												alert.setOnHidden(e2 -> {

													dialog.show();
												});
											}

											if (confidentScore > 100 || confidentScore < 1) {
												goButton.setDisable(true);

												alert.show();											
												alert.setOnHidden(e3 -> {

													dialog.show();
												});

											}
										});

										dialog.show();
										
									}*/

								}
							}); // area slider...

						}

						blueSearchArea.setOnMouseEntered(e -> blueSearchArea.setCursor(Cursor.HAND));

					}

				});/// mouse released


			}

		});


	}

	public void lineMovePathTransition() {

		//movement starting
		moveTransition.stop();


		//////////////distance for calculating duration time//////////////////////////////////
		double distance = Math.sqrt(Math.pow(releaseDelta.x - aircraftStartX.get(count), 2) + 
				Math.pow(releaseDelta.y - aircraftStartY.get(count), 2));

		/////////////////////////////////////////////
		int second = (int)(distance / aircraftSpeed);

		///
		lineSecond = (int) second / 3;
		moveTransition.setDuration(Duration.seconds(second));
		//moveTransition.setDelay(Duration.seconds(2));

		//////////////fixing position before transition play//////////////////////////
		setPositionFixed(blueAirPlane, blueAirPlane.getLayoutX() + blueAirPlane.getTranslateX(), blueAirPlane.getLayoutY() + blueAirPlane.getTranslateY());

		linePath = new Path();
		linePath.getElements().add(new MoveToAbs(blueAirPlane));
		linePath.getElements().add(new LineToAbs(blueAirPlane, userSelectX.get(0), userSelectY.get(0)));
		//fix smooth animation
		moveTransition.setInterpolator(Interpolator.LINEAR);
		moveTransition.setNode(blueAirPlane);
		moveTransition.setPath(linePath);

		hourHand.setStroke(Color.BLACK);
		hourHand.setFill(Color.BLACK);
		Calendar cal = Calendar.getInstance();
		userMoveTime = cal.getTimeInMillis();
		moveTransition.play();
		timer2.play();
		cPT2.play();


		Random r = new Random();

		double d = r.nextDouble();


		//do auto searching after arriving to the location chose
		moveTransition.setOnFinished(new EventHandler<ActionEvent>() {


			@Override
			public void handle(ActionEvent event) {

				autoSearchPathTransition(d);

			}
		}); // move transition




	}


	public void autoSearchPathTransition(double d) {
		
		int bigR = (int)blueAreaCircle.getRadius();
		double distance = 0;
		double second = 0;

		circlePath = new Path();

		if (bigR >= detectDistance) {

			int plus = (int) detectDistance;


			//initialize the start point of searching to fit inside the search area

			for (int i = plus; i < bigR; i = i + plus) {

				setPositionFixed(blueAirPlane, blueAirPlane.getLayoutX() + blueAirPlane.getTranslateX(), blueAirPlane.getLayoutY() + blueAirPlane.getTranslateY());

				MoveTo m = new MoveToAbs(blueAirPlane);
				double x = m.getX();
				double y = m.getY();

				circlePath.getElements().add(new MoveTo(x + i, y));

				circlePath.getElements().add(new ArcTo(i, i, 180, x - i, y, false, false));
				circlePath.getElements().add(new ArcTo(i, i, 180, x + i, y, false, false));

				circlePath.getElements().add(new ClosePath());


				distance = distance + Math.PI * 2 * i;

			}

			//calculate time
			second = (int)distance / aircraftSpeed;

			//////////////////////////////////
			circleSecond = (int)second / 3;

			autoSearchTransition.setDuration(Duration.seconds(second));


			autoSearchTransition.setNode(blueAirPlane);
			autoSearchTransition.setPath(circlePath);
			//pt.setDelay(Duration.seconds(1));

			autoSearchTransition.setCycleCount(Timeline.INDEFINITE);
			autoSearchTransition.setOrientation(OrientationType.ORTHOGONAL_TO_TANGENT);
			autoSearchTransition.setAutoReverse(false);
			autoSearchTransition.setInterpolator(Interpolator.LINEAR);

			autoSearchTransition.play();

			////detect target......
			autoSearchTransition.currentTimeProperty().addListener( e -> {		

				if (d < (probAir * probTarget) && replay == false) {

					detectTarget(blueSensorCircle);
				}

			}); //auto search


		}

	}


	////////////////// location fixed when transition playing ////////////////////////////////////////////
	public static class MoveToAbs extends MoveTo {

		public MoveToAbs(Node node) {
			super(node.getLayoutBounds().getWidth() / 2, node.getLayoutBounds().getHeight() / 2);

		}

	}

	public static class LineToAbs extends LineTo {

		public LineToAbs(Node node, double x, double y) {
			super( x - node.getLayoutX() + node.getLayoutBounds().getWidth() / 2, y - node.getLayoutY() + node.getLayoutBounds().getHeight() / 2);
		}

	}



	private void setPositionFixed(Node node, double x, double y) {
		node.relocate(x, y);
		node.setTranslateX(0);
		node.setTranslateY(0);			

	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static double angleBetweenTwoPointsWithFixedPoint(double point1X, double point1Y, double point2X, double point2Y, double fixedX, double fixedY) {

		double angle1 = Math.atan2(point1Y - fixedY, point1X - fixedX);
		double angle2 = Math.atan2(point2Y - fixedY, point2X - fixedX);

		return angle1 - angle2; 
	}

	public void sliderBinding() {

		blueAreaCircle.radiusProperty().bindBidirectional(areaSlider.valueProperty());


	}

	private void targetRun() {

		textArea.appendText("New Game" + "\n" + "Target is moving..." + "\n");
		//////fixing position of node before transition playing///////////
		setPositionFixed(circle, circle.getLayoutX() + circle.getTranslateX(), circle.getLayoutY() + circle.getTranslateY());

		Path p = new Path();
		p.getElements().add(new MoveToAbs(circle));
		p.getElements().add(new LineToAbs(circle, targetLastVisibleX.get(count), targetLastVisibleY.get(count)));
		cPT.setPath(p);
		cPT.setCycleCount(1);
		cPT.setNode(circle);
		cPT.setDuration(Duration.seconds(visibleTime));
		cPT.setInterpolator(Interpolator.LINEAR);

		cPT.play();
		circle.setVisible(true);

		cPT.setOnFinished(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				//enable the '+'
				blueSearchArea.setDisable(false);
				blueSearchArea.setVisible(true);
				//message output
				textArea.appendText("Target lost..." + "\n" + "Please launch an airplane, then drag the '+' to a postiton you would like to search..." + "\n");


				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle(null);
				alert.setHeaderText(null);
				alert.setContentText("Target lost..." + "\n" + "Please launch an airplane, and drag the '+' to a postiton you would like to search, then select the area size" + "\n");
				alert.show();

				cPT.stop();
				circle.setVisible(false);
				///////////////////////fixing position of node before transition playing///////////////////////////////////////////
				setPositionFixed(circle, circle.getLayoutX() + circle.getTranslateX(), circle.getLayoutY() + circle.getTranslateY());

				Path p = new Path();
				p.getElements().add(new MoveToAbs(circle));
				p.getElements().add(new LineToAbs(circle, targetFinalX.get(count), targetFinalY.get(count)));
				cPT2.setPath(p);
				cPT2.setCycleCount(1);
				cPT2.setNode(circle);
				cPT2.setDuration(Duration.seconds(invisibleTime));
				cPT2.setInterpolator(Interpolator.LINEAR);
				
				//cPT2.play();

			}

		});


	}


	private void phase1(Timeline timer) {

		KeyFrame keyFrame = new KeyFrame(
				Duration.seconds(visibleTime), new KeyValue(rotate.angleProperty(), startAngleO + 30));

		timer.setCycleCount(1);
		startAngleO += 30;
		timer.getKeyFrames().setAll(
				keyFrame
				);
		timer.stop();

		timer.playFromStart();
		timer.setOnFinished( e -> {

			Calendar cal = Calendar.getInstance();			
			userStartTime = cal.getTimeInMillis();
			hourHand.setStroke(Color.RED);
			hourHand.setFill(Color.RED);
			phase2(timer2);

		});


	}

	private void phase2(Timeline timer2) {

		rotate.setAngle(startAngleO);

		KeyFrame keyFrame = new KeyFrame(
				Duration.seconds(invisibleTime), new KeyValue(rotate.angleProperty(), startAngleO + 330));
		timer2.setCycleCount(1);
		timer2.getKeyFrames().setAll(
				keyFrame
				);
		timer2.stop();
		timer2.setOnFinished(e-> {

			cPT2.stop();
			autoSearchTransition.stop();
			moveTransition.stop();
			go = false;

			scoreLabel.setVisible(false);

			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle(null);
			alert.setHeaderText(null);
			alert.setContentText("Time runs out, Sorry you didn't find the target" + "\n");
			
			count++;
			count2++;
		
			
			alert.setOnHidden(evt -> {
				
				
				if (count <= replayCount && go == false  && count <= targetStartX.size()) {

					replay = true;
					
					replayTimeLine(replayTimer, rotate, lineSecond, circleSecond, true);
					
				}
				else if (count > replayCount && go == false && count <= targetStartX.size())  {

					replay = false;
					
					replayTimeLine(replayTimer, rotate, lineSecond, circleSecond, false);
				}




			});//alert hidden

			alert.show();


		});//timer 2 on finished


	}

	private void replayTimeLine(Timeline replayTimer, Rotate rotate, double lineSecond, double circleSecond, boolean vis) {

		blueAirPlane.setVisible(false);
		blueSearchArea.setVisible(false);
		circle.setVisible(false);


		hourHand.getTransforms().add(rotate);
		rotate.setAngle(0);
		KeyFrame keyFrame = new KeyFrame(
				Duration.seconds(1),
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {

						timer2.stop();
						replayTimer.stop();
						cPT.stop();
						cPT2.stop();
						moveTransition.stop();
						autoSearchTransition.stop();

						//reset location....
						setPositionFixed(circle, circle.getLayoutX() + circle.getTranslateX(), circle.getLayoutY() + circle.getTranslateY());						
						circle.setLayoutX(targetStartX.get(count - 1));
						circle.setLayoutY(targetStartY.get(count - 1));

						int startTime = (int)visibleTime / 3;
						int startTime2 = (int)invisibleTime / 3;

						Point2D targetLastVisiblePoint = new Point2D(targetLastVisibleX.get(count - 1), targetLastVisibleY.get(count - 1));
						Point2D sensorPoint = new Point2D(userSelectX.get(0), userSelectY.get(0));
						closestDistance2 = targetLastVisiblePoint.distance(sensorPoint);

						//Keep updating the distance between target and selected point of searching 
						DoubleBinding distanceFirst = Bindings.createDoubleBinding(() -> {

							Point2D searchPoint = new Point2D(userSelectX.get(0), userSelectY.get(0));

							Bounds targetBound = circle.localToScene(circle.getBoundsInLocal());
							Point2D targetPoint = new Point2D((targetBound.getMinX() + targetBound.getMaxX()) / 2, (targetBound.getMinY() + targetBound.getMaxY()) / 2);

							currentDistance2 = targetPoint.distance(searchPoint);

							if (currentDistance2 < closestDistance2) {

								closestDistance2 = currentDistance2;
							}

							return closestDistance2;

						}, circle.translateXProperty(), circle.translateYProperty(), circle.layoutXProperty(), circle.layoutYProperty());

						///displaying information for replay...
						if (vis == true && replay == true && targetFound == false) {
							
							Alert alert = new Alert(Alert.AlertType.INFORMATION);
							alert.setContentText("See the replay... ");
							alert.setTitle(null);
							alert.setHeaderText(null);
							alert.setOnHidden(e2 -> {				

							});
							alert.show();
							
							Timeline waitTime = new Timeline(new KeyFrame(Duration.seconds(1)));
							waitTime.play();
							waitTime.setOnFinished(he-> {							
								alert.hide();
								cPT.setDuration(Duration.seconds(startTime));
								cPT2.setDuration(Duration.seconds(startTime2));
								cPT.play();
								circle.setVisible(vis);
							});
							


						}

						else if (vis == false && replay == false & targetFound == true) {

							cPT.setDuration(Duration.seconds(1));
							cPT2.setDuration(Duration.seconds(1));
							cPT.play();
							circle.setVisible(vis);

						}

						else if (vis == false && replay == false & targetFound == false) {
							
							cPT.setDuration(Duration.seconds(1));
							cPT2.setDuration(Duration.seconds(1));
							cPT.play();
							circle.setVisible(vis);

						}


						cPT.setOnFinished(new EventHandler<ActionEvent>() {

							@Override
							public void handle(ActionEvent event) {

								setPositionFixed(circle, circle.getLayoutX() + circle.getTranslateX(), circle.getLayoutY() + circle.getTranslateY());

								cPT.stop();

								areaSlider.setValue(radiusSelected.get(0));

								setPositionFixed(blueSearchArea, blueSearchArea.getLayoutX() + blueSearchArea.getTranslateX(), blueSearchArea.getLayoutY() + blueSearchArea.getTranslateY());
								blueSearchArea.setLayoutX(userSelectX.get(0));
								blueSearchArea.setLayoutY(userSelectY.get(0));

								setPositionFixed(blueAirPlane, blueAirPlane.getLayoutX() + blueAirPlane.getTranslateX(), blueAirPlane.getLayoutY() + blueAirPlane.getTranslateY());
								blueAirPlane.setLayoutX(aircraftStartX.get(count - 1));
								blueAirPlane.setLayoutY(aircraftStartY.get(count - 1));

								blueAirPlane.setVisible(vis);
								blueSearchArea.setVisible(vis);

								linePath = new Path();
								linePath.getElements().add(new MoveToAbs(blueAirPlane));
								linePath.getElements().add(new LineToAbs(blueAirPlane, userSelectX.get(0), userSelectY.get(0)));
								moveTransition.setPath(linePath);


								Timeline waitTime = new Timeline();
								//double second = (userMoveTime - userStartTime) / 3000;
								KeyFrame k = new KeyFrame(Duration.seconds(1));

								waitTime.setCycleCount(1);
								waitTime.getKeyFrames().setAll(k);
								waitTime.stop();
								waitTime.play();

								waitTime.setOnFinished(e -> {

									autoSearchTransition.currentTimeProperty().removeListener(re -> {

										detectTarget(blueSensorCircle);
									});

									moveTransition.currentTimeProperty().removeListener(re2 -> {

										detectTarget(blueSensorCircle);
									});

									moveTransition.setDuration(Duration.seconds(lineSecond));
									moveTransition.play();
									cPT2.play();

									moveTransition.setOnFinished(evt -> {

										moveTransition.stop();

										setPositionFixed(blueAirPlane, blueAirPlane.getLayoutX() + blueAirPlane.getTranslateX(), blueAirPlane.getLayoutY() + blueAirPlane.getTranslateY());

										autoSearchTransition.setDuration(Duration.seconds(circleSecond));

										autoSearchTransition.play();


									});//move transition on finished



								});

								cPT2.setOnFinished(e1 -> {

									replayTimer.stop();
									cPT2.stop();
									moveTransition.stop();
									autoSearchTransition.stop();


									Alert alert = new Alert(Alert.AlertType.INFORMATION);
									alert.setTitle("Result");
									alert.setHeaderText(null);
									alert.setContentText("Game done, press ok to start a new game");

									alert.setOnHidden(evt1 -> {

										/// write output file.....
										try {
											writeFileTrial(pId);
										} catch (IOException ex) {
											// TODO Auto-generated catch block
											ex.printStackTrace();
										}

										initialUI();
										gameProcess();



									});

									alert.show(); 


								}); // cPT2 on finished


							}
						});/// cPt on finished


					}

				});

		replayTimer.setCycleCount(1);
		replayTimer.stop();
		replayTimer.getKeyFrames().setAll(
				keyFrame
				);
		replayTimer.play();


	}


	//detect target
	private Boolean detectTarget(Shape blueSensorCircle) {

		boolean collisionDetected = false;

		Shape intersect = Shape.intersect(blueSensorCircle, targetCircle);

		if (intersect.getBoundsInLocal().getWidth() != -1) {
			collisionDetected = true;	

		}


		if (collisionDetected) {

			circle.setVisible(true);
			timer.stop();
			timer2.stop();
			cPT2.stop();
			moveTransition.stop();
			autoSearchTransition.stop();
			go = false;
			targetFound = true;
			replay = false;
						

			if (count2 > count) {

				targetWasFound();

			}

		}

		return collisionDetected;


	}

	private void targetWasFound() {

		textArea.appendText("\n" + "Something was detected...");

		targetCircle.setFill(Color.RED);
		plusLabel.setVisible(true);
		bonusLabel.setText(String.valueOf(awardPoint));
		bonusLabel.setVisible(true);
		scoreLabel.setVisible(true);
		blueSearchArea.setDisable(false);
		scoreBar.setVisible(false);

		Calendar cal = Calendar.getInstance();
		userFindTime = (cal.getTimeInMillis() - userMoveTime) / 1000;
		
		count2--;

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Congratulation");
		alert.setHeaderText(null);
		alert.setContentText("You found the target, you socre is: " + scoreLabel.getText() + " + " + bonusLabel.getText());

		alert.setOnHidden(evt -> {

			count++;
			
			replayTimeLine(replayTimer, rotate, lineSecond, circleSecond, false);

		});

		alert.show();


	}

	// calculate the shortest distance between aircraft starting point and the optimal path (line) of target
	private double distanceFromPointToLine(double x1, double y1, double x2, double y2, double startX, double startY) {

		double distance = 0;

		distance = Math.abs(((y2 - y1) * startX) - ((x2 - x1) * startY) + x2 * y1 - y2 * x1) / Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));


		return distance;

	}

	// calculate the angle for target from starting point to last visible point
	private double getOptimalAngle(double x1, double y1, double x2, double y2) {

		double angle = Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));

		if (angle < 0) {
			angle += 360;
		}

		return angle;
	}

	// calculate the optimal distance for target intersecting with aircraft after the certain seconds..
	private double getTargetOptimalDistance(int count) {

		double sec = 0;
		double targetSpeed;

		sec = (distanceFromPointToLine(targetStartX.get(count), targetStartY.get(count), targetLastVisibleX.get(count), targetLastVisibleY.get(count), aircraftStartX.get(count), aircraftStartY.get(count)) /
				aircraftSpeed);
		
		if (sec < visibleTime) {
			
			
		}

		targetSpeed = getTargetSpeed(targetLastVisibleX.get(count), targetLastVisibleY.get(count), targetStartX.get(count), targetStartY.get(count));

		return sec * targetSpeed;

	}

	// calculate target speed base on time and distance(two point)
	private double getTargetSpeed(double finalX, double finalY, double firstX, double firstY) {

		double distance = Math.sqrt(Math.pow(finalX - firstX, 2) + Math.pow(finalY - firstY, 2));

		double speed = distance / visibleTime;

		return speed;

	}
	
	


}

