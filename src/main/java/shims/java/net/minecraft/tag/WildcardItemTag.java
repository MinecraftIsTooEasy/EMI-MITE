package shims.java.net.minecraft.tag;

import dev.emi.emi.Prototype;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WildcardItemTag implements TagKey<Prototype> {

	private final Item item;
	private final List<Prototype> subtypes;
	public WildcardItemTag(Item item) {
		this.item = item;
		ArrayList<ItemStack> li = new ArrayList<ItemStack>();
		item.getSubItems(item.itemID, item.getCreativeTab(), li);
		if (li.isEmpty()) {
			li.add(new ItemStack(item));
		}
		subtypes = li.stream()
				.map(Prototype::of)
				.collect(Collectors.toList());
	}

	@Override
	public ResourceLocation id() {
		return new ResourceLocation("wildcard", item.getItemDisplayName(new ItemStack(item))+"/"+item.itemID);
	}

	@Override
	public List<Prototype> get() {
		return subtypes;
	}

	@Override
	public String getFlavor() {
		return "wildcard";
	}

}
