package net.dries007.tfc.common.items;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.util.Helpers;


public class TFCCreativeModeTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TerraFirmaCraft.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ORES = register("ores", TFCBlocks.ORES.get(Rock.ANDESITE).get(Ore.BISMUTHINITE));

    public static RegistryObject<CreativeModeTab> register(String name, Supplier<? extends ItemLike> displayItem)
    {
        return CREATIVE_TABS.register(name, () -> CreativeModeTab.builder().title(Helpers.translatable("tfc.creative_mode_tab." + name)).icon(() -> displayItem.get().asItem().getDefaultInstance()).build());
    }

    public static void buildContents(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTab() == ORES.get())
        {
            TFCBlocks.ORES.values().forEach(map -> map.values().forEach(reg -> {
                event.accept(reg.get());
            }));
        }
    }
}
