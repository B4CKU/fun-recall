package com.zhaba.funrecall;

import com.zhaba.funrecall.networking.RecallPacket;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
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
	public static final StatusEffect RECALL_EXHAUSTION_EFFECT = new RecallExhaustionEffect()
			.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "95402DD1-D4EA-42A7-A846-C27D73CB62F1", -0.25F, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
			.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED, "CD5440CB-D53B-4AD1-858E-BEBA56B6356E", -0.25F, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);

	@Override
	public void onInitialize() {
		Registry.register(Registries.STATUS_EFFECT, new Identifier(MOD_ID, "recall"), RECALL_EFFECT);
		Registry.register(Registries.STATUS_EFFECT, new Identifier(MOD_ID, "recall_exhaustion"), RECALL_EXHAUSTION_EFFECT);

		ServerPlayNetworking.registerGlobalReceiver(RecallPacket.RECALL_PACKET_ID, ( (minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> RecallPacket.handleRecallPacket(serverPlayerEntity) ));
	}
}