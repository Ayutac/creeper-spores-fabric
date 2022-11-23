package org.abos.fabricmc.creeperspores.mixin;

import net.minecraft.entity.ai.goal.UntamedActiveTargetGoal;
import net.minecraft.entity.passive.*;
import org.abos.fabricmc.creeperspores.common.CreeperlingEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({OcelotEntity.class, CatEntity.class})
public abstract class CatEntitiesMixin extends AnimalEntity {
    protected CatEntitiesMixin(EntityType<? extends AnimalEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "initGoals", at = @At("RETURN"))
    private void initGoals(CallbackInfo ci) {
        this.targetSelector.add(1, new UntamedActiveTargetGoal<>((TameableEntity) (Object)this, CreeperlingEntity.class, false, null));
    }
}