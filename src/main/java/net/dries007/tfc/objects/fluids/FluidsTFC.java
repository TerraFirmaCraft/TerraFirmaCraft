/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.fluids;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class FluidsTFC
{
    public static final Material MATERIAL_ALCOHOL = new MaterialLiquid(MapColor.WATER);
    private static final ResourceLocation STILL = new ResourceLocation(MOD_ID, "blocks/fluid_still");
    private static final ResourceLocation FLOW = new ResourceLocation(MOD_ID, "blocks/fluid_flow");
    private static final ResourceLocation LAVA_STILL = new ResourceLocation(MOD_ID, "blocks/lava_still");
    private static final ResourceLocation LAVA_FLOW = new ResourceLocation(MOD_ID, "blocks/lava_flow");
    public static Fluid HOT_WATER;
    private static ImmutableSet<Fluid> allInfiniteFluids;
    private static ImmutableSet<Fluid> allAlcoholsFluids;
    private static ImmutableMap<Metal, Fluid> allMetalFluids;
    private static ImmutableSet<Fluid> allOtherFiniteFluids;

    public static ImmutableSet<Fluid> getAllInfiniteFluids()
    {
        return allInfiniteFluids;
    }

    public static ImmutableSet<Fluid> getAllAlcoholsFluids()
    {
        return allAlcoholsFluids;
    }

    public static ImmutableSet<Fluid> getAllOtherFiniteFluids()
    {
        return allOtherFiniteFluids;
    }

    public static ImmutableCollection<Fluid> getAllMetalFluids()
    {
        return allMetalFluids.values();
    }

    @Nonnull
    public static Fluid getMetalFluid(@Nonnull Metal metal)
    {
        return allMetalFluids.get(metal);
    }

    public static void preInit()
    {
        {
            ImmutableSet.Builder<Fluid> b = ImmutableSet.builder();

            registerFluid(b, new Fluid("salt_water", STILL, FLOW, 0xFF1F5099));
            registerFluid(b, new Fluid("fresh_water", STILL, FLOW, 0xFF1F32DA));
            registerFluid(b, HOT_WATER = new Fluid("hot_water", STILL, FLOW, 0xFF345FDA).setTemperature(350));

            allInfiniteFluids = b.build();
        }
        {
            ImmutableSet.Builder<Fluid> b = ImmutableSet.builder();

            registerFluid(b, new Fluid("rum", STILL, FLOW, 0xFF6E0123).setRarity(EnumRarity.UNCOMMON));
            registerFluid(b, new Fluid("beer", STILL, FLOW, 0xFFC39E37).setRarity(EnumRarity.UNCOMMON));
            registerFluid(b, new Fluid("whiskey", STILL, FLOW, 0xFF583719).setRarity(EnumRarity.UNCOMMON));
            registerFluid(b, new Fluid("rye_whiskey", STILL, FLOW, 0xFFC77D51).setRarity(EnumRarity.UNCOMMON));
            registerFluid(b, new Fluid("corn_whiskey", STILL, FLOW, 0xFFD9C7B7).setRarity(EnumRarity.UNCOMMON));
            registerFluid(b, new Fluid("sake", STILL, FLOW, 0xFFB7D9BC).setRarity(EnumRarity.UNCOMMON));
            registerFluid(b, new Fluid("vodka", STILL, FLOW, 0xFFDCDCDC).setRarity(EnumRarity.UNCOMMON));
            registerFluid(b, new Fluid("cider", STILL, FLOW, 0xFFB0AE32).setRarity(EnumRarity.UNCOMMON));

            allAlcoholsFluids = b.build();
        }
        {
            ImmutableSet.Builder<Fluid> b = ImmutableSet.builder();

            registerFluid(b, new Fluid("vinegar", STILL, FLOW, 0xFFC7C2AA));
            registerFluid(b, new Fluid("brine", STILL, FLOW, 0xFFDCD3C9));
            registerFluid(b, new Fluid("milk", STILL, FLOW, 0xFFFFFFFF));
            registerFluid(b, new Fluid("olive_oil", STILL, FLOW, 0xFF6A7537).setRarity(EnumRarity.RARE));
            registerFluid(b, new Fluid("tannin", STILL, FLOW, 0xFF63594E));
            registerFluid(b, new Fluid("limewater", STILL, FLOW, 0xFFB4B4B4));
            registerFluid(b, new Fluid("milk_curdled", STILL, FLOW, 0xFFFFFBE8));
            registerFluid(b, new Fluid("milk_vinegar", STILL, FLOW, 0xFFFFFBE8));

            allOtherFiniteFluids = b.build();
        }
        {
            ImmutableMap.Builder<Metal, Fluid> b = ImmutableMap.builder();

            for (Metal metal : TFCRegistries.METALS.getValuesCollection())
            {
                //noinspection ConstantConditions
                registerFluid(b, metal, new FluidMetal(metal, metal.getRegistryName().getPath(), LAVA_STILL, LAVA_FLOW, metal.getColor()));
            }
            allMetalFluids = b.build();
        }
    }

    private static <T extends Fluid> void registerFluid(ImmutableSet.Builder<T> b, T fluid)
    {
        FluidRegistry.registerFluid(fluid);
        FluidRegistry.addBucketForFluid(fluid);
        b.add(fluid);
    }

    private static <T extends Fluid, V> void registerFluid(ImmutableMap.Builder<V, T> b, V key, T fluid)
    {
        FluidRegistry.registerFluid(fluid);
        FluidRegistry.addBucketForFluid(fluid);
        b.put(key, fluid);
    }
}
