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
public class Main extends Application  implements IMoveController{
    //private static final int SCALE_FACTOR = 1;
    public static final int CANVAS_WITDH = 1200;
    public static final int CANVAS_HEIGHT = 300;

    public static final int SVG_MAX_WIDTH = 600;
    public static final int SVG_MAX_HEIGHT = 150;

    Map m = new Map(SVG_MAX_WIDTH, SVG_MAX_HEIGHT);

    GraphicsContext gc;
    Canvas canvas;

    TextField tfHost;
    TextField tfPort;

    Button bConnect;

    LeJOSClient myclient;
    private Boolean connected = false;

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
        canvas = new Canvas(CANVAS_WITDH, CANVAS_HEIGHT);
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
        gc.clearRect(0,0,CANVAS_WITDH,CANVAS_HEIGHT);
        drawMap();
    }
    private void drawMap() {

        gc.setFill(Color.BLACK);
        gc.setStroke(Color.DARKRED);
        gc.setLineWidth(2);
        gc.setLineDashes(0);

        for ( Line l : m.getLines()){
            gc.strokeLine(Helper.absMapX(l.x1) , Helper.absMapY(l.y1) , Helper.absMapX(l.x2 ), Helper.absMapY(l.y2));
        }

        gc.setLineDashes(10);
        for ( Particle particle : m.getParticles()) {
            Point absCenter = Helper.absMapPoint(particle.centerPoint);
            Point lineA = Helper.getRotationPoint(Helper.absMapPoint(particle.centerPoint),0.005,particle.rotation);
            Point lineB = Helper.getRotationPoint(Helper.absMapPoint(particle.centerPoint),0.005,particle.rotation + Math.PI);

            gc.strokeLine(lineA.x, lineA.y, lineB.x,lineB.y);
            gc.fillOval(absCenter.x-3, absCenter.y-3, 6, 6);
            gc.fillOval( lineA.x-2 , lineB.y-2 , 4,4 );
            gc.strokeLine(lineB.x,lineB.y , Helper.absMapPoint(particle.intersectPoint.relPoint).x,Helper.absMapPoint(particle.intersectPoint.relPoint).y);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void moveForward(int cm) {
        for ( Particle particle : m.getParticles()){
            particle.moveForward(cm);
            particle.calculateIntersect(m.getLines());

        }
    }

    @Override
    public void moveBackward(int cm) {
        for ( Particle particle : m.getParticles()){
            particle.moveBackward(cm);
            particle.calculateIntersect(m.getLines());
        }
    }

    @Override
    public void turnLeft(double angle) {
        for ( Particle particle : m.getParticles()){
            particle.turnLeft((int)angle);
            particle.calculateIntersect(m.getLines());
        }
    }

    @Override
    public void turnRight(double angle) {
        for ( Particle particle : m.getParticles()){
            particle.turnRight((int)angle);
            particle.calculateIntersect(m.getLines());
        }
    }

    @Override
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
