package org.abos.fabricmc.creeperspores.mixin.client;

import org.abos.fabricmc.creeperspores.CreeperEntry;
import org.abos.fabricmc.creeperspores.common.CreeperSporeEffect;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.effect.StatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectSpriteManager.class)
public abstract class StatusEffectSpriteManagerMixin {
    @Unique
    private static final StatusEffect BASE_CREEPER_SPORES = CreeperEntry.getVanilla().sporeEffect();

    @Shadow public abstract Sprite getSprite(StatusEffect statusEffect_1);

    @Inject(method = "getSprite", at = @At("HEAD"), cancellable = true)
    private void creeperspores$getCreeperSporesSprite(StatusEffect effect, CallbackInfoReturnable<Sprite> cir) {
        if (effect instanceof CreeperSporeEffect && effect != BASE_CREEPER_SPORES) {
            cir.setReturnValue(getSprite(BASE_CREEPER_SPORES));
        }
    }
}