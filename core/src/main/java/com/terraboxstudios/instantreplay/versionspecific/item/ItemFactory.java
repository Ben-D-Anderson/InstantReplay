package com.terraboxstudios.instantreplay.versionspecific.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public interface ItemFactory {

    ItemStack[] getHands(PlayerInventory playerInventory);

    ItemStack getEmptyItemGUIPlaceholder();
    ItemStack getHealthItemGUI();

}
