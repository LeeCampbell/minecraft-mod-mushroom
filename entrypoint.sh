#!/usr/bin/env bash
set -euo pipefail

TIMEOUT="${TIMEOUT:-900}"
LOG_DIR="/workspace/logs"
PLAN_FILE="/workspace/specs/plan.md"

mkdir -p "$LOG_DIR"

# Count tasks by looking for "## Task N:" headers in the plan
TASK_COUNT=$(grep -cE '^## Task [0-9]+:' "$PLAN_FILE")

echo "=== Claude Agent started at $(date -Iseconds) ==="
echo "Plan: ${PLAN_FILE}"
echo "Tasks found: ${TASK_COUNT}"
echo "Timeout per task: ${TIMEOUT}s"
echo "==========================================="

# Configure git identity for commits inside the container
git config user.email "claude-agent@local"
git config user.name "Claude Agent"

for TASK_NUM in $(seq 1 "$TASK_COUNT"); do
  LOG_FILE="${LOG_DIR}/task-${TASK_NUM}.log"

  # Skip tasks that are already completed (all sub-items checked off)
  # Extract the task section and check if any unchecked items remain
  TASK_SECTION=$(sed -n "/^## Task ${TASK_NUM}:/,/^## /p" "$PLAN_FILE" | head -n -1)
  if echo "$TASK_SECTION" | grep -q '\- \[ \]'; then
    : # Has unchecked items, proceed
  else
    echo "--- Task ${TASK_NUM} already completed, skipping ---"
    continue
  fi

  echo ""
  echo "--- Task ${TASK_NUM}/${TASK_COUNT} started at $(date -Iseconds) ---"

  # Extract short task name for the branch
  TASK_NAME=$(grep -oP "^## Task ${TASK_NUM}: \K.*" "$PLAN_FILE" | tr '[:upper:]' '[:lower:]' | tr ' ' '-' | tr -cd 'a-z0-9-')
  BRANCH="task-${TASK_NUM}-${TASK_NAME}"

  PROMPT="You are implementing a Minecraft Fabric mod. Read /workspace/specs/plan.md for full context — especially the **Agent Workflow** section at the bottom.

Your job RIGHT NOW is to complete **Task ${TASK_NUM}** only. Do not work on other tasks.

Follow the Agent Workflow from the plan:

1. Create and checkout feature branch: ${BRANCH} (from main)
2. Read specs/plan.md to understand Task ${TASK_NUM} scope
3. Use an Explore sub-agent to research any unknowns before coding
4. For each sub-item, use TDD:
   a. Write the test first (GameTest) where applicable
   b. Write the implementation
   c. Run ./gradlew build — fix errors if any
   d. Git commit the sub-item: \"Task ${TASK_NUM}.M: <description>\"
5. Use a sub-agent to review the completed work
6. Update specs/plan.md — mark completed items with [x], commit
7. Create a pull request from ${BRANCH} to main

Stop after creating the PR. Do not proceed to the next task."

  timeout "$TIMEOUT" claude \
    --dangerously-skip-permissions \
    --output-format stream-json \
    --verbose \
    --max-turns 50 \
    -p "$PROMPT" \
    2>&1 | tee "$LOG_FILE" || true

  echo "--- Task ${TASK_NUM}/${TASK_COUNT} finished at $(date -Iseconds) ---"

  # Check if a PR was created or at least commits exist on the branch
  if git branch --list "$BRANCH" | grep -q "$BRANCH"; then
    COMMIT_COUNT=$(git log main.."$BRANCH" --oneline 2>/dev/null | wc -l)
    echo "--- Task ${TASK_NUM}: branch ${BRANCH} has ${COMMIT_COUNT} commit(s) ---"

    # Merge to main if there are commits (agent may not have created PR in local-only setup)
    if [ "$COMMIT_COUNT" -gt 0 ]; then
      git checkout main
      git merge "$BRANCH" --no-edit
      echo "--- Task ${TASK_NUM}: merged ${BRANCH} to main ---"
    fi
  else
    echo "--- WARNING: Branch ${BRANCH} not found, stopping ---"
    break
  fi
done

echo ""
echo "=== Claude Agent finished at $(date -Iseconds) ==="
