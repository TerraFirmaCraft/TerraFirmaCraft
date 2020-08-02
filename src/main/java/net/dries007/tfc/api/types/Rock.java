/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import java.util.function.Function;
import javax.annotation.Nonnull;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.objects.items.rock.*;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.api.types.Rock.FallingBlockType.*;


public class Rock extends IForgeRegistryEntry.Impl<Rock>
{
    @GameRegistry.ObjectHolder("tfc:granite")
    public static final Rock GRANITE = Helpers.getNull();
    @GameRegistry.ObjectHolder("tfc:basalt")
    public static final Rock BASALT = Helpers.getNull();
    @GameRegistry.ObjectHolder("tfc:rhyolite")
    public static final Rock RHYOLITE = Helpers.getNull();
    @GameRegistry.ObjectHolder("tfc:limestone")
    public static final Rock LIMESTONE = Helpers.getNull();

    private final RockCategory rockCategory;
    private final ResourceLocation textureLocation;
    private final boolean isFluxStone;

    public Rock(@Nonnull ResourceLocation name, @Nonnull RockCategory rockCategory, boolean isFluxStone)
    {
        //noinspection ConstantConditions
        if (rockCategory == null)
            throw new IllegalArgumentException("Rock category is not allowed to be null (on rock " + name + ")");

        setRegistryName(name);
        this.rockCategory = rockCategory;
        this.textureLocation = new ResourceLocation(MOD_ID, "textures/blocks/stonetypes/raw/" + name.getPath() + ".png");
        this.isFluxStone = isFluxStone;
    }

    public Rock(@Nonnull ResourceLocation name, @Nonnull ResourceLocation categoryName, boolean isFluxStone)
    {
        //noinspection ConstantConditions
        this(name, TFCRegistries.ROCK_CATEGORIES.getValue(categoryName), isFluxStone);
    }

    /**
     * Used for knapping GUI
     *
     * @return a texture resource location
     */
    public ResourceLocation getTexture()
    {
        return textureLocation;
    }

    public RockCategory getRockCategory()
    {
        return rockCategory;
    }

    public boolean isFluxStone()
    {
        return isFluxStone;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public String toString()
    {
        return getRegistryName().getPath();
    }

    public enum ToolType
    {
        AXE(ItemRockAxe::new, " X   ", "XXXX ", "XXXXX", "XXXX ", " X   "),
        SHOVEL(ItemRockShovel::new, "XXX", "XXX", "XXX", "XXX", " X "),
        HOE(ItemRockHoe::new, "XXXXX", "   XX"),
        KNIFE(ItemRockKnife::new, "X ", "XX", "XX", "XX", "XX"),
        JAVELIN(ItemRockJavelin::new, "XXX  ", "XXXX ", "XXXXX", " XXX ", "  X  "),
        HAMMER(ItemRockHammer::new, "XXXXX", "XXXXX", "  X  ");

        private final Function<RockCategory, Item> supplier;
        private final String[] pattern;

        ToolType(@Nonnull Function<RockCategory, Item> supplier, String... pattern)
        {
            this.supplier = supplier;
            this.pattern = pattern;
        }

        public Item create(RockCategory category)
        {
            return supplier.apply(category);
        }

        public String[] getPattern()
        {
            return pattern;
        }
    }

    public enum Type
    {
        RAW(Material.ROCK, FALL_VERTICAL, false),
        ANVIL(Material.ROCK, FALL_VERTICAL, false),
        SPIKE(Material.ROCK, NO_FALL, false),
        SMOOTH(Material.ROCK, FALL_VERTICAL, false),
        COBBLE(Material.ROCK, FALL_HORIZONTAL, false),
        BRICKS(Material.ROCK, NO_FALL, false),
        SAND(Material.SAND, FALL_HORIZONTAL, false),
        GRAVEL(Material.SAND, FALL_HORIZONTAL, false),
        DIRT(Material.GROUND, FALL_HORIZONTAL, false),
        GRASS(Material.GRASS, FALL_HORIZONTAL, true),
        DRY_GRASS(Material.GRASS, FALL_HORIZONTAL, true),
        CLAY(Material.CLAY, FALL_VERTICAL, false),
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

    public enum FallingBlockType
    {
        NO_FALL,
        FALL_VERTICAL,
        FALL_HORIZONTAL
    }
}
