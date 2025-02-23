package com.zhaba.funrecall;

import net.fabricmc.api.ModInitializer;

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

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		Registry.register(Registries.STATUS_EFFECT, new Identifier(MOD_ID, "recall"), RECALL_EFFECT);
	}
}