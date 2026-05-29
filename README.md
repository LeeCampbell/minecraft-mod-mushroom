# Power Mushroom Mod

A Minecraft Fabric mod that adds a **Power Mushroom** — eat it to double in size, become much stronger, and jump higher. The effects last until you get hit.

Built as a father-son project to learn Minecraft modding.

## The Mod

When you eat the Power Mushroom:
- **Double size** — you grow to twice your normal height
- **Super strength** — massively increased attack damage
- **High jump** — jump much higher than normal

All effects are removed the moment you take any damage.

## Tech Stack

- **Minecraft** 1.21.4
- **Fabric** mod loader + Fabric API
- **Java** 21 (Zulu JDK 26)
- **Vanilla attributes** for all effects (no extra mod dependencies)
- **Fabric GameTest** for automated headless testing

## Project Structure

```
specs/
  understand.md          # Original requirements
  plan.md                # Implementation plan with task checklist
src/
  main/java/...          # Mod source code
  main/resources/        # fabric.mod.json, assets, textures
  client/java/...        # Client-side code
  gametest/java/...      # Automated GameTests
```

## Development

### Prerequisites

- Java 21+ (or use the Docker setup below)
- Minecraft 1.21.4 (for manual playtesting)

### Build & Test

```bash
./gradlew build
```

This compiles the mod and runs all GameTests headlessly — no Minecraft client needed.

### Run Minecraft with the Mod (manual testing)

```bash
./gradlew runClient
```

Launches Minecraft in a dev environment with the mod loaded. Try:
1. Creative mode — find "Power Mushroom" in the Food & Drinks tab
2. `/give @p mushroom-mod:power_mushroom 16`
3. Switch to survival and eat one
4. Get hit by a mob to lose the effects

### Build the Release JAR

```bash
./gradlew build
```

The installable JAR is at `build/libs/mushroom-mod-1.0.0.jar`.

## Automated Agent (Docker)

The mod can be built unattended using Claude Code in a Docker container. This runs the agent in YOLO mode — it reads `specs/plan.md` and works through each task automatically.

### Setup

1. Copy `.env.example` to `.env` and fill in your credentials:

   ```bash
   cp .env.example .env
   ```

2. Add your credentials to `.env`:
   - `CLAUDE_CODE_OAUTH_TOKEN` — your Claude subscription token (run `claude auth token` to retrieve it)
   - `GH_TOKEN` — a GitHub personal access token with `repo` scope

### Run the Agent

```bash
docker compose run claude
```

With a custom timeout (default is 600 seconds):

```bash
TIMEOUT=1800 docker compose run claude
```

### What Happens

- The agent reads `specs/plan.md` and implements each task in order
- Each task is verified with `./gradlew build` (compile + GameTests) before moving on
- Log output goes to both **stdout** and **`claude-agent.log`** in the repo root
- The agent runs as a non-root user inside the container
- Your repo is mounted at `/workspace` so all changes are written directly to your local files

### Rebuild the Docker Image

If you change the `Dockerfile` or `entrypoint.sh`:

```bash
docker compose build
```
