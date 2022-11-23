package org.abos.fabricmc.creeperspores.common;

import com.google.common.base.Suppliers;
import net.minecraft.entity.effect.StatusEffectCategory;
import org.abos.fabricmc.creeperspores.CreeperEntry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.function.Supplier;

public class CreeperSporeEffect extends StatusEffect {
    private final EntityType<?> creeperType;
    private final Supplier<CreeperEntry> creeperEntry;

    public CreeperSporeEffect(StatusEffectCategory type, int color, EntityType<?> creeperType) {
        super(type, color);
        this.creeperType = creeperType;
        this.creeperEntry = Suppliers.memoize(() -> Objects.requireNonNull(CreeperEntry.get(this.creeperType)));
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration == 1 && Math.random() < 0.6;
    }

    @Override
    public void applyUpdateEffect(LivingEntity affected, int amplifier) {
        this.creeperEntry.get().spawnCreeperling(affected);
    }

    @Override
    public String loadTranslationKey() {
        return "effect.creeperspores.creeper_spore";
    }

    public Text getLocalizedName() {
        return Text.translatable("effect.creeperspores.generic_spore", this.creeperType.getName());
    }

}