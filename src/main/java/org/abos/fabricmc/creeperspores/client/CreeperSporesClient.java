package org.abos.fabricmc.creeperspores.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.abos.fabricmc.creeperspores.CreeperEntry;
import org.abos.fabricmc.creeperspores.CreeperSpores;
import org.abos.fabricmc.creeperspores.common.CreeperlingEntity;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class CreeperSporesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(
                CreeperEntry.getVanilla().creeperlingType(),
                (context) -> new CreeperlingEntityRenderer(context, CreeperlingEntityRenderer.DEFAULT_SKIN)
        );
        ClientPlayNetworking.registerGlobalReceiver(
                CreeperSpores.CREEPERLING_FERTILIZATION_PACKET,
                (client, handler, buf, responseSender) -> CreeperlingEntity.createParticles(client, client.player, buf)
        );
    }
}