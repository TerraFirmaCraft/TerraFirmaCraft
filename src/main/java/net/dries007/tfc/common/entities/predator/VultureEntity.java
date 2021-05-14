package net.dries007.tfc.common.entities.predator;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VultureEntity extends FlyingEntity
{
    private static final DataParameter<Integer> ID_SIZE = EntityDataManager.defineId(VultureEntity.class, DataSerializers.INT);
    private static final int STALL_LIMIT = 60;

    private Vector3d moveTargetPoint = Vector3d.ZERO;
    private BlockPos anchorPoint = BlockPos.ZERO;
    private AttackPhase attackPhase = AttackPhase.CIRCLE;
    public boolean isDiving = false;

    public VultureEntity(EntityType<? extends VultureEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.xpReward = 5;
        this.moveControl = new VultureMovementController(this);
        this.lookControl = new LookController(this) {
            @Override
            public void tick() { }
        };
    }

    @Override
    protected BodyController createBodyControl()
    {
        return new BodyController(this) {
            @Override
            public void clientTick()
            {
                yHeadRot = yBodyRot;
                yBodyRot = yRot;
            }
        };
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(1, new PickAttackGoal());
        this.goalSelector.addGoal(2, new SweepAttackGoal());
        this.goalSelector.addGoal(3, new OrbitPointGoal());
        this.targetSelector.addGoal(1, new AttackPlayerGoal());
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(ID_SIZE, 0);
    }

    public void setSize(int sizeIn)
    {
        this.entityData.set(ID_SIZE, MathHelper.clamp(sizeIn, 0, 64));
    }

    private void updateSize()
    {
        refreshDimensions();
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6 + getVultureSize());
    }

    public int getVultureSize()
    {
        return entityData.get(ID_SIZE);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn)
    {
        return sizeIn.height * 0.35F;
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> key)
    {
        if (ID_SIZE.equals(key)) updateSize();
        super.onSyncedDataUpdated(key);
    }

    @Override
    protected boolean shouldDespawnInPeaceful()
    {
        return true;
    }

    @Override
    public void tick()
    {
        super.tick();
        if (level.isClientSide)
        {
            float f = MathHelper.cos((float) (getId() * 3 + tickCount) * 0.13F + (float) Math.PI);
            float f1 = MathHelper.cos((float) (getId() * 3 + tickCount + 1) * 0.13F + (float) Math.PI);
            if (f > 0.0F && f1 <= 0.0F)
            {
                level.playLocalSound(getX(), getY(), getZ(), SoundEvents.PHANTOM_FLAP, getSoundSource(), 0.95F + random.nextFloat() * 0.05F, 0.95F + random.nextFloat() * 0.05F, false);
            }
        }
    }

    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag)
    {
        anchorPoint = blockPosition().above(5);
        setSize(0);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound)
    {
        super.readAdditionalSaveData(compound);
        if (compound.contains("AX"))
        {
            anchorPoint = new BlockPos(compound.getInt("AX"), compound.getInt("AY"), compound.getInt("AZ"));
        }
        setSize(compound.getInt("Size"));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putInt("AX", anchorPoint.getX());
        compound.putInt("AY", anchorPoint.getY());
        compound.putInt("AZ", anchorPoint.getZ());
        compound.putInt("Size", getVultureSize());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldRenderAtSqrDistance(double distance)
    {
        return true;
    }

    @Override
    public SoundCategory getSoundSource()
    {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.PHANTOM_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.PHANTOM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.PHANTOM_DEATH;
    }

    @Override
    public CreatureAttribute getMobType()
    {
        return CreatureAttribute.UNDEAD;
    }

    @Override
    protected float getSoundVolume()
    {
        return 1.0F;
    }

    @Override
    public boolean canAttackType(EntityType<?> typeIn)
    {
        return true;
    }

    @Override
    public EntitySize getDimensions(Pose poseIn)
    {
        int i = getVultureSize();
        EntitySize entitysize = super.getDimensions(poseIn);
        float f = (entitysize.width + 0.2F * (float) i) / entitysize.width;
        return entitysize.scale(f);
    }

    public enum AttackPhase
    {
        CIRCLE,
        SWOOP
    }

    public class AttackPlayerGoal extends Goal
    {
        private final EntityPredicate attackTargeting = (new EntityPredicate()).range(64.0D);
        private int nextScanTick = 20;

        private AttackPlayerGoal() { }

        public boolean canUse()
        {
            if (nextScanTick > 0)
            {
                --nextScanTick;
            }
            else
            {
                nextScanTick = 60;
                List<PlayerEntity> list = level.getNearbyPlayers(attackTargeting, VultureEntity.this, getBoundingBox().inflate(16.0D, 64.0D, 16.0D));
                if (!list.isEmpty())
                {
                    list.sort(Comparator.<Entity, Double>comparing(Entity::getY).reversed());

                    for (PlayerEntity playerentity : list)
                    {
                        if (canAttack(playerentity, EntityPredicate.DEFAULT))
                        {
                            setTarget(playerentity);
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        public boolean canContinueToUse()
        {
            LivingEntity livingentity = getTarget();
            return livingentity != null && canAttack(livingentity, EntityPredicate.DEFAULT);
        }
    }

    public abstract class AbstractMoveGoal extends Goal
    {
        public AbstractMoveGoal()
        {
            setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        protected boolean touchingTarget()
        {
            return moveTargetPoint.distanceToSqr(getX(), getY(), getZ()) < 4.0D;
        }
    }

    public class VultureMovementController extends MovementController
    {
        private float speed = 0.1F;

        public VultureMovementController(MobEntity entityIn)
        {
            super(entityIn);
        }

        @Override
        public void tick()
        {
            if (horizontalCollision)
            {
                yRot += 180.0F;
                speed = 0.1F;
            }

            float dx = (float) (moveTargetPoint.x - getX());
            float dy = (float) (moveTargetPoint.y - getY());
            float dz = (float) (moveTargetPoint.z - getZ());
            double horizontalSquared = MathHelper.sqrt(dx * dx + dz * dz);
            double verticalScaled = 1.0D - (double) MathHelper.abs(dy * 0.7F) / horizontalSquared;
            dx = (float) (dx * verticalScaled);
            dz = (float) (dz * verticalScaled);
            horizontalSquared = MathHelper.sqrt(dx * dx + dz * dz);
            double length = MathHelper.sqrt(dx * dx + dz * dz + dy * dy);
            float yRotOld = yRot;
            float angleRadians = (float) MathHelper.atan2(dz, dx);
            float yRot90Old = MathHelper.wrapDegrees(yRot + 90.0F);
            float angle = MathHelper.wrapDegrees(angleRadians * (180F / (float) Math.PI));
            yRot = MathHelper.approachDegrees(yRot90Old, angle, 4.0F) - 90.0F;
            yBodyRot = yRot;
            if (MathHelper.degreesDifferenceAbs(yRotOld, yRot) < 3.0F)
            {
                speed = MathHelper.approach(speed, 1.8F, 0.005F * (1.8F / speed));
            }
            else
            {
                speed = MathHelper.approach(speed, 0.2F, 0.025F);
            }

            float horizontalScaled = (float) (-(MathHelper.atan2(-dy, horizontalSquared) * (double) (180F / (float) Math.PI)));
            xRot = horizontalScaled;
            float yRot90 = yRot + 90.0F;
            double speedX = speed * MathHelper.cos(yRot90 * ((float) Math.PI / 180F)) * Math.abs(dx / length);
            double speedZ = speed * MathHelper.sin(yRot90 * ((float) Math.PI / 180F)) * Math.abs(dz / length);
            double speedY = speed * MathHelper.sin(horizontalScaled * ((float) Math.PI / 180F)) * Math.abs(dy / length);
            Vector3d moveCurrent = getDeltaMovement();
            if (isDiving)
            {
                moveCurrent.multiply(2.0D, 2.0D, 2.0D);
            }
            setDeltaMovement(moveCurrent.add((new Vector3d(speedX, speedY, speedZ)).subtract(moveCurrent).scale(0.2D)));
        }
    }

    public class OrbitPointGoal extends AbstractMoveGoal
    {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        private OrbitPointGoal() { }

        @Override
        public boolean canUse()
        {
            return getTarget() == null || attackPhase == AttackPhase.CIRCLE;
        }

        @Override
        public void start()
        {
            distance = 5.0F + random.nextFloat() * 10.0F;
            height = -4.0F + random.nextFloat() * 9.0F;
            clockwise = random.nextBoolean() ? 1.0F : -1.0F;
            selectNext();
        }

        @Override
        public void tick()
        {
            if (random.nextInt(360) == 0 && level.canSeeSky(blockPosition()))
            {
                int x = blockPosition().getX();
                int y = level.getMaxBuildHeight();
                int z = blockPosition().getZ();
                if (y < getY() + 10)
                {
                    remove(); // this is safe to call -- it's the same thing as de-spawning
                    return;
                }

                moveTargetPoint = new Vector3d(x, y, z);
                anchorPoint = new BlockPos(x, y, z);
            }

            if (random.nextInt(350) == 0)
            {
                height = -4.0F + random.nextFloat() * 9.0F;
            }

            if (random.nextInt(250) == 0)
            {
                ++distance;
                if (distance > 15.0F)
                {
                    distance = 5.0F;
                    clockwise = -clockwise;
                }
            }

            if (random.nextInt(450) == 0)
            {
                angle = random.nextFloat() * 2.0F * (float) Math.PI;
                selectNext();
            }

            if (touchingTarget()) selectNext();

            if (moveTargetPoint.y < getY() && !level.isEmptyBlock(blockPosition().below()))
            {
                height = Math.max(1.0F, height);
                selectNext();
            }

            if (moveTargetPoint.y > getY() && !level.isEmptyBlock(blockPosition().above()))
            {
                height = Math.min(-1.0F, height);
                selectNext();
            }
        }

        private void selectNext()
        {
            if (BlockPos.ZERO.equals(anchorPoint))
            {
                anchorPoint = blockPosition();
            }

            angle += clockwise * 15.0F * ((float) Math.PI / 180F);
            moveTargetPoint = Vector3d.atLowerCornerOf(anchorPoint).add(distance * MathHelper.cos(angle), -4.0F + height, distance * MathHelper.sin(angle));
        }
    }

    public class PickAttackGoal extends Goal
    {
        private int nextSweepTick;
        private int stallTicks;

        private PickAttackGoal() { }

        @Override
        public boolean canUse()
        {
            LivingEntity livingentity = getTarget();
            return livingentity != null && canAttack(getTarget(), EntityPredicate.DEFAULT) && stallTicks < STALL_LIMIT;
        }

        @Override
        public void start()
        {
            stallTicks = 0;
            this.nextSweepTick = 10;
            attackPhase = AttackPhase.CIRCLE;
            this.setAnchorAboveTarget();
        }

        @Override
        public void stop()
        {
            stallTicks = 0;
            anchorPoint = level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, anchorPoint).above(10 + random.nextInt(20));
            isDiving = false;
        }

        @Override
        public void tick()
        {
            LivingEntity target = getTarget();
            if (isHorizontal() && target != null && isClose(target)) stallTicks++;
            if (attackPhase == AttackPhase.CIRCLE)
            {
                --nextSweepTick;
                if (nextSweepTick <= 0)
                {
                    attackPhase = AttackPhase.SWOOP;
                    isDiving = true;
                    setAnchorAboveTarget();
                    nextSweepTick = (8 + random.nextInt(4)) * 20;
                    playSound(SoundEvents.PHANTOM_SWOOP, 10.0F, 0.95F + random.nextFloat() * 0.1F);
                }
            }

        }

        private void setAnchorAboveTarget()
        {
            LivingEntity target = getTarget();
            if (target == null) return;
            anchorPoint = target.blockPosition().above(20 + random.nextInt(20));
            if (anchorPoint.getY() < level.getSeaLevel())
            {
                anchorPoint = new BlockPos(anchorPoint.getX(), level.getSeaLevel() + 1, anchorPoint.getZ());
            }
        }
    }

    public class SweepAttackGoal extends AbstractMoveGoal
    {
        private int stallTicks;

        private SweepAttackGoal() { }

        @Override
        public boolean canUse()
        {
            return getTarget() != null && attackPhase == AttackPhase.SWOOP;
        }

        @Override
        public boolean canContinueToUse()
        {
            LivingEntity target = getTarget();
            if (target == null || !target.isAlive() || stallTicks > STALL_LIMIT)
            {
                return false;
            }
            else if (EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(target))
            {
                return canUse();
            }
            return false;
        }

        @Override
        public void start()
        {
            stallTicks = 0;
            isDiving = true;
        }

        @Override
        public void stop()
        {
            stallTicks = 0;
            setTarget(null);
            attackPhase = AttackPhase.CIRCLE;
            isDiving = false;
        }

        @Override
        public void tick()
        {
            LivingEntity target = getTarget();
            if (target == null) return;
            if (isHorizontal() && isClose(target)) stallTicks++;
            moveTargetPoint = new Vector3d(target.getX(), target.getY(0.5D), target.getZ());
            if (getBoundingBox().inflate(0.2F).intersects(target.getBoundingBox()))
            {
                doHurtTarget(target);
                attackPhase = AttackPhase.CIRCLE;
                if (!isSilent()) level.levelEvent(1039, blockPosition(), 0);
            }
            else if (horizontalCollision || hurtTime > 0)
            {
                attackPhase = AttackPhase.CIRCLE;
            }
        }
    }

    private boolean isHorizontal()
    {
        return isDiving && getDeltaMovement().y() < 1E-5;
    }

    private boolean isClose(Entity target)
    {
        return Math.abs(getY() - target.getY()) < 3.0D;
    }
}
