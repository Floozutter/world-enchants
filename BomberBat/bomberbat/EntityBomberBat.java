package bomberbat;
import bomberbat.ControllerMoveFlyingCustom;
import bomberbat.Swellable;
import bomberbat.PathfinderGoalSwellCustom;

import org.bukkit.Bukkit;

import net.minecraft.server.v1_16_R1.ControllerMoveFlying;

import net.minecraft.server.v1_16_R1.DataWatcherObject;
import net.minecraft.server.v1_16_R1.DataWatcher;
import net.minecraft.server.v1_16_R1.DataWatcherRegistry;

import net.minecraft.server.v1_16_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_16_R1.EntityHuman;

import net.minecraft.server.v1_16_R1.GenericAttributes;
import net.minecraft.server.v1_16_R1.AttributeProvider;

import net.minecraft.server.v1_16_R1.NavigationAbstract;
import net.minecraft.server.v1_16_R1.NavigationFlying;

import net.minecraft.server.v1_16_R1.SoundEffects;

import net.minecraft.server.v1_16_R1.GameRules;
import net.minecraft.server.v1_16_R1.Explosion;

import net.minecraft.server.v1_16_R1.MobEffect;
import net.minecraft.server.v1_16_R1.EntityAreaEffectCloud;
import java.util.Collection;
import java.util.Iterator;

import net.minecraft.server.v1_16_R1.EntityBat;
import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.World;

import net.minecraft.server.v1_16_R1.Vec3D;
import net.minecraft.server.v1_16_R1.PathfinderGoal;
import net.minecraft.server.v1_16_R1.EntityInsentient;
import net.minecraft.server.v1_16_R1.EntityLiving;
import net.minecraft.server.v1_16_R1.EntityPlayer;
import net.minecraft.server.v1_16_R1.EnumGamemode;


public class EntityBomberBat extends EntityBat implements Swellable {
	private static final DataWatcherObject<Integer> DATA_SWELL_DIR = (
		DataWatcher.a(EntityBomberBat.class, DataWatcherRegistry.b)
	);
	private int oldSwell;
	private int swell;
	private int maxSwell = 30;
	private int explosionRadius = 3;
	
