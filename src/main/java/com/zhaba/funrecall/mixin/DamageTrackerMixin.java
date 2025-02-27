package com.zhaba.funrecall.mixin;

import com.zhaba.funrecall.FunRecall;
import com.zhaba.funrecall.RecallEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageTracker.class)
public class DamageTrackerMixin {

    @Shadow LivingEntity entity;

    @Inject(method = "onDamage", at = @At(value="TAIL"))
    public void onDamageOverride(DamageSource damageSource, float damage, CallbackInfo ci) {
        StatusEffectInstance recallInstance = entity.getStatusEffect(FunRecall.RECALL_EFFECT);
        if(recallInstance != null) {
            entity.removeStatusEffect(FunRecall.RECALL_EFFECT);
            entity.getWorld().playSound(null, entity.getBlockPos(), RecallEffect.getInterruptRecallSound(), SoundCategory.PLAYERS, 0.4f, 1.0f);
        }
    }

}
