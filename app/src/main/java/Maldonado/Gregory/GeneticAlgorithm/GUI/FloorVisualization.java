package Maldonado.Gregory.GeneticAlgorithm.GUI;

import Maldonado.Gregory.GeneticAlgorithm.FactoryFloor;
import Maldonado.Gregory.GeneticAlgorithm.Util.Constants;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class FloorVisualization extends JFrame {

    private volatile FactoryFloor floor;
    private int spacing;
    private final int PADDING = 150;
    Graphics2D g2d;

    public FloorVisualization() {
        this.spacing = (Constants.WINDOW_SIZE - PADDING) / ( Constants.FACTORY_FLOOR_SIZE );


        setSize(Constants.WINDOW_SIZE, Constants.WINDOW_SIZE);
        setTitle("Best Floor Design");
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        this.g2d = (Graphics2D) g;
        try {
            if (floor != null) {

                System.out.println(floor + " \t " + floor.getStations().size());
                g2d.setColor(Color.GREEN);

                floor.getStations().forEach((station) -> {
                    if (station != null) {
                        g2d.setColor(Constants.colors[station.getFlavor()]);
                        g2d.fillRect((station.getX() * spacing) + PADDING / 2, (station.getY() * spacing) + PADDING / 2, spacing, spacing);
                        g2d.setColor(Color.BLACK);
                        g2d.drawString(
                                "(" + station.getX() + ", " + station.getY() + ")",
                                (station.getX() * spacing + (spacing / 2)) + PADDING / 2 - 15,
                                (station.getY() * spacing + (spacing / 2)) + PADDING / 2
                        );
                    }
                });
                g2d.drawString(
                        "Generation: " + floor.getCurrentGeneration(),
                        50,
                        Constants.WINDOW_SIZE - 50
                );
                DecimalFormat df = new DecimalFormat("###.######");
                g2d.drawString("Fitness Score: " + df.format(floor.getFitnessScore()),
                        Constants.WINDOW_SIZE - 200,
                        Constants.WINDOW_SIZE - 50
                );
            }
        } catch (Exception e) {e.printStackTrace();}
        repaint();
    }

    @Override
    public void repaint() {
        super.repaint();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setBackground(Color.WHITE);
    }

    public void setFloor(FactoryFloor floor) { this.floor = floor; }

}
