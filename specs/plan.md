# Power Mushroom Mod - Implementation Plan

## Context

Father-son team building their first Minecraft mod. Goal: a custom mushroom food item that when eaten gives the player double size, increased strength, and higher jumping — lasting until the player takes damage. The repo is empty (just `.gitignore` and `specs/`), so we're building from scratch.

**Tech stack:** Fabric mod loader, Minecraft 1.21.4, Java 21, Mojang official mappings.

**Key design decisions:**
- Use vanilla Minecraft attributes (`generic.scale`, `generic.attack_damage`, `generic.jump_strength`) — no extra mod dependencies
- Fabric API's `ServerLivingEntityEvents.AFTER_DAMAGE` handles the "remove on hit" mechanic
- Fabric GameTest framework for automated headless verification in Docker
- Each task is atomic — build + test must pass before moving to the next

---

## Task 1: Project Scaffolding

- [ ] **1.1** Create `gradle.properties` with version numbers (MC 1.21.4, Fabric Loader 0.19.2, Fabric API, Java 21)
- [ ] **1.2** Create `settings.gradle` with Fabric Maven repo, project name `mushroom-mod`
- [ ] **1.3** Create `build.gradle` with Fabric Loom plugin, Mojang mappings, Fabric API dependency, and GameTest source set via `fabricApi.configureTests { createSourceSet = true; eula = true; enableGameTests = true }`
- [ ] **1.4** Add Gradle wrapper files (`gradlew`, `gradlew.bat`, `gradle/wrapper/*`)
- [ ] **1.5** Create `src/main/resources/fabric.mod.json` with mod metadata and entrypoints
- [ ] **1.6** Create empty mixin configs (`mushroom-mod.mixins.json`, `mushroom-mod.client.mixins.json`)
- [ ] **1.7** Create skeleton `MushroomMod.java` (main entrypoint) and `MushroomModClient.java` (client entrypoint)
- [ ] **1.8** Create `src/gametest/resources/fabric.mod.json` for test entrypoint
- [ ] **1.9** Create skeleton GameTest class `MushroomModGameTest.java` with one passing smoke test

**Verify:** `./gradlew build` compiles and GameTest smoke test passes.

---

## Task 2: Register the Power Mushroom Item

- [x] **2.1** Create `ModItems.java` — item registration using `ResourceKey` pattern (1.21.2+ style), registers `POWER_MUSHROOM` with food properties (nutrition 4, alwaysEdible, stacksTo 16), added to Food & Drinks creative tab
- [x] **2.2** Create `PowerMushroomItem.java` — extends `Item`, overrides `finishUsingItem` (skeleton that logs for now)
- [x] **2.3** Call `ModItems.initialize()` from `MushroomMod.onInitialize()`
- [x] **2.4** Add GameTest: verify `ModItems.POWER_MUSHROOM` is registered in the item registry

**Verify:** `./gradlew build` passes, GameTest confirms item is registered.

---

## Task 3: Item Assets (Texture, Model, Language)

- [x] **3.1** Create `src/main/resources/assets/mushroom-mod/models/item/power_mushroom.json` — standard `item/generated` model
- [x] **3.2** Create `src/main/resources/assets/mushroom-mod/textures/item/power_mushroom.png` — 16x16 pixel art mushroom
- [x] **3.3** Create `src/main/resources/assets/mushroom-mod/lang/en_us.json` — `"item.mushroom-mod.power_mushroom": "Power Mushroom"`
- [x] **3.4** Create `src/main/resources/assets/mushroom-mod/icon.png` — simple mod icon

**Verify:** `./gradlew build` passes. (Asset correctness is visual — verified manually on host later.)

---

## Task 4: Power-Up Effect System

- [x] **4.1** Create `MushroomPowerManager.java` with:
  - `applyPower(ServerPlayer)` — removes existing modifiers first, then applies:
    - Scale: `ADD_MULTIPLIED_BASE` value `1.0` (doubles size)
    - Attack Damage: `ADD_VALUE` value `10.0` (big damage boost)
    - Jump Strength: `ADD_MULTIPLIED_BASE` value `0.7` (70% higher jumps)
  - `removePower(ServerPlayer)` — removes all three modifiers
  - `hasPower(ServerPlayer)` — checks if scale modifier is present
  - Uses unique `ResourceLocation` IDs per modifier (e.g. `mushroom-mod:power_mushroom_scale`)
  - Uses `addPermanentModifier` for persistence across save/load
