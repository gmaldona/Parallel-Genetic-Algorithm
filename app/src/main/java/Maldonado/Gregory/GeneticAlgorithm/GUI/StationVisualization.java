package Maldonado.Gregory.GeneticAlgorithm.GUI;

import Maldonado.Gregory.GeneticAlgorithm.FactoryFloor;
import Maldonado.Gregory.GeneticAlgorithm.Util.Constants;

import javax.swing.*;
import java.awt.*;

public class StationVisualization extends JFrame {

    private FactoryFloor floor;
    private int spacing;

    public StationVisualization(FactoryFloor floor, int ID) {
        this.floor = floor;
        this.spacing = Constants.WINDOW_SIZE / ( Constants.FACTORY_FLOOR_SIZE + 10);

        setSize(Constants.WINDOW_SIZE, Constants.WINDOW_SIZE);
        setTitle("Factory Floor #" + ID);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        floor.getStations().forEach( (station) -> {
            g2d.setColor(Constants.colors[station.getFlavor() - 1]);
            g2d.fillOval(
                    station.getX() * spacing, station.getY() * spacing,
                    Constants.DRAWING_SIZE, Constants.DRAWING_SIZE
            );
        });
    }

}
