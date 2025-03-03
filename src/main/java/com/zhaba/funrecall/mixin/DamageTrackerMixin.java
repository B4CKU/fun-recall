package com.zhaba.funrecall.mixin;

import com.zhaba.funrecall.RecallEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageTracker.class)
public abstract class DamageTrackerMixin {
    @Shadow LivingEntity entity;

    @Inject(method = "onDamage", at = @At(value="TAIL"))
    public void onDamageOverride(DamageSource damageSource, float damage, CallbackInfo ci) {
        RecallEffect.interruptRecall(entity);
    }

}
