# Manual Testing Plan: WorldGuard Integration and Event Cancellation Handling for PvP Custom Enchantments

This document outlines the manual testing steps to verify that UberEnchant's custom PvP enchantments (Harm, Poison, Wither) correctly interact with WorldGuard region protections and also respect event cancellation by other plugins.

**Important Note on Order of Checks:** UberEnchant's enchantment handlers now check for event cancellation (`event.isCancelled()`) *before* evaluating WorldGuard flags. This ensures that if another plugin has already cancelled the interaction, UberEnchant will not apply its effects, regardless of WorldGuard settings.

**Reporter:** (Your Name/Username)
**Date:** (Date of Testing)

**I. Prerequisites:**

1.  **Server Setup:**
    *   A Bukkit/Spigot/Paper server.
    *   **WorldGuard plugin installed and configured.** Ensure you have permissions to define regions and set flags.
    *   **UberEnchant plugin installed** with the latest changes incorporating WorldGuard checks.
2.  **Player Setup:**
    *   At least two players are required: an "Attacker" and a "Victim".
    *   Both players need to be in Survival or Adventure mode for damage and effects to apply correctly.
3.  **Items:**
    *   Obtain weapons (e.g., Diamond Swords).
    *   Enchant these weapons with the following UberEnchant custom enchantments. Use one enchantment per weapon to isolate effects, or a single weapon with all three if preferred, noting all expected effects.
        *   `Harm`
        *   `Poison`
        *   `Wither`
    *   Commands like `/uadd harm 1`, `/uadd poison 1`, `/uadd wither 1` can be used if permissions allow.
4.  **Permissions:**
    *   Ensure testers have permissions to use WorldGuard commands for region definition (`/rg define`, `/rg flag`) or have an admin perform the setup.
    *   Permissions to use UberEnchant commands if needed for item setup.

**II. WorldGuard Region Setup:**

Perform these steps in a designated test area on your server.

1.  **Region for PvP Denied (e.g., "pvp_deny_zone"):**
    *   Select an area using WorldEdit selection tools (e.g., `//wand`, left/right click to set positions).
    *   Define the region: `/rg define pvp_deny_zone`
    *   Set the PvP flag to deny: `/rg flag pvp_deny_zone pvp deny`
    *   *(Optional but Recommended)* Set other flags to observe behavior, e.g., `damage-animals deny` or `mob-spawning deny` to ensure the region is working as expected for other flags.

