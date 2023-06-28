/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.DecorationBlockRegistryObject;
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
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;


@SuppressWarnings("unused")
public final class TFCCreativeTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TerraFirmaCraft.MOD_ID);

    // todo most items are still missing

    public static final RegistryObject<CreativeModeTab> EARTH = register("earth", () -> new ItemStack(TFCBlocks.ROCK_BLOCKS.get(Rock.QUARTZITE).get(Rock.BlockType.RAW).get()), TFCCreativeTabs::fillEarthTab);
    public static final RegistryObject<CreativeModeTab> ORES = register("ores", () -> new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.NORMAL).get()), TFCCreativeTabs::fillOresTab);
    public static final RegistryObject<CreativeModeTab> ROCK_STUFFS = register("rock", () -> new ItemStack(TFCBlocks.ROCK_BLOCKS.get(Rock.ANDESITE).get(Rock.BlockType.RAW).get()), TFCCreativeTabs::fillRocksTab);
    public static final RegistryObject<CreativeModeTab> METAL = register("metals", () -> new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.WROUGHT_IRON).get(Metal.ItemType.INGOT).get()), TFCCreativeTabs::fillMetalTab);
    public static final RegistryObject<CreativeModeTab> WOOD = register("wood", () -> new ItemStack(TFCBlocks.WOODS.get(Wood.DOUGLAS_FIR).get(Wood.BlockType.LOG).get()), TFCCreativeTabs::fillWoodTab);
    public static final RegistryObject<CreativeModeTab> FOOD = register("food", () -> new ItemStack(TFCItems.FOOD.get(Food.RED_APPLE).get()), TFCCreativeTabs::fillFoodTab);
    public static final RegistryObject<CreativeModeTab> FLORA = register("flora", () -> new ItemStack(TFCBlocks.PLANTS.get(Plant.GOLDENROD).get()), TFCCreativeTabs::fillPlantsTab);
    public static final RegistryObject<CreativeModeTab> DECORATIONS = register("decorations", () -> new ItemStack(TFCBlocks.ALABASTER_BRICKS.get(DyeColor.CYAN).get()), TFCCreativeTabs::fillDecorationsTab);
    public static final RegistryObject<CreativeModeTab> MISC = register("misc", () -> new ItemStack(TFCItems.FIRESTARTER.get()), TFCCreativeTabs::fillMiscTab);

    private static void fillEarthTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        for (SoilBlockType.Variant variant : SoilBlockType.Variant.values())
        {
            TFCBlocks.SOIL.forEach((type, map) -> accept(out, map, variant));
        }
        accept(out, TFCBlocks.PEAT);
        accept(out, TFCBlocks.PEAT_GRASS);

        TFCBlocks.GROUNDCOVER.forEach((type, reg) -> {
            if (type.getVanillaItem() == null)
            {
                accept(out, reg);
            }
            else
            {
                accept(out, type.getVanillaItem());
            }
        });
        TFCBlocks.SMALL_ORES.values().forEach(reg -> accept(out, reg));

        for (SandBlockType type : SandBlockType.values())
        {
            accept(out, TFCBlocks.SAND, type);
            TFCBlocks.SANDSTONE.get(type).values().forEach(reg -> accept(out, reg));
            TFCBlocks.SANDSTONE_DECORATIONS.get(type).values().forEach(reg -> accept(out, reg));
        }

        out.accept(Blocks.ICE);
        accept(out, TFCBlocks.SEA_ICE);
        out.accept(Blocks.PACKED_ICE);
        out.accept(Blocks.BLUE_ICE);

        TFCBlocks.WILD_CROPS.forEach((crop, reg) -> {
            accept(out, reg);
            if (crop == Crop.PUMPKIN)
            {
                accept(out, TFCBlocks.PUMPKIN);
            }
            if (crop == Crop.MELON)
            {
                accept(out, TFCBlocks.MELON);
            }
        });
        TFCBlocks.SPREADING_BUSHES.values().forEach(reg -> accept(out, reg));
        TFCBlocks.STATIONARY_BUSHES.values().forEach(reg -> accept(out, reg));
        accept(out, TFCBlocks.CRANBERRY_BUSH);

        for (FruitBlocks.Tree tree : FruitBlocks.Tree.values())
        {
            accept(out, TFCBlocks.FRUIT_TREE_SAPLINGS, tree);
            accept(out, TFCBlocks.FRUIT_TREE_LEAVES, tree);
        }
        accept(out, TFCBlocks.BANANA_SAPLING);

        accept(out, TFCBlocks.CALCITE);
        accept(out, TFCBlocks.ICICLE);
        TFCBlocks.CORAL.values().forEach(map -> map.values().forEach(reg -> accept(out, reg)));
    }

    private static void fillMetalTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        for (Metal.Default metal : Metal.Default.values())
        {
            TFCBlocks.METALS.get(metal).values().forEach(reg -> accept(out, reg));

            for (Metal.ItemType itemType : new Metal.ItemType[] {
                Metal.ItemType.INGOT,
                Metal.ItemType.DOUBLE_INGOT,
                Metal.ItemType.SHEET,
                Metal.ItemType.DOUBLE_SHEET,
                Metal.ItemType.ROD,

                Metal.ItemType.TUYERE,

                Metal.ItemType.PICKAXE,
                Metal.ItemType.PROPICK,
                Metal.ItemType.AXE,
                Metal.ItemType.SHOVEL,
                Metal.ItemType.HOE,
                Metal.ItemType.CHISEL,
                Metal.ItemType.HAMMER,
                Metal.ItemType.SAW,
                Metal.ItemType.KNIFE,
                Metal.ItemType.SCYTHE,
                Metal.ItemType.JAVELIN,
                Metal.ItemType.SWORD,
                Metal.ItemType.MACE,
                Metal.ItemType.FISHING_ROD,
                Metal.ItemType.SHEARS,

                Metal.ItemType.HELMET,
                Metal.ItemType.CHESTPLATE,
                Metal.ItemType.GREAVES,
                Metal.ItemType.BOOTS,

                Metal.ItemType.SHIELD,
                Metal.ItemType.HORSE_ARMOR,

                Metal.ItemType.PICKAXE_HEAD,
                Metal.ItemType.PROPICK_HEAD,
                Metal.ItemType.AXE_HEAD,
                Metal.ItemType.SHOVEL_HEAD,
                Metal.ItemType.HOE_HEAD,
                Metal.ItemType.CHISEL_HEAD,
                Metal.ItemType.HAMMER_HEAD,
                Metal.ItemType.SAW_BLADE,
                Metal.ItemType.KNIFE_BLADE,
                Metal.ItemType.SCYTHE_BLADE,
                Metal.ItemType.JAVELIN_HEAD,
                Metal.ItemType.SWORD_BLADE,
                Metal.ItemType.MACE_HEAD,
                Metal.ItemType.FISH_HOOK,

                Metal.ItemType.UNFINISHED_HELMET,
                Metal.ItemType.UNFINISHED_CHESTPLATE,
                Metal.ItemType.UNFINISHED_GREAVES,
                Metal.ItemType.UNFINISHED_BOOTS,
            })
            {
                accept(out, TFCItems.METAL_ITEMS, metal, itemType);
            }
        }
    }

    private static void fillOresTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        accept(out, TFCItems.RAW_IRON_BLOOM);
        accept(out, TFCItems.REFINED_IRON_BLOOM);
        for (Ore ore : Ore.values())
        {
            if (ore.isGraded())
            {
                accept(out, TFCItems.GRADED_ORES, ore, Ore.Grade.POOR);
                accept(out, TFCBlocks.SMALL_ORES, ore);
                accept(out, TFCItems.GRADED_ORES, ore, Ore.Grade.NORMAL);
                accept(out, TFCItems.GRADED_ORES, ore, Ore.Grade.RICH);
            }
        }
        for (Ore ore : Ore.values())
        {
            if (!ore.isGraded())
            {
                accept(out, TFCItems.ORES, ore);
            }
        }
        for (Gem gem : Gem.values())
        {
            accept(out, TFCItems.GEMS, gem);
            accept(out, TFCItems.GEM_DUST, gem);
        }
        for (OreDeposit deposit : OreDeposit.values())
        {
            TFCBlocks.ORE_DEPOSITS.values().forEach(map -> accept(out, map, deposit));
        }
        for (Ore ore : Ore.values())
        {
            if (ore.isGraded())
            {
                TFCBlocks.GRADED_ORES.values().forEach(map -> map.get(ore).values().forEach(reg -> accept(out, reg)));
            }
            else
            {
                TFCBlocks.ORES.values().forEach(map -> accept(out, map, ore));
            }
        }
    }

    private static void fillRocksTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        for (Rock rock : Rock.VALUES)
        {
            for (Rock.BlockType type : new Rock.BlockType[] {
                Rock.BlockType.HARDENED,
                Rock.BlockType.RAW,
                Rock.BlockType.PRESSURE_PLATE,
                Rock.BlockType.BUTTON,
                Rock.BlockType.SPIKE,
                Rock.BlockType.COBBLE,
                Rock.BlockType.MOSSY_COBBLE,
                Rock.BlockType.BRICKS,
                Rock.BlockType.CRACKED_BRICKS,
                Rock.BlockType.MOSSY_BRICKS,
                Rock.BlockType.SMOOTH,
                Rock.BlockType.CHISELED,
                Rock.BlockType.AQUEDUCT,
                Rock.BlockType.GRAVEL,
                Rock.BlockType.LOOSE,
            })
            {
                accept(out, TFCBlocks.ROCK_BLOCKS, rock, type);
                if (type.hasVariants())
                {
                    accept(out, TFCBlocks.ROCK_DECORATIONS.get(rock).get(type));
                }
            }
            accept(out, TFCItems.BRICKS, rock);
        }
    }

    private static void fillFoodTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        TFCItems.FOOD.values().forEach(reg -> accept(out, reg));
        TFCItems.SOUPS.values().forEach(reg -> accept(out, reg));
        TFCItems.SALADS.values().forEach(reg -> accept(out, reg));
    }

    private static void fillMiscTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        accept(out, TFCItems.SOOT);
        accept(out, TFCItems.BLANK_DISC);
        accept(out, TFCItems.BLUBBER);
        accept(out, TFCItems.BRASS_MECHANISMS);
        accept(out, TFCItems.BURLAP_CLOTH);
        accept(out, TFCItems.SILK_CLOTH);
        accept(out, TFCItems.WOOL_CLOTH);
        accept(out, TFCItems.WOOL);
        accept(out, TFCItems.WOOL_YARN);
        accept(out, TFCItems.COMPOST);
        accept(out, TFCItems.ROTTEN_COMPOST);
        accept(out, TFCItems.PURE_NITROGEN);
        accept(out, TFCItems.PURE_POTASSIUM);
        accept(out, TFCItems.PURE_PHOSPHORUS);
        accept(out, TFCItems.DAUB);
        accept(out, TFCItems.DIRTY_JUTE_NET);
        accept(out, TFCItems.FIRE_CLAY);
        accept(out, TFCItems.GLASS_SHARD);
        accept(out, TFCItems.GLUE);
        accept(out, TFCItems.JUTE);
        accept(out, TFCItems.JUTE_FIBER);
        accept(out, TFCItems.OLIVE_PASTE);
        accept(out, TFCItems.JUTE_NET);
        accept(out, TFCItems.HANDSTONE);
        accept(out, TFCItems.MORTAR);
        accept(out, TFCItems.PAPYRUS);
        accept(out, TFCItems.PAPYRUS_STRIP);
        accept(out, TFCItems.SOAKED_PAPYRUS_STRIP);
        accept(out, TFCItems.UNREFINED_PAPER);
        accept(out, TFCItems.SPINDLE);
        accept(out, TFCItems.STICK_BUNCH);
        accept(out, TFCItems.STICK_BUNDLE);
        accept(out, TFCItems.STRAW);
        accept(out, TFCItems.WROUGHT_IRON_GRILL);
        accept(out, TFCItems.EMPTY_PAN);

        for (Fluid fluid : ForgeRegistries.FLUIDS)
        {
            if (Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(fluid)).getNamespace().equals(TerraFirmaCraft.MOD_ID))
            {
                out.accept(fluid.getBucket());
            }
        }
    }

    private static void fillDecorationsTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        accept(out, TFCBlocks.MELON);
        accept(out, TFCBlocks.PUMPKIN);
        out.accept(Blocks.CARVED_PUMPKIN);
        accept(out, TFCBlocks.JACK_O_LANTERN);
        accept(out, TFCBlocks.BARREL_RACK);
        accept(out, TFCBlocks.FIRE_BRICKS);
        accept(out, TFCBlocks.FIRE_CLAY_BLOCK);

        accept(out, TFCBlocks.AGGREGATE);
        accept(out, TFCBlocks.PLAIN_ALABASTER);
        accept(out, TFCBlocks.PLAIN_ALABASTER_BRICKS);
        accept(out, TFCBlocks.PLAIN_POLISHED_ALABASTER);
        for (DyeColor color : DyeColor.values())
        {
            accept(out, TFCBlocks.RAW_ALABASTER, color);
            accept(out, TFCBlocks.ALABASTER_BRICKS, color);
            accept(out, TFCBlocks.ALABASTER_BRICK_DECORATIONS.get(color));
            accept(out, TFCBlocks.POLISHED_ALABASTER, color);
            accept(out, TFCBlocks.ALABASTER_POLISHED_DECORATIONS.get(color));
        }
    }

    private static void fillWoodTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        for (Wood wood : Wood.VALUES)
        {
            TFCBlocks.WOODS.get(wood).forEach((type, reg) -> {
                if (type.needsItem())
                {
                    accept(out, reg);
                }
            });
            accept(out, TFCItems.LUMBER, wood);
            accept(out, TFCItems.BOATS, wood);
            accept(out, TFCItems.SUPPORTS, wood);
            accept(out, TFCItems.SIGNS, wood);
            accept(out, TFCItems.CHEST_MINECARTS, wood);
        }
    }

    private static void fillPlantsTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        TFCBlocks.PLANTS.forEach((plant, reg) -> {
            if (plant.needsItem())
            {
                accept(out, reg);
            }
        });
        accept(out, TFCBlocks.SEA_PICKLE);
    }
    
    
    // Helpers

    private static RegistryObject<CreativeModeTab> register(String name, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator displayItems)
    {
        return CREATIVE_TABS.register(name, () -> CreativeModeTab.builder()
            .icon(icon)
            .title(Helpers.translatable("tfc.creative_tab." + name))
            .displayItems(displayItems)
            .build());
    }

    private static <T extends ItemLike, R extends Supplier<T>, K1, K2> void accept(CreativeModeTab.Output out, Map<K1, Map<K2, R>> map, K1 key1, K2 key2)
    {
        if (map.containsKey(key1) && map.get(key1).containsKey(key2))
        {
            out.accept(map.get(key1).get(key2).get());
        }
    }

    private static <T extends ItemLike, R extends Supplier<T>, K> void accept(CreativeModeTab.Output out, Map<K, R> map, K key)
    {
        if (map.containsKey(key))
        {
            out.accept(map.get(key).get());
        }
    }
    
    private static <T extends ItemLike, R extends Supplier<T>> void accept(CreativeModeTab.Output out, R reg)
    {
        out.accept(reg.get());
    }

    private static void accept(CreativeModeTab.Output out, DecorationBlockRegistryObject decoration)
    {
        out.accept(decoration.stair().get());
        out.accept(decoration.slab().get());
        out.accept(decoration.wall().get());
    }
}