package org.abos.fabricmc.creeperspores.mixin;

import org.abos.fabricmc.creeperspores.CreeperEntry;
import org.abos.fabricmc.creeperspores.CreeperSpores;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.LightType;
import net.minecraft.world.SpawnHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(SpawnHelper.class)
public abstract class SpawnHelperMixin {
    @ModifyVariable(method = "spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;refreshPositionAndAngles(DDDFF)V", shift = AFTER))
    private static MobEntity substituteCreeper(MobEntity spawnedEntity) {
        if (spawnedEntity instanceof CreeperEntity
                && spawnedEntity.world.getLightLevel(LightType.SKY, spawnedEntity.getBlockPos()) > 0
                && spawnedEntity.world.getGameRules().get(CreeperSpores.CREEPER_REPLACE_CHANCE).get() > spawnedEntity.getRandom().nextDouble()) {
            CreeperEntry creeperEntry = CreeperEntry.get(spawnedEntity.getType());
            if (creeperEntry != null) {
                return creeperEntry.createCreeperling(spawnedEntity);
            }
        }
        return spawnedEntity;
    }
}