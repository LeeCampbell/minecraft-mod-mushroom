package com.leecampbell.mushroom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MushroomPowerManager {

    private static final ResourceLocation SCALE_ID = ResourceLocation.fromNamespaceAndPath("mushroom-mod", "power_mushroom_scale");
    private static final ResourceLocation ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath("mushroom-mod", "power_mushroom_attack_damage");
    private static final ResourceLocation JUMP_STRENGTH_ID = ResourceLocation.fromNamespaceAndPath("mushroom-mod", "power_mushroom_jump_strength");

    public static void applyPower(ServerPlayer player) {
        removePower(player);

        AttributeInstance scaleAttr = player.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            scaleAttr.addPermanentModifier(new AttributeModifier(SCALE_ID, 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }

        AttributeInstance attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr != null) {
            attackAttr.addPermanentModifier(new AttributeModifier(ATTACK_DAMAGE_ID, 10.0, AttributeModifier.Operation.ADD_VALUE));
        }

        AttributeInstance jumpAttr = player.getAttribute(Attributes.JUMP_STRENGTH);
        if (jumpAttr != null) {
            jumpAttr.addPermanentModifier(new AttributeModifier(JUMP_STRENGTH_ID, 0.7, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }

    public static void removePower(ServerPlayer player) {
        AttributeInstance scaleAttr = player.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            scaleAttr.removeModifier(SCALE_ID);
        }

        AttributeInstance attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr != null) {
            attackAttr.removeModifier(ATTACK_DAMAGE_ID);
        }

        AttributeInstance jumpAttr = player.getAttribute(Attributes.JUMP_STRENGTH);
        if (jumpAttr != null) {
            jumpAttr.removeModifier(JUMP_STRENGTH_ID);
        }
    }

    public static boolean hasPower(ServerPlayer player) {
        AttributeInstance scaleAttr = player.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return false;
        }
        return scaleAttr.hasModifier(SCALE_ID);
    }
}
