/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.trees;

// This is the thing that will be registered
public class Tree
{
    // Properties to be accessed by world gen
    public final float minTemp;
    public final float maxTemp;
    public final float minRain;
    public final float maxRain;
    public final float minEVT;
    public final float maxEVT;

    // Used when growing a tree
    public final ITreeGenerator gen;
    public final int maxGrowthRadius;

    /**
     * This is the object that should be registered via the custom registry event
     * It needs to have a string name, which is used similar to how the enum is used.
     * It also needs to have an int index, which will be used by the worldgen to save and load this value
     *
     * Addon mods that want to add trees should subscribe to the registry event for this class
     * They also must put (in their mod) the required resources in /assets/tfc/...
     * That way there is no need for custom logic during tree generation
     *
     * @param minTemp min temperature
     * @param maxTemp max temperature
     * @param minRain min rainfall
     * @param maxRain max rainfall
     * @param minEVT  min EVT
     * @param maxEVT  max EVT
     * @param gen     the generator that should be called to generate this tree, both during world gen and when growing from a sapling
     */
    public Tree(float minTemp, float maxTemp, float minRain, float maxRain, float minEVT, float maxEVT, ITreeGenerator gen, int maxGrowthRadius)
    {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minRain = minRain;
        this.maxRain = maxRain;
        this.minEVT = minEVT;
        this.maxEVT = maxEVT;

        this.gen = gen;
        this.maxGrowthRadius = maxGrowthRadius;

    }
}
