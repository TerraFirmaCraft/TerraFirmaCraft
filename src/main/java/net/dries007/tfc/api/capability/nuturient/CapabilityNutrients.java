/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.nuturient;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.agriculture.Nutrient;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public final class CapabilityNutrients
{
    public static final float MIN_PLAYER_NUTRIENTS = 0f;
    public static final float MAX_PLAYER_NUTRIENTS = 100f;

    @CapabilityInject(INutrients.class)
    public static final Capability<INutrients> CAPABILITY_NUTRIENTS = Helpers.getNull();
    @CapabilityInject(IPlayerNutrients.class)
    public static final Capability<IPlayerNutrients> CAPABILITY_PLAYER_NUTRIENTS = Helpers.getNull();

    private static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "nutrients");
    private static final ResourceLocation PLAYER_KEY = new ResourceLocation(MOD_ID, "player_nutrients");

    public static void preInit()
    {
        // Item nutrient capability
        CapabilityManager.INSTANCE.register(INutrients.class, new DumbStorage<>(), () -> null);
        // Player nutrient capability
        CapabilityManager.INSTANCE.register(IPlayerNutrients.class, new DumbStorage<>(), PlayerNutrientsHandler::new);
    }

    public static void add(AttachCapabilitiesEvent<ItemStack> event)
    {
        // Attaches a nutrient capability to a food item.
        // todo: lookup an ore dictionary / item table for the respective nutrients (for vanilla)
        // todo: populate via json / craft tweaker?
        event.addCapability(KEY, new NutrientsHandler(1, 1, 1, 1, 1));
    }

    /**
     * This is the handler for anything nutrient / food / decay related
     */
    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static final class EventHandler
    {
        @SubscribeEvent
        public static void onFinishUsingItem(LivingEntityUseItemEvent.Finish event)
        {
            if (event.getEntity() instanceof EntityPlayer)
            {
                ItemStack stack = event.getItem();

                INutrients itemCap = stack.getCapability(CAPABILITY_NUTRIENTS, null);
                IPlayerNutrients playerCap = event.getEntity().getCapability(CAPABILITY_PLAYER_NUTRIENTS, null);

                if (itemCap != null && playerCap != null)
                {
                    TerraFirmaCraft.getLog().info("Adding nutrients!!!");
                    for (Nutrient nutrient : Nutrient.values())
                    {
                        playerCap.addNutrient(nutrient, itemCap.getNutrients(stack, nutrient));


                    }
                    ((PlayerNutrientsHandler) playerCap).debug();
                }
            }
        }

        @SubscribeEvent
        public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
        {
            if (event.getObject() instanceof EntityPlayer)
            {
                event.addCapability(PLAYER_KEY, new PlayerNutrientsHandler());
            }
        }
    }
}
