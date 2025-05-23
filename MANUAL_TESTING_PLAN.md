# Manual Testing Plan: Grindstone Interactions with Custom Enchantments

This document outlines the manual testing steps to verify that custom enchantments are correctly handled by the grindstone in UberEnchant.

**Prerequisites:**
*   UberEnchant plugin is installed and enabled.
*   Access to server commands, specifically for giving items and applying custom/vanilla enchantments (e.g., `/give`, `/enchant`, `/uadd`).
*   A way to identify custom enchantments (e.g., distinct lore, name color).
*   A grindstone placed in the world.

**General Test Setup:**
For each test case, you will need to prepare items as described. The command `/uadd <enchantment_key> [level]` can be used to add custom enchantments. Vanilla enchantments can be applied via an anvil, enchantment table, or the `/enchant` command.

**Test Cases:**

**Case 1: Item with Only Custom Enchantments**

1.  **Item Setup:**
    *   Obtain a new, unenchanted item (e.g., Diamond Sword).
    *   Apply one or more custom enchantments to it. For example:
        *   `/uadd custom_strength_enchant 1`
        *   `/uadd custom_fire_aspect_enchant 2`
    *   Verify the item shows only these custom enchantments in its lore.

2.  **Grindstone Actions:**
    *   Place the item with only custom enchantments into the **top slot** of the grindstone.
    *   Do **not** place any item in the bottom slot.
    *   Observe the item preview in the result slot.
    *   Take the item from the result slot.

3.  **Expected Outcome:**
    *   **Custom Enchantments Removal:** All custom enchantments should be removed from the item.
    *   **Vanilla Enchantments Removal:** Not applicable (no vanilla enchantments were present).
    *   **Curses Behavior:** Not applicable.
    *   **XP Orb Generation:** Experience orbs should be generated, similar to disenchanting vanilla enchantments. The amount of XP should be reasonable for the removed custom enchantments.
    *   **Resulting Item:** The item should be a plain, unenchanted Diamond Sword (or whatever base item was used), with no remaining custom enchantment lore.

**Case 2: Item with Only Vanilla Enchantments**

1.  **Item Setup:**
    *   Obtain a new, unenchanted item (e.g., Diamond Pickaxe).
    *   Apply one or more vanilla enchantments to it. For example:
        *   `/enchant @p minecraft:efficiency 5`
        *   `/enchant @p minecraft:unbreaking 3`
    *   Verify the item shows only these vanilla enchantments.

2.  **Grindstone Actions:**
    *   Place the item with only vanilla enchantments into the **top slot** of the grindstone.
    *   Do **not** place any item in the bottom slot.
    *   Observe the item preview in the result slot.
    *   Take the item from the result slot.

3.  **Expected Outcome:**
    *   **Custom Enchantments Removal:** Not applicable.
    *   **Vanilla Enchantments Removal:** All vanilla enchantments (that are not curses) should be removed.
    *   **Curses Behavior:** Not applicable.
    *   **XP Orb Generation:** Experience orbs should be generated as per standard vanilla mechanics.
    *   **Resulting Item:** The item should be a plain, unenchanted Diamond Pickaxe, with no remaining vanilla enchantments.

**Case 3: Item with a Mix of Custom and Vanilla Enchantments**

1.  **Item Setup:**
    *   Obtain a new, unenchanted item (e.g., Netherite Helmet).
    *   Apply a mix of custom and vanilla enchantments. For example:
        *   `/enchant @p minecraft:protection 4`
        *   `/uadd custom_night_vision_enchant 1`
        *   `/enchant @p minecraft:respiration 3`
        *   `/uadd custom_water_breathing_enchant 1`
    *   Verify the item shows all applied enchantments in its lore.

2.  **Grindstone Actions:**
    *   Place the mixed-enchantment item into the **top slot** of the grindstone.
    *   Do **not** place any item in the bottom slot.
    *   Observe the item preview in the result slot.
    *   Take the item from the result slot.

3.  **Expected Outcome:**
    *   **Custom Enchantments Removal:** All custom enchantments should be removed.
    *   **Vanilla Enchantments Removal:** All vanilla enchantments (that are not curses) should be removed.
    *   **Curses Behavior:** Not applicable.
    *   **XP Orb Generation:** Experience orbs should be generated, reflecting the removal of both vanilla and custom enchantments.
    *   **Resulting Item:** The item should be a plain, unenchanted Netherite Helmet, with no remaining custom or vanilla enchantment lore.

