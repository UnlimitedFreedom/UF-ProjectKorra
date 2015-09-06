package com.projectkorra.items.customs;

import com.projectkorra.items.EnchantGlow;
import com.projectkorra.items.Messages;
import com.projectkorra.items.attribute.Attribute;
import com.projectkorra.items.attribute.AttributeList;
import com.projectkorra.projectkorra.ProjectKorra;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CustomItem {

    public static ConcurrentHashMap<String, CustomItem> items = new ConcurrentHashMap<String, CustomItem>();
    public static ArrayList<CustomItem> itemList = new ArrayList<CustomItem>();

    private String name;
    private String displayName;
    private ArrayList<String> lore;
    private Material material;
    private int quantity;
    private short damage;
    private ArrayList<RecipeIngredient> recipe;
    private boolean unshapedRecipe;
    private boolean valid;
    private boolean alreadyFinal;
    private boolean glow;
    private ArrayList<Attribute> attributes;

    public CustomItem() {
        name = "";
        displayName = "";
        lore = new ArrayList<String>();
        material = null;
        quantity = 1;
        damage = (short) 0;
        recipe = new ArrayList<RecipeIngredient>();
        valid = true;
        unshapedRecipe = true;
        attributes = new ArrayList<Attribute>();
        glow = false;
    }

    public void updateName(String s) {
        if (s == null || s.length() == 0 || s.contains(" ")) {
            valid = false;
            ProjectKorra.log.info(Messages.BAD_NAME + ": " + toString());
            if (s != null) {
                name = s;
            }
        } else {
            name = s;
        }
    }

    public void updateDisplayName(String s) {
        if (s == null || s.length() == 0) {
            valid = false;
            ProjectKorra.log.info(Messages.BAD_DNAME + ": " + toString());
        } else {
            if (!s.contains("<&")) {
                s = "<&f>" + s;
            }
            displayName = colorizeString(s);
        }
    }

    public void updateLore(String s) {
        if (s == null || s.length() == 0) {
            valid = false;
            ProjectKorra.log.info(Messages.BAD_LORE + ": " + toString());
        } else {
            String[] lines = s.split("<n>");
            for (String line : lines) {
                lore.add(colorizeString(line));
            }
        }
    }

    public void updateMaterial(String s) {
        if (s == null || s.length() == 0) {
            valid = false;
            ProjectKorra.log.info(Messages.BAD_MATERIAL + ": " + toString());
        } else {
            material = Material.getMaterial(s);
            if (material == null) {
                valid = false;
                ProjectKorra.log.info(Messages.BAD_MATERIAL + ": " + s);
            }
        }
    }

    public void updateQuantity(String s) {
        try {
            quantity = Integer.parseInt(s);
        } catch (Exception e) {
            valid = false;
            ProjectKorra.log.info(Messages.BAD_QUANTITY + ": " + toString());
        }
    }

    public void updateDamage(String s) {
        try {
            damage = (short) Integer.parseInt(s);
        } catch (Exception e) {
            valid = false;
            ProjectKorra.log.info(Messages.BAD_DAMAGE + ": " + toString());
        }
    }

    public void updateGlow(String s) {
        try {
            glow = Boolean.parseBoolean(s);
        } catch (Exception e) {
            valid = false;
            ProjectKorra.log.info(Messages.BAD_GLOW + ": " + toString());
        }
    }

    /**
     * Updates this CustomItem with a new Recipe. The recipe will consist of
     * comma and colon separated entries to define the recipe ingredients. For
     * example: <i>UnshapedRecipe: WOOL, WOOL:3:14, WOOL</i> Recipe ingredients
     * can be both Material and other CustomItems.
     *
     * @param s
     * @param customItemNames
     */
    @SuppressWarnings("deprecation")
    public void updateRecipe(String s, Set<String> customItemNames) {
        try {
            s = s.replaceAll(" ", "");
            String[] commas = s.split(",");

            for (String comma : commas) {
                String[] colons = comma.split(":");
                Material mat = Material.getMaterial(colons[0]);

                // Try to get the Material by id
                if (mat == null) {
                    try {
                        mat = Material.getMaterial(Integer.parseInt(colons[0]));
                    } catch (NumberFormatException e) {
                    }
                }

                int quantity = 1;
                short damage = 0;
                if (colons.length > 1) {
                    quantity = Integer.parseInt(colons[1]);
                }
                if (colons.length > 2) {
                    damage = (short) Integer.parseInt(colons[2]);
                }

                // The material is either invalid or it is a custom item name
                if (mat != null) {
                    recipe.add(new RecipeIngredient(mat, quantity, damage));
                } else if (customItemNames.contains(colons[0])) {
                    recipe.add(new RecipeIngredient(colons[0], quantity, damage));
                } else {
                    ProjectKorra.log.info(Messages.BAD_RECIPE_MAT + ": " + colons[0]);
                    valid = false;
                    continue;
                }
            }
            while (recipe.size() < 9) {
                recipe.add(new RecipeIngredient(Material.AIR));
            }
        } catch (Exception e) {
            ProjectKorra.log.info(Messages.BAD_RECIPE + ": " + s);
            valid = false;
        }
    }

    public void build() {
        if (alreadyFinal) {
            return;
        }
        alreadyFinal = true;
        if (!valid) {
            ProjectKorra.log.info(Messages.BAD_ITEM + ": " + toString());
        } else if (items.containsKey(name.toLowerCase())) {
            ProjectKorra.log.info(Messages.DUPLICATE_ITEM + ": " + toString());
        } else if (name.length() == 0) {
            ProjectKorra.log.info(Messages.BAD_NAME + ": " + toString());
        } else if (displayName.length() == 0) {
            ProjectKorra.log.info(Messages.BAD_DNAME + ": " + toString());
        } else if (material == null) {
            ProjectKorra.log.info(Messages.BAD_MATERIAL + ": " + toString());
        } else {
            items.put(name.toLowerCase(), this);
            itemList.add(this);
        }
    }

    public ItemStack generateItem() {
        ItemStack istack = new ItemStack(material, quantity, damage);
        ItemMeta meta = istack.getItemMeta();
        List<String> tempLore = new ArrayList<String>(lore);
        meta.setDisplayName(displayName);

        for (Attribute attr : attributes) {
            try {
                if (attr.getName().equalsIgnoreCase("Charges")) {
                    tempLore.add(AttributeList.CHARGES_STR + Integer.parseInt(attr.getValues().get(0)));
                } else if (attr.getName().equalsIgnoreCase("ClickCharges")) {
                    tempLore.add(AttributeList.CLICK_CHARGES_STR + Integer.parseInt(attr.getValues().get(0)));
                } else if (attr.getName().equalsIgnoreCase("SneakCharges")) {
                    tempLore.add(AttributeList.SNEAK_CHARGES_STR + Integer.parseInt(attr.getValues().get(0)));
                }
            } catch (Exception e) {
            }

            try {
                if (attr.getName().equalsIgnoreCase("LeatherColor")) {
                    LeatherArmorMeta lmeta = (LeatherArmorMeta) meta;
                    lmeta.setColor(Color.fromRGB(Integer.parseInt(attr.getValues().get(0).trim()), Integer.parseInt(attr.getValues().get(1).trim()), Integer.parseInt(attr.getValues().get(2).trim())));
                    meta = lmeta;
                }
            } catch (Exception e) {
            }
        }

        meta.setLore(tempLore);
        istack.setItemMeta(meta);
        if (glow) {
            EnchantGlow.addGlow(istack);
        }

        return istack;
    }

    /**
     * Checks if a CustomItem has a specific attribute, and also that the
     * attribute has a boolean value of true.
     *
     * If the value is false, or it was not found, then this returns false.
     *
     * @param attrib the name of the attribute
     * @return true if the attribute was found and true
     */
    public boolean getBooleanAttributeValue(String attrib) {
        for (Attribute attr : attributes) {
            if (attr.getBooleanValue(attrib)) {
                return true;
            }
        }
        return false;
    }

    public static ConcurrentHashMap<String, CustomItem> getItems() {
        return items;
    }

    public static void setItems(ConcurrentHashMap<String, CustomItem> items) {
        CustomItem.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ArrayList<String> getLore() {
        return lore;
    }

    public void setLore(ArrayList<String> lore) {
        this.lore = lore;
    }

    public Material getMaterial() {
        return material;
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * Given an attribute name, returns the attribute if this item has it, else
     * returns null.
     *
     * @param attrName the name of the attribute
     * @return an attribute or null
     */
    public Attribute getAttribute(String attrName) {
        for (Attribute attr : attributes) {
            if (attr.getName().equalsIgnoreCase(attrName)) {
                return attr;
            }
        }
        return null;
    }

    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }

    public void setDamage(short damage) {
        this.damage = damage;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public short getDamage() {
        return damage;
    }

    public void setData(short damage) {
        this.damage = damage;
    }

    public ArrayList<RecipeIngredient> getRecipe() {
        return recipe;
    }

    public void setRecipe(ArrayList<RecipeIngredient> recipe) {
        this.recipe = recipe;
    }

    public boolean isUnshapedRecipe() {
        return unshapedRecipe;
    }

    public void setUnshapedRecipe(boolean unshapedRecipe) {
        this.unshapedRecipe = unshapedRecipe;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "CustomItem [name=" + name + ", displayName=" + displayName + ", lore=" + lore + ", material=" + material + ", quantity=" + quantity + ", damage=" + damage;
    }

    public static String colorizeString(String s) {
        s = s.replaceAll("<", "");
        s = s.replaceAll(">", "");
        s = ChatColor.translateAlternateColorCodes('&', s);
        return s;
    }

    public static CustomItem getCustomItem(ItemStack istack) {
        ItemMeta meta = istack.getItemMeta();
        if (meta == null || meta.getDisplayName() == null) {
            return null;
        }
        for (CustomItem citem : items.values()) {
            if (meta.getDisplayName().equals(citem.getDisplayName())) {
                return citem;
            }
        }
        return null;
    }

    public static CustomItem getCustomItem(String itemName) {
        if (items.containsKey(itemName.toLowerCase())) {
            return items.get(itemName.toLowerCase());
        }
        return null;
    }
}
