/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.fluids;

import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.food.FoodStatsTFC;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.fluids.properties.DrinkableFluidWrapper;
import net.dries007.tfc.objects.fluids.properties.FluidWrapper;
import net.dries007.tfc.objects.fluids.properties.MetalFluidWrapper;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public final class FluidsTFC
{
    private static final ResourceLocation STILL = new ResourceLocation(MOD_ID, "blocks/fluid_still");
    private static final ResourceLocation FLOW = new ResourceLocation(MOD_ID, "blocks/fluid_flow");

    private static final HashBiMap<Fluid, FluidWrapper> WRAPPERS = HashBiMap.create();
    private static final ResourceLocation LAVA_STILL = new ResourceLocation(MOD_ID, "blocks/lava_still");
    private static final ResourceLocation LAVA_FLOW = new ResourceLocation(MOD_ID, "blocks/lava_flow");
    // Water variants
    public static FluidWrapper HOT_WATER;
    public static FluidWrapper FRESH_WATER;
    public static FluidWrapper SALT_WATER;
    // Other fluids
    public static FluidWrapper LIMEWATER;
    public static FluidWrapper TANNIN;
    public static FluidWrapper VINEGAR;
    // Alcohols
    public static FluidWrapper CIDER;
    public static FluidWrapper VODKA;
    public static FluidWrapper SAKE;
    public static FluidWrapper CORN_WHISKEY;
    public static FluidWrapper RYE_WHISKEY;
    public static FluidWrapper WHISKEY;
    public static FluidWrapper BEER;
    public static FluidWrapper RUM;
    private static ImmutableSet<FluidWrapper> allAlcoholsFluids;
    private static ImmutableMap<Metal, FluidWrapper> allMetalFluids;
    private static ImmutableSet<FluidWrapper> allOtherFiniteFluids;

    public static ImmutableSet<FluidWrapper> getAllAlcoholsFluids()
    {
        return allAlcoholsFluids;
    }

    public static ImmutableSet<FluidWrapper> getAllOtherFiniteFluids()
    {
        return allOtherFiniteFluids;
    }

    public static ImmutableCollection<FluidWrapper> getAllMetalFluids()
    {
        return allMetalFluids.values();
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static FluidWrapper getWrapper(Fluid fluid)
    {
        return WRAPPERS.get(fluid);
    }

    @Nonnull
    public static Set<FluidWrapper> getAllWrappers()
    {
        return WRAPPERS.values();
    }

    @Nonnull
    public static Fluid getFluidFromMetal(@Nonnull Metal metal)
    {
        return allMetalFluids.get(metal).get();
    }

    @Nullable
    public static Metal getMetalFromFluid(@Nonnull Fluid fluid)
    {
        FluidWrapper wrapper = getWrapper(fluid);
        if (wrapper instanceof MetalFluidWrapper)
        {
            return ((MetalFluidWrapper) wrapper).getMetal();
        }
        return null;
    }

    public static void registerFluids()
    {
        FRESH_WATER = registerFluid(new Fluid("fresh_water", STILL, FLOW, 0xFF1F32DA), (fluid, isDefault) -> new DrinkableFluidWrapper(fluid, isDefault, player -> {
            if (player.getFoodStats() instanceof FoodStatsTFC)
            {
                ((FoodStatsTFC) player.getFoodStats()).addThirst(40);
            }
        }));
        HOT_WATER = registerFluid(new Fluid("hot_water", STILL, FLOW, 0xFF345FDA).setTemperature(350));
        SALT_WATER = registerFluid(new Fluid("salt_water", STILL, FLOW, 0xFF1F5099), (fluid, isDefault) -> new DrinkableFluidWrapper(fluid, isDefault, player -> {
            if (player.getFoodStats() instanceof FoodStatsTFC)
            {
                ((FoodStatsTFC) player.getFoodStats()).addThirst(-10);
            }
        }));

        FluidWrapper.Factory alcoholWrapper = (fluid, isDefault) -> new DrinkableFluidWrapper(fluid, isDefault, player -> {
            if (player.getFoodStats() instanceof FoodStatsTFC)
            {
                ((FoodStatsTFC) player.getFoodStats()).addThirst(10);
                if (Constants.RNG.nextFloat() < 0.25f)
                {
                    player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 1200, 1));
                }
            }
        });
        allAlcoholsFluids = ImmutableSet.<FluidWrapper>builder()
            .add(
                RUM = registerFluid(new Fluid("rum", STILL, FLOW, 0xFF6E0123).setRarity(EnumRarity.UNCOMMON), alcoholWrapper),
                BEER = registerFluid(new Fluid("beer", STILL, FLOW, 0xFFC39E37).setRarity(EnumRarity.UNCOMMON), alcoholWrapper),
                WHISKEY = registerFluid(new Fluid("whiskey", STILL, FLOW, 0xFF583719).setRarity(EnumRarity.UNCOMMON), alcoholWrapper),
                RYE_WHISKEY = registerFluid(new Fluid("rye_whiskey", STILL, FLOW, 0xFFC77D51).setRarity(EnumRarity.UNCOMMON), alcoholWrapper),
                CORN_WHISKEY = registerFluid(new Fluid("corn_whiskey", STILL, FLOW, 0xFFD9C7B7).setRarity(EnumRarity.UNCOMMON), alcoholWrapper),
                SAKE = registerFluid(new Fluid("sake", STILL, FLOW, 0xFFB7D9BC).setRarity(EnumRarity.UNCOMMON), alcoholWrapper),
                VODKA = registerFluid(new Fluid("vodka", STILL, FLOW, 0xFFDCDCDC).setRarity(EnumRarity.UNCOMMON), alcoholWrapper),
                CIDER = registerFluid(new Fluid("cider", STILL, FLOW, 0xFFB0AE32).setRarity(EnumRarity.UNCOMMON), alcoholWrapper)
            )
            .build();

        allOtherFiniteFluids = ImmutableSet.<FluidWrapper>builder()
            .add(
                VINEGAR = registerFluid(new Fluid("vinegar", STILL, FLOW, 0xFFC7C2AA)),
                registerFluid(new Fluid("brine", STILL, FLOW, 0xFFDCD3C9)),
                registerFluid(new Fluid("milk", STILL, FLOW, 0xFFFFFFFF)),
                registerFluid(new Fluid("olive_oil", STILL, FLOW, 0xFF6A7537).setRarity(EnumRarity.RARE)),
                TANNIN = registerFluid(new Fluid("tannin", STILL, FLOW, 0xFF63594E)),
                LIMEWATER = registerFluid(new Fluid("limewater", STILL, FLOW, 0xFFB4B4B4)),
                registerFluid(new Fluid("milk_curdled", STILL, FLOW, 0xFFFFFBE8)),
                registerFluid(new Fluid("milk_vinegar", STILL, FLOW, 0xFFFFFBE8))
            )
            .build();

        //noinspection ConstantConditions
        allMetalFluids = ImmutableMap.<Metal, FluidWrapper>builder()
            .putAll(
                TFCRegistries.METALS.getValuesCollection()
                    .stream()
                    .collect(Collectors.toMap(
                        metal -> metal,
                        metal -> registerFluid(new Fluid(metal.getRegistryName().getPath(), LAVA_STILL, LAVA_FLOW, metal.getColor()), (fluid, isDefault) -> new MetalFluidWrapper(fluid, isDefault, metal))
                    ))
            )
            .build();
    }

    private static FluidWrapper registerFluid(Fluid fluid)
    {
        return registerFluid(fluid, FluidWrapper::new);
    }

    private static FluidWrapper registerFluid(Fluid newFluid, FluidWrapper.Factory fluidPropertyWrapper)
    {
        boolean isDefault = FluidRegistry.registerFluid(newFluid);
        if (!isDefault)
        {
            // Fluid was already registered with this name, default to that fluid
            newFluid = FluidRegistry.getFluid(newFluid.getName());
        }
        FluidRegistry.addBucketForFluid(newFluid);
        FluidWrapper properties = fluidPropertyWrapper.create(newFluid, isDefault);
        WRAPPERS.put(newFluid, properties);
        return properties;
    }
}
