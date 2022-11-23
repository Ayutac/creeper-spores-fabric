package org.abos.fabricmc.creeperspores.client;

import org.abos.fabricmc.creeperspores.common.CreeperlingEntity;
import net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.util.Identifier;

public class CreeperlingChargeFeatureRenderer extends EnergySwirlOverlayFeatureRenderer<CreeperlingEntity, CreeperEntityModel<CreeperlingEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/creeper/creeper_armor.png");
    private final CreeperEntityModel<CreeperlingEntity> creeperModel;

    public CreeperlingChargeFeatureRenderer(FeatureRendererContext<CreeperlingEntity, CreeperEntityModel<CreeperlingEntity>> ctx, EntityModelLoader loader) {
        super(ctx);
        this.creeperModel = new CreeperEntityModel<>(loader.getModelPart(EntityModelLayers.CREEPER_ARMOR));
    }

    @Override
    protected float getEnergySwirlX(float v) {
        return v * 0.01F;
    }

    @Override
    protected Identifier getEnergySwirlTexture() {
        return SKIN;
    }

    @Override
    protected EntityModel<CreeperlingEntity> getEnergySwirlModel() {
        return this.creeperModel;
    }

}