package net.dries007.tfc;

import net.dries007.tfc.noise.NoiseTests;
import net.dries007.tfc.util.Debug;

/**
 * Safe entry point (as safe as it can be, really) that avoids classloading fails from using alternate entry points to test / draw things
 */
public class TestMain
{
    public static void main(String[] args)
    {
        // Must happen first
        Debug.DEBUG = true;

        // Then we can class load other things
        NoiseTests.main();
    }
}
