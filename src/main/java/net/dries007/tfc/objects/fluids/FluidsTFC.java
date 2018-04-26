/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.fluids;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import net.dries007.tfc.objects.blocks.BlocksTFC;

import static net.dries007.tfc.Constants.MOD_ID;

@SuppressWarnings("WeakerAccess")
public class FluidsTFC
{
    public static final Material MATERIAL_ALCOHOL = new MaterialLiquid(MapColor.WATER);
    private static final ResourceLocation STILL = new ResourceLocation(MOD_ID, "blocks/fluid_still");
    private static final ResourceLocation FLOW = new ResourceLocation(MOD_ID, "blocks/fluid_flow");
    public static final Fluid SALT_WATER = new Fluid("salt_water", STILL, FLOW, 0xFF1F5099);
    public static final Fluid FRESH_WATER = new Fluid("fresh_water", STILL, FLOW, 0xFF1F32DA);
    public static final Fluid HOT_WATER = new Fluid("hot_water", STILL, FLOW, 0xFF345FDA).setTemperature(350);
    // todo set viscosity & density etc
    public static final Fluid RUM = new Fluid("rum", STILL, FLOW, 0xFF6E0123).setRarity(EnumRarity.UNCOMMON);
    public static final Fluid BEER = new Fluid("beer", STILL, FLOW, 0xFFC39E37).setRarity(EnumRarity.UNCOMMON);
    public static final Fluid WHISKEY = new Fluid("whiskey", STILL, FLOW, 0xFF583719).setRarity(EnumRarity.UNCOMMON);
    public static final Fluid RYE_WHISKEY = new Fluid("rye_whiskey", STILL, FLOW, 0xFFC77D51).setRarity(EnumRarity.UNCOMMON);
    public static final Fluid CORN_WHISKEY = new Fluid("corn_whiskey", STILL, FLOW, 0xFFD9C7B7).setRarity(EnumRarity.UNCOMMON);
    public static final Fluid SAKE = new Fluid("sake", STILL, FLOW, 0xFFB7D9BC).setRarity(EnumRarity.UNCOMMON);
    public static final Fluid VODKA = new Fluid("vodka", STILL, FLOW, 0xFFDCDCDC).setRarity(EnumRarity.UNCOMMON);
    public static final Fluid CIDER = new Fluid("cider", STILL, FLOW, 0xFFB0AE32).setRarity(EnumRarity.UNCOMMON);
    public static final Fluid VINEGAR = new Fluid("vinegar", STILL, FLOW, 0xFFC7C2AA);
    public static final Fluid BRINE = new Fluid("brine", STILL, FLOW, 0xFFDCD3C9);
    public static final Fluid MILK = new Fluid("milk", STILL, FLOW, 0xFFFFFFFF);
    public static final Fluid OLIVE_OIL = new Fluid("olive_oil", STILL, FLOW, 0xFF6A7537).setRarity(EnumRarity.RARE);
    public static final Fluid TANNIN = new Fluid("tannin", STILL, FLOW, 0xFF63594E);
    public static final Fluid LIMEWATER = new Fluid("limewater", STILL, FLOW, 0xFFB4B4B4);
    public static final Fluid MILK_CURDLED = new Fluid("milk_curdled", STILL, FLOW, 0xFFFFFBE8);
    public static final Fluid MILK_VINEGAR = new Fluid("milk_vinegar", STILL, FLOW, 0xFFFFFBE8);
    private static final ImmutableSet<Fluid> allInfiniteFluids;
    private static final ImmutableSet<Fluid> allAlcoholsFluids;
    private static final ImmutableSet<Fluid> allOtherFiniteFluids;

    static
    {
        allInfiniteFluids = ImmutableSet.of(SALT_WATER, FRESH_WATER, HOT_WATER);
        allAlcoholsFluids = ImmutableSet.of(RUM, BEER, WHISKEY, RYE_WHISKEY, CORN_WHISKEY, SAKE, VODKA, CIDER);
        allOtherFiniteFluids = ImmutableSet.of(VINEGAR, BRINE, MILK, OLIVE_OIL, TANNIN, LIMEWATER, MILK_CURDLED, MILK_VINEGAR);

        for (Fluid f : allInfiniteFluids)
            FluidRegistry.addBucketForFluid(f);
        for (Fluid f : allAlcoholsFluids)
            FluidRegistry.addBucketForFluid(f);
        for (Fluid f : allOtherFiniteFluids)
            FluidRegistry.addBucketForFluid(f);
    }

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

    public static void preInit()
    {
        SALT_WATER.setBlock(BlocksTFC.FLUID_SALT_WATER);
        FRESH_WATER.setBlock(BlocksTFC.FLUID_FRESH_WATER);
        HOT_WATER.setBlock(BlocksTFC.FLUID_HOT_WATER);

        RUM.setBlock(BlocksTFC.FLUID_RUM);
        BEER.setBlock(BlocksTFC.FLUID_BEER);
        WHISKEY.setBlock(BlocksTFC.FLUID_WHISKEY);
        RYE_WHISKEY.setBlock(BlocksTFC.FLUID_RYE_WHISKEY);
        CORN_WHISKEY.setBlock(BlocksTFC.FLUID_CORN_WHISKEY);
        SAKE.setBlock(BlocksTFC.FLUID_SAKE);
        VODKA.setBlock(BlocksTFC.FLUID_VODKA);
        CIDER.setBlock(BlocksTFC.FLUID_CIDER);
        VINEGAR.setBlock(BlocksTFC.FLUID_VINEGAR);
        BRINE.setBlock(BlocksTFC.FLUID_BRINE);
        MILK.setBlock(BlocksTFC.FLUID_MILK);
        OLIVE_OIL.setBlock(BlocksTFC.FLUID_OLIVE_OIL);
        TANNIN.setBlock(BlocksTFC.FLUID_TANNIN);
        LIMEWATER.setBlock(BlocksTFC.FLUID_LIMEWATER);
        MILK_CURDLED.setBlock(BlocksTFC.FLUID_MILK_CURDLED);
        MILK_VINEGAR.setBlock(BlocksTFC.FLUID_MILK_VINEGAR);

    }
}
