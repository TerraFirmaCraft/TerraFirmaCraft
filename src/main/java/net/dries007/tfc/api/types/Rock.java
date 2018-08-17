/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import static net.dries007.tfc.api.types.Rock.FallingBlockType.*;

/**
 * todo: document API
 */
public class Rock extends IForgeRegistryEntry.Impl<Rock>
{
    @Nonnull
    public static Collection<Rock> values()
    {
        return Collections.unmodifiableCollection(TFCRegistries.getRocks().getValuesCollection());
    }

    @Nullable
    public static Rock get(String name)
    {
        return values().stream().filter(x -> x.name().equals(name)).findFirst().orElse(null);
    }

    private static int i = -1;

    @Nullable
    public static Rock get(int id)
    {
        return values().stream().filter(x -> x.id == id).findFirst().orElse(null);
    }

    private final ResourceLocation name;
    private final RockCategory rockCategory;
    private final int id;

    public Rock(@Nonnull ResourceLocation name, @Nonnull RockCategory rockCategory)
    {
        setRegistryName(name);
        this.id = ++i;
        this.rockCategory = rockCategory;
        this.name = name;
    }

    public Rock(@Nonnull ResourceLocation name, @Nonnull ResourceLocation categoryName)
    {
        setRegistryName(name);
        this.id = ++i;
        this.name = name;
        this.rockCategory = TFCRegistries.getRockCategories().getValue(categoryName);
        if (rockCategory == null)
            throw new IllegalStateException("Rock category '" + categoryName.toString() + "' is not allowed to be null");
    }

    @Nonnull
    public String name()
    {
        return name.getResourcePath();
    }

    public int getId()
    {
        return id;
    }

    public RockCategory getRockCategory()
    {
        return rockCategory;
    }

    public enum Type
    {
        RAW(Material.ROCK, NO_FALL, false), // Todo: add collapsing when broken
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
        FARMLAND(Material.GROUND, FALL_VERTICAL, false),
        PATH(Material.GROUND, FALL_VERTICAL, false);

        public final Material material;
        public final boolean isGrass;

        private final FallingBlockType gravType;

        Type(Material material, FallingBlockType gravType, boolean isGrass)
        {
            this.material = material;
            this.gravType = gravType;
            this.isGrass = isGrass;
        }

        public boolean canFall()
        {
            return gravType != NO_FALL;
        }

        public boolean canFallHorizontal()
        {
            return gravType == FALL_HORIZONTAL;
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

    protected enum FallingBlockType
    {
        NO_FALL,
        FALL_VERTICAL,
        FALL_HORIZONTAL
    }
}
