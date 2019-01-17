package client.localization;

import client.montecarlo.IMoveController;
import client.montecarlo.ActionException;
import client.montecarlo.MonteCarloAlgorithmen;
import client.montecarlo.SensorDataSet;
import client.net.LeJOSClient;
import client.util.NoLogger;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;


/**
 *  SAMPLE -->
 *
 *
 *
 *  Ablauf:
 *      1. Initialer Belief
 *         k Samples auf dem Bildschirm erstellen, die Gleichverteilt auf allen Möglichen Positionen verteilt sind
 *         Der importance factor ist 1/k.
 *         Ein Partikel ( Sample ) repräsentiert eine Position
 *
 *         Ziel: Partikel ( Samples ) zu generieren, welche am ( Hochpunkt ) der Wahrscheinlichkeitsdichte liegen
 *
 *      2. Belief aktualisieren
 *          Ein Sample wird zufällig aus der Menge genommen
 *          Der importance factor gibt die Auswahlwahrscheinlichkeit.
 */
public class Main extends Application{

    public static int SCALE_FACTOR = 2;
    Map m = new Map(Helper.BUILDING_WIDTH_CM * SCALE_FACTOR, Helper.BUILDING_HEIGHT_CM * SCALE_FACTOR);

    GraphicsContext gc;
    Canvas canvas;

    TextField tfHost;
    TextField tfPort;

    Button bConnect;
    Button bLocate;

    LeJOSClient myclient;
    NoLogger logger = new NoLogger();

    MonteCarloAlgorithmen monte;

    private VBox getMainLayout()
    {
        VBox vLayout = new VBox();
        HBox inputs = new HBox();
        inputs.setPadding(new Insets(15, 12, 15, 12));
        inputs.setSpacing(10);

        inputs.getChildren().add(new Label("Host:"));
        tfHost = new TextField("10.0.1.15");
        inputs.getChildren().add(tfHost);

        inputs.getChildren().add(new Label("Port:"));
        tfPort = new TextField("6789");
        inputs.getChildren().add(tfPort);

        bConnect = new Button("Connect");
        inputs.getChildren().add(bConnect);

        bLocate = new Button("Locate");
        bLocate.setOnAction(event -> {
           try {
              ArrayList<IMoveController> movables = new ArrayList<>();
              for ( Particle p : m.getParticles()){
                  IMoveController m = p;
                  movables.add(m);
              }
              monte.run(movables);
           } catch (ActionException e) {
              new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
           }
        });

       switchConnectedButton();

        inputs.getChildren().add(bLocate);

        bConnect.setOnAction(event -> {
            if (myclient.isConnected())
                disconnectFromLeJOS();
            else
                connectToLeJOS();
        });


        vLayout.getChildren().add(inputs);
        vLayout.getChildren().add(canvas);

        return vLayout;
    }

    @Override
    public void stop()
    {
        if (myclient.isConnected())
            disconnectFromLeJOS();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("LeJOS - Client (Team: D_GELB)");
        Group root = new Group();

        myclient = new LeJOSClient(logger);
        monte = new MonteCarloAlgorithmen(myclient);

        canvas = new Canvas(Helper.BUILDING_WIDTH_CM * SCALE_FACTOR, Helper.BUILDING_HEIGHT_CM * SCALE_FACTOR);
        canvas.setFocusTraversable(true);
        canvas.addEventFilter(MouseEvent.MOUSE_PRESSED, (e) -> canvas.requestFocus());
        gc = canvas.getGraphicsContext2D();
        drawMap();

        root.getChildren().add(getMainLayout());

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setOnKeyPressed(event -> {
            //System.out.println("press");
            KeyCode w = KeyCode.W;
            KeyCode a = KeyCode.A;
            KeyCode s = KeyCode.S;
            KeyCode d = KeyCode.D;
            if ( w.equals(event.getCode())){
                moveForward(5);
            }
            if ( s.equals(event.getCode())){
                moveBackward(5);
            }
            if ( a.equals(event.getCode())){
                turnLeft(Helper.degreeToRadiand(5));
            }
            if ( d.equals(event.getCode())){
                turnRight(Helper.degreeToRadiand(5));
            }

            reDraw();
        });
    }

    private void reDraw(){
        gc.clearRect(0,0,Helper.BUILDING_WIDTH_CM*SCALE_FACTOR,Helper.BUILDING_HEIGHT_CM*SCALE_FACTOR);
        drawMap();
    }
    private void drawMap() {

        gc.setFill(Color.BLACK);
        gc.setStroke(Color.DARKRED);
        gc.setLineWidth(2);
        gc.setLineDashes(0);

        for ( Line l : m.getLines()){
            gc.strokeLine(l.x1*SCALE_FACTOR , l.y1*SCALE_FACTOR , l.x2*SCALE_FACTOR , l.y2*SCALE_FACTOR);
        }

        gc.setLineDashes(10);
        for ( Particle particle : m.getParticles()) {
            Point absCenter = particle.centerPoint;
            Point lineA = Helper.getRotationPoint(particle.centerPoint,0.005,particle.currentRotation );
            Point lineB = Helper.getRotationPoint(particle.centerPoint,0.005,particle.currentRotation+ Math.PI);

            gc.strokeLine(lineA.x*SCALE_FACTOR, lineA.y*SCALE_FACTOR, lineB.x*SCALE_FACTOR,lineB.y*SCALE_FACTOR);
            gc.fillOval(absCenter.x*SCALE_FACTOR-3, absCenter.y*SCALE_FACTOR-3, 6, 6);
            gc.fillOval( lineA.x*SCALE_FACTOR-2 , lineB.y*SCALE_FACTOR-2 , 4,4 );
            gc.strokeLine(lineB.x*SCALE_FACTOR,lineB.y*SCALE_FACTOR , particle.forwardIntersect.point.x*SCALE_FACTOR,particle.forwardIntersect.point.y*SCALE_FACTOR);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }


    public void moveForward(double cm) {
        for ( Particle particle : m.getParticles()){
            try {
                particle.moveForward(cm);
            } catch (ActionException e) {
                e.printStackTrace();
            }
            particle.calculateIntersect(particle.currentRotation, m.getLines());

        }
    }

    public void moveBackward(double cm) {
        for ( Particle particle : m.getParticles()){
            try {
                particle.moveBackward(cm);
            } catch (ActionException e) {
                e.printStackTrace();
            }
            particle.calculateIntersect(particle.currentRotation, m.getLines());
        }
    }


    public void turnLeft(double angle){
        for ( Particle particle : m.getParticles()){
            particle.turnLeft(angle);
            particle.calculateIntersects();
        }
    }

    public void turnRight(double angle)  {
        for ( Particle particle : m.getParticles()){
            particle.turnRight(angle);
            particle.calculateIntersects();
        }
    }


    public SensorDataSet getSensorDataSet() throws ActionException {
        return null;
    }

    private void switchConnectedButton() {

        bLocate.setDisable(!myclient.isConnected());

        if (myclient.isConnected())
            bConnect.setText("Disconnect");
        else
            bConnect.setText("Connect");
    }

    private void connectToLeJOS() {

        String host = tfHost.getText();
        int port = Integer.parseInt(tfPort.getText());
        try {
            myclient.connect(host, port);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }

        switchConnectedButton();
    }

    private void disconnectFromLeJOS() {
        try {
            myclient.disconnect();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }

        switchConnectedButton();
    }
}
