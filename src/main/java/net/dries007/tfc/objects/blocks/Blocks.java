package net.dries007.tfc.objects.blocks;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import static net.dries007.tfc.Constants.MOD_ID;
import static net.dries007.tfc.objects.CreativeTab.CT_MISC;

@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public final class Blocks
{
    private Blocks() {}

    public static final BlockDebug DEBUG = null;

    @SubscribeEvent
    public static void addBlocks(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> r = event.getRegistry();

        register(r, "debug", new BlockDebug());
    }

    private static void register(IForgeRegistry<Block> r, String name, Block item)
    {
        item.setRegistryName(MOD_ID, name);
        item.setUnlocalizedName(MOD_ID + "." + name.replace('_', '.'));
        item.setCreativeTab(CT_MISC);

        r.register(item);
    }
}
