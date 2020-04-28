package net.dries007.tfc.api;

import java.util.Arrays;
import java.util.List;

public class Tree {

    public enum Default {
        ACACIA,
        ASH,
        ASPEN,
        BIRCH,
        BLACKWOOD,
        CHESTNUT,
        DOUGLAS_FIR,
        HICKORY,
        KAPOK,
        MAPLE,
        OAK,
        PALM,
        PINE,
        ROSEWOOD,
        SEQUOIA,
        SPRUCE,
        SYCAMORE,
        WHITE_CEDAR,
        WILLOW;

        public static List<Default> NO_TINT = Arrays.asList(ASH, DOUGLAS_FIR, HICKORY, SPRUCE, SYCAMORE);
    }
}
