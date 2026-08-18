package com.nnm.bot.core.plugin;

/**
 * Describes how a command is selected before its plugin handles the message.
 */
public enum MatchType {
    /**
     * The normalized message must equal the command or one of its aliases.
     */
    EXACT,

    /**
     * The plugin remains in the ordered fallback chain and decides how to match.
     */
    CUSTOM
}
