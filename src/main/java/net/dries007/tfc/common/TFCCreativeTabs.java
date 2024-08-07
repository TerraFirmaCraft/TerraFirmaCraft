/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.DecorationBlockHolder;
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
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.HideItemType;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Metal;


@SuppressWarnings("unused")
public final class TFCCreativeTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TerraFirmaCraft.MOD_ID);


    public static final Id EARTH = register("earth", () -> new ItemStack(TFCBlocks.ROCK_BLOCKS.get(Rock.QUARTZITE).get(Rock.BlockType.RAW)), TFCCreativeTabs::fillEarthTab);
    public static final Id ORES = register("ores", () -> new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.NORMAL)), TFCCreativeTabs::fillOresTab);
    public static final Id ROCKS = register("rock", () -> new ItemStack(TFCBlocks.ROCK_BLOCKS.get(Rock.ANDESITE).get(Rock.BlockType.RAW)), TFCCreativeTabs::fillRocksTab);
    public static final Id METAL = register("metals", () -> new ItemStack(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.INGOT)), TFCCreativeTabs::fillMetalTab);
    public static final Id WOOD = register("wood", () -> new ItemStack(TFCBlocks.WOODS.get(Wood.DOUGLAS_FIR).get(Wood.BlockType.LOG)), TFCCreativeTabs::fillWoodTab);
    public static final Id FOOD = register("food", () -> new ItemStack(TFCItems.FOOD.get(Food.RED_APPLE)), TFCCreativeTabs::fillFoodTab);
    public static final Id FLORA = register("flora", () -> new ItemStack(TFCBlocks.PLANTS.get(Plant.GOLDENROD)), TFCCreativeTabs::fillPlantsTab);
    public static final Id DECORATIONS = register("decorations", () -> new ItemStack(TFCBlocks.ALABASTER_BRICKS.get(DyeColor.CYAN)), TFCCreativeTabs::fillDecorationsTab);
    public static final Id MISC = register("misc", () -> new ItemStack(TFCItems.FIRESTARTER), TFCCreativeTabs::fillMiscTab);

    public static Stream<CreativeModeTab.DisplayItemsGenerator> generators()
    {
        return Stream.of(EARTH, ORES, ROCKS, METAL, WOOD, FOOD, FLORA, DECORATIONS, MISC).map(holder -> holder.generator);
    }

    public static void setAllTabContentAsNonDecaying(BuildCreativeModeTabContentsEvent event)
    {
        // todo 1.21, verify that this works properly (event priority first, then mod order). Needs an addon lol
        // Otherwise, re-add the mixin from 1.20
        FoodCapability.setTransientNonDecaying(event.getTab().getIconItem());
        event.getParentEntries().forEach(FoodCapability::setTransientNonDecaying);
        event.getSearchEntries().forEach(FoodCapability::setTransientNonDecaying);
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
        out.accept(TFCBlocks.SMOOTH_MUD_BRICKS);
        out.accept(TFCBlocks.TREE_ROOTS);
        out.accept(TFCBlocks.PEAT);
        out.accept(TFCBlocks.PEAT_GRASS);
        out.accept(TFCBlocks.WHITE_KAOLIN_CLAY);
        out.accept(TFCBlocks.PINK_KAOLIN_CLAY);
        out.accept(TFCBlocks.RED_KAOLIN_CLAY);
        out.accept(TFCBlocks.KAOLIN_CLAY_GRASS);

        TFCBlocks.GROUNDCOVER.forEach((type, reg) -> {
            if (type.getVanillaItem() == null)
            {
                out.accept(reg);
            }
            else
            {
                out.accept(type.getVanillaItem());
            }
        });
        TFCBlocks.SMALL_ORES.values().forEach(out::accept);
        TFCItems.GEMS.values().forEach(out::accept);

        for (SandBlockType type : SandBlockType.values())
        {
            accept(out, TFCBlocks.SAND, type);
            TFCBlocks.SANDSTONE.get(type).values().forEach(out::accept);
            TFCBlocks.SANDSTONE_DECORATIONS.get(type).values().forEach(reg -> accept(out, reg));
        }

        out.accept(Blocks.ICE);
        out.accept(TFCBlocks.SEA_ICE);
        out.accept(Blocks.PACKED_ICE);
        out.accept(Blocks.BLUE_ICE);

        TFCBlocks.MAGMA_BLOCKS.values().forEach(out::accept);

        for (Crop crop : Crop.values())
        {
            accept(out, TFCBlocks.WILD_CROPS, crop);
            if (crop == Crop.PUMPKIN)
                out.accept(TFCBlocks.PUMPKIN);
            else if (crop == Crop.MELON)
                out.accept(TFCBlocks.MELON);
            accept(out, TFCItems.CROP_SEEDS, crop);
        }
        TFCBlocks.SPREADING_BUSHES.values().forEach(out::accept);
        TFCBlocks.STATIONARY_BUSHES.values().forEach(out::accept);
        out.accept(TFCBlocks.CRANBERRY_BUSH);

        for (FruitBlocks.Tree tree : FruitBlocks.Tree.values())
        {
            accept(out, TFCBlocks.FRUIT_TREE_SAPLINGS, tree);
            accept(out, TFCBlocks.FRUIT_TREE_LEAVES, tree);
        }
        out.accept(TFCBlocks.BANANA_SAPLING);

        out.accept(TFCBlocks.CALCITE);
        out.accept(TFCBlocks.ICICLE);
        for (Coral coral : Coral.values())
        {
            TFCBlocks.CORAL.get(coral).values().forEach(out::accept);
            accept(out, TFCItems.CORAL_FANS, coral);
            accept(out, TFCItems.DEAD_CORAL_FANS, coral);
        }
    }

    private static void fillMetalTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        for (Metal metal : Metal.values())
        {
            for (Metal.BlockType type : new Metal.BlockType[] {
                Metal.BlockType.ANVIL,
                Metal.BlockType.BLOCK,
                Metal.BlockType.EXPOSED_BLOCK,
                Metal.BlockType.WEATHERED_BLOCK,
                Metal.BlockType.OXIDIZED_BLOCK,
                Metal.BlockType.BLOCK_SLAB,
                Metal.BlockType.EXPOSED_BLOCK_SLAB,
                Metal.BlockType.WEATHERED_BLOCK_SLAB,
                Metal.BlockType.OXIDIZED_BLOCK_SLAB,
                Metal.BlockType.BLOCK_STAIRS,
                Metal.BlockType.EXPOSED_BLOCK_STAIRS,
                Metal.BlockType.WEATHERED_BLOCK_STAIRS,
                Metal.BlockType.OXIDIZED_BLOCK_STAIRS,
                Metal.BlockType.BARS,
                Metal.BlockType.CHAIN,
                Metal.BlockType.TRAPDOOR,
                Metal.BlockType.LAMP,
            })
            {
                accept(out, TFCBlocks.METALS, metal, type);
            }

            accept(out, TFCItems.METAL_ITEMS, metal, Metal.ItemType.UNFINISHED_LAMP);

            if (metal == Metal.BRONZE)
                out.accept(TFCBlocks.BRONZE_BELL);
            else if (metal == Metal.BRASS)
            {
                out.accept(TFCBlocks.BRASS_BELL);
                out.accept(TFCItems.BRASS_MECHANISMS);
                out.accept(TFCItems.JACKS);
            }
            else if (metal == Metal.GOLD)
                out.accept(Blocks.BELL);
            else if (metal == Metal.RED_STEEL)
                out.accept(TFCItems.RED_STEEL_BUCKET);
            else if (metal == Metal.BLUE_STEEL)
                out.accept(TFCItems.BLUE_STEEL_BUCKET);
            else if (metal == Metal.WROUGHT_IRON)
                out.accept(TFCItems.WROUGHT_IRON_GRILL);
            else if (metal == Metal.STEEL)
            {
                out.accept(TFCBlocks.STEEL_PIPE);
                out.accept(TFCBlocks.STEEL_PUMP);
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
        out.accept(TFCItems.RAW_IRON_BLOOM);
        out.accept(TFCItems.REFINED_IRON_BLOOM);
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
            if (!ore.isGraded()) accept(out, TFCItems.ORES, ore);
        }
        for (OreDeposit deposit : OreDeposit.values())
        {
            TFCBlocks.ORE_DEPOSITS.values().forEach(map -> accept(out, map, deposit));
        }
        for (Ore ore : Ore.values())
        {
            if (ore.isGraded())
            {
                TFCBlocks.GRADED_ORES.values().forEach(map -> map.get(ore).values().forEach(out::accept));
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
        TFCItems.FOOD.values().forEach(out::accept);
        TFCItems.SOUPS.values().forEach(out::accept);
        TFCItems.SALADS.values().forEach(out::accept);

        out.accept(TFCItems.EMPTY_JAR);
        out.accept(TFCItems.EMPTY_JAR_WITH_LID);

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
            // todo: 1.21, these break? weird?
            //out.accept(PatchouliIntegration.getFieldGuide(false));
            //out.accept(PatchouliIntegration.getFieldGuide(true));
        }

        out.accept(TFCItems.FIRESTARTER);
        out.accept(Items.FLINT_AND_STEEL);
        out.accept(TFCItems.SOOT);
        out.accept(TFCItems.SANDPAPER);
        out.accept(TFCItems.BONE_NEEDLE);
        out.accept(TFCItems.BLANK_DISC);
        out.accept(TFCItems.BRASS_MECHANISMS);
        out.accept(TFCItems.BURLAP_CLOTH);
        out.accept(TFCItems.SILK_CLOTH);
        out.accept(TFCItems.WOOL_CLOTH);
        out.accept(TFCItems.WOOL);
        out.accept(TFCItems.WOOL_YARN);
        out.accept(TFCItems.SPINDLE);
        out.accept(TFCItems.COMPOST);
        out.accept(TFCItems.ROTTEN_COMPOST);
        out.accept(TFCItems.PURE_NITROGEN);
        out.accept(TFCItems.PURE_POTASSIUM);
        out.accept(TFCItems.PURE_PHOSPHORUS);
        out.accept(TFCItems.DAUB);
        out.accept(TFCItems.DIRTY_JUTE_NET);
        out.accept(TFCItems.FIRE_CLAY);
        out.accept(TFCItems.KAOLIN_CLAY);
        out.accept(TFCItems.GLUE);
        out.accept(TFCItems.GOAT_HORN);
        out.accept(TFCItems.JUTE);
        out.accept(TFCItems.JUTE_FIBER);
        out.accept(TFCItems.OLIVE_PASTE);
        out.accept(TFCItems.JUTE_NET);
        out.accept(TFCItems.HANDSTONE);
        out.accept(TFCItems.MORTAR);
        out.accept(TFCItems.PAPYRUS);
        out.accept(TFCItems.PAPYRUS_STRIP);
        out.accept(TFCItems.SOAKED_PAPYRUS_STRIP);
        out.accept(TFCItems.UNREFINED_PAPER);
        out.accept(TFCItems.STICK_BUNCH);
        out.accept(TFCItems.STICK_BUNDLE);
        out.accept(Items.BOWL);
        out.accept(TFCItems.STRAW);
        out.accept(TFCItems.WROUGHT_IRON_GRILL);
        out.accept(TFCItems.LOAM_MUD_BRICK);
        out.accept(TFCItems.SANDY_LOAM_MUD_BRICK);
        out.accept(TFCItems.SILTY_LOAM_MUD_BRICK);
        out.accept(TFCItems.SILT_MUD_BRICK);

        TFCItems.POWDERS.values().forEach(out::accept);
        TFCItems.ORE_POWDERS.values().forEach(out::accept);

        out.accept(TFCItems.BLUBBER);
        for (HideItemType type : HideItemType.values())
        {
            TFCItems.HIDES.get(type).values().forEach(out::accept);
        }
        out.accept(TFCItems.TREATED_HIDE);
        out.accept(Items.INK_SAC);
        out.accept(Items.GLOW_INK_SAC);
        out.accept(TFCItems.GLOW_ARROW);

        out.accept(TFCItems.ALABASTER_BRICK);
        out.accept(TFCItems.UNFIRED_BRICK);
        out.accept(Items.BRICK);
        out.accept(TFCItems.UNFIRED_FIRE_BRICK);
        out.accept(TFCItems.FIRE_BRICK);
        out.accept(TFCItems.UNFIRED_CRUCIBLE);
        out.accept(TFCBlocks.CRUCIBLE);
        out.accept(TFCItems.UNFIRED_FLOWER_POT);
        out.accept(Items.FLOWER_POT);
        out.accept(TFCItems.UNFIRED_BOWL);
        out.accept(TFCBlocks.CERAMIC_BOWL);
        out.accept(TFCItems.UNFIRED_PAN);
        out.accept(TFCItems.EMPTY_PAN);
        out.accept(TFCItems.UNFIRED_SPINDLE_HEAD);
        out.accept(TFCItems.SPINDLE_HEAD);
        out.accept(TFCItems.UNFIRED_POT);
        out.accept(TFCItems.POT);
        out.accept(TFCItems.UNFIRED_VESSEL);
        out.accept(TFCItems.VESSEL);
        out.accept(TFCItems.UNFIRED_LARGE_VESSEL);
        out.accept(TFCBlocks.LARGE_VESSEL);
        out.accept(TFCItems.UNFIRED_JUG);
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
                out.accept(TFCItems.UNFIRED_FIRE_INGOT_MOLD);
                out.accept(TFCItems.FIRE_INGOT_MOLD);
            }
        }
        out.accept(TFCItems.UNFIRED_BELL_MOLD);
        out.accept(TFCItems.BELL_MOLD);

        out.accept(TFCItems.WOODEN_BUCKET);
        out.accept(TFCItems.JUG);
        out.accept(TFCItems.UNFIRED_BLOWPIPE);
        out.accept(TFCItems.CERAMIC_BLOWPIPE);
        out.accept(TFCItems.CERAMIC_BLOWPIPE_WITH_GLASS);
        out.accept(TFCItems.BLOWPIPE);
        out.accept(TFCItems.BLOWPIPE_WITH_GLASS);
        out.accept(TFCItems.GEM_SAW);
        out.accept(TFCItems.JACKS);
        out.accept(TFCItems.PADDLE);
        out.accept(TFCItems.SILICA_GLASS_BATCH);
        out.accept(TFCItems.HEMATITIC_GLASS_BATCH);
        out.accept(TFCItems.OLIVINE_GLASS_BATCH);
        out.accept(TFCItems.VOLCANIC_GLASS_BATCH);
        out.accept(TFCItems.LAMP_GLASS);
        out.accept(TFCItems.LENS);
        out.accept(TFCItems.SILICA_GLASS_BOTTLE);
        out.accept(TFCItems.HEMATITIC_GLASS_BOTTLE);
        out.accept(TFCItems.OLIVINE_GLASS_BOTTLE);
        out.accept(TFCItems.VOLCANIC_GLASS_BOTTLE);
        out.accept(TFCItems.EMPTY_JAR);
        out.accept(TFCItems.EMPTY_JAR_WITH_LID);
        out.accept(TFCItems.JAR_LID);
        TFCItems.WINDMILL_BLADES.values().forEach(out::accept);
        out.accept(TFCItems.RUSTIC_WINDMILL_BLADE);
        out.accept(TFCItems.LATTICE_WINDMILL_BLADE);
        TFCFluids.FLUIDS.getEntries().forEach(fluid -> out.accept(fluid.value().getBucket()));

        TFCItems.FRESHWATER_FISH_BUCKETS.values().forEach(out::accept);
        out.accept(TFCItems.COD_BUCKET);
        out.accept(TFCItems.JELLYFISH_BUCKET);
        out.accept(TFCItems.TROPICAL_FISH_BUCKET);
        out.accept(TFCItems.PUFFERFISH_BUCKET);

        TFCEntities.ENTITIES.getEntries().forEach(entity -> {
            final SpawnEggItem item = SpawnEggItem.byId(entity.value());
            if (item != null)
            {
                out.accept(item);
            }
        });
    }

    private static void fillDecorationsTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        out.accept(TFCBlocks.WATTLE);
        out.accept(TFCBlocks.UNSTAINED_WATTLE);
        TFCBlocks.STAINED_WATTLE.values().forEach(out::accept);
        out.accept(TFCBlocks.THATCH);
        out.accept(TFCBlocks.THATCH_BED);
        out.accept(TFCBlocks.FIREPIT);
        out.accept(TFCBlocks.GRILL);
        out.accept(TFCBlocks.POT);
        out.accept(TFCBlocks.BELLOWS);
        out.accept(TFCBlocks.POWDERKEG);
        out.accept(TFCBlocks.BARREL_RACK);
        out.accept(TFCBlocks.CERAMIC_BOWL);
        out.accept(TFCBlocks.QUERN);
        out.accept(TFCItems.HANDSTONE);
        out.accept(TFCBlocks.CRANKSHAFT);
        out.accept(TFCBlocks.TRIP_HAMMER);
        out.accept(TFCBlocks.CRUCIBLE);
        out.accept(TFCBlocks.COMPOSTER);
        out.accept(TFCBlocks.BLOOMERY);
        out.accept(TFCBlocks.BLAST_FURNACE);
        out.accept(TFCBlocks.NEST_BOX);
        out.accept(TFCBlocks.MELON);
        out.accept(TFCBlocks.PUMPKIN);
        out.accept(Blocks.CARVED_PUMPKIN);
        out.accept(TFCBlocks.JACK_O_LANTERN);
        out.accept(TFCItems.TORCH);
        out.accept(TFCItems.DEAD_TORCH);
        out.accept(TFCBlocks.BARREL_RACK);
        out.accept(TFCBlocks.FIRE_BRICKS);
        out.accept(TFCBlocks.FIRE_CLAY_BLOCK);

        out.accept(TFCBlocks.AGGREGATE);
        out.accept(TFCBlocks.PLAIN_ALABASTER);
        out.accept(TFCBlocks.PLAIN_ALABASTER_BRICKS);
        out.accept(TFCBlocks.PLAIN_POLISHED_ALABASTER);
        for (DyeColor color : DyeColor.values())
        {
            accept(out, TFCBlocks.RAW_ALABASTER, color);
            accept(out, TFCBlocks.ALABASTER_BRICKS, color);
            accept(out, TFCBlocks.ALABASTER_BRICK_DECORATIONS.get(color));
            accept(out, TFCBlocks.POLISHED_ALABASTER, color);
            accept(out, TFCBlocks.ALABASTER_POLISHED_DECORATIONS.get(color));
        }
        out.accept(TFCBlocks.LARGE_VESSEL);
        TFCBlocks.GLAZED_LARGE_VESSELS.values().forEach(out::accept);
        out.accept(TFCBlocks.CANDLE);
        out.accept(TFCBlocks.CAKE);
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
                    out.accept(reg);
                }
                if (type == Wood.BlockType.SAPLING)
                {
                    switch (wood)
                    {
                        case PINE -> out.accept(TFCBlocks.PINE_KRUMMHOLZ);
                        case SPRUCE -> out.accept(TFCBlocks.SPRUCE_KRUMMHOLZ);
                        case WHITE_CEDAR -> out.accept(TFCBlocks.WHITE_CEDAR_KRUMMHOLZ);
                        case DOUGLAS_FIR -> out.accept(TFCBlocks.DOUGLAS_FIR_KRUMMHOLZ);
                        case ASPEN -> out.accept(TFCBlocks.ASPEN_KRUMMHOLZ);
                    }
                }
            });
            if (wood == Wood.PALM)
            {
                out.accept(TFCBlocks.PALM_MOSAIC);
                out.accept(TFCBlocks.PALM_MOSAIC_STAIRS);
                out.accept(TFCBlocks.PALM_MOSAIC_SLAB);
            }
            accept(out, TFCItems.LUMBER, wood);
            accept(out, TFCItems.BOATS, wood);
            accept(out, TFCItems.SUPPORTS, wood);
            accept(out, TFCItems.CHEST_MINECARTS, wood);
            accept(out, TFCItems.SIGNS, wood);

            for (Metal metal : Metal.values())
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
                out.accept(reg);
            }
        });
        out.accept(TFCBlocks.SEA_PICKLE);
    }
    
    
    // Helpers

    private static Id register(String name, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator displayItems)
    {
        final var holder = CREATIVE_TABS.register(name, () -> CreativeModeTab.builder()
            .icon(icon)
            .title(Component.translatable("tfc.creative_tab." + name))
            .displayItems(displayItems)
            .build());
        return new Id(holder, displayItems);
    }

    private static <R extends ItemLike, K1, K2> void accept(CreativeModeTab.Output out, Map<K1, Map<K2, R>> map, K1 key1, K2 key2)
    {
        if (map.containsKey(key1))
        {
            accept(out, map.get(key1), key2);
        }
    }

    private static <R extends ItemLike, K> void accept(CreativeModeTab.Output out, Map<K, R> map, K key)
    {
        if (map.containsKey(key))
        {
            out.accept(map.get(key));
        }
    }

    private static void accept(CreativeModeTab.Output out, DecorationBlockHolder decoration)
    {
        out.accept(decoration.stair());
        out.accept(decoration.slab());
        out.accept(decoration.wall());
    }

    public record Id(DeferredHolder<CreativeModeTab, CreativeModeTab> tab, CreativeModeTab.DisplayItemsGenerator generator) {}
}