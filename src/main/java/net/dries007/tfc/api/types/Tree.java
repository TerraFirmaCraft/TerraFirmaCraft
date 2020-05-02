/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.util.ITreeGenerator;
import net.dries007.tfc.types.DefaultTrees;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class Tree extends IForgeRegistryEntry.Impl<Tree>
{
    @GameRegistry.ObjectHolder(MOD_ID + ":sequoia")
    public static final Tree SEQUOIA = Helpers.getNull();

    private final int maxGrowthRadius;
    private final float dominance;
    private final int maxHeight;
    private final int maxDecayDistance;
    private final boolean isConifer;
    private final ITreeGenerator bushGenerator;
    private final boolean canMakeTannin;
    private final float minGrowthTime;
    private final float minTemp;
    private final float maxTemp;
    private final float minRain;
    private final float maxRain;
    private final float minDensity;
    private final float maxDensity;
    private final float burnTemp;
    private final int burnTicks;

    /* This is open to be replaced, i.e. for dynamic trees */
    private ITreeGenerator generator;

    /**
     * This is a registry object that will create a number of things:
     * 1. Wood logs, planks, and leaf blocks, and all the respective variants
     * 2. A Tree object to be used in TFC world gen
     *
     * Addon mods that want to add trees should subscribe to the registry event for this class
     * They also must put (in their mod) the required resources in /assets/tfc/...
     *
     * When using this class, use the provided Builder to create your trees. This will require all the default values, as well as
     * provide optional values that you can change
     *
     * @param name             the ResourceLocation registry name of this tree
     * @param generator        the generator that should be called to generate this tree, both during world gen and when growing from a sapling
     * @param minTemp          min temperature
     * @param maxTemp          max temperature
     * @param minRain          min rainfall
     * @param maxRain          max rainfall
     * @param minDensity       min density. Use -1 to get all density values. 0.1 is the default, to create really low density regions of no trees
     * @param maxDensity       max density. Use 2 to get all density values
     * @param dominance        how much this tree is chosen over other trees. Range 0 <> 10 with 10 being the most common
     * @param maxGrowthRadius  used to check growth conditions
     * @param maxHeight        used to check growth conditions
     * @param maxDecayDistance maximum decay distance for leaves
     * @param isConifer        todo: make this do something
     * @param bushGenerator    a generator to make small bushes, null means the tree won't generate bushes
     * @param minGrowthTime    the amount of time (in in-game days) that this tree requires to grow
     * @param burnTemp         the temperature at which this will burn in a fire pit or similar
     * @param burnTicks        the number of ticks that this will burn in a fire pit or similar
     */
    public Tree(@Nonnull ResourceLocation name, @Nonnull ITreeGenerator generator, float minTemp, float maxTemp, float minRain, float maxRain, float minDensity, float maxDensity, float dominance, int maxGrowthRadius, int maxHeight, int maxDecayDistance, boolean isConifer, @Nullable ITreeGenerator bushGenerator, boolean canMakeTannin, float minGrowthTime, float burnTemp, int burnTicks)
    {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.minRain = minRain;
        this.maxRain = maxRain;
        this.dominance = dominance;
        this.maxGrowthRadius = maxGrowthRadius;
        this.maxHeight = maxHeight;
        this.maxDecayDistance = maxDecayDistance;
        this.isConifer = isConifer;
        this.minGrowthTime = minGrowthTime;
        this.minDensity = minDensity;
        this.maxDensity = maxDensity;
        this.bushGenerator = bushGenerator;
        this.canMakeTannin = canMakeTannin;
        this.burnTemp = burnTemp;
        this.burnTicks = burnTicks;

        this.generator = generator;
        setRegistryName(name);
    }

    public boolean makeTree(TemplateManager manager, World world, BlockPos pos, Random rand, boolean isWorldGen)
    {
        if (generator.canGenerateTree(world, pos, this))
        {
            generator.generateTree(manager, world, pos, this, rand, isWorldGen);
            return true;
        }
        return false;
    }

    public boolean makeTree(World world, BlockPos pos, Random rand, boolean isWorldGen)
    {
        if (!world.isRemote)
        {
            return makeTree(((WorldServer) world).getStructureTemplateManager(), world, pos, rand, isWorldGen);
        }
        return false;
    }

    public boolean isValidLocation(float temp, float rain, float density)
    {
        return minTemp <= temp && maxTemp >= temp && minRain <= rain && maxRain >= rain && minDensity <= density && maxDensity >= density;
    }

    @SuppressWarnings("unused")
    public void setTreeGenerator(ITreeGenerator generator)
    {
        this.generator = generator;
    }

    public int getMaxGrowthRadius()
    {
        return maxGrowthRadius;
    }

    public float getDominance()
    {
        return dominance;
    }

    public int getMaxHeight()
    {
        return maxHeight;
    }

    public int getMaxDecayDistance()
    {
        return maxDecayDistance;
    }

    public boolean isConifer()
    {
        return isConifer;
    }

    public boolean canMakeTannin()
    {
        return canMakeTannin;
    }

    public boolean hasBushes()
    {
        return bushGenerator != null;
    }

    @Nullable
    public ITreeGenerator getBushGen()
    {
        return bushGenerator;
    }

    public float getMinGrowthTime()
    {
        return minGrowthTime;
    }

    public float getBurnTemp()
    {
        return burnTemp;
    }

    public int getBurnTicks()
    {
        return burnTicks;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public String toString()
    {
        return getRegistryName().getPath();
    }

    public static class Builder
    {
        private final float minTemp;
        private final float maxTemp;
        private final float minRain;
        private final float maxRain;
        private final ITreeGenerator gen;
        private final ResourceLocation name;
        private float minDensity;
        private float maxDensity;
        private float dominance;
        private int maxHeight;
        private int maxGrowthRadius;
        private int maxDecayDistance;
        private boolean isConifer;
        private ITreeGenerator bushGenerator;
        private boolean canMakeTannin;
        private float minGrowthTime;
        private float burnTemp;
        private int burnTicks;

        public Builder(@Nonnull ResourceLocation name, float minRain, float maxRain, float minTemp, float maxTemp, @Nonnull ITreeGenerator gen)
        {
            this.minTemp = minTemp; // required values
            this.maxTemp = maxTemp;
            this.minRain = minRain;
            this.maxRain = maxRain;
            this.name = name;
            this.gen = gen;
            this.maxGrowthRadius = 1; // default values
            this.dominance = 0.001f * (maxTemp - minTemp) * (maxRain - minRain);
            this.maxHeight = 6;
            this.maxDecayDistance = 4;
            this.isConifer = false;
            this.bushGenerator = null;
            this.canMakeTannin = false;
            this.minGrowthTime = 7;
            this.minDensity = 0.1f;
            this.maxDensity = 2f;
            this.burnTemp = 675;
            this.burnTicks = 1500;
        }

        public Builder setRadius(int maxGrowthRadius)
        {
            this.maxGrowthRadius = maxGrowthRadius;
            return this;
        }

        public Builder setDecayDist(int maxDecayDistance)
        {
            this.maxDecayDistance = maxDecayDistance;
            return this;
        }

        public Builder setConifer()
        {
            isConifer = true;
            return this;
        }

        public Builder setBushes()
        {
            bushGenerator = DefaultTrees.GEN_BUSHES;
            return this;
        }

        public Builder setBushes(ITreeGenerator bushGenerator)
        {
            this.bushGenerator = bushGenerator;
            return this;
        }

        public Builder setTannin()
        {
            canMakeTannin = true;
            return this;
        }

        public Builder setHeight(int maxHeight)
        {
            this.maxHeight = maxHeight;
            return this;
        }

        public Builder setGrowthTime(float growthTime)
        {
            this.minGrowthTime = growthTime;
            return this;
        }

        public Builder setDensity(float min, float max)
        {
            this.minDensity = min;
            this.maxDensity = max;
            return this;
        }

        public Builder setDominance(float dom)
        {
            this.dominance = dom;
            return this;
        }

        public Builder setBurnInfo(float burnTemp, int burnTicks)
        {
            this.burnTemp = burnTemp;
            this.burnTicks = burnTicks;
            return this;
        }

        public Tree build()
        {
            return new Tree(name, gen, minTemp, maxTemp, minRain, maxRain, minDensity, maxDensity, dominance, maxGrowthRadius, maxHeight, maxDecayDistance, isConifer, bushGenerator, canMakeTannin, minGrowthTime, burnTemp, burnTicks);
        }
    }
}