**Case 4: Item with Custom Enchantments and Vanilla Curses**

1.  **Item Setup:**
    *   Obtain a new, unenchanted item (e.g., Chestplate).
    *   Apply one or more custom enchantments and at least one vanilla curse. For example:
        *   `/uadd custom_thorns_enchant 3`
        *   `/enchant @p minecraft:curse_of_binding 1`
        *   `/uadd custom_health_boost_enchant 2`
        *   `/enchant @p minecraft:curse_of_vanishing 1`
    *   Verify the item shows all applied enchantments and curses.

2.  **Grindstone Actions:**
    *   Place the item with custom enchantments and curses into the **top slot** of the grindstone.
    *   Do **not** place any item in the bottom slot.
    *   Observe the item preview in the result slot.
    *   Take the item from the result slot.

3.  **Expected Outcome:**
    *   **Custom Enchantments Removal:** All custom enchantments should be removed.
    *   **Vanilla Enchantments Removal:** Regular vanilla enchantments (if any were present) would be removed.
    *   **Curses Behavior:** Vanilla curses (Curse of Binding, Curse of Vanishing) **should remain** on the item, as per standard Minecraft grindstone behavior.
    *   **XP Orb Generation:** Experience orbs should be generated for the removed custom (and any non-curse vanilla) enchantments.
    *   **Resulting Item:** The item should be a Chestplate with only the Curse of Binding and Curse of Vanishing remaining. All custom enchantment lore should be gone.

**Case 5: Combining Two Items - Transferring Enchantments (Vanilla Behavior Check)**

This case is primarily to ensure that the custom enchantment stripping logic doesn't interfere with standard grindstone combination mechanics where enchantments are *not* stripped (i.e., when combining two of the same item type to consolidate durability, or combining an item with an enchanted book). The plugin *should not* interfere here, but it's a good sanity check.

1.  **Item Setup A (Item to keep enchantments):**
    *   Obtain an item (e.g., Diamond Sword).
    *   Apply a mix of vanilla and custom enchantments.
        *   `/enchant @p minecraft:sharpness 3`
        *   `/uadd custom_looting_enchant 1`
2.  **Item Setup B (Sacrifice item - plain):**
    *   Obtain another Diamond Sword, unenchanted.

3.  **Grindstone Actions:**
    *   Place Item A (enchanted) in the **top slot**.
    *   Place Item B (plain) in the **bottom slot**.
    *   Observe the item preview in the result slot.
    *   Take the item from the result slot.

