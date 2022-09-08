package me.roundaround.morestats.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.morestats.MoreStats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(DamageTracker.class)
public abstract class DamageTrackerMixin {
  @Shadow
  private LivingEntity entity;

  @Inject(method = "onDamage", at = @At(value = "HEAD"))
  public void onDamage(DamageSource damageSource, float originalHealth, float damage, CallbackInfo info) {
    if (!(entity instanceof PlayerEntity)) {
      return;
    }

    PlayerEntity player = (PlayerEntity) entity;

    float remaining = originalHealth - damage;
    if (originalHealth > 4f && remaining <= 4f && remaining > 0.5f) {
      player.incrementStat(MoreStats.CLOSE_CALL);
    } else if (originalHealth > 0.5f && remaining <= 0.5f && remaining > 0f) {
      player.incrementStat(MoreStats.VERY_CLOSE_CALL);
    }

    int amount = Math.round(damage * 10f);

    if (damageSource == DamageSource.FLY_INTO_WALL) {
      player.incrementStat(MoreStats.CRUNCH);
      player.increaseStat(MoreStats.CRUNCH_DAMAGE, amount);
    } else if (damageSource == DamageSource.FALL) {
      player.increaseStat(MoreStats.FALL_DAMAGE, amount);
    } else if (damageSource == DamageSource.DROWN) {
      player.increaseStat(MoreStats.DROWN_DAMAGE, amount);
    } else if (damageSource == DamageSource.STARVE) {
      player.increaseStat(MoreStats.STARVE_DAMAGE, amount);
    } else if (damageSource == DamageSource.IN_FIRE || damageSource == DamageSource.LAVA
        || damageSource == DamageSource.ON_FIRE) {
      player.increaseStat(MoreStats.FIRE_DAMAGE, amount);
    }
  }
}