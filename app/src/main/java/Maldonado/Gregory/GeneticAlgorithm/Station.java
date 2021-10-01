package Maldonado.Gregory.GeneticAlgorithm;

import Maldonado.Gregory.GeneticAlgorithm.Util.Constants;
import com.google.common.util.concurrent.AtomicDouble;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Station {

    private FactoryFloor factoryFloor;

    private int flavor;
    private int x, y;

    public Station(FactoryFloor factoryFloor) {
        this.factoryFloor = factoryFloor;
        boolean availableLocation;

        Random rd = new Random();
        do {
            this.x = rd.nextInt(Constants.FACTORY_FLOOR_SIZE);
            this.y = rd.nextInt(Constants.FACTORY_FLOOR_SIZE);

            availableLocation = checkFloorAvailability(x, y);
        } while (!availableLocation);
        factoryFloor.getFloor()[x][y] = this;
        this.flavor = rd.nextInt(Constants.STATION_FLAVORS) + 1;

    }

    public Station(int x, int y, int flavor) {
        this.x = x;
        this.y = y;
        this.flavor = flavor;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getFlavor() { return flavor; }

    public void setFactoryFloor(FactoryFloor factoryFloor) { this.factoryFloor = factoryFloor; }

    public void setX(int x) {
        if (checkFloorAvailability(x, this.y))
            this.x = x;
    }
    public void setY(int y) {
        if (checkFloorAvailability(this.x, y))
            this.y = y;
    }
    public void setF(int f) { this.flavor = f; }

    public void setXYF(int x, int y, int f) { setX(x); setY(y); setF(f); }

    public String toString() {
        return "(" + x + "," + y + ")" + "\t" + flavor;
    }

    private boolean checkFloorAvailability(int x, int y) { return factoryFloor.getFloor()[x][y] == null; }

    private double getDistance(Station otherStation) {
        return Math.sqrt( (this.x - otherStation.x) * (this.x - otherStation.x) + (this.y - otherStation.y) * (this.y - otherStation.y) );
    }

    private int getFlavorDifference(Station otherStation) {
        return Math.abs( this.flavor - otherStation.flavor );
    }

    public double getStationsScore(Station otherStation) {
        int flavorDifference = getFlavorDifference(otherStation);
        int HalfOfFlavors = (int) Math.floor(Constants.STATION_FLAVORS);

        if (flavorDifference < HalfOfFlavors) {
            return Math.cos( getDistance(otherStation) / (Constants.FACTORY_FLOOR_SIZE * Math.sqrt(2)) );
        }

        return Math.sin( getDistance(otherStation) / (Constants.FACTORY_FLOOR_SIZE * Math.sqrt(2)) );
    }

}
