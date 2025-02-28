package com.zhaba.funrecall;

import com.zhaba.funrecall.networking.RecallPacket;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunRecall implements ModInitializer {
	public static final String MOD_ID = "fun-recall";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	public static final StatusEffect RECALL_EFFECT = new RecallEffect();
	public static final StatusEffect RECALL_EXHAUSTION_EFFECT = new RecallExhaustionEffect();

	@Override
	public void onInitialize() {
		Registry.register(Registries.STATUS_EFFECT, new Identifier(MOD_ID, "recall"), RECALL_EFFECT);
		Registry.register(Registries.STATUS_EFFECT, new Identifier(MOD_ID, "recall_exhaustion"), RECALL_EXHAUSTION_EFFECT);

		ServerPlayNetworking.registerGlobalReceiver(RecallPacket.RECALL_PACKET_ID, ( (minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> RecallPacket.handleRecallPacket(serverPlayerEntity) ));
	}
}