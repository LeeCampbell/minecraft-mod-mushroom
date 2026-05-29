package com.leecampbell.mushroom;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PowerMushroomItem extends Item {

    public PowerMushroomItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        MushroomMod.LOGGER.info("Power Mushroom consumed by {}", user.getName().getString());
        return super.finishUsingItem(stack, level, user);
    }
}
