package org.abos.fabricmc.creeperspores.mixin;

import org.abos.fabricmc.creeperspores.CreeperEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public abstract float getHealth();

    @Shadow public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isDead()Z", ordinal = 1))
    private void spawnCreeperling(DamageSource cause, float amount, CallbackInfoReturnable<Boolean> cir) {
        for (CreeperEntry creeperEntry : CreeperEntry.all()) {
            StatusEffectInstance spores = this.getStatusEffect(creeperEntry.sporeEffect());
            if (spores != null) {
                float chance = 0.2f * (spores.getAmplifier() + 1);
                if (this.getHealth() <= 0.0f) {
                    chance *= 4;
                }
                if (cause.isExplosive()) {
                    chance *= 2;
                }
                if (random.nextFloat() < chance) {
                    creeperEntry.spawnCreeperling(this);
                }
            }
        }
    }

    @ModifyVariable(method = "damage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float dealDoubleFireDamage(float damageAmount, DamageSource damage) {
        //noinspection ConstantConditions
        if ((Entity) this instanceof CreeperEntity && damage.isFire()) {
            return damageAmount * 2;
        }
        return damageAmount;
    }
}