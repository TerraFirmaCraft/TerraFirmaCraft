/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryModifiable;

import net.dries007.tfc.ConfigTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class RecipeRegistryEvents
{
    @SubscribeEvent
    public static void onRecipeRegister(RegistryEvent.Register<IRecipe> event)
    {
        if (ConfigTFC.General.OVERRIDES.removeVanillaRecipes)
        {
            IForgeRegistryModifiable<IRecipe> registry = (IForgeRegistryModifiable<IRecipe>) event.getRegistry();

            //misc AKA too lazy to categorize somehow
            RecipeUtils.removeRecipeByName(registry, "minecraft", "sugar");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "speckled_melon");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "sea_lantern");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "rabbit_stew_from_red_mushroom");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "rabbit_stew_from_brown_mushroom");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "pumpkin_pie");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "prismarine_bricks");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "prismarine");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "paper");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "mushroom_stew");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "melon_seeds");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "melon_block");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "pumpkin_seeds");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "golden_apple");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "glowstone");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "furnace");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "flint_and_steel");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "fishing_rod");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "fire_charge");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "fermented_spider_eye");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "crafting_table");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "cookie");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "compass");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "comparator");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "repeater");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "coarse_dirt");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "chest");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "cauldron");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "carrot_on_a_stick");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "cake");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "bucket");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "brewing_stand");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "bread");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "bookshelf");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "boat");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "beetroot_soup");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "beacon");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "armor_stand");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "anvil");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "painting");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "torch");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "shield");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "shears");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "lead");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "glass_bottle");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "flower_pot");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "brick_block");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "enchanting_table");

            //breakydowny, buildyupy things.
            RecipeUtils.removeRecipeByName(registry, "minecraft", "wheat");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "slime_ball");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "slime");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "lapis_lazuli");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "lapis_block");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "iron_ingot_from_nuggets");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "iron_ingot_from_block");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "iron_nugget");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "iron_block");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "iron_bars");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "hay_block");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "gold_nugget");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "gold_ingot_from_nuggets");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "gold_ingot_from_block");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "gold_block");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "diamond");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "dark_prismarine");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "emerald_block");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "emerald");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "coal_block");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "coal");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "bone_meal_from_bone");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "bone_meal_from_block");

            //nether
            RecipeUtils.removeRecipeByName(registry, "minecraft", "red_nether_brick");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "quartz_stairs");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "quartz_slab");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "quartz_block");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "pillar_quartz_block");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "nether_wart_block");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "nether_brick_stairs");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "nether_brick_slab");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "nether_brick_fence");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "nether_brick");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "magma_cream");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "magma");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "chiseled_quartz_block");

            //end
            RecipeUtils.removeRecipeByName(registry, "minecraft", "purpur_stairs");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "purpur_slab");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "purpur_pillar");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "purpur_block");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "ender_eye");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "end_rod");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "end_crystal");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "end_bricks");

            //Fire Works
            RecipeUtils.removeRecipeByName(registry, "minecraft", "fireworks");

            //leather
            RecipeUtils.removeRecipeByName(registry, "minecraft", "leather_helmet");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "leather_chestplate");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "leather_leggings");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "leather_boots");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "leather");

            //General wood
            RecipeUtils.removeRecipeByName(registry, "minecraft", "wooden_hoe");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "wooden_axe");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "wooden_sword");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "wooden_shovel");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "wooden_pickaxe");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "wooden_button");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "wooden_pressure_plate");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "wooden_door");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "trapdoor");
            //birch
            RecipeUtils.removeRecipeByName(registry, "minecraft", "birch_wooden_slab");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "birch_stairs");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "birch_planks");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "birch_fence_gate");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "birch_fence");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "birch_door");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "birch_boat");
            //acacia
            RecipeUtils.removeRecipeByName(registry, "minecraft", "acacia_wooden_slab");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "acacia_stairs");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "acacia_planks");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "acacia_fence_gate");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "acacia_fence");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "acacia_door");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "acacia_boat");
            //dark oak
            RecipeUtils.removeRecipeByName(registry, "minecraft", "dark_oak_boat");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "dark_oak_door");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "dark_oak_fence");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "dark_oak_fence_gate");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "dark_oak_planks");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "dark_oak_stairs");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "dark_oak_wooden_slab");
            //jungle
            RecipeUtils.removeRecipeByName(registry, "minecraft", "jungle_boat");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "jungle_door");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "jungle_fence");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "jungle_fence_gate");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "jungle_planks");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "jungle_stairs");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "jungle_wooden_slab");
            //oak
            RecipeUtils.removeRecipeByName(registry, "minecraft", "oak_planks");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "oak_stairs");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "oak_wooden_slab");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "fence_gate");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "fence");
            //spruce
            RecipeUtils.removeRecipeByName(registry, "minecraft", "spruce_wooden_slab");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "spruce_stairs");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "spruce_planks");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "spruce_fence_gate");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "spruce_fence");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "spruce_door");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "spruce_boat");

            //redstone
            RecipeUtils.removeRecipeByName(registry, "minecraft", "trapped_chest");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "redstone_lamp");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "piston");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "observer");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "daylight_detector");

            //rail
            RecipeUtils.removeRecipeByName(registry, "minecraft", "rail");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "minecart");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "furnace_minecart");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "detector_rail");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "chest_minecart");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "activator_rail");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "golden_rail");

            //Stone
            RecipeUtils.removeRecipeByName(registry, "minecraft", "stone_hoe");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "stone_axe");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "stone_sword");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "stone_shovel");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "stone_pickaxe");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "stonebrick");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "stone_stairs");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "stone_slab");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "stone_brick_stairs");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "stone_brick_slab");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "polished_granite");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "polished_diorite");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "polished_andesite");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "mossy_stonebrick");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "mossy_cobblestone_wall");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "mossy_cobblestone");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "granite");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "diorite");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "cobblestone_wall");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "cobblestone_slab");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "chiseled_stonebrick");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "andesite");

            //sandstone
            RecipeUtils.removeRecipeByName(registry, "minecraft", "smooth_sandstone");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "smooth_red_sandstone");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "sandstone_stairs");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "sandstone_slab");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "sandstone");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "red_sandstone_stairs");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "red_sandstone_slab");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "red_sandstone");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "chiseled_sandstone");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "chiseled_red_sandstone");

            //iron
            RecipeUtils.removeRecipeByName(registry, "minecraft", "iron_trapdoor");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "iron_hoe");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "iron_axe");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "iron_sword");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "iron_shovel");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "iron_pickaxe");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "iron_helmet");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "iron_chestplate");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "iron_leggings");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "iron_boots");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "hopper");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "heavy_weighted_pressure_plate");

            //gold
            RecipeUtils.removeRecipeByName(registry, "minecraft", "golden_helmet");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "golden_chestplate");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "golden_leggings");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "golden_boots");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "golden_hoe");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "golden_axe");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "golden_sword");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "golden_shovel");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "golden_pickaxe");

            //chainmail
            RecipeUtils.removeRecipeByName(registry, "minecraft", "chainmail_helmet");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "chainmail_chestplate");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "chainmail_leggings");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "chainmail_boots");

            //diamond
            RecipeUtils.removeRecipeByName(registry, "minecraft", "diamond_helmet");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "diamond_chestplate");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "diamond_leggings");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "diamond_boots");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "diamond_hoe");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "diamond_axe");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "diamond_sword");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "diamond_shovel");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "diamond_pickaxe");

            //arrows
            RecipeUtils.removeRecipeByName(registry, "minecraft", "tippedarrow");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "spectral_arrow");

            //white
            RecipeUtils.removeRecipeByName(registry, "minecraft", "string_to_wool");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "white_concrete_powder");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "white_bed");

            //Yellow
            RecipeUtils.removeRecipeByName(registry, "minecraft", "yellow_dye_from_sunflower");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "yellow_dye_from_dandelion");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "yellow_concrete_powder");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "yellow_bed");

            //red
            RecipeUtils.removeRecipeByName(registry, "minecraft", "red_dye_from_tulip");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "red_dye_from_rose_bush");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "red_dye_from_poppy");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "red_dye_from_beetroot");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "red_concrete_powder");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "red_bed");

            //purple
            RecipeUtils.removeRecipeByName(registry, "minecraft", "purple_concrete_powder");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "purple_bed");

            //pink
            RecipeUtils.removeRecipeByName(registry, "minecraft", "pink_dye_from_pink_tulip");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "pink_dye_from_peony");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "pink_concrete_powder");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "pink_bed");

            //orange
            RecipeUtils.removeRecipeByName(registry, "minecraft", "orange_dye_from_orange_tulip");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "orange_concrete_powder");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "orange_bed");

            //magenta
            RecipeUtils.removeRecipeByName(registry, "minecraft", "magenta_dye_from_lilac");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "magenta_dye_from_allium");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "magenta_concrete_powder");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "magenta_bed");

            //lime
            RecipeUtils.removeRecipeByName(registry, "minecraft", "lime_concrete_powder");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "magenta_concrete_powder");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "lime_bed");

            //gray
            RecipeUtils.removeRecipeByName(registry, "minecraft", "light_gray_dye_from_white_tulip");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "light_gray_dye_from_oxeye_daisy");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "light_gray_dye_from_ink_bonemeal");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "light_gray_dye_from_azure_bluet");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "light_gray_concrete_powder");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "light_gray_bed");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "gray_concrete_powder");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "gray_bed");

            //blue
            RecipeUtils.removeRecipeByName(registry, "minecraft", "light_blue_dye_from_blue_orchid");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "light_blue_concrete_powder");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "light_blue_bed");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "blue_stained_hardened_clay");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "blue_stained_glass");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "blue_concrete_powder");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "blue_bed_from_white_bed");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "blue_bed");

            //green
            RecipeUtils.removeRecipeByName(registry, "minecraft", "green_concrete_powder");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "green_bed");

            //cyan
            RecipeUtils.removeRecipeByName(registry, "minecraft", "cyan_concrete_powder");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "cyan_bed");

            //brown
            RecipeUtils.removeRecipeByName(registry, "minecraft", "brown_concrete_powder");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "brown_bed");

            //black
            RecipeUtils.removeRecipeByName(registry, "minecraft", "black_wool");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "black_stained_hardened_clay");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "black_stained_glass");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "black_concrete_powder");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "black_bed_from_white_bed");
            RecipeUtils.removeRecipeByName(registry, "minecraft", "black_bed");
        }

    }
}
