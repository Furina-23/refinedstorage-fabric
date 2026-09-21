package com.refinedmods.refinedstorage.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import com.refinedmods.refinedstorage.registry.ForgeRegistries;

import javax.annotation.Nullable;

public class UpgradeWithEnchantedBookRecipeSerializer implements RecipeSerializer<UpgradeWithEnchantedBookRecipe> {
    @Override
    public UpgradeWithEnchantedBookRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        JsonObject enchantmentInfo = json.getAsJsonObject("enchantment");

        ItemStack result = new ItemStack(ForgeRegistries.ITEMS.get(new ResourceLocation(json.getAsJsonPrimitive("result").getAsString())));
        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.get(new ResourceLocation(enchantmentInfo.getAsJsonPrimitive("id").getAsString()));

        int level = 1;
        if (enchantmentInfo.has("level")) {
            level = enchantmentInfo.getAsJsonPrimitive("level").getAsInt();
        }

        return new UpgradeWithEnchantedBookRecipe(recipeId, enchantment, level, result);
    }

    @Nullable
    @Override
    public UpgradeWithEnchantedBookRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        ItemStack result = buffer.readItem();
        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.get(buffer.readResourceLocation());
        int level = buffer.readInt();

        return new UpgradeWithEnchantedBookRecipe(recipeId, enchantment, level, result);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, UpgradeWithEnchantedBookRecipe recipe) {
        buffer.writeItem(recipe.getResult());
        buffer.writeResourceLocation(ForgeRegistries.ENCHANTMENTS.getKey(recipe.getEnchant().enchantment));
        buffer.writeInt(recipe.getEnchant().level);
    }
}
