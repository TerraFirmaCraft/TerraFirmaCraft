/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.fluids;

import java.awt.*;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.EnumRarity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.api.capability.food.FoodStatsTFC;
import net.dries007.tfc.api.capability.food.IFoodStatsTFC;
import net.dries007.tfc.api.capability.player.CapabilityPlayerData;
import net.dries007.tfc.api.capability.player.IPlayerData;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.fluids.properties.DrinkableProperty;
import net.dries007.tfc.objects.fluids.properties.FluidWrapper;
import net.dries007.tfc.objects.fluids.properties.MetalProperty;
import net.dries007.tfc.objects.potioneffects.PotionEffectsTFC;
import net.dries007.tfc.util.calendar.ICalendar;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class FluidsTFC
{
    private static final ResourceLocation STILL = new ResourceLocation(MOD_ID, "blocks/fluid_still");
    private static final ResourceLocation FLOW = new ResourceLocation(MOD_ID, "blocks/fluid_flow");

    private static final HashBiMap<Fluid, FluidWrapper> WRAPPERS = HashBiMap.create();
    private static final ResourceLocation LAVA_STILL = new ResourceLocation(MOD_ID, "blocks/lava_still");
    private static final ResourceLocation LAVA_FLOW = new ResourceLocation(MOD_ID, "blocks/lava_flow");
    private static final Map<EnumDyeColor, FluidWrapper> DYE_FLUIDS = new EnumMap<>(EnumDyeColor.class);
    // Water variants
    public static FluidWrapper HOT_WATER;
    public static FluidWrapper FRESH_WATER;
    public static FluidWrapper SALT_WATER;
    // Other fluids
    public static FluidWrapper LIMEWATER;
    public static FluidWrapper TANNIN;
    public static FluidWrapper VINEGAR;
    public static FluidWrapper BRINE;
    public static FluidWrapper MILK;
    public static FluidWrapper CURDLED_MILK;
    public static FluidWrapper MILK_VINEGAR;
    public static FluidWrapper OLIVE_OIL;
    public static FluidWrapper OLIVE_OIL_WATER;
    public static FluidWrapper LYE;
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
    public static FluidWrapper getWrapper(@Nonnull Fluid fluid)
    {
        if (!WRAPPERS.containsKey(fluid))
        {
            // Should only ever get called for non-tfc fluids, but in which case prevents a null wrapper getting returned
            WRAPPERS.put(fluid, new FluidWrapper(fluid, false));
        }
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

    @Nonnull
    public static Metal getMetalFromFluid(@Nonnull Fluid fluid)
    {
        return getWrapper(fluid).get(MetalProperty.METAL).getMetal();
    }

    @Nonnull
    public static FluidWrapper getFluidFromDye(@Nonnull EnumDyeColor dyeColor)
    {
        return DYE_FLUIDS.get(dyeColor);
    }

    public static void registerFluids()
    {
        FRESH_WATER = registerFluid(new Fluid("fresh_water", STILL, FLOW, 0xFF296ACD)).with(DrinkableProperty.DRINKABLE, player -> {
            if (player.getFoodStats() instanceof FoodStatsTFC)
            {
                ((FoodStatsTFC) player.getFoodStats()).addThirst(40);
            }
        });
        HOT_WATER = registerFluid(new Fluid("hot_water", STILL, FLOW, 0xFF345FDA).setTemperature(350));
        SALT_WATER = registerFluid(new Fluid("salt_water", STILL, FLOW, 0xFF1F5099)).with(DrinkableProperty.DRINKABLE, player -> {
            if (player.getFoodStats() instanceof FoodStatsTFC)
            {
                ((FoodStatsTFC) player.getFoodStats()).addThirst(-10);
                if (Constants.RNG.nextDouble() < ConfigTFC.General.PLAYER.chanceThirstOnSaltyDrink)
                {
                    player.addPotionEffect(new PotionEffect(PotionEffectsTFC.THIRST, 600, 0));
                }
            }
        });

        DrinkableProperty alcoholProperty = player -> {
            IPlayerData playerData = player.getCapability(CapabilityPlayerData.CAPABILITY, null);
            if (player.getFoodStats() instanceof FoodStatsTFC && playerData != null)
            {
                ((FoodStatsTFC) player.getFoodStats()).addThirst(10);
                playerData.addIntoxicatedTime(4 * ICalendar.TICKS_IN_HOUR);
                if (playerData.getIntoxicatedTime() > 24 * ICalendar.TICKS_IN_HOUR && Constants.RNG.nextFloat() < 0.5f)
                {
                    player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 1200, 1));
                }
            }
        };
        allAlcoholsFluids = ImmutableSet.<FluidWrapper>builder()
            .add(
                RUM = registerFluid(new Fluid("rum", STILL, FLOW, 0xFF6E0123).setRarity(EnumRarity.UNCOMMON)).with(DrinkableProperty.DRINKABLE, alcoholProperty),
                BEER = registerFluid(new Fluid("beer", STILL, FLOW, 0xFFC39E37).setRarity(EnumRarity.UNCOMMON)).with(DrinkableProperty.DRINKABLE, alcoholProperty),
                WHISKEY = registerFluid(new Fluid("whiskey", STILL, FLOW, 0xFF583719).setRarity(EnumRarity.UNCOMMON)).with(DrinkableProperty.DRINKABLE, alcoholProperty),
                RYE_WHISKEY = registerFluid(new Fluid("rye_whiskey", STILL, FLOW, 0xFFC77D51).setRarity(EnumRarity.UNCOMMON)).with(DrinkableProperty.DRINKABLE, alcoholProperty),
                CORN_WHISKEY = registerFluid(new Fluid("corn_whiskey", STILL, FLOW, 0xFFD9C7B7).setRarity(EnumRarity.UNCOMMON)).with(DrinkableProperty.DRINKABLE, alcoholProperty),
                SAKE = registerFluid(new Fluid("sake", STILL, FLOW, 0xFFB7D9BC).setRarity(EnumRarity.UNCOMMON)).with(DrinkableProperty.DRINKABLE, alcoholProperty),
                VODKA = registerFluid(new Fluid("vodka", STILL, FLOW, 0xFFDCDCDC).setRarity(EnumRarity.UNCOMMON)).with(DrinkableProperty.DRINKABLE, alcoholProperty),
                CIDER = registerFluid(new Fluid("cider", STILL, FLOW, 0xFFB0AE32).setRarity(EnumRarity.UNCOMMON)).with(DrinkableProperty.DRINKABLE, alcoholProperty)
            )
            .build();

        allOtherFiniteFluids = ImmutableSet.<FluidWrapper>builder()
            .add(
                VINEGAR = registerFluid(new Fluid("vinegar", STILL, FLOW, 0xFFC7C2AA)),
                BRINE = registerFluid(new Fluid("brine", STILL, FLOW, 0xFFDCD3C9)),
                MILK = registerFluid(new Fluid("milk", STILL, FLOW, 0xFFFFFFFF)).with(DrinkableProperty.DRINKABLE, player -> {
                    if (player.getFoodStats() instanceof IFoodStatsTFC)
                    {
                        IFoodStatsTFC foodStats = (IFoodStatsTFC) player.getFoodStats();
                        foodStats.addThirst(10);
                        foodStats.getNutrition().addBuff(FoodData.MILK);
                    }
                }),
                OLIVE_OIL = registerFluid(new Fluid("olive_oil", STILL, FLOW, 0xFF6A7537).setRarity(EnumRarity.RARE)),
                OLIVE_OIL_WATER = registerFluid(new Fluid("olive_oil_water", STILL, FLOW, 0xFF4A4702)),
                TANNIN = registerFluid(new Fluid("tannin", STILL, FLOW, 0xFF63594E)),
                LIMEWATER = registerFluid(new Fluid("limewater", STILL, FLOW, 0xFFB4B4B4)),
                CURDLED_MILK = registerFluid(new Fluid("milk_curdled", STILL, FLOW, 0xFFFFFBE8)),
                MILK_VINEGAR = registerFluid(new Fluid("milk_vinegar", STILL, FLOW, 0xFFFFFBE8)),
                LYE = registerFluid(new Fluid("lye", STILL, FLOW, 0xFFfeffde))
            )
            .build();

        //noinspection ConstantConditions
        allMetalFluids = ImmutableMap.<Metal, FluidWrapper>builder()
            .putAll(
                TFCRegistries.METALS.getValuesCollection()
                    .stream()
                    .collect(Collectors.toMap(
                        metal -> metal,
                        metal -> registerFluid(new Fluid(metal.getRegistryName().getPath(), LAVA_STILL, LAVA_FLOW, metal.getColor())).with(MetalProperty.METAL, new MetalProperty(metal))
                    ))
            )
            .build();

        DYE_FLUIDS.putAll(Arrays.stream(EnumDyeColor.values()).collect(Collectors.toMap(
            color -> color,
            color -> {
                float[] c = color.getColorComponentValues();
                String actualName = color == EnumDyeColor.SILVER ? "light_gray" : color.getName();
                return registerFluid(new Fluid(actualName + "_dye", STILL, FLOW, new Color(c[0], c[1], c[2]).getRGB()));
            })));
    }

    @Nonnull
    private static FluidWrapper registerFluid(@Nonnull Fluid newFluid)
    {
        boolean isDefault = !FluidRegistry.isFluidRegistered(newFluid.getName());

        if (!isDefault)
        {
            // Fluid was already registered with this name, default to that fluid
            newFluid = FluidRegistry.getFluid(newFluid.getName());
        }
        else
        {
            // No fluid found we are safe to register our default
            FluidRegistry.registerFluid(newFluid);
        }
        FluidRegistry.addBucketForFluid(newFluid);
        FluidWrapper properties = new FluidWrapper(newFluid, isDefault);
        WRAPPERS.put(newFluid, properties);
        return properties;
    }
}
