package net.dries007.tfc.client;

import net.dries007.tfc.objects.blocks.Blocks;
import net.dries007.tfc.objects.items.Items;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import static net.dries007.tfc.Constants.MOD_ID;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
public final class ModelRegistry
{
    private ModelRegistry() {}

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(Items.WAND, 0, new ModelResourceLocation(Items.WAND.getRegistryName().toString()));

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Blocks.DEBUG), 0, new ModelResourceLocation(Blocks.DEBUG.getRegistryName(), "inventory"));
    }
}
