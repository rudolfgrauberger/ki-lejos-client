package client.localization;

import client.montecarlo.IParticleGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Handler;

public class Map implements IParticleGenerator {

    public static final int PARTICLE_COUNT = 25;


    private ArrayList<Line> lines = new ArrayList<Line>();
    private ArrayList<Point> polygon = new ArrayList<Point>();
    private List<Particle> particles = new ArrayList<Particle>();

    private int x, y;

    public Map(int x, int y) {
        this.x = x;
        this.y = y;

        initWall();
        initParticles();
        System.out.println("Initialized Particles");
    }

    public void addLine(Line l) {
        lines.add(l);
    }

    public void addPoint(Point p) {
        polygon.add(p);
    }

    public void addParticle(Particle p) {
        particles.add(p);
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public void setParticles(List<Particle> particles) {
        this.particles = particles;
    }

    public ArrayList<Point> getPolygon() {
        return polygon;
    }

    public int getPolygonPointCount() {
        return polygon.size();
    }

    public boolean checkPointInsidePolygon(Point p) {
        int i, j, vertNum;
        boolean c = false;
        vertNum = polygon.size();
        double testx = p.x;
        double testy = p.y;

        for (i = 0, j = vertNum - 1; i < vertNum; j = i++) {
            if (((polygon.get(i).y > p.y) != (polygon.get(j).y > p.y))
                    && (testx < (polygon.get(j).x - polygon.get(i).x) * (testy - polygon.get(i).y) / (polygon.get(j).y - polygon.get(i).y) + polygon.get(i).x)) {
                c = !c;
            }
        }

        return c;
    }

    private void initWall() {
        //ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        File map = new File("map.svg");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        Document doc;
        Random rand = new Random();

        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(map);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("line");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node n = nList.item(temp);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    NamedNodeMap nnm = n.getAttributes();
                    int x1 = Integer.parseInt(nnm.getNamedItem("x1").getNodeValue().replace("px", ""));
                    int y1 = Integer.parseInt(nnm.getNamedItem("y1").getNodeValue().replace("px", ""));
                    int x2 = Integer.parseInt(nnm.getNamedItem("x2").getNodeValue().replace("px", ""));
                    int y2 = Integer.parseInt(nnm.getNamedItem("y2").getNodeValue().replace("px", ""));
                    addLine(new Line(x1, y1, x2, y2));
                    addPoint(new Point(x1, y1));
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    Point getPointInPolygon() {

        Random rand = new Random();
        while (true) {
            Point p = new Point(rand.nextInt(Helper.BUILDING_WIDTH_CM), rand.nextInt(Helper.BUILDING_HEIGHT_CM));//rand.nextInt(150));
            if (checkPointInsidePolygon(p)) {
                return p;
            }
        }
    }

    private void initParticles() {
        Random rand = new Random();
        for (int i = 0; i < PARTICLE_COUNT; ++i) {
            Particle particle = ParticleFactory.createNewParticle(this);
            particles.add(particle);
        }

    }

    public Particle getRandomParticle() {
        return ParticleFactory.createNewParticle(this);
    }

}