	public EntityBomberBat(
		EntityTypes<? extends EntityBomberBat> entitytypes,
		World world
	) {
		super(EntityTypes.BAT, world);
		this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(30);
		this.moveController = new ControllerMoveFlyingCustom(this, 10, false);  // Arguments copied from EntityParrot.
		Bukkit.broadcastMessage("Bomber Bat!");
	}
	
    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityBomberBat.DATA_SWELL_DIR, -1);
    }
	
    @Override
    protected void initPathfinder() {
		this.goalSelector.a(1, new PathfinderGoalSwellCustom(this));
		this.goalSelector.a(2, new PathfinderGoalBomberBatAttack(this, 5F, 0.4F, 7F));
		this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(
			this, EntityHuman.class, true
		));
    }
	
	@Override
	protected NavigationAbstract b(World world) {
		// Copy from EntityParrot.
		NavigationFlying navigationflying = new NavigationFlying(this, world);
		navigationflying.a(false);
		navigationflying.d(true);
		navigationflying.b(true);
		return navigationflying;
	}
	
	@Override
	public void tick() {
		if (this.isAlive()) {
			this.oldSwell = this.swell;
			/*
			if (this.isIgnited()) {
				this.setSwellDir(1);
			}
			*/
			final int i = this.getSwellDir();
			if (i > 0 && this.swell == 0) {
				this.playSound(SoundEffects.ENTITY_CREEPER_PRIMED, 1.0f, 0.5f);
			}
			this.swell += i;
			if (this.swell < 0) {
				this.swell = 0;
			}
			if (this.swell >= this.maxSwell) {
				this.swell = this.maxSwell;
				this.explode();
			}
		}
		super.tick();
	}
	
	@Override
	protected void mobTick() {
		if (getGoalTarget() == null) {
			super.mobTick();
		}
	}
	
	public int getSwellDir() {
		return (Integer) this.datawatcher.get(EntityBomberBat.DATA_SWELL_DIR);
	}

	public void setSwellDir(int i) {
		int old = this.getSwellDir();
		this.datawatcher.set(EntityBomberBat.DATA_SWELL_DIR, i);
		if (i > 0 && old < 0) {
			this.getBukkitEntity().setGlowing(true);
		} else if (i < 0 && old > 0) {
			this.getBukkitEntity().setGlowing(false);
		}
	}
	
	private void explode() {
		if (!this.world.isClientSide) {
			Explosion.Effect explosion_effect = this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) ? Explosion.Effect.DESTROY : Explosion.Effect.NONE;
			//float f = this.isPowered() ? 2.0F : 1.0F;
			float f = 1.0F;

			this.killed = true;
			this.world.explode(this, this.locX(), this.locY(), this.locZ(), (float) this.explosionRadius * f, explosion_effect);
			this.die();
			this.createEffectCloud();
		}
	}
	
	private void createEffectCloud() {
		Collection<MobEffect> collection = this.getEffects();

		if (!collection.isEmpty()) {
			EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.world, this.locX(), this.locY(), this.locZ());
			entityareaeffectcloud.setRadius(2.5F);
			entityareaeffectcloud.setRadiusOnUse(-0.5F);
			entityareaeffectcloud.setWaitTime(10);
			entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() / 2);
			entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float) entityareaeffectcloud.getDuration());
			Iterator iterator = collection.iterator();

			while (iterator.hasNext()) {
				MobEffect mobeffect = (MobEffect) iterator.next();
				entityareaeffectcloud.addEffect(new MobEffect(mobeffect));
			}

			this.world.addEntity(entityareaeffectcloud);
		}
	}
	
	private static class PathfinderGoalBomberBatAttack extends PathfinderGoal {
		private final EntityInsentient entity;
		private final float diveRangeSq;
		private final float diveSpeed;
		private final float followSpeed;
		
		private EntityLiving target;
		private int diving;
		
		public PathfinderGoalBomberBatAttack(
			EntityInsentient entity,
			float diveRange,
			float diveSpeed,
			float followSpeed
			
		) {
			this.entity = entity;
			this.diveRangeSq = diveRange * diveRange;
			this.diveSpeed = diveSpeed;
			this.followSpeed = followSpeed;
			this.diving = 0;
		}
		
		@Override
		public boolean a() {
			return this.b();
		}
		
		@Override
		public boolean b() {
			EntityLiving target = this.entity.getGoalTarget();
			if (target == null || target.dead) {
				return false;
			}
			if (target instanceof EntityPlayer) {
				final EnumGamemode gm = ((EntityPlayer) target).playerInteractManager.getGameMode();
				if (!(gm == EnumGamemode.SURVIVAL || gm == EnumGamemode.ADVENTURE)) {
					return false;
				}
			}
			this.target = target;
			return true;
		}
	
		@Override
		public void c() {
			this.diving = 0;
			this.target = null;
		}
		
		@Override
		public void e() {
			if (this.target == null) { return; }
			this.diving++;
			this.entity.getControllerLook().a(this.target, 30f, 30f);
			this.entity.setNoGravity(true);
			final double dx = this.target.locX() - this.entity.locX();
			final double dy = this.target.locY() + this.target.getHeadHeight() - this.entity.locY();
			final double dz = this.target.locZ() - this.entity.locZ();
			final double distSq = dx*dx + dy*dy + dz*dz;
			if (distSq < this.diveRangeSq) {
				final Vec3D mot = this.entity.getMot();
				if (this.diving < 18) {
					// Pre-dive animation.
					//this.entity.getNavigation().p();
					this.entity.setMot(0.8*mot.x, 0.05, 0.8*mot.z);
				} else {
					// Dive.
					final double scale = this.diveSpeed * invSqrt(distSq);
					this.entity.setMot(dx * scale, dy * scale, dz * scale);
				}
			} else {
				// Follow.
				this.entity.getNavigation().a(this.target, this.followSpeed);
			}
		}
	
		private static double invSqrt(double x) {
			final double xhalf = 0.5D * x;
			final double a = Double.longBitsToDouble(0x5fe6ec85e7de30daL - (Double.doubleToLongBits(x) >> 1));
			final double b = a * (1.5D - xhalf * a * a);
			final double c = b * (1.5D - xhalf * b * b);
			return c;
		}
	}
}
