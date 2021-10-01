package Maldonado.Gregory.GeneticAlgorithm.Util;

import java.awt.*;

public class Constants {

    public static final int MAXIMUM_FLOORS      = 10;
    public static final int MAXIMUM_STATIONS    = 32;
    public static final int FACTORY_FLOOR_SIZE  = 10;
    public static final int STATION_FLAVORS     = 10;

    public static final int WINDOW_SIZE         = 800;

    public static final float MAXIMUM_MUTATIONS = 20;
    public static final int MAXIMUM_GENERATIONS = 100;

    public static final float    P_X = 0.1525f;
    public static final float    P_Y = 0.1525f;
    public static final float    P_F = 0.1525f;
    public static final float  P_ALL = 0.0425f;
    public static final float P_NONE = 0.5f;

    public static final Color[] colors = new Color[]{
            Color.BLACK, Color.BLUE, Color.PINK, Color.RED, Color.CYAN,
            Color.ORANGE, Color.YELLOW, Color.MAGENTA, Color.DARK_GRAY, Color.LIGHT_GRAY, Color.GREEN
    };
}
