package client.localization;

import client.montecarlo.IMoveController;
import client.montecarlo.ActionException;
import client.montecarlo.SensorDataSet;
import client.net.LeJOSClient;
import client.util.NoLogger;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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

    Map m = new Map(Helper.BUILDING_WIDTH_CM, Helper.BUILDING_HEIGHT_CM);

    GraphicsContext gc;
    Canvas canvas;

    TextField tfHost;
    TextField tfPort;

    Button bConnect;

    LeJOSClient myclient;
    private Boolean connected = false;
    private final static int SCALE_FACTOR = 2;

    private VBox getMainLayout()
    {
        VBox vLayout = new VBox();
        HBox inputs = new HBox();
        inputs.setPadding(new Insets(15, 12, 15, 12));
        inputs.setSpacing(10);

        inputs.getChildren().add(new Label("Host:"));
        tfHost = new TextField("10.0.1.9");
        inputs.getChildren().add(tfHost);

        inputs.getChildren().add(new Label("Port:"));
        tfPort = new TextField("6789");
        inputs.getChildren().add(tfPort);

        bConnect = new Button("Connect");
        inputs.getChildren().add(bConnect);

        bConnect.setOnAction(event -> {
            if (connected)
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
        if (connected)
            disconnectFromLeJOS();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("LeJOS - Client (Team: D_GELB)");
        Group root = new Group();
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
                //particle.calculateIntersect(particle.currentRotation, m.getLines());
            } catch (ActionException e) {
                e.printStackTrace();
            }


        }
    }

    public void moveBackward(double cm) {
        for ( Particle particle : m.getParticles()){
            try {
                particle.moveBackward(cm);
                //particle.calculateIntersect(particle.currentRotation, m.getLines());
            } catch (ActionException e) {
                e.printStackTrace();
            }

        }
    }


    public void turnLeft(double angle){
        for ( Particle particle : m.getParticles()){
            particle.turnLeft(angle);
            //particle.calculateIntersects();
        }
    }

    public void turnRight(double angle)  {
        for ( Particle particle : m.getParticles()){
            particle.turnRight(angle);
            //particle.calculateIntersects();
        }
    }


    public SensorDataSet getSensorDataSet() throws ActionException {
        return null;
    }

    private void switchConnectedButton() {
        if (connected)
            bConnect.setText("Disconnect");
        else
            bConnect.setText("Connect");
    }

    private void connectToLeJOS() {

        String host = tfHost.getText();
        int port = Integer.parseInt(tfPort.getText());
        try {
            NoLogger logger = new NoLogger();
            myclient = new LeJOSClient(host, port, logger);
            connected = true;

        } catch (Exception e) {
            //console.log(e.getMessage());
            //error(e.getMessage());
        }

        //info(String.format("Conntection established to %s on Port %d...", host, port));
        switchConnectedButton();
    }

    private void disconnectFromLeJOS() {
        try {
            myclient.close();
            connected = false;
        } catch (IOException e) {
            //error(e.getMessage());
        }

        switchConnectedButton();
    }
}
