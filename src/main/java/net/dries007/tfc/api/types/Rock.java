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
        RAW(Material.ROCK, false, false),
        SMOOTH(Material.ROCK, false, false),
        COBBLE(Material.ROCK, true, false),
        BRICKS(Material.ROCK, false, false),
        SAND(Material.SAND, true, false),
        GRAVEL(Material.SAND, true, false),
        DIRT(Material.GROUND, false, false),
        GRASS(Material.GRASS, false, true),
        DRY_GRASS(Material.GRASS, false, true),
        CLAY(Material.GRASS, false, false),
        CLAY_GRASS(Material.GRASS, false, true),
        FARMLAND(Material.GROUND, false, false),
        PATH(Material.GROUND, false, false);

        public final Material material;
        public final boolean isAffectedByGravity;
        public final boolean isGrass;

        Type(Material material, boolean isAffectedByGravity, boolean isGrass)
        {
            this.material = material;
            this.isAffectedByGravity = isAffectedByGravity;
            this.isGrass = isGrass;
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
}
