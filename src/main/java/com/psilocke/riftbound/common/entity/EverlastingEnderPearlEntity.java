package com.psilocke.riftbound.common.entity;

import javax.annotation.Nullable;

import com.psilocke.riftbound.build.ModEntityTypes;
import com.psilocke.riftbound.build.ModItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class EverlastingEnderPearlEntity extends ProjectileItemEntity {

	public EverlastingEnderPearlEntity(EntityType<? extends EverlastingEnderPearlEntity> type, World world) {
		super(type, world);
	}

	public EverlastingEnderPearlEntity(World world, LivingEntity entity) {
		super(ModEntityTypes.EVERLASTING_ENDER_PEARL.get(), entity, world);
	}

	public EverlastingEnderPearlEntity(World world, double x, double y, double z) {
		super(ModEntityTypes.EVERLASTING_ENDER_PEARL.get(), x, y, z, world);
	}

	protected Item getDefaultItem() {
		return ModItems.EVERLASTING_ENDER_PEARL.get();
	}

	protected void onHitEntity(EntityRayTraceResult hit) {
		super.onHitEntity(hit);
		hit.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 0.0F);
	}

	@SuppressWarnings("deprecation")
	protected void onHit(RayTraceResult hit) {
		super.onHit(hit);
		Entity entity = this.getOwner();

		for (int i = 0; i < 32; ++i) {
			this.level.addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0D,
					this.getZ(), this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
		}

		if (!this.level.isClientSide && !this.removed) {
			if (entity instanceof ServerPlayerEntity) {
				ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) entity;
				if (serverplayerentity.connection.getConnection().isConnected() && serverplayerentity.level == this.level && !serverplayerentity.isSleeping()) {
					net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(serverplayerentity, this.getX(), this.getY(), this.getZ(), 0.0F);
					if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) { // Don't indent to lower
																							// patch size
						if (this.random.nextFloat() < 0.05F && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
							EndermiteEntity endermiteentity = EntityType.ENDERMITE.create(this.level);
							endermiteentity.setPlayerSpawned(true);
							endermiteentity.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.yRot, entity.xRot);
							this.level.addFreshEntity(endermiteentity);
						}

						if (entity.isPassenger()) {
							entity.stopRiding();
						}

						entity.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
						entity.fallDistance = 0.0F;
						entity.hurt(DamageSource.MAGIC, event.getAttackDamage());
					} // Forge: End
				}
			} else if (entity != null) {
				entity.teleportTo(this.getX(), this.getY(), this.getZ());
				entity.fallDistance = 0.0F;
			}
			//drop item
			if (!((ServerPlayerEntity)entity).abilities.instabuild) {
				this.level.addFreshEntity(new ItemEntity(this.level, (double) this.getX(), (double) this.getY(), (double) this.getZ(), this.getDefaultItem().getDefaultInstance()));
			}
			
			this.remove();
			
		}

	}
	
	public void tick() {
		Entity entity = this.getOwner();
		if (entity instanceof PlayerEntity && !entity.isAlive()) {
			this.remove();
		} else {
			super.tick();
		}

	}

	@Nullable
	public Entity changeDimension(ServerWorld world, net.minecraftforge.common.util.ITeleporter teleporter) {
		Entity entity = this.getOwner();
		if (entity != null && entity.level.dimension() != world.dimension()) {
			this.setOwner((Entity) null);
		}

		return super.changeDimension(world, teleporter);
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
	
	
}
