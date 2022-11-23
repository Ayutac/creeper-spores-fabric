package org.abos.fabricmc.creeperspores.mixin.client;

import org.abos.fabricmc.creeperspores.CreeperEntry;
import org.abos.fabricmc.creeperspores.client.CreeperlingEntityRenderer;
import net.fabricmc.fabric.impl.client.rendering.EntityRendererRegistryImpl;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRendererRegistryImpl.class, remap = false)
public abstract class EntityRendererRegistryMixin {
    @Shadow
    public static <E extends Entity> void register(EntityType<?> entityType, EntityRendererFactory<E> factory) {
    }

    @Inject(method = "register", at = @At(value = "RETURN"))
    private static <E extends Entity> void creeperspores$onRendererRegistered(EntityType<? extends E> entityType, EntityRendererFactory<E> factory, CallbackInfo ci) {
        CreeperEntry creeperEntry = CreeperEntry.get(entityType);
        if (creeperEntry != null) {
            register(creeperEntry.creeperlingType(), (context) -> CreeperlingEntityRenderer.createRenderer(context, factory));
        }
    }
}