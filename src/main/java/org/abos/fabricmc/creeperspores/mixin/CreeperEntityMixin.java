package org.abos.fabricmc.creeperspores.mixin;

import org.abos.fabricmc.creeperspores.CreeperEntry;
import org.abos.fabricmc.creeperspores.CreeperGrief;
import org.abos.fabricmc.creeperspores.CreeperSpores;
import org.abos.fabricmc.creeperspores.common.CreeperlingEntity;
import org.abos.fabricmc.creeperspores.common.SporeSpreader;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends HostileEntity implements SporeSpreader {

    @Unique private TriState giveSpores = TriState.DEFAULT;

    @Shadow @Final private static TrackedData<Boolean> CHARGED;

    protected CreeperEntityMixin(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }

    @Unique
    private boolean shouldSpreadSpores() {
        return this.giveSpores == TriState.TRUE || (this.giveSpores == TriState.DEFAULT && !this.isAiDisabled());
    }

    @Override
    public void spreadSpores(Explosion explosion, Vec3d center, Entity affectedEntity) {
        if (affectedEntity instanceof LivingEntity victim && this.shouldSpreadSpores()) {
            double exposure = Explosion.getExposure(center, victim);
            CreeperEntry creeperEntry = CreeperEntry.get(this.getType());
            if (creeperEntry != null) {
                victim.addStatusEffect(new StatusEffectInstance(creeperEntry.sporeEffect(), (int) Math.round(CreeperSpores.MAX_SPORE_TIME * exposure)));
            }
        }
    }

    @Inject(
            method = "interactMob",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/HostileEntity;interactMob(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"),
            cancellable = true
    )
    private void interactSpawnEgg(PlayerEntity player, Hand hand, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = player.getStackInHand(hand);
        CreeperEntry creeperEntry = CreeperEntry.get(this.getType());
        if (creeperEntry != null && CreeperlingEntity.interactSpawnEgg(player, this, stack, creeperEntry)) {
            cir.setReturnValue(true);
        }
    }

    /*@ModifyVariable(method = "explode", ordinal = 0, at = @At(value = "STORE", ordinal = 0))
    private Explosion.DestructionType griefLessExplosion(Explosion.DestructionType explosionType) {
        CreeperGrief grief = world.getGameRules().get(CreeperSpores.CREEPER_GRIEF).get();
        if (!grief.shouldGrief(this.dataTracker.get(CHARGED))) {
            return Explosion.DestructionType.NONE;
        }
        return explosionType;
    }*/

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void writeCustomDataToTag(NbtCompound tag, CallbackInfo ci) {
        if (this.giveSpores != TriState.DEFAULT) {
            tag.putBoolean(CreeperSpores.GIVE_SPORES_TAG, this.giveSpores.get());
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void readCustomDataFromTag(NbtCompound tag, CallbackInfo ci) {
        if (tag.contains(CreeperSpores.GIVE_SPORES_TAG)) {
            this.giveSpores = TriState.of(tag.getBoolean(CreeperSpores.GIVE_SPORES_TAG));
        }
    }
}