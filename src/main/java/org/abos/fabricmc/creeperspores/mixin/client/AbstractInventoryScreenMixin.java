package org.abos.fabricmc.creeperspores.mixin.client;

import org.abos.fabricmc.creeperspores.common.CreeperSporeEffect;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractInventoryScreen.class)
public abstract class AbstractInventoryScreenMixin {
    @Unique
    private List<StatusEffectInstance> renderedEffects;
    @Unique
    private int renderedEffectsIndex;

    @Inject(method = "drawStatusEffectDescriptions", at = @At("HEAD"))
    private void creeperspores$retrieveRenderedEffects(MatrixStack matrices, int x, int width, Iterable<StatusEffectInstance> effects, CallbackInfo ci) {
        renderedEffects = (List<StatusEffectInstance>) effects;
        renderedEffectsIndex = 0;
    }

    @Inject(method = "drawStatusEffectDescriptions", at = @At("RETURN"))
    private void creeperspores$clearRenderedEffects(MatrixStack matrices, int x, int width, Iterable<StatusEffectInstance> effects, CallbackInfo ci) {
        renderedEffects = null;
    }

    /*@ModifyVariable(method = "getStatusEffectDescription", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/text/Text;copy()Lnet/minecraft/text/MutableText;"), index = 2)
    private MutableText creeperspores$updateRenderedEffectName(MutableText drawnString) {
        if (renderedEffects != null) {
            StatusEffect renderedEffect = renderedEffects.get(renderedEffectsIndex++).getEffectType();
            if (renderedEffect instanceof CreeperSporeEffect sporeEffect) {
                return sporeEffect.getLocalizedName().copyContentOnly();
            }
        }
        return drawnString;
    }*/
}