package net.dries007.tfc.common.items;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.util.Helpers;


public class TFCCreativeModeTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TerraFirmaCraft.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ORES = register("ores");

    public static RegistryObject<CreativeModeTab> register(String name)
    {
        return CREATIVE_TABS.register(name, () -> CreativeModeTab.builder().title(Helpers.translatable("tfc.creative_mode_tab." + name)).build());
    }

    public static void buildContents(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTab() == ORES.get())
        {
            TFCBlocks.ORES.values().forEach(map -> map.values().forEach(reg -> {
                add(event, reg.get());
            }));
        }
    }

    private static void add(BuildCreativeModeTabContentsEvent event, ItemStack stack)
    {
        event.accept(FoodCapability.setStackNonDecaying(stack));
    }

    private static void add(BuildCreativeModeTabContentsEvent event, ItemLike itemLike)
    {
        add(event, itemLike.asItem().getDefaultInstance());
    }
}
