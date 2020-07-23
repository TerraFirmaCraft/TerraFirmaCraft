/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.damage.DamageType;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.util.OreDictionaryHelper;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemMetalTool extends ItemMetal
{
    public final ToolMaterial material;
    private final double attackDamage;
    private final int areaOfEffect;
    private final float attackSpeed;
    private float efficiency;

    public ItemMetalTool(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
        if (metal.getToolMetal() == null)
            throw new IllegalArgumentException("You can't make tools out of non tool metals.");
        material = metal.getToolMetal();
        int harvestLevel = material.getHarvestLevel();

        setMaxStackSize(1);
        setMaxDamage(material.getMaxUses());
        efficiency = material.getEfficiency();

        float typeDamage;
        switch (type)
        {
            case PICK:
                // Non-Weapon (50% efficient for damaging, tweaks in attack speed / damage)
                setHarvestLevel("pickaxe", harvestLevel);
                typeDamage = 0.75f;
                areaOfEffect = 1;
                attackSpeed = -2.8f;
                OreDictionaryHelper.registerDamageType(this, DamageType.PIERCING);
                break;
            case SHOVEL:
                // Non-Weapon
                setHarvestLevel("shovel", harvestLevel);
                typeDamage = 0.875f;
                areaOfEffect = 1;
                attackSpeed = -3f;
                OreDictionaryHelper.registerDamageType(this, DamageType.CRUSHING);
                break;
            case AXE:
                // Weapon
                setHarvestLevel("axe", harvestLevel);
                typeDamage = 1.5f;
                areaOfEffect = 1;
                attackSpeed = -3f;
                OreDictionaryHelper.registerDamageType(this, DamageType.SLASHING);
                break;
            case HOE:
                // Non-Weapon
                setHarvestLevel("hoe", harvestLevel);
                typeDamage = 0.875f;
                areaOfEffect = 1;
                attackSpeed = -3;
                OreDictionaryHelper.registerDamageType(this, DamageType.PIERCING);
                break;
            case CHISEL:
                // Non-Weapon
                setHarvestLevel("chisel", harvestLevel);
                typeDamage = 0.27f;
                areaOfEffect = 1;
                attackSpeed = -1.5f;
                OreDictionaryHelper.registerDamageType(this, DamageType.SLASHING);
                break;
            case SAW:
                // Non-Weapon (should be even worse than non-weapons)
                setHarvestLevel("axe", harvestLevel);
                setHarvestLevel("saw", harvestLevel);
                typeDamage = 0.5f;
                areaOfEffect = 1;
                attackSpeed = -3;
                OreDictionaryHelper.registerDamageType(this, DamageType.SLASHING);
                break;
            case PROPICK:
                // Non-Weapon (should be even worse than non-weapons)
                typeDamage = 0.5f;
                areaOfEffect = 1;
                attackSpeed = -2.8f;
                setMaxDamage(material.getMaxUses() / 3);
                efficiency = material.getEfficiency() * 0.5F;
                OreDictionaryHelper.registerDamageType(this, DamageType.PIERCING);
                break;
            case SCYTHE:
                // Weapon
                setHarvestLevel("scythe", harvestLevel);
                setMaxDamage((int) (material.getMaxUses() * 1.5));
                typeDamage = 2f;
                areaOfEffect = 2;
                attackSpeed = -3.2f;
                OreDictionaryHelper.registerDamageType(this, DamageType.PIERCING);
                break;
            case SHEARS:
                // Worst Non-Weapon, ever!
                setHarvestLevel("shears", harvestLevel);
                setMaxDamage((int) (material.getMaxUses() * 0.3));
                typeDamage = 0.2f;
                areaOfEffect = 1;
                attackSpeed = -2.8f;
                OreDictionaryHelper.registerDamageType(this, DamageType.CRUSHING);
                break;
            case KNIFE:
                // Weapon
                setHarvestLevel("knife", harvestLevel);
                typeDamage = 0.54f;
                areaOfEffect = 1;
                attackSpeed = -1.5f;
                OreDictionaryHelper.registerDamageType(this, DamageType.PIERCING);
                break;
            case HAMMER:
                // Non-Weapon
                setHarvestLevel("hammer", harvestLevel);
                typeDamage = 1f;
                areaOfEffect = 1;
                attackSpeed = -3f;
                OreDictionaryHelper.registerDamageType(this, DamageType.CRUSHING);
                break;
            case SWORD:
                // Weapon, but it has it's own class now
                typeDamage = 1f;
                areaOfEffect = 1;
                attackSpeed = -2.4f;
                OreDictionaryHelper.registerDamageType(this, DamageType.SLASHING);
                break;
            case MACE:
                // Weapon
                typeDamage = 1.3f;
                areaOfEffect = 1;
                attackSpeed = -2.8f;
                OreDictionaryHelper.registerDamageType(this, DamageType.CRUSHING);
                break;
            case JAVELIN:
                // Weapon
                typeDamage = 0.7f;
                areaOfEffect = 1;
                attackSpeed = -1.8f;
                OreDictionaryHelper.registerDamageType(this, DamageType.PIERCING);
                break;
            case SHIELD:
                // Non Weapon (or, is the best attack, defense?)
                typeDamage = 0.1f;
                areaOfEffect = 1;
                attackSpeed = -3;
                break;
            default:
                throw new IllegalArgumentException("Tool from non tool type.");
        }

        attackDamage = typeDamage * material.getAttackDamage();
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack itemstack = player.getHeldItem(hand);

        if (!player.canPlayerEdit(pos.offset(facing), facing, itemstack))
        {
            return EnumActionResult.FAIL;
        }
        else if (type == Metal.ItemType.SHOVEL)
        {
            IBlockState iblockstate = worldIn.getBlockState(pos);
            Block block = iblockstate.getBlock();
            if (!(block instanceof BlockRockVariant))
            {
                return EnumActionResult.PASS;
            }
            BlockRockVariant rockVariant = (BlockRockVariant) block;
            if (ConfigTFC.General.OVERRIDES.enableGrassPath && facing != EnumFacing.DOWN && worldIn.getBlockState(pos.up()).getMaterial() == Material.AIR && rockVariant.getType() == Rock.Type.GRASS || rockVariant.getType() == Rock.Type.DRY_GRASS || rockVariant.getType() == Rock.Type.DIRT)
            {
                IBlockState iblockstate1 = BlockRockVariant.get(rockVariant.getRock(), Rock.Type.PATH).getDefaultState();
                worldIn.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);

                if (!worldIn.isRemote)
                {
                    worldIn.setBlockState(pos, iblockstate1, 11);
                    itemstack.damageItem(1, player);
                }

                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state)
    {
        return canHarvestBlock(state, stack) ? efficiency : 1.0f;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
        switch (type)
        {
            case PROPICK:
            case SAW:
            case SHEARS:
                stack.damageItem(4, attacker);
                break;
            case HOE:
            case CHISEL:
                stack.damageItem(3, attacker);
                break;
            case PICK:
            case SHOVEL:
            case AXE:
            case SCYTHE:
                stack.damageItem(2, attacker);
                break;
            case SWORD:
            case MACE:
            case JAVELIN:
            case HAMMER:
            case KNIFE:
                stack.damageItem(1, attacker);
                break;
        }
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving)
    {
        if (state.getBlockHardness(worldIn, pos) > 0 || type == Metal.ItemType.KNIFE || type == Metal.ItemType.SCYTHE)
        {
            if (!worldIn.isRemote)
            {
                stack.damageItem(1, entityLiving);
            }
        }
        if (areaOfEffect > 1 && entityLiving instanceof EntityPlayer && !worldIn.isRemote)
        {
            EntityPlayer player = (EntityPlayer) entityLiving;
            int areaPlus = areaOfEffect - 1; //First block already added
            for (BlockPos.MutableBlockPos extraPos : BlockPos.getAllInBoxMutable(pos.add(-areaPlus, -areaPlus, -areaPlus), pos.add(areaPlus, areaPlus, areaPlus)))
            {
                IBlockState st = worldIn.getBlockState(extraPos);
                if (!extraPos.equals(pos) && !worldIn.isAirBlock(extraPos) && canHarvestBlock(st))
                {
                    st.getBlock().onPlayerDestroy(worldIn, extraPos, st);
                    st.getBlock().harvestBlock(worldIn, player, extraPos, st, worldIn.getTileEntity(extraPos), stack);
                    worldIn.setBlockToAir(extraPos);
                    stack.damageItem(1, entityLiving);
                }
            }
        }
        return true;
    }

    @Override
    public boolean canHarvestBlock(IBlockState state)
    {
        Material material = state.getMaterial();
        switch (type)
        {
            case AXE:
                return material == Material.WOOD || material == Material.PLANTS || material == Material.VINE;
            case PICK:
                return material == Material.IRON || material == Material.ANVIL || material == Material.ROCK;
            case SHOVEL:
                return material == Material.SNOW || material == Material.CRAFTED_SNOW;
            case SCYTHE:
                return material == Material.PLANTS || material == Material.VINE || material == Material.LEAVES;
            case KNIFE:
                return material == Material.VINE || material == Material.LEAVES;
            case SWORD:
                return material == Material.WEB;
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }

    @Override
    public int getItemEnchantability()
    {
        return material.getEnchantability();
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
    {
        Multimap<String, AttributeModifier> multimap = HashMultimap.create();
        if (slot == EntityEquipmentSlot.MAINHAND)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", attackDamage, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", attackSpeed, 0));
        }
        return multimap;
    }

    @Override
    public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player)
    {
        //This stops swords and other weapons breaking blocks in creative
        switch (type)
        {
            case SWORD:
            case JAVELIN:
            case MACE:
                return false;
            default:
                return true;
        }
    }

    @Override
    public boolean canHarvestBlock(IBlockState state, ItemStack stack)
    {
        for (String type : getToolClasses(stack))
        {
            if (state.getBlock().isToolEffective(type, state))
            {
                return true;
            }
        }
        return canHarvestBlock(state);
    }

    @Override
    public boolean canApplyAtEnchantingTable(@Nonnull ItemStack stack, @Nonnull Enchantment enchantment)
    {
        if (enchantment.type == EnumEnchantmentType.WEAPON)
        {
            return isWeapon();
        }
        else if (enchantment.type == EnumEnchantmentType.DIGGER)
        {
            return isTool();
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean canStack(ItemStack stack)
    {
        return false;
    }

    public double getAttackDamage() { return this.attackDamage; }

    private boolean isWeapon()
    {
        switch (type)
        {
            case AXE:
            case SWORD:
            case MACE:
            case KNIFE:
            case HAMMER:
            case JAVELIN:
                return true;
            default:
                return false;
        }
    }

    private boolean isTool()
    {
        switch (type)
        {
            case PICK:
            case HAMMER:
            case KNIFE:
            case AXE:
            case HOE:
            case SAW:
            case CHISEL:
            case SCYTHE:
            case SHEARS:
            case SHOVEL:
            case PROPICK:
                return true;
            default:
                return false;
        }
    }
}
