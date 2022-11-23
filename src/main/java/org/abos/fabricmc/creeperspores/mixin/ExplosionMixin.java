package org.abos.fabricmc.creeperspores.mixin;

import org.abos.fabricmc.creeperspores.common.SporeSpreader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    @Shadow @Final private double x;

    @Shadow @Final private double y;

    @Shadow @Final private double z;

    @Shadow public abstract LivingEntity getCausingEntity();


    // Using ModifyVariable is way easier than an Inject capturing every local
    @ModifyVariable(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", shift = At.Shift.AFTER), ordinal = 0)
    private Entity spreadSpores(Entity affectedEntity) {
        if (this.getCausingEntity() instanceof SporeSpreader) {
            ((SporeSpreader) this.getCausingEntity()).spreadSpores((Explosion) (Object) this, new Vec3d(this.x, this.y, this.z), affectedEntity);
        }
        return affectedEntity;
    }
}