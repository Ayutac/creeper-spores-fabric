package org.abos.fabricmc.creeperspores.api;

import com.google.common.base.Preconditions;
import org.abos.fabricmc.creeperspores.CreeperSpores;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.explosion.Explosion;

public final class CreeperSporesApi {

    /**
     * Register an {@link EntityType} as a creeper equivalent based on its id.
     * Creeper equivalents are able to spread spores and spawn creeperlings.
     *
     * <p> If {@code typeId} corresponds to an already registered {@code EntityType},
     * this method behaves as if {@code registerCreeperLike(Registry.ENTITY_TYPE.get(typeId))}.
     * Otherwise, it will listen to future entity type registrations and react to {@code typeId}.
     * <strong>The designated entity type must extend {@code LivingEntity}.</strong>
     *
     * @param typeId the identifier of a type of entity to consider creeper-like
     * @throws NullPointerException if {@code typeId} is null
     * @see #registerCreeperLike(EntityType)
     */
    public static void registerCreeperLike(Identifier typeId) {
        Preconditions.checkNotNull(typeId);
        CreeperSpores.registerCreeperLike(typeId);
    }

    /**
     * Register a previously {@linkplain Registry#register(Registry, Identifier, Object) registered} {@link EntityType}
     * as a creeper equivalent, able to spread spores and spawn creeperlings.
     *
     * <p> When an explosion's {@link Explosion#getCausingEntity() cause} is of a registered creeper-like type,
     * affected entities get a spore effect applied. The spore effect spawns creeperlings of the source type,
     * that eventually grow into regular entities of the appropriate type.
     *
     * <p> Because creeperlings take the texture of their parent type, entities with a different model than the base
     * creeper should use their own creeperling subclass and renderer.
     *
     * @param type a type of entity to consider creeper-like
     * @throws IllegalStateException if {@code type} has not been registered before calling this method
     * @throws NullPointerException  if {@code type} is null
     */
    public static void registerCreeperLike(EntityType<? extends LivingEntity> type) {
        Preconditions.checkNotNull(type);
        Preconditions.checkState(!Registry.ENTITY_TYPE.getId(type).equals(Registry.ENTITY_TYPE.getDefaultId()), "Entity types need to be registered first");
        CreeperSpores.registerCreeperLike(Registry.ENTITY_TYPE.getId(type), type);
    }
}