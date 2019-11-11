/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityAlpacaWoolTFC extends EntityAnimal implements IShearable
{
    private static final DataParameter<Byte> DYE_COLOR;
    private static final Map<EnumDyeColor, float[]> DYE_TO_RGB;

    static
    {
        DYE_COLOR = EntityDataManager.createKey(EntityAlpacaWoolTFC.class, DataSerializers.BYTE);
        DYE_TO_RGB = Maps.newEnumMap(EnumDyeColor.class);
        EnumDyeColor[] var0 = EnumDyeColor.values();
        int var1 = var0.length;

        for (int var2 = 0; var2 < var1; ++var2)
        {
            EnumDyeColor enumdyecolor = var0[var2];
            DYE_TO_RGB.put(enumdyecolor, createAlpacaColor(enumdyecolor));
        }

        DYE_TO_RGB.put(EnumDyeColor.WHITE, new float[] {0.9019608F, 0.9019608F, 0.9019608F});
    }

    @SideOnly(Side.CLIENT)
    public static float[] getDyeRgb(EnumDyeColor dyeColor)
    {
        return DYE_TO_RGB.get(dyeColor);
    }

    public static void registerFixesSheep(DataFixer fixer)
    { EntityLiving.registerFixesMob(fixer, EntityAlpacaWoolTFC.class); }

    public static EnumDyeColor getRandomAlpacaColor(Random random)
    {
        int i = random.nextInt(100);
        if (i < 8)
        { return EnumDyeColor.BLACK; }
        else if (i < 15)
        { return EnumDyeColor.WHITE; }
        else if (i < 26)
        { return EnumDyeColor.SILVER; }
        else if (i < 35)
        { return EnumDyeColor.GRAY; }
        else { return random.nextInt(500) == 0 ? EnumDyeColor.CYAN : EnumDyeColor.BROWN; }
    }

    private static float[] createAlpacaColor(EnumDyeColor p_192020_0_)
    {
        float[] afloat = p_192020_0_.getColorComponentValues();
        float f = 0.75F;
        return new float[] {afloat[0] * 0.75F, afloat[1] * 0.75F, afloat[2] * 0.75F};
    }
    private final InventoryCrafting inventoryCrafting = new InventoryCrafting(new Container()
    {
        public boolean canInteractWith(EntityPlayer playerIn)
        { return false; }
    }, 2, 1);
    private int alpacaTimer;
    private EntityAIEatGrass entityAIEatGrass;

    public EntityAlpacaWoolTFC(World worldIn)
    {
        super(worldIn);
        this.setSize(0.9F, 1.3F);
        this.inventoryCrafting.setInventorySlotContents(0, new ItemStack(Items.DYE));
        this.inventoryCrafting.setInventorySlotContents(1, new ItemStack(Items.DYE));
    }

    public EnumDyeColor getFleeceColor()
    {
        return EnumDyeColor.byMetadata(this.dataManager.get(DYE_COLOR) & 15);
    }

    public void setFleeceColor(EnumDyeColor color)
    {
        byte b0 = this.dataManager.get(DYE_COLOR);
        this.dataManager.set(DYE_COLOR, (byte) (b0 & 240 | color.getMetadata() & 15));
    }

    public boolean getSheared()
    {
        return (this.dataManager.get(DYE_COLOR) & 16) != 0;
    }

    public void setSheared(boolean sheared)
    {
        byte b0 = this.dataManager.get(DYE_COLOR);
        if (sheared) { this.dataManager.set(DYE_COLOR, (byte) (b0 | 16)); }
        else { this.dataManager.set(DYE_COLOR, (byte) (b0 & -17)); }

    }

    public EntityAlpacaWoolTFC createChild(EntityAgeable ageable)
    {
        EntityAlpacaWoolTFC entityalpaca = (EntityAlpacaWoolTFC) ageable;
        EntityAlpacaWoolTFC entityalpaca1 = new EntityAlpacaWoolTFC(this.world);
        entityalpaca1.setFleeceColor(this.getDyeColorMixFromParents(this, entityalpaca));
        return entityalpaca1;
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(DYE_COLOR, (byte) 0);
    }

    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos)
    { return !this.getSheared() && !this.isChild(); }

    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        this.setSheared(true);
        int i = 1 + this.rand.nextInt(3);
        List<ItemStack> ret = new ArrayList();

        for (int j = 0; j < i; ++j)
        { ret.add(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, this.getFleeceColor().getMetadata())); }

        this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
        return ret;
    }

    protected void initEntityAI()
    {
        this.entityAIEatGrass = new EntityAIEatGrass(this);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
        this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(3, new EntityAITempt(this, 1.1D, Items.WHEAT, false));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(5, this.entityAIEatGrass);
        this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
    }

    public void eatGrassBonus()
    {
        this.setSheared(false);
        if (this.isChild()) { this.addGrowth(60); }
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        if (this.getSheared())
        {
            return LootTableList.ENTITIES_SHEEP;
        }
        else
        {
            switch (this.getFleeceColor())
            {
                case WHITE:
                default:
                    return LootTableList.ENTITIES_SHEEP_WHITE;
                case ORANGE:
                    return LootTableList.ENTITIES_SHEEP_ORANGE;
                case MAGENTA:
                    return LootTableList.ENTITIES_SHEEP_MAGENTA;
                case LIGHT_BLUE:
                    return LootTableList.ENTITIES_SHEEP_LIGHT_BLUE;
                case YELLOW:
                    return LootTableList.ENTITIES_SHEEP_YELLOW;
                case LIME:
                    return LootTableList.ENTITIES_SHEEP_LIME;
                case PINK:
                    return LootTableList.ENTITIES_SHEEP_PINK;
                case GRAY:
                    return LootTableList.ENTITIES_SHEEP_GRAY;
                case SILVER:
                    return LootTableList.ENTITIES_SHEEP_SILVER;
                case CYAN:
                    return LootTableList.ENTITIES_SHEEP_CYAN;
                case PURPLE:
                    return LootTableList.ENTITIES_SHEEP_PURPLE;
                case BLUE:
                    return LootTableList.ENTITIES_SHEEP_BLUE;
                case BROWN:
                    return LootTableList.ENTITIES_SHEEP_BROWN;
                case GREEN:
                    return LootTableList.ENTITIES_SHEEP_GREEN;
                case RED:
                    return LootTableList.ENTITIES_SHEEP_RED;
                case BLACK:
                    return LootTableList.ENTITIES_SHEEP_BLACK;
            }
        }
    }

    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
    {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        this.setFleeceColor(getRandomAlpacaColor(this.world.rand));
        return livingdata;
    }

    protected void updateAITasks()
    {
        this.alpacaTimer = this.entityAIEatGrass.getEatingGrassTimer();
        super.updateAITasks();
    }

    public void onLivingUpdate()
    {
        if (this.world.isRemote) { this.alpacaTimer = Math.max(0, this.alpacaTimer - 1); }
        super.onLivingUpdate();
    }

    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Sheared", this.getSheared());
        compound.setByte("Color", (byte) this.getFleeceColor().getMetadata());
    }

    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setSheared(compound.getBoolean("Sheared"));
        this.setFleeceColor(EnumDyeColor.byMetadata(compound.getByte("Color")));
    }

    public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        player.getHeldItem(hand);
        return super.processInteract(player, hand);
    }

    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
        if (id == 10) { this.alpacaTimer = 40; }
        else { super.handleStatusUpdate(id); }

    }

    private EnumDyeColor getDyeColorMixFromParents(EntityAnimal father, EntityAnimal mother)
    {
        int i = ((EntityAlpacaWoolTFC) father).getFleeceColor().getDyeDamage();
        int j = ((EntityAlpacaWoolTFC) mother).getFleeceColor().getDyeDamage();
        this.inventoryCrafting.getStackInSlot(0).setItemDamage(i);
        this.inventoryCrafting.getStackInSlot(1).setItemDamage(j);
        ItemStack itemstack = CraftingManager.findMatchingResult(this.inventoryCrafting, ((EntityAlpacaWoolTFC) father).world);

        int k;
        if (itemstack.getItem() == Items.DYE) { k = itemstack.getMetadata(); }
        else { k = this.world.rand.nextBoolean() ? i : j; }

        return EnumDyeColor.byDyeDamage(k);
    }
}
