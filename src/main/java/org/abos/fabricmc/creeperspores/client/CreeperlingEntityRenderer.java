package org.abos.fabricmc.creeperspores.client;

import org.abos.fabricmc.creeperspores.common.CreeperlingEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class CreeperlingEntityRenderer extends MobEntityRenderer<CreeperlingEntity, CreeperEntityModel<CreeperlingEntity>> {
    public static final Identifier DEFAULT_SKIN = new Identifier("textures/entity/creeper/creeper.png");

    private final Identifier texture;

    public static <E extends Entity> EntityRenderer<CreeperlingEntity> createRenderer(EntityRendererFactory.Context context, EntityRendererFactory<E> factory) {
        EntityRenderer<?> baseRenderer = factory.create(context);
        Identifier texture = baseRenderer.getTexture(null);
        return new CreeperlingEntityRenderer(context, texture == null ? DEFAULT_SKIN : texture);
    }

    public CreeperlingEntityRenderer(EntityRendererFactory.Context context, Identifier texture) {
        super(context, new CreeperEntityModel<>(context.getPart(EntityModelLayers.CREEPER)), 0.25F);
        this.addFeature(new CreeperlingChargeFeatureRenderer(this, context.getModelLoader()));
        this.texture = texture;
    }

    @Override
    protected void scale(CreeperlingEntity entity, MatrixStack matrix, float tickDelta) {
        matrix.scale(0.5f, 0.5f, 0.5f);
    }

    @Override
    public Identifier getTexture(CreeperlingEntity creeperling) {
        return texture;
    }
}