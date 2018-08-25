/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import java.util.function.BiFunction;
import javax.annotation.Nonnull;

import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.objects.blocks.stone.*;

import static net.dries007.tfc.api.types.Rock.FallingBlockType.*;

/**
 * todo: document API
 */
public class Rock extends IForgeRegistryEntry.Impl<Rock>
{
    private final RockCategory rockCategory;

    public Rock(@Nonnull ResourceLocation name, @Nonnull RockCategory rockCategory)
    {
        setRegistryName(name);
        this.rockCategory = rockCategory;
        //noinspection ConstantConditions
        if (rockCategory == null)
            throw new IllegalArgumentException("Rock category is not allowed to be null (on rock " + name + ")");
    }

    public Rock(@Nonnull ResourceLocation name, @Nonnull ResourceLocation categoryName)
    {
        //noinspection ConstantConditions
        this(name, TFCRegistries.ROCK_CATEGORIES.getValue(categoryName));
    }

    public RockCategory getRockCategory()
    {
        return rockCategory;
    }

    public enum Type
    {
        RAW(Material.ROCK, NO_FALL, false, BlockRockRaw::new), // Todo: add collapsing when broken
        SMOOTH(Material.ROCK, NO_FALL, false),
        COBBLE(Material.ROCK, FALL_HORIZONTAL, false),
        BRICKS(Material.ROCK, NO_FALL, false),
        SAND(Material.SAND, FALL_HORIZONTAL, false),
        GRAVEL(Material.SAND, FALL_HORIZONTAL, false),
        DIRT(Material.GROUND, FALL_HORIZONTAL, false),
        GRASS(Material.GRASS, FALL_HORIZONTAL, true),
        DRY_GRASS(Material.GRASS, FALL_HORIZONTAL, true),
        CLAY(Material.GRASS, FALL_VERTICAL, false),
        CLAY_GRASS(Material.GRASS, FALL_VERTICAL, true),
        FARMLAND(Material.GROUND, FALL_VERTICAL, false, BlockFarmlandTFC::new),
        PATH(Material.GROUND, FALL_VERTICAL, false, BlockPathTFC::new);

        public final Material material;
        public final boolean isGrass;

        private final FallingBlockType gravType;
        private final BiFunction<Type, Rock, BlockRockVariant> supplier;

        Type(Material material, FallingBlockType gravType, boolean isGrass)
        {
            // If no fall + no grass, then normal. If it can fall, then eiether fallable or fallable + connected (since grass always falls)
            this(material, gravType, isGrass, (gravType == NO_FALL && !isGrass) ? BlockRockVariant::new :
                (isGrass ? BlockRockVariantConnected::new : BlockRockVariantFallable::new));
        }

        Type(Material material, FallingBlockType gravType, boolean isGrass, BiFunction<Type, Rock, BlockRockVariant> supplier)
        {
            this.material = material;
            this.gravType = gravType;
            this.isGrass = isGrass;
            this.supplier = supplier;
        }

        public boolean canFall()
        {
            return gravType != NO_FALL;
        }

        public boolean canFallHorizontal()
        {
            return gravType == FALL_HORIZONTAL;
        }

        public BlockRockVariant create(Rock rock)
        {
            return supplier.apply(this, rock);
        }

        public Type getNonGrassVersion()
        {
            if (!isGrass) return this;
            switch (this)
            {
                case GRASS:
                    return DIRT;
                case DRY_GRASS:
                    return DIRT;
                case CLAY_GRASS:
                    return CLAY;
            }
            throw new IllegalStateException("Someone forgot to add enum constants to this switch case...");
        }

        public Type getGrassVersion(Type spreader)
        {
            if (!spreader.isGrass) throw new IllegalArgumentException("Non-grass can't spread.");
            switch (this)
            {
                case DIRT:
                    return spreader == DRY_GRASS ? DRY_GRASS : GRASS;
                case CLAY:
                    return CLAY_GRASS;
            }
            throw new IllegalArgumentException("You cannot get grass from rock types.");
        }
    }

    public enum FallingBlockType
    {
        NO_FALL,
        FALL_VERTICAL,
        FALL_HORIZONTAL
    }
}