- [x] **4.2** Wire `PowerMushroomItem.finishUsingItem` to call `MushroomPowerManager.applyPower()`
- [x] **4.3** Add GameTests:
  - Test that `applyPower` adds the scale modifier (verify via `getAttribute`)
  - Test that `removePower` removes modifiers
  - Test that `hasPower` returns correct state
  - Test that eating multiple mushrooms doesn't stack (applies cleanly)

**Verify:** `./gradlew build` passes, GameTests confirm attribute modifiers apply/remove correctly.

---

## Task 5: "Until Hit" Mechanic

- [ ] **5.1** In `MushroomMod.onInitialize()`, register `ServerLivingEntityEvents.AFTER_DAMAGE` callback:
  - If entity is `ServerPlayer` AND `damageTaken > 0` AND `hasPower(player)` → `removePower(player)`
- [ ] **5.2** Add GameTests:
  - Test that applying damage to a powered player removes the power
  - Test that a non-powered player taking damage does nothing (no error)

**Verify:** `./gradlew build` passes, GameTests confirm damage removes effects.

---

## Task 6: Polish & Player Feedback

- [ ] **6.1** Add chat message on power activation: "Power Mushroom activated!"
- [ ] **6.2** Add chat message on power removal (hit): "Your mushroom power fades!"
- [ ] **6.3** Review and tweak attribute values for fun gameplay

**Verify:** `./gradlew build` passes. Manual playtesting on host for feel.

---

## Key Files

| File | Purpose |
|------|---------|
| `build.gradle` | Fabric Loom, dependencies, GameTest config |
| `src/main/java/com/leecampbell/mushroom/MushroomMod.java` | Main entrypoint, damage event listener |
| `src/main/java/com/leecampbell/mushroom/ModItems.java` | Item registration |
| `src/main/java/com/leecampbell/mushroom/PowerMushroomItem.java` | Custom food item |
| `src/main/java/com/leecampbell/mushroom/MushroomPowerManager.java` | Core logic: apply/remove attribute modifiers |
| `src/main/resources/fabric.mod.json` | Mod metadata |
| `src/gametest/java/com/leecampbell/mushroom/test/MushroomModGameTest.java` | Automated GameTests |
| `src/gametest/resources/fabric.mod.json` | GameTest entrypoint registration |

## Agent Workflow

Each task is executed as a separate Claude Code session to keep context fresh and avoid compaction.

### Per-Task Workflow

1. **Branch** — Create a feature branch from `main` named `task-N-short-description`
2. **Plan** — Read this plan file and understand the task scope. Use an Explore sub-agent to research any unknowns before writing code
3. **Implement with TDD** — For each sub-item in the task:
   a. Write the test first (GameTest), verify it fails or is pending
   b. Write the implementation to make the test pass
   c. Run `./gradlew build` to confirm compilation and tests pass
   d. Git commit the sub-item with a message like: `Task N.M: <short description>`
4. **Review** — Use a sub-agent to review the completed task: does the code compile, do tests pass, is the implementation correct and minimal?
5. **Update plan** — Check off completed items in `specs/plan.md` with `[x]`, commit the update
6. **PR** — Create a pull request from the feature branch to `main` with a summary of what was done

### Principles

- **One task at a time** — never work on multiple tasks in a single session
- **Test first** — write GameTests before implementation where possible
- **Small commits** — commit after each sub-item, not in one big batch
- **Verify before advancing** — `./gradlew build` must pass before moving to the next sub-item
- **Use sub-agents** — delegate research (Explore) and review (Plan) to sub-agents to manage context and get independent perspectives
- **Feature branches** — all work happens on a branch, merged to `main` via PR
- **Stop on failure** — if the build breaks and can't be fixed within a few attempts, stop and report rather than spiralling

### Docker Notes

- Each task runs as a separate `claude` invocation inside Docker (see `entrypoint.sh`)
- `--max-turns 50` prevents runaway sessions
- Logs are written per-task to `logs/task-N.log`
- No GUI or Minecraft client needed — all verification is via Fabric GameTest framework
- Final manual playtesting (`./gradlew runClient`) is done on the host machine as a last step
