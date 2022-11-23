package org.abos.fabricmc.creeperspores;

import org.abos.fabricmc.creeperspores.common.CreeperSporeEffect;
import org.abos.fabricmc.creeperspores.common.CreeperlingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record CreeperEntry(EntityType<? extends LivingEntity> creeperType,
                           EntityType<CreeperlingEntity> creeperlingType,
                           CreeperSporeEffect sporeEffect) {
    private static final Map<EntityType<?>, CreeperEntry> CREEPER_ENTRIES = new HashMap<>();

    static void register(EntityType<? extends LivingEntity> type, EntityType<CreeperlingEntity> creeperlingType, CreeperSporeEffect sporesEffect) {
        CREEPER_ENTRIES.put(type, new CreeperEntry(type, creeperlingType, sporesEffect));
    }

    public static Collection<CreeperEntry> all() {
        return CREEPER_ENTRIES.values();
    }

    public static CreeperEntry get(EntityType<?> creeperType) {
        return CREEPER_ENTRIES.get(creeperType);
    }

    public static CreeperEntry getVanilla() {
        return Objects.requireNonNull(CREEPER_ENTRIES.get(EntityType.CREEPER));
    }

    /**
     * Spawns a creeperling at an affected entity
     */
    public CreeperlingEntity spawnCreeperling(Entity affected) {
        if (!affected.world.isClient) {
            CreeperlingEntity spawn = Objects.requireNonNull(this.creeperlingType.create(affected.world));
            spawn.refreshPositionAndAngles(affected.getX(), affected.getY(), affected.getZ(), 0, 0);
            affected.world.spawnEntity(spawn);
            return spawn;
        }
        return null;
    }

    /**
     * Create a creeperling for an entity, without spawning it
     */
    public CreeperlingEntity createCreeperling(LivingEntity entity) {
        CreeperlingEntity creeperlingEntity = Objects.requireNonNull(creeperlingType.create(entity.world));
        creeperlingEntity.copyPositionAndRotation(entity);
        return creeperlingEntity;
    }
}