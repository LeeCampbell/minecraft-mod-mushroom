package com.leecampbell.mushroom;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class ModItems {

    public static final FoodProperties POWER_MUSHROOM_FOOD = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(0.3f)
            .alwaysEdible()
            .build();

    public static final PowerMushroomItem POWER_MUSHROOM = register("power_mushroom");

    private static PowerMushroomItem register(String name) {
        ResourceKey<Item> key = ResourceKey.create(
                Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath(MushroomMod.MOD_ID, name)
        );
        PowerMushroomItem item = new PowerMushroomItem(
                new Item.Properties()
                        .food(POWER_MUSHROOM_FOOD)
                        .stacksTo(16)
                        .setId(key)
        );
        Registry.register(BuiltInRegistries.ITEM, key, item);
        return item;
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register(entries -> entries.accept(POWER_MUSHROOM));
    }
}
