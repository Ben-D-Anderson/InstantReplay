package com.terraboxstudios.instantreplay.inventory;

import com.terraboxstudios.instantreplay.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryFactory {

    private static InventoryFactory instance;

    public static InventoryFactory getInstance() {
        if (instance == null) instance = new InventoryFactory();
        return instance;
    }

    private void updateInventory(Inventory inventory, CustomInventory customInventory) {
        for (int i = 0; i < 9; i++) {
            ItemStack item = customInventory.getContents()[i];
            if (item == null)
                item = new ItemStack(Material.AIR, 1);
            inventory.setItem(i, item);
        }
        for (int i = 9; i < 18; i++) {
            ItemStack item = customInventory.getContents()[i];
            if (item == null)
                item = new ItemStack(Material.AIR, 1);
            inventory.setItem(i + 18, item);
        }
        for (int i = 18; i < 27; i++) {
            ItemStack item = customInventory.getContents()[i];
            if (item == null)
                item = new ItemStack(Material.AIR, 1);
            inventory.setItem(i, item);
        }
        for (int i = 27; i < 36; i++) {
            ItemStack item = customInventory.getContents()[i];
            if (item == null)
                item = new ItemStack(Material.AIR, 1);
            inventory.setItem(i - 18, item);
        }
        for (int i = 36; i < 40; i++) {
            ItemStack item = customInventory.getContents()[i - 36];
            if (item == null)
                item = new ItemStack(Material.AIR, 1);
            inventory.setItem(i, item);
        }
        ItemStack healthItem = customInventory.getHealth()[0];
        if (healthItem == null)
            healthItem = new ItemStack(Material.AIR, 1);
        inventory.setItem(40, healthItem);
        for (int i = 41; i < 45; i++) {
            ItemStack item = Main.getVersionSpecificProvider().getItemFactory().getEmptyItemGUIPlaceholder();
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(" ");
            }
            item.setItemMeta(itemMeta);
            inventory.setItem(i, item);
        }
    }

    public Inventory createNPCInventory(CustomInventory customInventory, String name) {
        Inventory inventory = Bukkit.createInventory(null, 45, name + "'s Inventory");
        updateInventory(inventory, customInventory);
        return inventory;
    }

    public void updateNPCInventory(Inventory inventory, CustomInventory customInventory) {
        updateInventory(inventory, customInventory);
    }
}