2.  **Region for PvP Allowed (e.g., "pvp_allow_zone"):**
    *   Select a different, distinct area.
    *   Define the region: `/rg define pvp_allow_zone`
    *   Set the PvP flag to allow: `/rg flag pvp_allow_zone pvp allow`
    *   *(Note: If your server's global default is PvP allowed, this region explicitly confirms behavior within a flagged area. If the global default is deny, this region is essential.)*

3.  **Global Area (No Specific Test Region):**
    *   This will be any area on the server *outside* of `pvp_deny_zone` and `pvp_allow_zone`.
    *   The behavior here will depend on WorldGuard's global region settings (`__global__`) or default Minecraft server PvP settings if WorldGuard doesn't define a global PvP flag. For testing, it's useful to know what the default is. Assume default Minecraft PvP is enabled unless `__global__` in WorldGuard is set to deny PvP.

**III. Test Scenarios:**

For each weapon (Harm, Poison, Wither), perform the following tests. The "Attacker" is the player wielding the custom enchanted weapon. The "Victim" is the other player. Unless otherwise specified (e.g., in Scenario 7), these scenarios assume no other plugin is cancelling the `EntityDamageByEntityEvent`, allowing the WorldGuard check to be the primary factor.

**Scenario 1: PvP Disabled Region (`pvp_deny_zone`)**

1.  **Action:** Both Attacker and Victim stand inside `pvp_deny_zone`. Attacker hits Victim with the custom enchanted weapon.
2.  **Expected Outcome:**
    *   **Primary Reason for No Effect:** WorldGuard PvP flag (`pvp deny`). The `event.isCancelled()` check by UberEnchant is expected to be `false` unless WorldGuard itself cancels the event at a higher priority (which it often does).
    *   **Custom Effects:** Harm, Poison, or Wither effects should **NOT** apply to the Victim.
    *   **Vanilla Damage:** Standard melee damage may or may not be dealt, depending on how WorldGuard's `pvp deny` flag is configured (WorldGuard usually cancels the entire event).
    *   **Console:** No errors.

**Scenario 2: PvP Enabled Region (`pvp_allow_zone`)**

1.  **Action:** Both Attacker and Victim stand inside `pvp_allow_zone`. Attacker hits Victim with the custom enchanted weapon.
2.  **Expected Outcome:**
    *   **Primary Reason for Effect:** WorldGuard PvP flag (`pvp allow`) and `event.isCancelled()` is expected to be `false`.
    *   **Custom Effects:** Harm, Poison, or Wither effects **SHOULD** apply to the Victim.
    *   **Vanilla Damage:** Standard melee damage should be dealt.
    *   **Console:** No errors.

**Scenario 3: Global Area (No Specific UberEnchant/PvP Test Region)**

1.  **Action:** Both Attacker and Victim stand in an area not covered by `pvp_deny_zone` or `pvp_allow_zone`. Attacker hits Victim with the custom enchanted weapon.
2.  **Expected Outcome:**
    *   **Primary Reason for Effect/No Effect:** Depends on server's default PvP rules (vanilla or WorldGuard `__global__` region flags), and `event.isCancelled()` is expected to be `false`.
    *   **Custom Effects:**
        *   If PvP is allowed by default: Custom effects **SHOULD** apply.
        *   If PvP is denied by default: Custom effects should **NOT** apply.
    *   **Console:** No errors.

**Scenario 4: No WorldGuard Plugin Present**

1.  **Setup:**
    *   Temporarily stop the server.
    *   Remove or disable the WorldGuard plugin (e.g., rename `WorldGuard.jar` to `WorldGuard.jar.disabled`).
    *   Start the server.
2.  **Action:** Attacker hits Victim with the custom enchanted weapon in any location.
3.  **Expected Outcome:**
    *   **Primary Reason for Effect:** WorldGuard not present, so its checks are bypassed; `event.isCancelled()` is assumed `false`.
    *   **Custom Effects:** Harm, Poison, or Wither effects **SHOULD** apply to the Victim.
    *   **Vanilla Damage:** Standard melee damage should be dealt (assuming default Minecraft PvP is on).
    *   **Console:** No `NoClassDefFoundError` for WorldGuard classes from UberEnchant. A startup message indicating WorldGuard was not found (if implemented by UberEnchant) is acceptable.

**Scenario 5: Player vs. Mob (PvE)**

1.  **Setup:** Spawn a hostile mob (e.g., Zombie, Skeleton).
2.  **Actions:**
    *   Attacker (with custom enchanted weapon) hits the mob inside `pvp_deny_zone`.
    *   Attacker (with custom enchanted weapon) hits the mob inside `pvp_allow_zone`.
    *   Attacker (with custom enchanted weapon) hits the mob in the global area.
3.  **Expected Outcome (for all actions in this scenario):**
    *   **Primary Reason for Effect:** WorldGuard PvP checks are player-specific; `event.isCancelled()` is assumed `false`.
    *   **Custom Effects:** Harm, Poison, or Wither effects **SHOULD** apply to the mob.
    *   **Vanilla Damage:** Standard melee damage should be dealt to the mob.
    *   **Console:** No errors.

**Scenario 6: Victim Invincibility (WorldGuard Flag)**

1.  **Region Setup:**
    *   Use the `pvp_allow_zone` or create a new region.
    *   Set the invincibility flag for this region: `/rg flag <region_id> invincibility allow`
    *   (Ensure `pvp` flag is also set to `allow` or not set, or is undefined and defaults to allow, to ensure invincibility is the primary factor being tested. `event.isCancelled()` is assumed `false`.)
2.  **Action:** Both Attacker and Victim stand inside this region. Attacker hits Victim with the custom enchanted weapon.
3.  **Expected Outcome:**
    *   **Primary Reason for No Effect:** WorldGuard `invincibility allow` flag.
    *   **Custom Effects:** Harm, Poison, or Wither effects should **NOT** apply to the Victim.
    *   **Vanilla Damage:** No damage should be dealt.
    *   **Console:** No errors.

**Scenario 7: Event Cancellation by Another Plugin (Testing `event.isCancelled()` Precedence)**

1.  **Setup:**
    *   **Environment:** This test is best performed in an area where WorldGuard *would permit PvP* (e.g., `pvp_allow_zone` or a global area where PvP is on by default). This ensures that the `event.isCancelled()` check is the determining factor, not WorldGuard.
    *   **Simulated/Actual Plugin:**
        *   **Option A (Simulated):** If possible, create a very simple test plugin. This plugin should listen to `EntityDamageByEntityEvent` at a `HIGH` or `HIGHEST` priority. If the damager and entity are both players, it should unconditionally call `event.setCancelled(true);`.
        *   **Option B (Actual):** Use an existing protection plugin (e.g., a factions plugin, a team management plugin, an anti-grief plugin like GriefPrevention, or a minigame plugin) that has a feature to prevent PvP between certain players (e.g., teammates, faction allies, players in a safe zone defined by *that other* plugin, newbie protection). Configure this other plugin to prevent PvP between the Attacker and Victim.
2.  **Action:** Attacker (with custom enchanted weapon) hits Victim in the configured environment.
3.  **Expected Outcome:**
    *   **Primary Reason for No Effect:** The `EntityDamageByEntityEvent` is cancelled by the other plugin *before* UberEnchant's effect logic (including WorldGuard checks) is processed.
    *   **Custom Effects:** Harm, Poison, or Wither effects should **NOT** apply to the Victim. This is because UberEnchant's `OnHit` methods now check `event.isCancelled()` first and return immediately if true.
    *   **Vanilla Damage:** No damage should be dealt, as the event is cancelled by the other plugin.
    *   **Console:** No errors from UberEnchant. The other plugin might log its cancellation action. This test specifically verifies that UberEnchant respects the prior cancellation.

**IV. Verification Methods:**

1.  **Observing Effects:**
    *   **Poison/Wither:** Check for potion effect icons on the Victim's screen and particle effects around the Victim. The Victim's health bar should show the respective effect (green hearts for poison, black hearts for wither).
    *   **Harm:** Observe an immediate drop in the Victim's health greater than standard melee damage. Combat log messages might also indicate extra damage if detailed.
2.  **Checking Health:** Monitor the Victim's health bar closely after being hit.
3.  **Server Console Monitoring:** Keep the server console visible during all tests. Look for:
    *   **Absence of errors:** Specifically, no `NullPointerExceptions`, `NoClassDefFoundError` (except potentially during startup in Scenario 4 if WorldGuard is missing and the plugin logs this), or other exceptions related to UberEnchant or WorldGuard event handling.
    *   **(Optional) Debug Messages:** If UberEnchant has debug messages for WorldGuard interactions, check if they match expectations (e.g., "PvP denied by WorldGuard in region X").

**V. Reporting Results:**

For each test scenario and each enchantment:
*   Document the region used.
*   Document the action taken.
*   Document the observed outcome (Did effects apply? Was damage dealt? Any console errors?).
*   Confirm if the observed outcome matched the expected outcome. If not, provide details.

This comprehensive testing will help ensure the WorldGuard integration is functioning correctly and robustly.
