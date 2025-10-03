package com.balugaq.runtimepylon.config;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class Pack {
    public PackID packID();
    public PackNamespace packNamespace();
    public PackVersion packVersion();
    public MinecraftVersion packMinAPIVersion();
    public MinecraftVersion packMaxAPIVersion();
    public UnsArrayList<PackName> loadBefores();
    public UnsArrayList<PackDesc> packDependencies();
    public UnsArrayList<PluginDesc> pluginDependencies();
    public UnsArrayList<Author> authors();
    public UnsArrayList<Contributor> contributors();
    public UnsArrayList<WebsiteLink> website();
    public GitHubUpdateLink githubUpdateLink();
}
