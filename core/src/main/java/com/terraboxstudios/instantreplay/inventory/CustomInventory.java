package com.terraboxstudios.instantreplay.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public class CustomInventory {

	//todo change health to just an integer stored in other event container
	private final ItemStack[] contents, armourContents, health;
	//todo change to itemstack for mainhand and offhand
	private final int heldSlot;

}
