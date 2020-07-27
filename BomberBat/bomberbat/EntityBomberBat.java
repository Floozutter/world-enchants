package bomberbat;

import org.bukkit.Bukkit;

import net.minecraft.server.v1_16_R1.DataWatcherObject;
import net.minecraft.server.v1_16_R1.DataWatcher;
import net.minecraft.server.v1_16_R1.DataWatcherRegistry;

import net.minecraft.server.v1_16_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_16_R1.EntityHuman;

import net.minecraft.server.v1_16_R1.SoundEffects;

import net.minecraft.server.v1_16_R1.GameRules;
import net.minecraft.server.v1_16_R1.Explosion;

import net.minecraft.server.v1_16_R1.MobEffect;
import net.minecraft.server.v1_16_R1.EntityAreaEffectCloud;
import java.util.Collection;
import java.util.Iterator;

import net.minecraft.server.v1_16_R1.PathfinderGoal;
import net.minecraft.server.v1_16_R1.EntityLiving;
import net.minecraft.server.v1_16_R1.Entity;
import java.util.EnumSet;

import net.minecraft.server.v1_16_R1.EntityBat;
import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.World;


public class EntityBomberBat extends EntityBat {
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
		Bukkit.broadcastMessage("Bomber Bat!");
	}
	
    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityBomberBat.DATA_SWELL_DIR, -1);
    }
	
    @Override
    protected void initPathfinder() {
		this.goalSelector.a(2, new PathfinderGoalSwell(this));
		this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
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
		this.datawatcher.set(EntityBomberBat.DATA_SWELL_DIR, i);
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
	
	static class PathfinderGoalSwell extends PathfinderGoal {
		private final EntityBomberBat entityBomberBat;
		private EntityLiving target;

		public PathfinderGoalSwell(EntityBomberBat entityBomberBat) {
			this.entityBomberBat = entityBomberBat;
			this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
		}

		@Override
		public boolean a() {
			EntityLiving entityliving = entityBomberBat.getGoalTarget();
			return (
				(this.entityBomberBat.getSwellDir() > 0) || (
					(entityliving != null)
					&& (this.entityBomberBat.h((Entity) entityliving) < 9.0D)
				)
			);
		}

		@Override
		public void c() {
			this.entityBomberBat.getNavigation().o();
			this.target = entityBomberBat.getGoalTarget();
		}

		@Override
		public void d() {
			this.target = null;
		}

		@Override
		public void e() {
			if (this.target == null) {
				this.entityBomberBat.setSwellDir(-1);
			} else if (this.entityBomberBat.h((Entity) this.target) > 49.0D) {
				this.entityBomberBat.setSwellDir(-1);
			} else if (!this.entityBomberBat.getEntitySenses().a(this.target)) {
				this.entityBomberBat.setSwellDir(-1);
			} else {
				this.entityBomberBat.setSwellDir(1);
			}
		}
	}
}
