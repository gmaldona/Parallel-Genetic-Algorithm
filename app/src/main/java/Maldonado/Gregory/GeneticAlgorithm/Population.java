package Maldonado.Gregory.GeneticAlgorithm;

import Maldonado.Gregory.GeneticAlgorithm.GUI.FloorVisualization;
import Maldonado.Gregory.GeneticAlgorithm.Util.Constants;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Population {

    private static List<FactoryFloor> floors = new ArrayList<>();
    private static List<FactoryFloor> orderedFloors;
    private static status futureStatus = status.INPROGRESS;
    public static HashMap<Integer, FactoryFloor> hashedFloors = new HashMap<>();
    public static volatile boolean updatePause;

    public static volatile FactoryFloor topPerformingFloor ;

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
        topPercent = (int) Math.floor( 0.10 * orderedFloors.size() );

        FactoryFloor.population = this;
        List<Future> futures = floors.stream()
                .map(ex::submit)
                .collect(Collectors.toList());

        FloorVisualization GUI = new FloorVisualization();

        Thread updateThread = new Thread( ()-> {
            GUI.setFloor(floors.get(0));
            while (futureStatus == status.INPROGRESS) {
                try {
                    Thread.sleep(400);
                    updatePause = true;

                    updateTopList();
                    GUI.setFloor(topPerformingFloor);
                    GUI.repaint();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
            FactoryFloor bestPerformingFloor = null;

            for (FactoryFloor floor : orderedFloors) {
                if (bestPerformingFloor == null) { bestPerformingFloor = floor; continue; }
                if (floor.getFitnessScore() > bestPerformingFloor.getFitnessScore()) {
                    bestPerformingFloor = floor;
                }
            }
            if (bestPerformingFloor != null) topPerformingFloor = bestPerformingFloor;
        }
    }

    public static FactoryFloor getBestFloorDesign() { return topPerformingFloor; }

    private enum status {
        FINISHED, INPROGRESS
    }

}
