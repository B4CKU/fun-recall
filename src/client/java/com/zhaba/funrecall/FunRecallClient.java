package com.zhaba.funrecall;

import com.zhaba.funrecall.networking.RecallPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.particle.GlowParticle;

public class FunRecallClient implements ClientModInitializer {

	private static KeyBinding recallButton;

	@Override
	public void onInitializeClient() {
		ParticleFactoryRegistry.getInstance().register(FunRecall.RECALL_DUST_PARTICLE, GlowParticle.WaxOffFactory::new);

		//code 66 on keyboard is the "B" button
		recallButton = KeyBindingHelper.registerKeyBinding( new KeyBinding("key.fun-recall.recall", 66, "key.fun-recall.category") );

		ClientTickEvents.END_CLIENT_TICK.register(client-> {

			//FIXME: i dislike how nested this is, but i'll have to manage until i find a way to do my usual
			//"if (wrong) return;" in a way that works here (or until i test it out and find out it actually works here)
			//for now, i'm fairly confident it would try to return the "onInitializeClient" somehow and break everything
			if(recallButton.wasPressed()){
				ClientPlayerEntity clientPlayer = client.player;

				//i'll be honest, i'm not 100% sure why this is here, i just saw multiple mods doing this
				//UPDATE: after a whole night of thinking, i realised it's probably so if the button is pressed
				//we don't try to recall in the main menu, which would most likely cause a crash
				if(clientPlayer != null) {
					ClientPlayNetworking.send(RecallPacket.RECALL_PACKET_ID, PacketByteBufs.empty());
				}
			}
        });
	}
}