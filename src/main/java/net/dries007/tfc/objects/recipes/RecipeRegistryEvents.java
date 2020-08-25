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
        // todo: in 1.13 move to json overrides
        // This causes massive log spawm
        // See https://github.com/MinecraftForge/MinecraftForge/pull/4541#issuecomment-354033516
        if (ConfigTFC.General.OVERRIDES.removeVanillaRecipes)
        {
            IForgeRegistryModifiable<IRecipe> modRegistry = (IForgeRegistryModifiable<IRecipe>) event.getRegistry();

            //misc AKA too lazy to categorize somehow
            modRegistry.remove(new ResourceLocation("minecraft:sugar"));
            modRegistry.remove(new ResourceLocation("minecraft:speckled_melon"));
            modRegistry.remove(new ResourceLocation("minecraft:sea_lantern"));
            modRegistry.remove(new ResourceLocation("minecraft:rabbit_stew_from_red_mushroom"));
            modRegistry.remove(new ResourceLocation("minecraft:rabbit_stew_from_brown_mushroom"));
            modRegistry.remove(new ResourceLocation("minecraft:pumpkin_pie"));
            modRegistry.remove(new ResourceLocation("minecraft:prismarine_bricks"));
            modRegistry.remove(new ResourceLocation("minecraft:prismarine"));
            modRegistry.remove(new ResourceLocation("minecraft:paper"));
            modRegistry.remove(new ResourceLocation("minecraft:mushroom_stew"));
            modRegistry.remove(new ResourceLocation("minecraft:melon_seeds"));
            modRegistry.remove(new ResourceLocation("minecraft:melon_block"));
            modRegistry.remove(new ResourceLocation("minecraft:pumpkin_seeds"));
            modRegistry.remove(new ResourceLocation("minecraft:golden_apple"));
            modRegistry.remove(new ResourceLocation("minecraft:glowstone"));
            modRegistry.remove(new ResourceLocation("minecraft:furnace"));
            modRegistry.remove(new ResourceLocation("minecraft:flint_and_steel"));
            modRegistry.remove(new ResourceLocation("minecraft:fishing_rod"));
            modRegistry.remove(new ResourceLocation("minecraft:fire_charge"));
            modRegistry.remove(new ResourceLocation("minecraft:fermented_spider_eye"));
            modRegistry.remove(new ResourceLocation("minecraft:crafting_table"));
            modRegistry.remove(new ResourceLocation("minecraft:cookie"));
            modRegistry.remove(new ResourceLocation("minecraft:compass"));
            modRegistry.remove(new ResourceLocation("minecraft:comparator"));
            modRegistry.remove(new ResourceLocation("minecraft:repeater"));
            modRegistry.remove(new ResourceLocation("minecraft:coarse_dirt"));
            modRegistry.remove(new ResourceLocation("minecraft:chest"));
            modRegistry.remove(new ResourceLocation("minecraft:cauldron"));
            modRegistry.remove(new ResourceLocation("minecraft:carrot_on_a_stick"));
            modRegistry.remove(new ResourceLocation("minecraft:cake"));
            modRegistry.remove(new ResourceLocation("minecraft:bucket"));
            modRegistry.remove(new ResourceLocation("minecraft:brewing_stand"));
            modRegistry.remove(new ResourceLocation("minecraft:bread"));
            modRegistry.remove(new ResourceLocation("minecraft:bookshelf"));
            modRegistry.remove(new ResourceLocation("minecraft:boat"));
            modRegistry.remove(new ResourceLocation("minecraft:beetroot_soup"));
            modRegistry.remove(new ResourceLocation("minecraft:beacon"));
            modRegistry.remove(new ResourceLocation("minecraft:armor_stand"));
            modRegistry.remove(new ResourceLocation("minecraft:anvil"));
            modRegistry.remove(new ResourceLocation("minecraft:painting"));
            modRegistry.remove(new ResourceLocation("minecraft:torch"));
            modRegistry.remove(new ResourceLocation("minecraft:shield"));
            modRegistry.remove(new ResourceLocation("minecraft:shears"));
            modRegistry.remove(new ResourceLocation("minecraft:lead"));
            modRegistry.remove(new ResourceLocation("minecraft:glass_bottle"));
            modRegistry.remove(new ResourceLocation("minecraft:flower_pot"));
            modRegistry.remove(new ResourceLocation("minecraft:brick_block"));
            modRegistry.remove(new ResourceLocation("minecraft:enchanting_table"));

            //breakydowny, buildyupy things.
            modRegistry.remove(new ResourceLocation("minecraft:wheat"));
            modRegistry.remove(new ResourceLocation("minecraft:slime_ball"));
            modRegistry.remove(new ResourceLocation("minecraft:slime"));
            modRegistry.remove(new ResourceLocation("minecraft:lapis_lazuli"));
            modRegistry.remove(new ResourceLocation("minecraft:lapis_block"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_ingot_from_nuggets"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_ingot_from_block"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_nugget"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_block"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_bars"));
            modRegistry.remove(new ResourceLocation("minecraft:hay_block"));
            modRegistry.remove(new ResourceLocation("minecraft:gold_nugget"));
            modRegistry.remove(new ResourceLocation("minecraft:gold_ingot_from_nuggets"));
            modRegistry.remove(new ResourceLocation("minecraft:gold_ingot_from_block"));
            modRegistry.remove(new ResourceLocation("minecraft:gold_block"));
            modRegistry.remove(new ResourceLocation("minecraft:diamond"));
            modRegistry.remove(new ResourceLocation("minecraft:dark_prismarine"));
            modRegistry.remove(new ResourceLocation("minecraft:emerald_block"));
            modRegistry.remove(new ResourceLocation("minecraft:emerald"));
            modRegistry.remove(new ResourceLocation("minecraft:coal_block"));
            modRegistry.remove(new ResourceLocation("minecraft:coal"));
            modRegistry.remove(new ResourceLocation("minecraft:bone_meal_from_bone"));
            modRegistry.remove(new ResourceLocation("minecraft:bone_meal_from_block"));

            //nether
            modRegistry.remove(new ResourceLocation("minecraft:red_nether_brick"));
            modRegistry.remove(new ResourceLocation("minecraft:quartz_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:quartz_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:quartz_block"));
            modRegistry.remove(new ResourceLocation("minecraft:pillar_quartz_block"));
            modRegistry.remove(new ResourceLocation("minecraft:nether_wart_block"));
            modRegistry.remove(new ResourceLocation("minecraft:nether_brick_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:nether_brick_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:nether_brick_fence"));
            modRegistry.remove(new ResourceLocation("minecraft:nether_brick"));
            modRegistry.remove(new ResourceLocation("minecraft:magma_cream"));
            modRegistry.remove(new ResourceLocation("minecraft:magma"));
            modRegistry.remove(new ResourceLocation("minecraft:chiseled_quartz_block"));

            //end
            modRegistry.remove(new ResourceLocation("minecraft:purpur_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:purpur_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:purpur_pillar"));
            modRegistry.remove(new ResourceLocation("minecraft:purpur_block"));
            modRegistry.remove(new ResourceLocation("minecraft:ender_eye"));
            modRegistry.remove(new ResourceLocation("minecraft:end_rod"));
            modRegistry.remove(new ResourceLocation("minecraft:end_crystal"));
            modRegistry.remove(new ResourceLocation("minecraft:end_bricks"));

            //Fire Works
            modRegistry.remove(new ResourceLocation("minecraft:fireworks"));

            //leather
            modRegistry.remove(new ResourceLocation("minecraft:leather_helmet"));
            modRegistry.remove(new ResourceLocation("minecraft:leather_chestplate"));
            modRegistry.remove(new ResourceLocation("minecraft:leather_leggings"));
            modRegistry.remove(new ResourceLocation("minecraft:leather_boots"));
            modRegistry.remove(new ResourceLocation("minecraft:leather"));

            //General wood
            modRegistry.remove(new ResourceLocation("minecraft:wooden_hoe"));
            modRegistry.remove(new ResourceLocation("minecraft:wooden_axe"));
            modRegistry.remove(new ResourceLocation("minecraft:wooden_sword"));
            modRegistry.remove(new ResourceLocation("minecraft:wooden_shovel"));
            modRegistry.remove(new ResourceLocation("minecraft:wooden_pickaxe"));
            modRegistry.remove(new ResourceLocation("minecraft:wooden_button"));
            modRegistry.remove(new ResourceLocation("minecraft:wooden_pressure_plate"));
            modRegistry.remove(new ResourceLocation("minecraft:wooden_door"));
            modRegistry.remove(new ResourceLocation("minecraft:trapdoor"));
            //birch
            modRegistry.remove(new ResourceLocation("minecraft:birch_wooden_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:birch_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:birch_planks"));
            modRegistry.remove(new ResourceLocation("minecraft:birch_fence_gate"));
            modRegistry.remove(new ResourceLocation("minecraft:birch_fence"));
            modRegistry.remove(new ResourceLocation("minecraft:birch_door"));
            modRegistry.remove(new ResourceLocation("minecraft:birch_boat"));
            //acacia
            modRegistry.remove(new ResourceLocation("minecraft:acacia_wooden_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:acacia_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:acacia_planks"));
            modRegistry.remove(new ResourceLocation("minecraft:acacia_fence_gate"));
            modRegistry.remove(new ResourceLocation("minecraft:acacia_fence"));
            modRegistry.remove(new ResourceLocation("minecraft:acacia_door"));
            modRegistry.remove(new ResourceLocation("minecraft:acacia_boat"));
            //dark oak
            modRegistry.remove(new ResourceLocation("minecraft:dark_oak_boat"));
            modRegistry.remove(new ResourceLocation("minecraft:dark_oak_door"));
            modRegistry.remove(new ResourceLocation("minecraft:dark_oak_fence"));
            modRegistry.remove(new ResourceLocation("minecraft:dark_oak_fence_gate"));
            modRegistry.remove(new ResourceLocation("minecraft:dark_oak_planks"));
            modRegistry.remove(new ResourceLocation("minecraft:dark_oak_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:dark_oak_wooden_slab"));
            //jungle
            modRegistry.remove(new ResourceLocation("minecraft:jungle_boat"));
            modRegistry.remove(new ResourceLocation("minecraft:jungle_door"));
            modRegistry.remove(new ResourceLocation("minecraft:jungle_fence"));
            modRegistry.remove(new ResourceLocation("minecraft:jungle_fence_gate"));
            modRegistry.remove(new ResourceLocation("minecraft:jungle_planks"));
            modRegistry.remove(new ResourceLocation("minecraft:jungle_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:jungle_wooden_slab"));
            //oak
            modRegistry.remove(new ResourceLocation("minecraft:oak_planks"));
            modRegistry.remove(new ResourceLocation("minecraft:oak_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:oak_wooden_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:fence_gate"));
            modRegistry.remove(new ResourceLocation("minecraft:fence"));
            //spruce
            modRegistry.remove(new ResourceLocation("minecraft:spruce_wooden_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:spruce_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:spruce_planks"));
            modRegistry.remove(new ResourceLocation("minecraft:spruce_fence_gate"));
            modRegistry.remove(new ResourceLocation("minecraft:spruce_fence"));
            modRegistry.remove(new ResourceLocation("minecraft:spruce_door"));
            modRegistry.remove(new ResourceLocation("minecraft:spruce_boat"));

            //redstone
            modRegistry.remove(new ResourceLocation("minecraft:trapped_chest"));
            modRegistry.remove(new ResourceLocation("minecraft:redstone_lamp"));
            modRegistry.remove(new ResourceLocation("minecraft:piston"));
            modRegistry.remove(new ResourceLocation("minecraft:observer"));
            modRegistry.remove(new ResourceLocation("minecraft:daylight_detector"));

            //rail
            modRegistry.remove(new ResourceLocation("minecraft:rail"));
            modRegistry.remove(new ResourceLocation("minecraft:minecart"));
            modRegistry.remove(new ResourceLocation("minecraft:furnace_minecart"));
            modRegistry.remove(new ResourceLocation("minecraft:detector_rail"));
            modRegistry.remove(new ResourceLocation("minecraft:chest_minecart"));
            modRegistry.remove(new ResourceLocation("minecraft:activator_rail"));
            modRegistry.remove(new ResourceLocation("minecraft:golden_rail"));

            //Stone
            modRegistry.remove(new ResourceLocation("minecraft:stone_hoe"));
            modRegistry.remove(new ResourceLocation("minecraft:stone_axe"));
            modRegistry.remove(new ResourceLocation("minecraft:stone_sword"));
            modRegistry.remove(new ResourceLocation("minecraft:stone_shovel"));
            modRegistry.remove(new ResourceLocation("minecraft:stone_pickaxe"));
            modRegistry.remove(new ResourceLocation("minecraft:stonebrick"));
            modRegistry.remove(new ResourceLocation("minecraft:stone_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:stone_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:stone_brick_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:stone_brick_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:polished_granite"));
            modRegistry.remove(new ResourceLocation("minecraft:polished_diorite"));
            modRegistry.remove(new ResourceLocation("minecraft:polished_andesite"));
            modRegistry.remove(new ResourceLocation("minecraft:mossy_stonebrick"));
            modRegistry.remove(new ResourceLocation("minecraft:mossy_cobblestone_wall"));
            modRegistry.remove(new ResourceLocation("minecraft:mossy_cobblestone"));
            modRegistry.remove(new ResourceLocation("minecraft:granite"));
            modRegistry.remove(new ResourceLocation("minecraft:diorite"));
            modRegistry.remove(new ResourceLocation("minecraft:cobblestone_wall"));
            modRegistry.remove(new ResourceLocation("minecraft:cobblestone_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:chiseled_stonebrick"));
            modRegistry.remove(new ResourceLocation("minecraft:andesite"));

            //sandstone
            modRegistry.remove(new ResourceLocation("minecraft:smooth_sandstone"));
            modRegistry.remove(new ResourceLocation("minecraft:smooth_red_sandstone"));
            modRegistry.remove(new ResourceLocation("minecraft:sandstone_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:sandstone_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:sandstone"));
            modRegistry.remove(new ResourceLocation("minecraft:red_sandstone_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:red_sandstone_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:red_sandstone"));
            modRegistry.remove(new ResourceLocation("minecraft:chiseled_sandstone"));
            modRegistry.remove(new ResourceLocation("minecraft:chiseled_red_sandstone"));

            //iron
            modRegistry.remove(new ResourceLocation("minecraft:iron_trapdoor"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_hoe"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_axe"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_sword"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_shovel"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_pickaxe"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_helmet"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_chestplate"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_leggings"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_boots"));
            modRegistry.remove(new ResourceLocation("minecraft:hopper"));
            modRegistry.remove(new ResourceLocation("minecraft:heavy_weighted_pressure_plate"));

            //gold
            modRegistry.remove(new ResourceLocation("minecraft:golden_helmet"));
            modRegistry.remove(new ResourceLocation("minecraft:golden_chestplate"));
            modRegistry.remove(new ResourceLocation("minecraft:golden_leggings"));
            modRegistry.remove(new ResourceLocation("minecraft:golden_boots"));
            modRegistry.remove(new ResourceLocation("minecraft:golden_hoe"));
            modRegistry.remove(new ResourceLocation("minecraft:golden_axe"));
            modRegistry.remove(new ResourceLocation("minecraft:golden_sword"));
            modRegistry.remove(new ResourceLocation("minecraft:golden_shovel"));
            modRegistry.remove(new ResourceLocation("minecraft:golden_pickaxe"));

            //chainmail
            modRegistry.remove(new ResourceLocation("minecraft:chainmail_helmet"));
            modRegistry.remove(new ResourceLocation("minecraft:chainmail_chestplate"));
            modRegistry.remove(new ResourceLocation("minecraft:chainmail_leggings"));
            modRegistry.remove(new ResourceLocation("minecraft:chainmail_boots"));

            //diamond
            modRegistry.remove(new ResourceLocation("minecraft:diamond_helmet"));
            modRegistry.remove(new ResourceLocation("minecraft:diamond_chestplate"));
            modRegistry.remove(new ResourceLocation("minecraft:diamond_leggings"));
            modRegistry.remove(new ResourceLocation("minecraft:diamond_boots"));
            modRegistry.remove(new ResourceLocation("minecraft:diamond_hoe"));
            modRegistry.remove(new ResourceLocation("minecraft:diamond_axe"));
            modRegistry.remove(new ResourceLocation("minecraft:diamond_sword"));
            modRegistry.remove(new ResourceLocation("minecraft:diamond_shovel"));
            modRegistry.remove(new ResourceLocation("minecraft:diamond_pickaxe"));

            //arrows
            modRegistry.remove(new ResourceLocation("minecraft:tippedarrow"));
            modRegistry.remove(new ResourceLocation("minecraft:spectral_arrow"));

            //white
            modRegistry.remove(new ResourceLocation("minecraft:string_to_wool"));
            modRegistry.remove(new ResourceLocation("minecraft:white_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:white_bed"));

            //Yellow
            modRegistry.remove(new ResourceLocation("minecraft:yellow_dye_from_sunflower"));
            modRegistry.remove(new ResourceLocation("minecraft:yellow_dye_from_dandelion"));
            modRegistry.remove(new ResourceLocation("minecraft:yellow_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:yellow_bed"));

            //red
            modRegistry.remove(new ResourceLocation("minecraft:red_dye_from_tulip"));
            modRegistry.remove(new ResourceLocation("minecraft:red_dye_from_rose_bush"));
            modRegistry.remove(new ResourceLocation("minecraft:red_dye_from_poppy"));
            modRegistry.remove(new ResourceLocation("minecraft:red_dye_from_beetroot"));
            modRegistry.remove(new ResourceLocation("minecraft:red_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:red_bed"));

            //purple
            modRegistry.remove(new ResourceLocation("minecraft:purple_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:purple_bed"));

            //pink
            modRegistry.remove(new ResourceLocation("minecraft:pink_dye_from_pink_tulip"));
            modRegistry.remove(new ResourceLocation("minecraft:pink_dye_from_peony"));
            modRegistry.remove(new ResourceLocation("minecraft:pink_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:pink_bed"));

            //orange
            modRegistry.remove(new ResourceLocation("minecraft:orange_dye_from_orange_tulip"));
            modRegistry.remove(new ResourceLocation("minecraft:orange_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:orange_bed"));

            //magenta
            modRegistry.remove(new ResourceLocation("minecraft:magenta_dye_from_lilac"));
            modRegistry.remove(new ResourceLocation("minecraft:magenta_dye_from_allium"));
            modRegistry.remove(new ResourceLocation("minecraft:magenta_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:magenta_bed"));

            //lime
            modRegistry.remove(new ResourceLocation("minecraft:lime_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:magenta_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:lime_bed"));

            //gray
            modRegistry.remove(new ResourceLocation("minecraft:light_gray_dye_from_white_tulip"));
            modRegistry.remove(new ResourceLocation("minecraft:light_gray_dye_from_oxeye_daisy"));
            modRegistry.remove(new ResourceLocation("minecraft:light_gray_dye_from_ink_bonemeal"));
            modRegistry.remove(new ResourceLocation("minecraft:light_gray_dye_from_azure_bluet"));
            modRegistry.remove(new ResourceLocation("minecraft:light_gray_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:light_gray_bed"));
            modRegistry.remove(new ResourceLocation("minecraft:gray_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:gray_bed"));

            //blue
            modRegistry.remove(new ResourceLocation("minecraft:light_blue_dye_from_blue_orchid"));
            modRegistry.remove(new ResourceLocation("minecraft:light_blue_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:light_blue_bed"));
            modRegistry.remove(new ResourceLocation("minecraft:blue_stained_hardened_clay"));
            modRegistry.remove(new ResourceLocation("minecraft:blue_stained_glass"));
            modRegistry.remove(new ResourceLocation("minecraft:blue_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:blue_bed_from_white_bed"));
            modRegistry.remove(new ResourceLocation("minecraft:blue_bed"));

            //green
            modRegistry.remove(new ResourceLocation("minecraft:green_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:green_bed"));

            //cyan
            modRegistry.remove(new ResourceLocation("minecraft:cyan_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:cyan_bed"));

            //brown
            modRegistry.remove(new ResourceLocation("minecraft:brown_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:brown_bed"));

            //black
            modRegistry.remove(new ResourceLocation("minecraft:black_wool"));
            modRegistry.remove(new ResourceLocation("minecraft:black_stained_hardened_clay"));
            modRegistry.remove(new ResourceLocation("minecraft:black_stained_glass"));
            modRegistry.remove(new ResourceLocation("minecraft:black_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:black_bed_from_white_bed"));
            modRegistry.remove(new ResourceLocation("minecraft:black_bed"));
        }

    }
}
