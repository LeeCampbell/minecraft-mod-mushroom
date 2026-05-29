FROM azul/zulu-openjdk:26.0.1
#FROM eclipse-temurin:21-jdk

# Install Node.js (required for Claude Code CLI)
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    git \
    jq \
    && curl -fsSL https://deb.nodesource.com/setup_22.x | bash - \
    && apt-get install -y nodejs \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Install Claude Code CLI
RUN npm install -g @anthropic-ai/claude-code

# Install GitHub CLI
RUN curl -fsSL https://cli.github.com/packages/githubcli-archive-keyring.gpg \
    | dd of=/usr/share/keyrings/githubcli-archive-keyring.gpg \
    && echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/githubcli-archive-keyring.gpg] https://cli.github.com/packages stable main" \
    | tee /etc/apt/sources.list.d/github-cli.list > /dev/null \
    && apt-get update \
    && apt-get install -y gh \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Non-root user — Claude Code refuses --dangerously-skip-permissions as root
RUN useradd -m -s /bin/bash agent \
  && echo "agent ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers

# Pre-create ~/.claude so Docker volume mounts don't create it as root.
# Without this, mounting .mcp.json to /home/agent/.claude/.mcp.json causes
# Docker to create the .claude/ directory owned by root, which blocks
# Claude Code's Bash tool (session-env) and MCP config reads.
RUN mkdir -p /home/agent/.claude \
  && chown -R agent:agent /home/agent

WORKDIR /workspace
RUN chown agent:agent /workspace

COPY --chown=agent:agent entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

USER agent

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
