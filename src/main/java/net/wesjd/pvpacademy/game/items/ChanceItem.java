package net.wesjd.pvpacademy.game.items;

import org.bukkit.inventory.ItemStack;

public class ChanceItem {

    private final ItemStack stack;
    private final double percentChance;

    public ChanceItem(ItemStack stack, double percentChance) {
        this.stack = stack;
        this.percentChance = percentChance;
    }

    public ItemStack getStack() {
        return stack;
    }

    public double getPercentChance() {
        return percentChance;
    }

}
