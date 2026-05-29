- **Minimize manual approval interruptions.** Each Bash command should match an auto-approved pattern in `.claude/settings.json`. Things that break this:
  - Chaining commands with `&&` (the combined string won't match individual patterns)
  - Using `cat <<'EOF'` or heredocs (triggers quoted-newline detection)
  - Embedding newlines followed by `#`-prefixed lines in arguments (hides args from permission checks)
  - Any command structure that obscures what's actually being run
  - Avoid using `cd` or `git -C` when trying to perform tasks in the pwd (present working directory). It breaks the claude-code permission model forcing permission request that are unnessecary.

  Use separate Bash tool calls for each command instead.