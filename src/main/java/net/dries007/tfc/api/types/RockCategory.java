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
import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * todo: document API
 */
public class RockCategory extends IForgeRegistryEntry.Impl<RockCategory>
{
    @Nonnull
    public static Collection<RockCategory> values()
    {
        return Collections.unmodifiableCollection(TFCRegistries.getRockCategories().getValuesCollection());
    }

    @Nullable
    public static RockCategory get(String name)
    {
        return values().stream().filter(x -> x.name().equals(name)).findFirst().orElse(null);
    }

    public final float caveGenMod;
    public final float caveFreqMod;

    private final ResourceLocation name;
    private final Item.ToolMaterial toolMaterial;
    private final boolean layer1;
    private final boolean layer2;
    private final boolean layer3;

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
        this.name = name;
        this.toolMaterial = toolMaterial;
        this.caveGenMod = caveGenMod;
        this.caveFreqMod = caveFreqMod;
        this.layer1 = layer1;
        this.layer2 = layer2;
        this.layer3 = layer3;
    }

    public String name()
    {
        return name.getResourcePath();
    }

    @Nonnull
    public Item.ToolMaterial getToolMaterial()
    {
        return toolMaterial;
    }

    public Collection<Rock> getRocks()
    {
        return Rock.values().stream().filter(x -> x.getRockCategory() == this).collect(Collectors.toList());
    }

    public enum Layer
    {
        BOTTOM(3, x -> x.getRockCategory().layer3),
        MIDDLE(2, x -> x.getRockCategory().layer2),
        TOP(1, x -> x.getRockCategory().layer1);

        private static boolean isInitialized = false;

        public static void createLayers()
        {
            isInitialized = true;
            for (Layer layer : Layer.values())
                layer.rocks = Rock.values().stream().filter(layer.filter).toArray(Rock[]::new);
        }

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
            if (!isInitialized)
                createLayers();
            return rocks;
        }
    }
}
