package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.config.ConfigReader;
import com.balugaq.runtimepylon.config.Deserializer;
import com.balugaq.runtimepylon.config.Examinable;
import com.balugaq.runtimepylon.exceptions.ExamineFailedException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@NullMarked
public class GitHubUpdateLink implements Deserializer<GitHubUpdateLink>, Examinable<GitHubUpdateLink> {
    private final String link;

    @Override
    public GitHubUpdateLink examine() throws ExamineFailedException {
        if (!link.matches("^https?://github\\.com/[a-zA-Z0-9_-]+/[a-zA-Z0-9_-]+/releases$")) {
            throw new ExamineFailedException("GitHubUpdateLink must be ^https?://github\\.com/[a-zA-Z0-9_-]+/[a-zA-Z0-9_-]+/releases(/.*)?$");
        }
        return this;
    }

    @Override
    public List<ConfigReader<?, GitHubUpdateLink>> readers() {
        return ConfigReader.list(String.class, GitHubUpdateLink::new);
    }

    public String getRepoOwner() {
        return link.split("/")[3];
    }

    public String getRepoName() {
        return link.split("/")[4];
    }
}