4.  **Expected Outcome (Standard Grindstone - No Disenchant):**
    *   **Custom Enchantments Removal:** Custom enchantments **should remain** on the resulting item if this specific combination *does not* involve disenchanting (e.g., transferring enchants to a book, or repairing). *However, the implemented logic for `PrepareGrindstoneEvent` will currently always strip custom enchants from the result item if it has any enchants.*
    *   **Vanilla Enchantments Removal:** Vanilla enchantments **should remain** if this specific combination *does not* involve disenchanting.
    *   **Curses Behavior:** Curses would remain.
    *   **XP Orb Generation:** Minimal or no XP, as this isn't primarily a disenchanting operation.
    *   **Resulting Item:**
        *   **Current Implementation Behavior:** Based on the implemented `GrindstoneEvents.java`, *any* item appearing in the result slot will have its custom enchantments stripped. So, the `custom_looting_enchant` will be removed. The `minecraft:sharpness` will also be removed (as per vanilla grindstone logic when combining two items like this if they don't have compatible enchants to merge, or if one is plain). The result would likely be a plain Diamond Sword.
        *   **Ideal Vanilla-like Behavior for *Combining* (Not Disenchanting):** If combining two damaged items to repair, enchantments from the top item are usually preserved. If combining an item with a book, enchantments are transferred. The current code *will strip custom enchants* even in these scenarios from the `event.getResult()`. This might be an area for future refinement if the goal is to perfectly mimic vanilla combination behavior while *only* stripping custom enchants during explicit disenchanting operations. For now, the test is to confirm the current code's behavior.

**Case 6: Combining Two Items - Both with Custom Enchantments**

1.  **Item Setup A:**
    *   Diamond Sword with `/uadd custom_strength_enchant 1`.
2.  **Item Setup B:**
    *   Diamond Sword with `/uadd custom_fire_aspect_enchant 1`.

3.  **Grindstone Actions:**
    *   Place Item A in the top slot.
    *   Place Item B in the bottom slot.
    *   Observe the preview.
    *   Take the result.

4.  **Expected Outcome:**
    *   **Custom Enchantments Removal:** All custom enchantments from both items should be removed from the resulting item.
    *   **Vanilla Enchantments Removal:** Not applicable.
    *   **Curses Behavior:** Not applicable.
    *   **XP Orb Generation:** XP should be generated for the removed custom enchantments.
    *   **Resulting Item:** A plain Diamond Sword. The grindstone typically does not merge enchantments from two items unless it's an enchanted book scenario; it usually strips them.

**Case 7: Enchanted Book with only Custom Enchantments**

1.  **Item Setup:**
    *   Obtain a regular Book.
    *   Use an anvil and another item with a custom enchantment to create an Enchanted Book with only that custom enchantment. (Or use commands if available to directly create such a book: `/give @p minecraft:enchanted_book` then `/uadd custom_test_enchant 1` while holding the book if the plugin supports enchanting books this way).
    *   Verify the book shows only the custom enchantment.

2.  **Grindstone Actions:**
    *   Place the Enchanted Book with the custom enchantment into the **top slot** of the grindstone.
    *   Do **not** place any item in the bottom slot.
    *   Observe the item preview.
    *   Take the item from the result slot.

3.  **Expected Outcome:**
    *   **Custom Enchantments Removal:** The custom enchantment should be removed.
    *   **Vanilla Enchantments Removal:** Not applicable.
    *   **Curses Behavior:** Not applicable.
    *   **XP Orb Generation:** XP should be generated.
    *   **Resulting Item:** A regular, non-enchanted Book.

**Case 8: Enchanted Book with Mixed Custom and Vanilla Enchantments**

1.  **Item Setup:**
    *   Create an Enchanted Book with both vanilla (e.g., Mending) and custom enchantments (e.g., `custom_soulbound_enchant`).
    *   Verify the book shows both types of enchantments.

2.  **Grindstone Actions:**
    *   Place the mixed Enchanted Book into the **top slot**.
    *   Observe the preview.
    *   Take the result.

3.  **Expected Outcome:**
    *   **Custom Enchantments Removal:** All custom enchantments should be removed.
    *   **Vanilla Enchantments Removal:** All vanilla enchantments (that are not curses) should be removed.
    *   **Curses Behavior:** Not applicable for books usually.
    *   **XP Orb Generation:** XP should be generated for all removed enchantments.
    *   **Resulting Item:** A regular, non-enchanted Book.

**Summary of Expected Behavior (based on current `GrindstoneEvents.java`):**
The core logic in `GrindstoneEvents.java` clones the `event.getResult()` item (whatever vanilla Minecraft decided it should be) and then strips any `UberEnchantment` instances from this clone.
*   If vanilla Minecraft would produce a non-enchanted item (e.g., disenchanting an item, combining two items that results in stripping), then custom enchantments will also be gone.
*   If vanilla Minecraft would produce an item with vanilla enchantments (e.g., an item with only curses, as curses are not removed), the custom enchantments will *still* be stripped from that result by the plugin's code.
*   If vanilla Minecraft would produce an enchanted book with only curses, custom enchantments will *still* be stripped.
*   The key is that *any* item coming out of the grindstone's result slot will have its custom enchantments (UberEnchantments) removed by the plugin. Vanilla enchantments and curses are handled by vanilla logic first, and then custom ones are stripped from that intermediate result.

This testing plan aims to cover the primary scenarios to ensure custom enchantments are properly removed during grindstone operations, while standard vanilla mechanics (like curse persistence and XP generation) are respected.
The "Combining Two Items" case (Case 5) highlights a nuance: the current plugin code will *always* strip custom enchantments from the `resultItem` if the `resultItem` itself exists and has enchantments. This means even in repair/merge scenarios where vanilla might preserve enchantments, custom ones will be stripped. This is consistent with the implemented code but might differ from a more nuanced interpretation of "disenchanting."
