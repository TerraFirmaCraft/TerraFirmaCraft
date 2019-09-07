/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.gen.rock.RockCategory;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class Rock extends ForgeRegistryEntry<Rock>
{
    @ObjectHolder("tfc:granite")
    public static final Rock GRANITE = Helpers.getNull();
    @ObjectHolder("tfc:basalt")
    public static final Rock BASALT = Helpers.getNull();

    private final RockCategory rockCategory;
    private final ResourceLocation textureLocation;
    private final boolean isFluxStone;

    public Rock(@Nonnull ResourceLocation name, @Nonnull RockCategory rockCategory, boolean isFluxStone)
    {
        //noinspection ConstantConditions
        if (rockCategory == null)
        {
            throw new IllegalArgumentException("Rock category is not allowed to be null (on rock " + name + ")");
        }

        setRegistryName(name);
        this.rockCategory = rockCategory;
        this.textureLocation = new ResourceLocation(MOD_ID, "textures/blocks/stonetypes/raw/" + name.getPath() + ".png");
        this.isFluxStone = isFluxStone;
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

    public RockCategory getCategory()
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

    /*
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
    */


    public enum FallingBlockType
    {
        NO_FALL,
        FALL_VERTICAL,
        FALL_HORIZONTAL
    }
}
