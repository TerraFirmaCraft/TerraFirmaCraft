/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.api.types;

import java.util.Random;
import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.ITreeGenerator;

public class Tree extends IForgeRegistryEntry.Impl<Tree>
{

    public final float minTemp;
    public final float maxTemp;
    public final float minRain;
    public final float maxRain;
    public final float minEVT;
    public final float maxEVT;

    /**
     * The path part of the resource location, used for assigning block names
     */
    public final String name;
    public final int maxGrowthRadius;
    // Used when growing a tree
    private final ITreeGenerator gen;

    /**
     * This is a registry object that will create a number of things:
     * 1. Wood logs, planks, and leaf blocks, and all the respective variants
     * 2. A Tree object to be used in TFC world gen
     *
     * Addon mods that want to add trees should subscribe to the registry event for this class
     * They also must put (in their mod) the required resources in /assets/tfc/...
     *
     * @param minTemp min temperature
     * @param maxTemp max temperature
     * @param minRain min rainfall
     * @param maxRain max rainfall
     * @param minEVT  min EVT
     * @param maxEVT  max EVT
     * @param gen     the generator that should be called to generate this tree, both during world gen and when growing from a sapling
     */
    public Tree(@Nonnull ResourceLocation name, float minTemp, float maxTemp, float minRain, float maxRain, float minEVT, float maxEVT, int maxGrowthRadius, @Nonnull ITreeGenerator gen)
    {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minRain = minRain;
        this.maxRain = maxRain;
        this.minEVT = minEVT;
        this.maxEVT = maxEVT;

        this.gen = gen;
        this.maxGrowthRadius = maxGrowthRadius;

        this.name = name.getResourcePath().toLowerCase();
        setRegistryName(name);
    }

    public void makeTree(TemplateManager manager, World world, BlockPos pos, Random rand)
    {
        this.gen.generateTree(manager, world, pos, this, rand);
    }

    public void makeTree(World world, BlockPos pos, Random rand)
    {
        if (world.isRemote) return;
        final TemplateManager manager = ((WorldServer) world).getStructureTemplateManager();
        this.gen.generateTree(manager, world, pos, this, rand);
    }
}
