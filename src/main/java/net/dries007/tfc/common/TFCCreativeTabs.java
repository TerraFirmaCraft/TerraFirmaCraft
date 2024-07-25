/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.DecorationBlockRegistryObject;
import net.dries007.tfc.common.blocks.Gem;
import net.dries007.tfc.common.blocks.OreDeposit;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.crop.Crop;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.plant.coral.Coral;
import net.dries007.tfc.common.blocks.plant.fruit.FruitBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.HideItemType;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.compat.patchouli.PatchouliIntegration;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.SelfTests;


@SuppressWarnings("unused")
public final class TFCCreativeTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TerraFirmaCraft.MOD_ID);


    public static final CreativeTabHolder EARTH = register("earth", () -> new ItemStack(TFCBlocks.ROCK_BLOCKS.get(Rock.QUARTZITE).get(Rock.BlockType.RAW).get()), TFCCreativeTabs::fillEarthTab);
    public static final CreativeTabHolder ORES = register("ores", () -> new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.NORMAL).get()), TFCCreativeTabs::fillOresTab);
    public static final CreativeTabHolder ROCKS = register("rock", () -> new ItemStack(TFCBlocks.ROCK_BLOCKS.get(Rock.ANDESITE).get(Rock.BlockType.RAW).get()), TFCCreativeTabs::fillRocksTab);
    public static final CreativeTabHolder METAL = register("metals", () -> new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.WROUGHT_IRON).get(Metal.ItemType.INGOT).get()), TFCCreativeTabs::fillMetalTab);
    public static final CreativeTabHolder WOOD = register("wood", () -> new ItemStack(TFCBlocks.WOODS.get(Wood.DOUGLAS_FIR).get(Wood.BlockType.LOG).get()), TFCCreativeTabs::fillWoodTab);
    public static final CreativeTabHolder FOOD = register("food", () -> new ItemStack(TFCItems.FOOD.get(Food.RED_APPLE).get()), TFCCreativeTabs::fillFoodTab);
    public static final CreativeTabHolder FLORA = register("flora", () -> new ItemStack(TFCBlocks.PLANTS.get(Plant.GOLDENROD).get()), TFCCreativeTabs::fillPlantsTab);
    public static final CreativeTabHolder DECORATIONS = register("decorations", () -> new ItemStack(TFCBlocks.ALABASTER_BRICKS.get(DyeColor.CYAN).get()), TFCCreativeTabs::fillDecorationsTab);
    public static final CreativeTabHolder MISC = register("misc", () -> new ItemStack(TFCItems.FIRESTARTER.get()), TFCCreativeTabs::fillMiscTab);

    public static Stream<CreativeModeTab.DisplayItemsGenerator> generators()
    {
        return Stream.of(EARTH, ORES, ROCKS, METAL, WOOD, FOOD, FLORA, DECORATIONS, MISC).map(holder -> holder.generator);
    }

    private static void fillEarthTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        for (SoilBlockType.Variant variant : SoilBlockType.Variant.values())
        {
            for (SoilBlockType type : SoilBlockType.VALUES)
            {
                accept(out, TFCBlocks.SOIL, type, variant);
                if (type == SoilBlockType.MUD_BRICKS)
                {
                    accept(out, TFCBlocks.MUD_BRICK_DECORATIONS.get(variant));
                }
            }
        }
        accept(out, TFCBlocks.SMOOTH_MUD_BRICKS);
        accept(out, TFCBlocks.TREE_ROOTS);
        accept(out, TFCBlocks.PEAT);
        accept(out, TFCBlocks.PEAT_GRASS);
        accept(out, TFCBlocks.WHITE_KAOLIN_CLAY);
        accept(out, TFCBlocks.PINK_KAOLIN_CLAY);
        accept(out, TFCBlocks.RED_KAOLIN_CLAY);
        accept(out, TFCBlocks.KAOLIN_CLAY_GRASS);

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

        TFCBlocks.MAGMA_BLOCKS.values().forEach(reg -> accept(out, reg));

        for (Crop crop : Crop.values())
        {
            accept(out, TFCBlocks.WILD_CROPS, crop);
            if (crop == Crop.PUMPKIN)
                accept(out, TFCBlocks.PUMPKIN);
            else if (crop == Crop.MELON)
                accept(out, TFCBlocks.MELON);
            accept(out, TFCItems.CROP_SEEDS, crop);
        }
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
        for (Coral coral : Coral.values())
        {
            TFCBlocks.CORAL.get(coral).values().forEach(reg -> accept(out, reg));
            accept(out, TFCItems.CORAL_FANS, coral);
            accept(out, TFCItems.DEAD_CORAL_FANS, coral);
        }
    }

    private static void fillMetalTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        for (Metal.Default metal : Metal.Default.values())
        {
            for (Metal.BlockType type : new Metal.BlockType[] {
                Metal.BlockType.ANVIL,
                Metal.BlockType.BLOCK,
                Metal.BlockType.BLOCK_SLAB,
                Metal.BlockType.BLOCK_STAIRS,
                Metal.BlockType.BARS,
                Metal.BlockType.CHAIN,
                Metal.BlockType.TRAPDOOR,
                Metal.BlockType.LAMP,
            })
            {
                accept(out, TFCBlocks.METALS, metal, type);
            }

            accept(out, TFCItems.METAL_ITEMS, metal, Metal.ItemType.UNFINISHED_LAMP);

            if (metal == Metal.Default.BRONZE)
                accept(out, TFCBlocks.BRONZE_BELL);
            else if (metal == Metal.Default.BRASS)
            {
                accept(out, TFCBlocks.BRASS_BELL);
                accept(out, TFCItems.BRASS_MECHANISMS);
                accept(out, TFCItems.JACKS);
            }
            else if (metal == Metal.Default.GOLD)
                out.accept(Blocks.BELL);
            else if (metal == Metal.Default.RED_STEEL)
                accept(out, TFCItems.RED_STEEL_BUCKET);
            else if (metal == Metal.Default.BLUE_STEEL)
                accept(out, TFCItems.BLUE_STEEL_BUCKET);
            else if (metal == Metal.Default.WROUGHT_IRON)
                accept(out, TFCItems.WROUGHT_IRON_GRILL);
            else if (metal == Metal.Default.STEEL)
            {
                accept(out, TFCBlocks.STEEL_PIPE);
                accept(out, TFCBlocks.STEEL_PUMP);
            }

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
                Rock.BlockType.MOSSY_LOOSE,
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
        for (RockCategory.ItemType type : RockCategory.ItemType.values())
        {
            for (RockCategory category : RockCategory.values())
            {
                accept(out, TFCItems.ROCK_TOOLS, category, type);
            }
        }
    }

    private static void fillFoodTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        TFCItems.FOOD.values().forEach(reg -> accept(out, reg));
        TFCItems.SOUPS.values().forEach(reg -> accept(out, reg));
        TFCItems.SALADS.values().forEach(reg -> accept(out, reg));

        accept(out, TFCItems.EMPTY_JAR);
        accept(out, TFCItems.EMPTY_JAR_WITH_LID);

        for (Food food : Food.values())
        {
            accept(out, TFCItems.FRUIT_PRESERVES, food);
            accept(out, TFCItems.UNSEALED_FRUIT_PRESERVES, food);
        }
    }

    private static void fillMiscTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        // These only display in dev. First one is the normal book, second is the hot reloaded one
        if (!FMLEnvironment.production)
        {
            out.accept(PatchouliIntegration.getFieldGuide(false));
            out.accept(PatchouliIntegration.getFieldGuide(true));
        }

        accept(out, TFCItems.FIRESTARTER);
        out.accept(Items.FLINT_AND_STEEL);
        accept(out, TFCItems.SOOT);
        accept(out, TFCItems.SANDPAPER);
        accept(out, TFCItems.BONE_NEEDLE);
        accept(out, TFCItems.BLANK_DISC);
        accept(out, TFCItems.BRASS_MECHANISMS);
        accept(out, TFCItems.BURLAP_CLOTH);
        accept(out, TFCItems.SILK_CLOTH);
        accept(out, TFCItems.WOOL_CLOTH);
        accept(out, TFCItems.WOOL);
        accept(out, TFCItems.WOOL_YARN);
        accept(out, TFCItems.SPINDLE);
        accept(out, TFCItems.COMPOST);
        accept(out, TFCItems.ROTTEN_COMPOST);
        accept(out, TFCItems.PURE_NITROGEN);
        accept(out, TFCItems.PURE_POTASSIUM);
        accept(out, TFCItems.PURE_PHOSPHORUS);
        accept(out, TFCItems.DAUB);
        accept(out, TFCItems.DIRTY_JUTE_NET);
        accept(out, TFCItems.FIRE_CLAY);
        accept(out, TFCItems.KAOLIN_CLAY);
        accept(out, TFCItems.GLUE);
        accept(out, TFCItems.GOAT_HORN);
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
        accept(out, TFCItems.STICK_BUNCH);
        accept(out, TFCItems.STICK_BUNDLE);
        out.accept(Items.BOWL);
        accept(out, TFCItems.STRAW);
        accept(out, TFCItems.WROUGHT_IRON_GRILL);
        accept(out, TFCItems.LOAM_MUD_BRICK);
        accept(out, TFCItems.SANDY_LOAM_MUD_BRICK);
        accept(out, TFCItems.SILTY_LOAM_MUD_BRICK);
        accept(out, TFCItems.SILT_MUD_BRICK);

        TFCItems.POWDERS.values().forEach(p -> accept(out, p));
        TFCItems.ORE_POWDERS.values().forEach(p -> accept(out, p));
        for (Gem gem : Gem.values())
        {
            accept(out, TFCItems.GEMS, gem);
            accept(out, TFCItems.GEM_DUST, gem);
        }

        accept(out, TFCItems.BLUBBER);
        for (HideItemType type : HideItemType.values())
        {
            TFCItems.HIDES.get(type).values().forEach(reg -> accept(out, reg));
        }
        accept(out, TFCItems.TREATED_HIDE);
        out.accept(Items.INK_SAC);
        out.accept(Items.GLOW_INK_SAC);
        accept(out, TFCItems.GLOW_ARROW);

        accept(out, TFCItems.ALABASTER_BRICK);
        accept(out, TFCItems.UNFIRED_BRICK);
        out.accept(Items.BRICK);
        accept(out, TFCItems.UNFIRED_FIRE_BRICK);
        accept(out, TFCItems.FIRE_BRICK);
        accept(out, TFCItems.UNFIRED_CRUCIBLE);
        accept(out, TFCBlocks.CRUCIBLE);
        accept(out, TFCItems.UNFIRED_FLOWER_POT);
        out.accept(Items.FLOWER_POT);
        accept(out, TFCItems.UNFIRED_BOWL);
        accept(out, TFCBlocks.CERAMIC_BOWL);
        accept(out, TFCItems.UNFIRED_PAN);
        accept(out, TFCItems.EMPTY_PAN);
        accept(out, TFCItems.UNFIRED_SPINDLE_HEAD);
        accept(out, TFCItems.SPINDLE_HEAD);
        accept(out, TFCItems.UNFIRED_POT);
        accept(out, TFCItems.POT);
        accept(out, TFCItems.UNFIRED_VESSEL);
        accept(out, TFCItems.VESSEL);
        accept(out, TFCItems.UNFIRED_LARGE_VESSEL);
        accept(out, TFCBlocks.LARGE_VESSEL);
        accept(out, TFCItems.UNFIRED_JUG);
        for (DyeColor color : DyeColor.values())
        {
            accept(out, TFCItems.UNFIRED_GLAZED_VESSELS, color);
            accept(out, TFCItems.GLAZED_VESSELS, color);
            accept(out, TFCItems.UNFIRED_GLAZED_LARGE_VESSELS, color);
            accept(out, TFCBlocks.GLAZED_LARGE_VESSELS, color);
        }
        for (Metal.ItemType type : Metal.ItemType.values())
        {
            accept(out, TFCItems.UNFIRED_MOLDS, type);
            accept(out, TFCItems.MOLDS, type);
            if (type == Metal.ItemType.INGOT)
            {
                accept(out, TFCItems.UNFIRED_FIRE_INGOT_MOLD);
                accept(out, TFCItems.FIRE_INGOT_MOLD);
            }
        }
        accept(out, TFCItems.UNFIRED_BELL_MOLD);
        accept(out, TFCItems.BELL_MOLD);

        accept(out, TFCItems.WOODEN_BUCKET);
        accept(out, TFCItems.JUG);
        accept(out, TFCItems.UNFIRED_BLOWPIPE);
        accept(out, TFCItems.CERAMIC_BLOWPIPE);
        accept(out, TFCItems.CERAMIC_BLOWPIPE_WITH_GLASS);
        accept(out, TFCItems.BLOWPIPE);
        accept(out, TFCItems.BLOWPIPE_WITH_GLASS);
        accept(out, TFCItems.GEM_SAW);
        accept(out, TFCItems.JACKS);
        accept(out, TFCItems.PADDLE);
        accept(out, TFCItems.SILICA_GLASS_BATCH);
        accept(out, TFCItems.HEMATITIC_GLASS_BATCH);
        accept(out, TFCItems.OLIVINE_GLASS_BATCH);
        accept(out, TFCItems.VOLCANIC_GLASS_BATCH);
        accept(out, TFCItems.LAMP_GLASS);
        accept(out, TFCItems.LENS);
        accept(out, TFCItems.SILICA_GLASS_BOTTLE);
        accept(out, TFCItems.HEMATITIC_GLASS_BOTTLE);
        accept(out, TFCItems.OLIVINE_GLASS_BOTTLE);
        accept(out, TFCItems.VOLCANIC_GLASS_BOTTLE);
        accept(out, TFCBlocks.HAND_WHEEL_BASE);
        accept(out, TFCItems.HAND_WHEEL);
        accept(out, TFCItems.EMPTY_JAR);
        accept(out, TFCItems.EMPTY_JAR_WITH_LID);
        accept(out, TFCItems.JAR_LID);
        accept(out, TFCItems.WINDMILL_BLADE);
        TFCItems.COLORED_WINDMILL_BLADES.values().forEach(blade -> accept(out, blade));
        accept(out, TFCItems.RUSTIC_WINDMILL_BLADE);
        accept(out, TFCItems.LATTICE_WINDMILL_BLADE);
        consumeOurs(BuiltInRegistries.FLUID, fluid -> out.accept(fluid.getBucket()));

        TFCItems.FRESHWATER_FISH_BUCKETS.values().forEach(reg -> accept(out, reg));
        accept(out, TFCItems.COD_BUCKET);
        accept(out, TFCItems.JELLYFISH_BUCKET);
        accept(out, TFCItems.TROPICAL_FISH_BUCKET);
        accept(out, TFCItems.PUFFERFISH_BUCKET);

        consumeOurs(BuiltInRegistries.ENTITY_TYPE, entity -> {
            final var item = ForgeSpawnEggItem.fromEntityType(entity);
            if (item != null)
            {
                out.accept(item);
            }
        });
    }

    private static void fillDecorationsTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        accept(out, TFCBlocks.WATTLE);
        accept(out, TFCBlocks.UNSTAINED_WATTLE);
        TFCBlocks.STAINED_WATTLE.values().forEach(reg -> accept(out, reg));
        accept(out, TFCBlocks.THATCH);
        accept(out, TFCBlocks.THATCH_BED);
        accept(out, TFCBlocks.FIREPIT);
        accept(out, TFCBlocks.GRILL);
        accept(out, TFCBlocks.POT);
        accept(out, TFCBlocks.BELLOWS);
        accept(out, TFCBlocks.POWDERKEG);
        accept(out, TFCBlocks.BARREL_RACK);
        accept(out, TFCBlocks.CERAMIC_BOWL);
        accept(out, TFCBlocks.QUERN);
        accept(out, TFCItems.HANDSTONE);
        accept(out, TFCBlocks.CRANKSHAFT);
        accept(out, TFCBlocks.TRIP_HAMMER);
        accept(out, TFCBlocks.CRUCIBLE);
        accept(out, TFCBlocks.COMPOSTER);
        accept(out, TFCBlocks.BLOOMERY);
        accept(out, TFCBlocks.BLAST_FURNACE);
        accept(out, TFCBlocks.NEST_BOX);
        accept(out, TFCBlocks.MELON);
        accept(out, TFCBlocks.PUMPKIN);
        out.accept(Blocks.CARVED_PUMPKIN);
        accept(out, TFCBlocks.JACK_O_LANTERN);
        accept(out, TFCItems.TORCH);
        accept(out, TFCItems.DEAD_TORCH);
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
        accept(out, TFCBlocks.LARGE_VESSEL);
        TFCBlocks.GLAZED_LARGE_VESSELS.values().forEach(reg -> accept(out, reg));
        accept(out, TFCBlocks.CANDLE);
        accept(out, TFCBlocks.CAKE);
        for (DyeColor color : DyeColor.values())
        {
            accept(out, TFCBlocks.DYED_CANDLE, color);
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
                if (type == Wood.BlockType.SAPLING)
                {
                    switch (wood)
                    {
                        case PINE -> accept(out, TFCBlocks.PINE_KRUMMHOLZ);
                        case SPRUCE -> accept(out, TFCBlocks.SPRUCE_KRUMMHOLZ);
                        case WHITE_CEDAR -> accept(out, TFCBlocks.WHITE_CEDAR_KRUMMHOLZ);
                        case DOUGLAS_FIR -> accept(out, TFCBlocks.DOUGLAS_FIR_KRUMMHOLZ);
                        case ASPEN -> accept(out, TFCBlocks.ASPEN_KRUMMHOLZ);
                    }
                }
            });
            if (wood == Wood.PALM)
            {
                accept(out, TFCBlocks.PALM_MOSAIC);
                accept(out, TFCBlocks.PALM_MOSAIC_STAIRS);
                accept(out, TFCBlocks.PALM_MOSAIC_SLAB);
            }
            accept(out, TFCItems.LUMBER, wood);
            accept(out, TFCItems.BOATS, wood);
            accept(out, TFCItems.SUPPORTS, wood);
            accept(out, TFCItems.CHEST_MINECARTS, wood);
            accept(out, TFCItems.SIGNS, wood);

            for (Metal.Default metal : Metal.Default.values())
            {
                accept(out, TFCItems.HANGING_SIGNS.get(wood), metal);
            }
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

    private static CreativeTabHolder register(String name, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator displayItems)
    {
        final RegistryObject<CreativeModeTab> reg = CREATIVE_TABS.register(name, () -> CreativeModeTab.builder()
            .icon(icon)
            .title(Component.translatable("tfc.creative_tab." + name))
            .displayItems(displayItems)
            .build());
        return new CreativeTabHolder(reg, displayItems);
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
        if (reg.get().asItem() == Items.AIR)
        {
            TerraFirmaCraft.LOGGER.error("BlockItem with no Item added to creative tab: " + reg);
            SelfTests.reportExternalError();
            return;
        }
        out.accept(reg.get());
    }

    private static void accept(CreativeModeTab.Output out, DecorationBlockRegistryObject decoration)
    {
        out.accept(decoration.stair().get());
        out.accept(decoration.slab().get());
        out.accept(decoration.wall().get());
    }

    private static <T> void consumeOurs(Registry<T> registry, Consumer<T> consumer)
    {
        for (T value : registry)
        {
            if (Objects.requireNonNull(registry.getKey(value)).getNamespace().equals(TerraFirmaCraft.MOD_ID))
            {
                consumer.accept(value);
            }
        }
    }

    public record CreativeTabHolder(RegistryObject<CreativeModeTab> tab, CreativeModeTab.DisplayItemsGenerator generator) {}
}