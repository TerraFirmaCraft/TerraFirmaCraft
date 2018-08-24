/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.objects.MetalType;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class RecipeRegistryEvents
{
    @SubscribeEvent
    public static void onRecipeRegister(RegistryEvent.Register<IRecipe> event)
    {
        IForgeRegistry<IRecipe> r = event.getRegistry();

        r.register(new MetalToolRecipe(MetalType.PICK_HEAD, MetalType.PICK).setRegistryName(MOD_ID, "metal_pick"));
        r.register(new MetalToolRecipe(MetalType.SHOVEL_HEAD, MetalType.SHOVEL).setRegistryName(MOD_ID, "metal_shovel"));
        r.register(new MetalToolRecipe(MetalType.AXE_HEAD, MetalType.AXE).setRegistryName(MOD_ID, "metal_axe"));
        r.register(new MetalToolRecipe(MetalType.HOE_HEAD, MetalType.HOE).setRegistryName(MOD_ID, "metal_hoe"));
        r.register(new MetalToolRecipe(MetalType.CHISEL_HEAD, MetalType.CHISEL).setRegistryName(MOD_ID, "metal_chisel"));
        r.register(new MetalToolRecipe(MetalType.SWORD_BLADE, MetalType.SWORD).setRegistryName(MOD_ID, "metal_sword"));
        r.register(new MetalToolRecipe(MetalType.MACE_HEAD, MetalType.MACE).setRegistryName(MOD_ID, "metal_mace"));
        r.register(new MetalToolRecipe(MetalType.SAW_BLADE, MetalType.SAW).setRegistryName(MOD_ID, "metal_saw"));
        r.register(new MetalToolRecipe(MetalType.JAVELIN_HEAD, MetalType.JAVELIN).setRegistryName(MOD_ID, "metal_javelin"));
        r.register(new MetalToolRecipe(MetalType.HAMMER_HEAD, MetalType.HAMMER).setRegistryName(MOD_ID, "metal_hammer"));
        r.register(new MetalToolRecipe(MetalType.PROPICK_HEAD, MetalType.PROPICK).setRegistryName(MOD_ID, "metal_propick"));
        r.register(new MetalToolRecipe(MetalType.KNIFE_BLADE, MetalType.KNIFE).setRegistryName(MOD_ID, "metal_knife"));
        r.register(new MetalToolRecipe(MetalType.SCYTHE_BLADE, MetalType.SCYTHE).setRegistryName(MOD_ID, "metal_scythe"));
    }
}
