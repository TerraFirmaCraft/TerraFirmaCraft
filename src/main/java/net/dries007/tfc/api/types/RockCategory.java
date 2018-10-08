/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.registries.TFCRegistries;

/**
 * todo: document API
 */
public class RockCategory extends IForgeRegistryEntry.Impl<RockCategory>
{
    private final float caveGenMod;
    private final float caveFreqMod;

    private final Item.ToolMaterial toolMaterial;
    private final boolean layer1;
    private final boolean layer2;
    private final boolean layer3;

    private Collection<Rock> rocks;

    /**
     * A rock category.
     *
     * @param name         The resource location of the rock.
     * @param toolMaterial The tool material used for stone tools made of this rock
     * @param caveGenMod   a modifier for cave generation. Default 0, range -0.5 <> 0.5
     * @param caveFreqMod  another modifier for cave generation. Default 0, sedimentary uses +5
     */
    public RockCategory(@Nonnull ResourceLocation name, @Nonnull Item.ToolMaterial toolMaterial, boolean layer1, boolean layer2, boolean layer3, float caveGenMod, float caveFreqMod)
    {
        setRegistryName(name);
        this.toolMaterial = toolMaterial;
        this.caveGenMod = caveGenMod;
        this.caveFreqMod = caveFreqMod;
        this.layer1 = layer1;
        this.layer2 = layer2;
        this.layer3 = layer3;
    }

    @Nonnull
    public Item.ToolMaterial getToolMaterial()
    {
        return toolMaterial;
    }

    public Collection<? extends Rock> getRocks()
    {
        if (rocks == null)
        {
            if (!TerraFirmaCraft.pastState(LoaderState.ModState.PREINITIALIZED))
                throw new IllegalStateException("You can't call this before preinit is done!");
            rocks = Collections.unmodifiableList(TFCRegistries.ROCKS.getValuesCollection().stream().filter(e -> e.getRockCategory() == this).collect(Collectors.toList()));
        }
        return rocks;
    }

    public float getCaveGenMod()
    {
        return caveGenMod;
    }

    public float getCaveFreqMod()
    {
        return caveFreqMod;
    }

    @Override
    public String toString()
    {
        return getRegistryName().getPath();
    }

    public enum Layer
    {
        BOTTOM(3, x -> x.getRockCategory().layer3),
        MIDDLE(2, x -> x.getRockCategory().layer2),
        TOP(1, x -> x.getRockCategory().layer1);

        public final int layer;
        private final Predicate<? super Rock> filter;
        private Rock[] rocks;

        Layer(int layer, Predicate<? super Rock> filter)
        {
            this.layer = layer;
            this.filter = filter;
        }

        public Rock[] getRocks()
        {
            if (rocks == null)
            {
                if (!TerraFirmaCraft.pastState(LoaderState.ModState.PREINITIALIZED))
                    throw new IllegalStateException("You can't call this before preinit is done!");
                rocks = TFCRegistries.ROCKS.getValuesCollection().stream().filter(filter).toArray(Rock[]::new);
            }
            return rocks;
        }
    }
}
