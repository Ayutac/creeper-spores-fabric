package org.abos.fabricmc.creeperspores;

import com.google.common.base.Suppliers;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import org.abos.fabricmc.creeperspores.common.CreeperSporeEffect;
import org.abos.fabricmc.creeperspores.common.CreeperlingEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CreeperSpores implements ModInitializer {

    /** Identifiers corresponding to entity types that should be {@linkplain #registerCreeperLike(Identifier, EntityType)
    registered as creeper likes} if and when the entity type gets registered to {@link Registries#ENTITY_TYPE}.*/
    public static final Set<Identifier> CREEPER_LIKES = new HashSet<>(Arrays.asList(
            new Identifier("minecraft", "creeper"),
            new Identifier("mobz", "creep_entity"),
            new Identifier("mobz", "crip_entity")
    ));

    public static final TagKey<Item> FERTILIZERS = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "fertilizers"));

    public static final Identifier CREEPERLING_FERTILIZATION_PACKET = id("creeperling-fertilization");
    public static final String GIVE_SPORES_TAG = "cspores:giveSpores";
    public static final int MAX_SPORE_TIME = 20 * 180;

    public static final GameRules.Key<EnumRule<CreeperGrief>> CREEPER_GRIEF = registerGamerule(
            "creeper-spores:creeperGrief",
            GameRuleFactory.createEnumRule(CreeperGrief.CHARGED)
    );
    public static final GameRules.Key<DoubleRule> CREEPER_REPLACE_CHANCE = registerGamerule(
            "creeper-spores:creeperReplaceChance",
            GameRuleFactory.createDoubleRule(0.2, 0, 1)
    );

    public static Identifier id(String path) {
        return new Identifier("creeperspores", path);
    }

    public static <T> void visitRegistry(Registry<T> registry, BiConsumer<Identifier, T> visitor) {
        RegistryEntryAddedCallback.event(registry).register((index, identifier, entry) -> visitor.accept(identifier, entry));
        new HashSet<>(registry.getIds()).forEach(id -> visitor.accept(id, registry.get(id)));
    }

    @Override
    public void onInitialize() {
        visitRegistry(Registries.ENTITY_TYPE, (id, type) -> {
            if (CREEPER_LIKES.contains(id)) {
                // can't actually check that the entity type is living, so just hope nothing goes wrong
                @SuppressWarnings("unchecked") EntityType<? extends LivingEntity> livingType = (EntityType<? extends LivingEntity>) type;
                registerCreeperLike(id, livingType);
            }
        });
    }

    private static <T extends GameRules.Rule<T>> GameRules.Key<T> registerGamerule(String name, GameRules.Type<T> type) {
        return GameRuleRegistry.register(name, GameRules.Category.MOBS, type);
    }

    @ApiStatus.Internal
    public static void registerCreeperLike(Identifier id) {
        // can't actually check that the entity type is living, so just hope nothing goes wrong
        // the cast to Optional<?> is not optional, according to javac
        @SuppressWarnings({"unchecked", "RedundantCast"}) Optional<EntityType<? extends LivingEntity>> creeperType = (Optional<EntityType<? extends LivingEntity>>) (Optional<?>) Registries.ENTITY_TYPE.getOrEmpty(id);
        if (creeperType.isPresent()) {
            registerCreeperLike(id, creeperType.get());
        } else {
            CREEPER_LIKES.add(id);
        }
    }

    @ApiStatus.Internal
    public static void registerCreeperLike(Identifier id, EntityType<? extends LivingEntity> type) {
        String prefix = id.getNamespace().equals("minecraft") ? "" : (id.toString().replace(':', '_') + "_");
        EntityType<CreeperlingEntity> creeperlingType = Registry.register(
                Registries.ENTITY_TYPE,
                CreeperSpores.id(prefix + "creeperling"),
                createCreeperlingType(type)
        );
        DefaultAttributeContainer defaultAttributes = DefaultAttributeRegistry.get(type);
        FabricDefaultAttributeRegistry.register(creeperlingType, MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, defaultAttributes.getBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) * 0.5)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, defaultAttributes.getBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 0.8)
        );
        CreeperSporeEffect sporesEffect = Registry.register(
                Registries.STATUS_EFFECT,
                CreeperSpores.id(prefix + "creeper_spore"),
                createCreeperSporesEffect(type)
        );
        CreeperEntry.register(type, creeperlingType, sporesEffect);
    }

    @Contract(pure = true)
    private static CreeperSporeEffect createCreeperSporesEffect(EntityType<?> creeperType) {
        return new CreeperSporeEffect(StatusEffectCategory.NEUTRAL, 0x22AA00, creeperType);
    }

    @Contract(pure = true)
    private static EntityType<CreeperlingEntity> createCreeperlingType(EntityType<? extends LivingEntity> creeperType) {
        Supplier<CreeperEntry> kind = Suppliers.memoize(() -> CreeperEntry.get(creeperType));
        EntityType<CreeperlingEntity> creeperlingType = FabricEntityTypeBuilder
                .create(creeperType.getSpawnGroup(),
                        (EntityType<CreeperlingEntity> type, World world) -> new CreeperlingEntity(Objects.requireNonNull(kind.get()), world))
                .dimensions(EntityDimensions.changing(creeperType.getWidth() / 2f, creeperType.getHeight() / 2f))
                .trackRangeBlocks(64)
                .trackedUpdateRate(1)
                .forceTrackedVelocityUpdates(true)
                .build();
        creeperlingType.translationKey = "entity.creeperspores.creeperling";
        return creeperlingType;
    }
}