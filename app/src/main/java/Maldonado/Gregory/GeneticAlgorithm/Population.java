package Maldonado.Gregory.GeneticAlgorithm;

import Maldonado.Gregory.GeneticAlgorithm.GUI.StationVisualization;
import Maldonado.Gregory.GeneticAlgorithm.Util.Constants;
import com.google.common.util.concurrent.AtomicDouble;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Population {

    private static List<FactoryFloor> floors = new ArrayList<>();
    private static List<FactoryFloor> orderedFloors;
    private static status futureStatus = status.INPROGRESS;
    public static HashMap<Integer, FactoryFloor> hashedFloors = new HashMap<>();
    public static volatile boolean updatePause;

    static int topPercent;

    public void run() {
        ExecutorService ex = Executors.newWorkStealingPool();
        for (int i = 0; i < Constants.MAXIMUM_FLOORS; i++) {
            floors.add(new FactoryFloor());
            hashedFloors.put(floors.get(i).hashCode(), floors.get(i));
            System.out.println(floors.get(i));
        }

        System.out.println();
        orderedFloors = new ArrayList<>(floors);
        //updateTopList();
        topPercent = (int) Math.floor( 0.10 * orderedFloors.size() );


        FactoryFloor.population = this;
        List<Future> futures = floors.stream()
                .map(ex::submit)
                .collect(Collectors.toList());

        Thread updateThread = new Thread( ()-> {
            while (futureStatus == status.INPROGRESS) {
                System.out.println(updatePause);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updatePause = true;
                try {
                    updateTopList();
                } catch (Exception e) {}
                updatePause = false;
            }
        });

        new Thread( () -> {
            if (!updateThread.isAlive()) {
                updatePause = false;
            }
        }).start();

        updateThread.start();

        while (futureStatus == status.INPROGRESS ) {
            futureStatus = (int) futures.stream()
                    .filter(Future::isDone).count() == 0 ? status.INPROGRESS : status.FINISHED;
        }


        floors.forEach(System.out::println);
    }


    static class FactoryFloorCompare implements Comparator<FactoryFloor> {

        @Override
        public int compare(FactoryFloor factoryFloor, FactoryFloor otherFactoryFloor) {
            return (factoryFloor != null && otherFactoryFloor != null) ? Double.compare(factoryFloor.getFitnessScore(), otherFactoryFloor.getFitnessScore()) : 0;
        }
    }

    public List<FactoryFloor> getFactoryFloors() { return floors; }

    public List<FactoryFloor> getOrderedFloors() { return orderedFloors; }

    public HashMap<Integer, FactoryFloor> getHashedFloors() { return hashedFloors; }

    public static void updateTopList() {
        if (futureStatus == status.INPROGRESS) {
            orderedFloors.sort(new FactoryFloorCompare());
            Collections.reverse(orderedFloors);
            //orderedFloors.stream()
                    //.forEach(System.out::println);
            System.out.println("Gregory Maldonado " + orderedFloors.get(0).getStations().size());
        }
    }

    private enum status {
        FINISHED, INPROGRESS
    }

}
