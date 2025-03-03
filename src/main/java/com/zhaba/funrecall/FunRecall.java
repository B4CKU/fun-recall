package com.zhaba.funrecall;

import com.zhaba.funrecall.networking.RecallPacket;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunRecall implements ModInitializer {
	public static final String MOD_ID = "fun-recall";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final DefaultParticleType RECALL_DUST_PARTICLE = FabricParticleTypes.simple();
	public static final SoundEvent RECALL_DUST_SHIMMER = SoundEvent.of(new Identifier(MOD_ID, "recall_dust_shimmer"));
	public static final SoundEvent RECALL_CHANNEL = SoundEvent.of(new Identifier(MOD_ID, "recall_channel"));
	public static final SoundEvent RECALL_INTERRUPT = SoundEvent.of(new Identifier(MOD_ID, "recall_interrupt"));

	public static final StatusEffect RECALL_EFFECT = new RecallEffect();
	public static final StatusEffect RECALL_EXHAUSTION_EFFECT = new RecallExhaustionEffect()
			.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "95402DD1-D4EA-42A7-A846-C27D73CB62F1", -0.25F, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
			.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED, "CD5440CB-D53B-4AD1-858E-BEBA56B6356E", -0.25F, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);

	@Override
	public void onInitialize() {
		Registry.register(Registries.PARTICLE_TYPE, new Identifier(MOD_ID, "recall_dust"), RECALL_DUST_PARTICLE );
		Registry.register(Registries.SOUND_EVENT, new Identifier(MOD_ID, "recall_dust_shimmer"), RECALL_DUST_SHIMMER);
		Registry.register(Registries.SOUND_EVENT, new Identifier(MOD_ID, "recall_channel"), RECALL_CHANNEL);
		Registry.register(Registries.SOUND_EVENT, new Identifier(MOD_ID, "recall_interrupt"), RECALL_INTERRUPT);

		Registry.register(Registries.STATUS_EFFECT, new Identifier(MOD_ID, "recall"), RECALL_EFFECT);
		Registry.register(Registries.STATUS_EFFECT, new Identifier(MOD_ID, "recall_exhaustion"), RECALL_EXHAUSTION_EFFECT);

		ServerPlayNetworking.registerGlobalReceiver(RecallPacket.RECALL_PACKET_ID, ( (minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> RecallPacket.handleRecallPacket(serverPlayerEntity) ));
	}

	//this function's really important for the sake of compatibility. i originally meant for everything to operate on
	//player entities only, but there's a bunch of mixin methods that i can't inject into, because they belong to the
	//parent class and aren't overriden by PlayerEntity.java. as far as i know, this is the best way to make it
	//player-only, without compromising compatibility with other mods
	public static boolean isRecallableEntity(LivingEntity entity) {
		return entity instanceof PlayerEntity && entity instanceof RecallDataTrackerAccessor;
	}
}