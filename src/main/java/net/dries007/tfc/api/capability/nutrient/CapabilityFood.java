/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.nutrient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.classic.CalendarTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public final class CapabilityFood
{
    public static final float MIN_PLAYER_NUTRIENTS = 0f;
    public static final float MAX_PLAYER_NUTRIENTS = 100f;
    public static final int DEFAULT_ROT_TICKS = CalendarTFC.TICKS_IN_DAY * 15;

    @CapabilityInject(IFood.class)
    public static final Capability<IFood> CAPABILITY_NUTRIENTS = Helpers.getNull();
    @CapabilityInject(IPlayerNutrients.class)
    public static final Capability<IPlayerNutrients> CAPABILITY_PLAYER_NUTRIENTS = Helpers.getNull();

    private static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "nutrients");
    private static final ResourceLocation PLAYER_KEY = new ResourceLocation(MOD_ID, "player_nutrients");

    private static List<Supplier<PotionEffect>> rottenFoodEffects = null;

    public static void preInit()
    {
        // Item nutrient capability
        CapabilityManager.INSTANCE.register(IFood.class, new DumbStorage<>(), FoodHandler::new);
        // Player nutrient capability
        CapabilityManager.INSTANCE.register(IPlayerNutrients.class, new DumbStorage<>(), PlayerNutrientsHandler::new);
    }

    static List<Supplier<PotionEffect>> getRottenFoodEffects()
    {
        // todo: this is temp, it is subject to change
        if (rottenFoodEffects == null)
        {
            rottenFoodEffects = new ArrayList<>();
            rottenFoodEffects.add(() -> new PotionEffect(MobEffects.POISON, 600, 2));
            rottenFoodEffects.add(() -> new PotionEffect(MobEffects.WEAKNESS, 3600, 1));
            rottenFoodEffects.add(() -> new PotionEffect(MobEffects.SLOWNESS, 3600, 1));
            rottenFoodEffects.add(() -> new PotionEffect(MobEffects.HUNGER, 6000, 3));
            rottenFoodEffects.add(() -> new PotionEffect(MobEffects.NAUSEA, 3600, 1));
        }
        return rottenFoodEffects;
    }

    /**
     * This is the handler for anything nutrient / food / decay related
     */
    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static final class EventHandler
    {
        @SubscribeEvent
        public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event)
        {
            // This is only to attach food capabilities
            // todo: create a lookup or something for vanilla items
            // future plans: add via craft tweaker or json (1.14)
            ItemStack stack = event.getObject();
            if (stack.getItem() instanceof ItemFood && !stack.hasCapability(CapabilityFood.CAPABILITY_NUTRIENTS, null))
            {
                event.addCapability(KEY, new FoodHandler(stack.getTagCompound(), new float[] {1, 0, 0, 0, 0}, 1));
            }
        }

        @SubscribeEvent
        public static void onFinishUsingItem(LivingEntityUseItemEvent.Finish event)
        {
            if (event.getEntity() instanceof EntityPlayer)
            {
                ItemStack stack = event.getItem();
                IFood itemCap = stack.getCapability(CAPABILITY_NUTRIENTS, null);
                if (itemCap != null)
                {
                    itemCap.onConsumedByPlayer((EntityPlayer) event.getEntity(), stack);
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
