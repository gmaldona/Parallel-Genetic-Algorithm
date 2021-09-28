package Maldonado.Gregory.GeneticAlgorithm;

import Maldonado.Gregory.GeneticAlgorithm.Util.Constants;
import com.google.common.util.concurrent.AtomicDouble;

import java.util.*;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FactoryFloor implements Runnable {

    public static Population population;
    private List<Station> stations = new ArrayList<>();
    private final Station[][] floor = new Station[Constants.FACTORY_FLOOR_SIZE][Constants.FACTORY_FLOOR_SIZE];
    private final HashMap<Integer, Station> hashedStations = new HashMap<>();
    private double fitnessScore;
    private final static Exchanger<Object[]> exchanger = new Exchanger<>();

    public FactoryFloor() {
        setup(Constants.MAXIMUM_STATIONS);
    }

    public void setup(int Stations) {

        for (int i = 0; i < Constants.FACTORY_FLOOR_SIZE; i++) {
            for (int j = 0; j < Constants.FACTORY_FLOOR_SIZE; j++) {
                floor[i][j] = null;
            }
        }

        for (int i = 0; i < Stations; i++ ) { stations.add(new Station(this)); }

        stations.forEach( (station -> {
            hashedStations.put(station.hashCode(), station) ;
        }));

    }

    @Override
    public void run() {

        int currentGeneration = 0;

        while (currentGeneration < Constants.MAXIMUM_GENERATIONS) {

            //System.out.println(this.toString());

            if (currentGeneration == 1 || currentGeneration == Constants.MAXIMUM_GENERATIONS - 1) {
                System.out.println("Generation " + currentGeneration + " " +  this.toString());
            }

            // Mutations for X Times: probability of randomizing the X coordinate of a station, the Y coordinate, the flavor, all 3, or none
            for (int M = 0; M < Constants.MAXIMUM_MUTATIONS; M++) {

                mutation();
            }

            // Compute Fitness
            fitnessScore = this.getFitnessScore();

            Object[] sentChunk = this.getChunk();
            Object[] receivedChunk = null;

            try {
                receivedChunk = exchanger.exchange(sentChunk, 3000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | TimeoutException e) {
                e.printStackTrace();
            }

            if (receivedChunk != null) {
                int swapHashCode = (int) receivedChunk[0];
                Station[][] chunk = (Station[][]) receivedChunk[1];

                FactoryFloor receivingFloor = population.getHashedFloors().get(swapHashCode);
                int fitnessScoreIndex = population.getOrderedFloors().indexOf(receivingFloor);

                if (fitnessScoreIndex <= Population.topPercent) {
                    if (chunk != null) {
                        deleteChunk(chunk);
                        updateChunk(chunk);
                    }
                }
            }


            currentGeneration++;
        }

    }

    public Station[][] getFloor() { return floor; }

    public HashMap<Integer, Station> getHashedStations() { return hashedStations; }

    public List<Station> getStations() { return stations; }

    public double getFitnessScore() {
        HashMap<Long, Double> scores = new HashMap<>();

        AtomicDouble totalScore = new AtomicDouble(0);

        for (Station station_N : stations) {
            for (Station station_M : stations) {

                if (station_N.hashCode() == station_M.hashCode())
                    continue;

                long hashCode = station_N.hashCode() * station_M.hashCode();

                if (! scores.containsKey(hashCode)) {
                    scores.put(
                            hashCode,
                            station_N.getStationsScore(station_M)
                    );
                }
            }
        }
        scores.forEach( (hashcode, score) -> totalScore.getAndAdd(score) );

        return totalScore.get() / scores.size();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Station station : stations) {
            sb.append(station.toString()).append("\t");
        }

        sb.append("\n Factory Floor ").append(hashCode()).append("\t Fitness Score: ").append(this.fitnessScore);
        return sb.toString();
    }

    private Object[] getChunk() {
        float chunkChance = new Random().nextFloat();


        int chunkSize = (int)( 0.50 * Constants.FACTORY_FLOOR_SIZE);

        Station[][] chunk = new Station[Constants.FACTORY_FLOOR_SIZE][Constants.FACTORY_FLOOR_SIZE];

        // Top left chunk
        if (chunkChance <= 0.25f) {
            for (int i = 0; i < chunkSize; i++) {
                 for (int j = 0; j < chunkSize; j++) {
                     chunk[i][j] = floor[i][j];
                 }
            }
        }
        // top right chunk
        else if (chunkChance > 0.25f && chunkChance <= 0.5f) {
            for (int i = chunkSize; i < Constants.FACTORY_FLOOR_SIZE; i++) {
                for (int j = 0; j < chunkSize; j++) {
                    chunk[i][j] = floor[i][j];
                }
            }
        }
        // bottom left
        else if (chunkChance > 0.5f && chunkChance <= 0.75f) {
            for (int i = 0; i < chunkSize; i++) {
                for (int j = chunkSize; j < Constants.FACTORY_FLOOR_SIZE; j++) {
                    chunk[i][j] = floor[i][j];
                }
            }
        }
        // botom right
        else if(chunkChance > 0.75f && chunkChance <= 1.0f) {
            for (int i = chunkSize; i < Constants.FACTORY_FLOOR_SIZE; i++) {
                for (int j = chunkSize; j < Constants.FACTORY_FLOOR_SIZE; j++) {
                    chunk[i][j] = floor[i][j];
                }
            }
        }
        return new Object[]{this.hashCode(), chunk} ;
    }

    public void mutation() {
        float chanceOfMutation = new Random().nextFloat();


        if (chanceOfMutation <= Constants.P_NONE)
            return;


        int stationIndex = new Random().nextInt(stations.size());
        Station station = stations.get(stationIndex);

        if (chanceOfMutation > Constants.P_NONE && chanceOfMutation <= Constants.P_NONE + Constants.P_X)
            station.setX(new Random().nextInt(Constants.FACTORY_FLOOR_SIZE));

        else if (chanceOfMutation > Constants.P_NONE + Constants.P_X && chanceOfMutation <= Constants.P_NONE + 2 * Constants.P_Y)
            station.setY(new Random().nextInt(Constants.FACTORY_FLOOR_SIZE));

        else if (chanceOfMutation > Constants.P_NONE + 2 * Constants.P_Y && chanceOfMutation <= Constants.P_NONE + 3 * Constants.P_F)
            station.setF(new Random().nextInt(Constants.STATION_FLAVORS));

        else if (chanceOfMutation > Constants.P_NONE + 3 * Constants.P_F && chanceOfMutation <= Constants.P_NONE + (3 * Constants.P_F)  + Constants.P_ALL) {
            Random rd = new Random();
            station.setXYF(rd.nextInt(Constants.FACTORY_FLOOR_SIZE), rd.nextInt(Constants.FACTORY_FLOOR_SIZE), rd.nextInt(Constants.STATION_FLAVORS));
        }
    }

    private void updateChunk(Station[][] chunk) {
        for (int i = 0; i < Constants.FACTORY_FLOOR_SIZE; i++) {
            for (int j = 0; j < Constants.FACTORY_FLOOR_SIZE; j++) {
                if (chunk[i][j] != null)
                    floor[i][j] = chunk[i][j];
            }
        }
    }

    private void deleteChunk(Station[][] chunk) {
        for (int i = 0; i < Constants.FACTORY_FLOOR_SIZE; i++) {
            for (int j = 0; j < Constants.FACTORY_FLOOR_SIZE; j++) {
                if (chunk[i][j] == null) continue;
                floor[i][j] = null;
            }
        }
    }
}
