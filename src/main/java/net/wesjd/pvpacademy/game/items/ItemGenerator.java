package net.wesjd.pvpacademy.game.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemGenerator {

    private final Random random = new Random();
    private final List<ChanceItem> chanceItems = new ArrayList<>();

    public ItemGenerator() {
        {
            final ItemStack stack = new ItemStack(Material.GOLDEN_APPLE);
            final ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + "Lucky Apple");
            stack.setItemMeta(meta);
            chanceItems.add(new ChanceItem(stack, 20));
        }
        {
            final ItemStack stack = new ItemStack(Material.ENDER_PEARL);
            final ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Mystery Pearl");
            stack.setItemMeta(meta);
            chanceItems.add(new ChanceItem(stack, 5));
        }
        chanceItems.add(new ChanceItem(new ItemStack(Material.FLINT_AND_STEEL, 1, (short) 2), 40));
    }

    public void fillInventory(PlayerInventory inventory, int luck) {
        inventory.setHelmet(getHelmet(luck));
        inventory.setChestplate(getChestplate());
        inventory.setLeggings(getLeggings());
        inventory.setBoots(getBoots());
        inventory.setItem(0, getSword());
        inventory.setItem(1, getBow(luck));
        inventory.setItem(9, getArrows());

        final List<ItemStack> specialItems = getSpecialitems(luck);
        inventory.addItem(specialItems.toArray(new ItemStack[specialItems.size()]));
    }

    private List<ItemStack> getSpecialitems(int luck) {
        final List<ItemStack> ret = new ArrayList<>();
        chanceItems.stream()
                .filter((chanceItem) -> random.nextInt(100) <= chanceItem.getPercentChance())
                .map(ChanceItem::getStack)
                .forEach(ret::add);
        ret.add(new ItemStack(Material.WEB, random.nextInt(5) + 1));
        ret.add(new ItemStack(Material.COOKED_BEEF, random.nextInt(6) + 1));
        ret.add(new ItemStack(Material.APPLE, random.nextInt(4) + 1));
        return ret;
    }

    private ItemStack getSword() {
        return new ItemStack(Material.STONE_SWORD);
    }

    private ItemStack getBow(int luck) {
        final ItemStack bow = new ItemStack(Material.BOW);
        if(random.nextInt(luck + 2) <= luck) bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        return bow;
    }

    private ItemStack getArrows() {
        return new ItemStack(Material.ARROW, random.nextInt(10) + 1);
    }

    private ItemStack getHelmet(int luck) {
        if(random.nextInt(luck + 2) <= luck) return new ItemStack(Material.IRON_HELMET);
        else return new ItemStack(Material.GOLD_HELMET);
    }

    private ItemStack getChestplate() {
        final ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
        chestplate.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        return chestplate;
    }

    private ItemStack getLeggings() {
        return new ItemStack(Material.CHAINMAIL_LEGGINGS);
    }

    private ItemStack getBoots() {
        return new ItemStack(Material.IRON_BOOTS);
    }

}
