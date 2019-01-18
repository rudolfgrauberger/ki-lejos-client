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
import java.util.List;


/**
 * SAMPLE -->
 * <p>
 * <p>
 * <p>
 * Ablauf:
 * 1. Initialer Belief
 * k Samples auf dem Bildschirm erstellen, die Gleichverteilt auf allen Möglichen Positionen verteilt sind
 * Der importance factor ist 1/k.
 * Ein Partikel ( Sample ) repräsentiert eine Position
 * <p>
 * Ziel: Partikel ( Samples ) zu generieren, welche am ( Hochpunkt ) der Wahrscheinlichkeitsdichte liegen
 * <p>
 * 2. Belief aktualisieren
 * Ein Sample wird zufällig aus der Menge genommen
 * Der importance factor gibt die Auswahlwahrscheinlichkeit.
 */
public class Main extends Application {

    public static int SCALE_FACTOR = 2;
    public static boolean ANALYSE_MODE = true;
    public static boolean SIMULATE_MODE = true;
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

    private VBox getMainLayout() {
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
        bConnect.setDisable(SIMULATE_MODE);
        inputs.getChildren().add(bConnect);

        bLocate = new Button("Locate");
        bLocate.setOnAction(event -> {
            try {
                ArrayList<IMoveController> movables = new ArrayList<>();
                for (Particle p : m.getParticles()) {
                    IMoveController m = p;
                    movables.add(m);
                }
                List<IMoveController> resampledParticles = monte.run(movables);

                List<Particle> particles = new ArrayList<Particle>();
                for (IMoveController c : resampledParticles) {
                    particles.add((Particle)c);
                }
                m.setParticles(particles);
                reDraw();
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
    public void stop() {
        if (myclient.isConnected())
            disconnectFromLeJOS();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("LeJOS - Client (Team: D_GELB)");
        Group root = new Group();

        myclient = new LeJOSClient(logger);

        canvas = new Canvas(Helper.BUILDING_WIDTH_CM * SCALE_FACTOR, Helper.BUILDING_HEIGHT_CM * SCALE_FACTOR);
        canvas.setFocusTraversable(true);
        canvas.addEventFilter(MouseEvent.MOUSE_PRESSED, (e) -> canvas.requestFocus());
        gc = canvas.getGraphicsContext2D();
        drawMap();

        // Simulation (damit man alles ohne den Robotor testen kann)
        if (SIMULATE_MODE) {
            Particle robot = Particle.createParticle(this.m);
            monte = new MonteCarloAlgorithmen(robot, m);
        } else {
            monte = new MonteCarloAlgorithmen(myclient, m);
        }

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
            if (w.equals(event.getCode())) {
                moveForward(5);
            }
            if (s.equals(event.getCode())) {
                moveBackward(5);
            }
            if (a.equals(event.getCode())) {
                turnLeft(90);
            }
            if (d.equals(event.getCode())) {
                turnRight(90);
            }

            reDraw();
        });
    }

    private void reDraw() {
        gc.clearRect(0, 0, Helper.BUILDING_WIDTH_CM * SCALE_FACTOR, Helper.BUILDING_HEIGHT_CM * SCALE_FACTOR);
        drawMap();
    }

    private void drawMap() {

        gc.setFill(Color.BLACK);
        gc.setStroke(Color.DARKRED);
        gc.setLineWidth(2);
        gc.setLineDashes(0);

        for (Line l : m.getLines()) {
            gc.strokeLine(l.x1 * SCALE_FACTOR, l.y1 * SCALE_FACTOR, l.x2 * SCALE_FACTOR, l.y2 * SCALE_FACTOR);
        }

        gc.setLineDashes(10);
        for (Particle particle : m.getParticles()) {
            Point absCenter = particle.centerPoint;
            Point lineA = Helper.getRotationPoint(particle.centerPoint, 5, particle.currentRotation);
            Point lineB = Helper.getRotationPoint(particle.centerPoint, 5, particle.currentRotation + Math.PI);
            //System.out.println("Rotation: " + particle.currentRotation);
            double maxBeliefSize = 10;

            gc.fillOval(absCenter.x * SCALE_FACTOR - ((particle.belief * maxBeliefSize)/2.0), absCenter.y * SCALE_FACTOR - ((maxBeliefSize*particle.belief)/2.0), maxBeliefSize*particle.belief, maxBeliefSize*particle.belief);
            gc.fillOval(lineA.x * SCALE_FACTOR - 2, lineA.y * SCALE_FACTOR - 2, 4, 4);

            if (ANALYSE_MODE) {
                gc.strokeLine(lineA.x * SCALE_FACTOR, lineA.y * SCALE_FACTOR, lineB.x * SCALE_FACTOR, lineB.y * SCALE_FACTOR);
                gc.strokeLine(absCenter.x * SCALE_FACTOR, absCenter.y * SCALE_FACTOR, particle.forwardIntersect.point.x * SCALE_FACTOR, particle.forwardIntersect.point.y * SCALE_FACTOR);
                gc.strokeLine(absCenter.x * SCALE_FACTOR, absCenter.y * SCALE_FACTOR, particle.leftIntersect.point.x * SCALE_FACTOR, particle.leftIntersect.point.y * SCALE_FACTOR);
                gc.strokeLine(absCenter.x * SCALE_FACTOR, absCenter.y * SCALE_FACTOR, particle.rightIntersect.point.x * SCALE_FACTOR, particle.rightIntersect.point.y * SCALE_FACTOR);
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }


    public void moveForward(double cm) {
        for (Particle particle : m.getParticles()) {
            try {
                particle.moveForward(cm);
            } catch (ActionException e) {
                e.printStackTrace();
            }
        }
    }

    public void moveBackward(double cm) {
        for (Particle particle : m.getParticles()) {
            try {
                particle.moveBackward(cm);
            } catch (ActionException e) {
                e.printStackTrace();
            }
        }
    }


    public void turnLeft(double angle) {
        for (Particle particle : m.getParticles()) {
            particle.turnLeft(angle);
        }
    }

    public void turnRight(double angle) {
        for (Particle particle : m.getParticles()) {
            particle.turnRight(angle);
        }
    }


    public SensorDataSet getSensorDataSet() throws ActionException {
        return null;
    }

    private void switchConnectedButton() {

        if (SIMULATE_MODE)
            bLocate.setDisable(false);
        else
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
