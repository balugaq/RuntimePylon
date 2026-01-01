package com.balugaq.runtimepylon;

import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.io.File;

/**
 * @author balugaq
 */
@NullMarked
public interface DebuggablePlugin extends Plugin {
    String getRepoOwner();
    String getRepoName();

    default String getIssueTrackerLink() {
        return "https://github.com/" + getRepoOwner() + "/" + getRepoName() + "/issues";
    }
}
