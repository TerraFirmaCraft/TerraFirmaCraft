/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import java.util.Locale;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.common.blocks.Gem;
import net.dries007.tfc.common.blocks.OreDeposit;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.crop.Crop;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.plant.fruit.FruitBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

import static net.dries007.tfc.TerraFirmaCraft.*;

@SuppressWarnings("unused")
public final class TFCCreativeTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    // todo most items are still missing

    public static final RegistryObject<CreativeModeTab> EARTH = register("earth", () -> new ItemStack(TFCBlocks.ROCK_BLOCKS.get(Rock.QUARTZITE).get(Rock.BlockType.RAW).get()), TFCCreativeTabs.fillEarthTab());
    public static final RegistryObject<CreativeModeTab> ORES = register("ores", () -> new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.NORMAL).get()), TFCCreativeTabs.fillOresTab());
    public static final RegistryObject<CreativeModeTab> ROCK_STUFFS = register("rock", () -> new ItemStack(TFCBlocks.ROCK_BLOCKS.get(Rock.ANDESITE).get(Rock.BlockType.RAW).get()), TFCCreativeTabs.fillRocksTab());
    public static final RegistryObject<CreativeModeTab> METAL = register("metals", () -> new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.WROUGHT_IRON).get(Metal.ItemType.INGOT).get()), TFCCreativeTabs.fillMetalTab());
    public static final RegistryObject<CreativeModeTab> WOOD = register("wood", () -> new ItemStack(TFCBlocks.WOODS.get(Wood.DOUGLAS_FIR).get(Wood.BlockType.LOG).get()), TFCCreativeTabs.fillWoodTab());
    public static final RegistryObject<CreativeModeTab> FOOD = register("food", () -> new ItemStack(TFCItems.FOOD.get(Food.RED_APPLE).get()), TFCCreativeTabs.fillFoodTab());
    public static final RegistryObject<CreativeModeTab> FLORA = register("flora", () -> new ItemStack(TFCBlocks.PLANTS.get(Plant.GOLDENROD).get()), TFCCreativeTabs.fillPlantsTab());
    public static final RegistryObject<CreativeModeTab> DECORATIONS = register("decorations", () -> new ItemStack(TFCBlocks.ALABASTER_BRICKS.get(DyeColor.CYAN).get()), TFCCreativeTabs.fillDecorationsTab());
    public static final RegistryObject<CreativeModeTab> MISC = register("misc", () -> new ItemStack(TFCItems.FIRESTARTER.get()), TFCCreativeTabs.fillMiscTab());
    private static RegistryObject<CreativeModeTab> register(String name, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator displayItems)
    {
        return CREATIVE_TABS.register(name, () -> CreativeModeTab.builder()
            .icon(icon)
            .title(Helpers.translatable("tfc.itemGroup." + name))
            .displayItems(displayItems)
            .build());
    }

    private static CreativeModeTab.DisplayItemsGenerator fillEarthTab()
    {
        return (parameters, out) -> {
            for (SoilBlockType.Variant variant : SoilBlockType.Variant.values())
            {
                TFCBlocks.SOIL.forEach((type, map) -> {
                    out.accept(map.get(variant).get());
                });
            }
            out.accept(TFCBlocks.PEAT.get());
            out.accept(TFCBlocks.PEAT_GRASS.get());

            TFCBlocks.GROUNDCOVER.forEach((type, reg) -> {
                if (type.getVanillaItem() == null)
                {
                    out.accept(reg.get());
                }
            });
            TFCBlocks.SMALL_ORES.values().forEach(reg -> out.accept(reg.get()));

            for (SandBlockType type : SandBlockType.values())
            {
                out.accept(TFCBlocks.SAND.get(type).get());
                TFCBlocks.SANDSTONE.get(type).values().forEach(reg -> out.accept(reg.get()));
                TFCBlocks.SANDSTONE_DECORATIONS.get(type).values().forEach(reg -> {
                    out.accept(reg.stair().get());
                    out.accept(reg.slab().get());
                    out.accept(reg.wall().get());
                });
            }

            out.accept(Blocks.ICE);
            out.accept(TFCBlocks.SEA_ICE.get());
            out.accept(Blocks.PACKED_ICE);
            out.accept(Blocks.BLUE_ICE);

            TFCBlocks.WILD_CROPS.forEach((crop, reg) -> {
                out.accept(reg.get());
                if (crop == Crop.PUMPKIN)
                {
                    out.accept(TFCBlocks.PUMPKIN.get());
                }
                if (crop == Crop.MELON)
                {
                    out.accept(TFCBlocks.MELON.get());
                }
            });
            TFCBlocks.SPREADING_BUSHES.values().forEach(reg -> out.accept(reg.get()));
            TFCBlocks.STATIONARY_BUSHES.values().forEach(reg -> out.accept(reg.get()));
            out.accept(TFCBlocks.CRANBERRY_BUSH.get());

            for (FruitBlocks.Tree tree : FruitBlocks.Tree.values())
            {
                out.accept(TFCBlocks.FRUIT_TREE_SAPLINGS.get(tree).get());
                out.accept(TFCBlocks.FRUIT_TREE_LEAVES.get(tree).get());
            }
            out.accept(TFCBlocks.BANANA_SAPLING.get());

            out.accept(TFCBlocks.CALCITE.get());
            out.accept(TFCBlocks.ICICLE.get());
            TFCBlocks.CORAL.values().forEach(map -> map.values().forEach(reg -> out.accept(reg.get())));
        };
    }

    public static CreativeModeTab.DisplayItemsGenerator fillMetalTab()
    {
        return (parameters, out) -> {

            final Predicate<Metal.ItemType> isNotUseful = type -> {
                final String typeName = type.name().toLowerCase(Locale.ROOT);
                return typeName.contains("_head") || typeName.contains("_blade") || typeName.contains("unfinished");
            };

            for (Metal.Default metal : Metal.Default.values())
            {
                TFCItems.METAL_ITEMS.get(metal).forEach((type, reg) -> {
                    if (!isNotUseful.test(type))
                    {
                        out.accept(reg.get());
                    }
                });
                TFCItems.METAL_ITEMS.get(metal).forEach((type, reg) -> {
                    if (isNotUseful.test(type))
                    {
                        out.accept(reg.get());
                    }
                });
                TFCBlocks.METALS.get(metal).values().forEach(reg -> out.accept(reg.get()));
            }

        };
    }

    public static CreativeModeTab.DisplayItemsGenerator fillOresTab()
    {
        return (parameters, out) -> {
            out.accept(TFCItems.RAW_IRON_BLOOM.get());
            out.accept(TFCItems.REFINED_IRON_BLOOM.get());
            for (Ore ore : Ore.values())
            {
                if (ore.isGraded())
                {
                    out.accept(TFCItems.GRADED_ORES.get(ore).get(Ore.Grade.POOR).get());
                    out.accept(TFCBlocks.SMALL_ORES.get(ore).get());
                    out.accept(TFCItems.GRADED_ORES.get(ore).get(Ore.Grade.NORMAL).get());
                    out.accept(TFCItems.GRADED_ORES.get(ore).get(Ore.Grade.RICH).get());
                }
            }
            for (Ore ore : Ore.values())
            {
                if (!ore.isGraded())
                {
                    out.accept(TFCItems.ORES.get(ore).get());
                }
            }
            for (Gem gem : Gem.values())
            {
                out.accept(TFCItems.GEMS.get(gem).get());
                out.accept(TFCItems.GEM_DUST.get(gem).get());
            }
            for (OreDeposit deposit : OreDeposit.values())
            {
                TFCBlocks.ORE_DEPOSITS.values().forEach(map -> out.accept(map.get(deposit).get()));
            }
            for (Ore ore : Ore.values())
            {
                if (ore.isGraded())
                {
                    TFCBlocks.GRADED_ORES.values().forEach(map -> map.get(ore).values().forEach(reg -> out.accept(reg.get())));
                }
                else
                {
                    TFCBlocks.ORES.values().forEach(map -> out.accept(map.get(ore).get()));
                }
            }
        };
    }

    public static CreativeModeTab.DisplayItemsGenerator fillRocksTab()
    {
        return (parameters, out) -> {
            for (Rock rock : Rock.VALUES)
            {
                for (Rock.BlockType type : Rock.BlockType.values())
                {
                    out.accept(TFCBlocks.ROCK_BLOCKS.get(rock).get(type).get());
                    if (type.hasVariants())
                    {
                        out.accept(TFCBlocks.ROCK_DECORATIONS.get(rock).get(type).stair().get());
                        out.accept(TFCBlocks.ROCK_DECORATIONS.get(rock).get(type).slab().get());
                        out.accept(TFCBlocks.ROCK_DECORATIONS.get(rock).get(type).wall().get());
                    }
                }
                out.accept(TFCItems.BRICKS.get(rock).get());
            }
        };
    }

    public static CreativeModeTab.DisplayItemsGenerator fillFoodTab()
    {
        return (parameters, out) -> {
            TFCItems.FOOD.values().forEach(reg -> out.accept(reg.get()));
            TFCItems.SOUPS.values().forEach(reg -> out.accept(reg.get()));
            TFCItems.SALADS.values().forEach(reg -> out.accept(reg.get()));
        };
    }

    public static CreativeModeTab.DisplayItemsGenerator fillMiscTab()
    {
        return (parameters, out) -> {
            Stream.of(TFCItems.SOOT, TFCItems.BLANK_DISC, TFCItems.BLUBBER, TFCItems.BRASS_MECHANISMS, TFCItems.BURLAP_CLOTH, TFCItems.SILK_CLOTH,
                TFCItems.WOOL_CLOTH, TFCItems.WOOL, TFCItems.WOOL_YARN, TFCItems.COMPOST,
                TFCItems.ROTTEN_COMPOST, TFCItems.PURE_NITROGEN, TFCItems.PURE_POTASSIUM, TFCItems.PURE_PHOSPHORUS, TFCItems.DAUB,
                TFCItems.DIRTY_JUTE_NET, TFCItems.FIRE_CLAY, TFCItems.GLASS_SHARD,
                TFCItems.GLUE, TFCItems.JUTE, TFCItems.JUTE_FIBER, TFCItems.OLIVE_PASTE, TFCItems.JUTE_NET, TFCItems.HANDSTONE, TFCItems.MORTAR,
                TFCItems.PAPYRUS, TFCItems.PAPYRUS_STRIP, TFCItems.SOAKED_PAPYRUS_STRIP, TFCItems.UNREFINED_PAPER, TFCItems.SPINDLE,
                TFCItems.STICK_BUNCH, TFCItems.STICK_BUNDLE, TFCItems.STRAW, TFCItems.WROUGHT_IRON_GRILL, TFCItems.EMPTY_PAN
            ).forEach(reg -> out.accept(reg.get()));
        };
    }

    public static CreativeModeTab.DisplayItemsGenerator fillDecorationsTab()
    {
        return (parameters, out) -> {
            out.accept(TFCBlocks.MELON.get());
            out.accept(TFCBlocks.PUMPKIN.get());
            out.accept(Blocks.CARVED_PUMPKIN);
            out.accept(TFCBlocks.JACK_O_LANTERN.get());
            out.accept(TFCBlocks.BARREL_RACK.get());
            out.accept(TFCBlocks.FIRE_BRICKS.get());
            out.accept(TFCBlocks.FIRE_CLAY_BLOCK.get());

            out.accept(TFCBlocks.AGGREGATE.get());
            out.accept(TFCBlocks.PLAIN_ALABASTER.get());
            out.accept(TFCBlocks.PLAIN_ALABASTER_BRICKS.get());
            out.accept(TFCBlocks.PLAIN_POLISHED_ALABASTER.get());
            for (DyeColor color : DyeColor.values())
            {
                out.accept(TFCBlocks.RAW_ALABASTER.get(color).get());
                out.accept(TFCBlocks.ALABASTER_BRICKS.get(color).get());
                out.accept(TFCBlocks.ALABASTER_BRICK_DECORATIONS.get(color).stair().get());
                out.accept(TFCBlocks.ALABASTER_BRICK_DECORATIONS.get(color).slab().get());
                out.accept(TFCBlocks.ALABASTER_BRICK_DECORATIONS.get(color).wall().get());
                out.accept(TFCBlocks.POLISHED_ALABASTER.get(color).get());
                out.accept(TFCBlocks.ALABASTER_POLISHED_DECORATIONS.get(color).stair().get());
                out.accept(TFCBlocks.ALABASTER_POLISHED_DECORATIONS.get(color).slab().get());
                out.accept(TFCBlocks.ALABASTER_POLISHED_DECORATIONS.get(color).wall().get());
            }
        };
    }

    public static CreativeModeTab.DisplayItemsGenerator fillWoodTab()
    {
        return (parameters, out) -> {
            for (Wood wood : Wood.VALUES)
            {
                TFCBlocks.WOODS.get(wood).forEach((type, reg) -> {
                    if (type.needsItem())
                    {
                        out.accept(reg.get());
                    }
                });
                out.accept(TFCItems.LUMBER.get(wood).get());
                out.accept(TFCItems.BOATS.get(wood).get());
                out.accept(TFCItems.SUPPORTS.get(wood).get());
                out.accept(TFCItems.SIGNS.get(wood).get());
                out.accept(TFCItems.CHEST_MINECARTS.get(wood).get());
            }
        };
    }

    public static CreativeModeTab.DisplayItemsGenerator fillPlantsTab()
    {
        return (parameters, out) -> {
            TFCBlocks.PLANTS.forEach((plant, reg) -> {
                if (plant.needsItem())
                {
                    out.accept(reg.get());
                }
            });
            out.accept(TFCBlocks.SEA_PICKLE.get());
        };
    }

}