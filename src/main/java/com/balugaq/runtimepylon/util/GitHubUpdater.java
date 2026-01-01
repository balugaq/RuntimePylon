package com.balugaq.runtimepylon.util;

import com.balugaq.runtimepylon.RuntimePylon;
import com.balugaq.runtimepylon.config.Pack;
import com.balugaq.runtimepylon.config.PackDesc;
import com.balugaq.runtimepylon.manager.PackManager;
import com.balugaq.runtimepylon.config.pack.GitHubUpdateLink;
import com.balugaq.runtimepylon.config.pack.PackID;
import com.balugaq.runtimepylon.config.pack.PackVersion;
import com.balugaq.runtimepylon.data.GitHubRelease;
import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author lijinhong11
 */
@NullMarked
public class GitHubUpdater {
    public static boolean tryUpdate(Pack pack) throws IOException {
        GitHubUpdateLink link = pack.getGithubUpdateLink();
        if (link == null) return false;
        PackVersion version = pack.getPackVersion();
        PackID packId = pack.getPackID();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = "https://api.github.com/repos/" + link.getRepoOwner() + "/" + link.getRepoName() + "/releases/latest";
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);
            String entity = EntityUtils.toString(response.getEntity());

            GitHubRelease release = new Gson().fromJson(entity, GitHubRelease.class);

            String releaseName = release.getName();

            if (releaseName == null) {
                Debug.warning("Unable to check update for " + packId.getId() + ": hit GitHub API speed limit (60 requests/h)");
                return false;
            }

            if (releaseName.startsWith("v") && !version.getVersion().startsWith("v")) {
                releaseName = releaseName.replaceFirst("v", "");
            }

            if (!Objects.equals(version.getVersion(), releaseName)) {
                if (release.isPrerelease() && !RuntimePylon.getConfigManager().isUpdatePreReleasePacks())
                    return false;

                if (!RuntimePylon.getPackUpdateDownloadFolder().exists()) {
                    RuntimePylon.getPackUpdateDownloadFolder().mkdirs();
                }

                File zip = new File(RuntimePylon.getPackUpdateDownloadFolder(), packId + "-" + releaseName + ".zip");

                String zipUrl;
                List<GitHubRelease.Asset> assets = release.getAssets();
                if (assets == null || assets.isEmpty()) {
                    zipUrl = release.getZipball_url();
                } else {
                    Pack pk = PackManager.findPack(new PackDesc(packId.getId()));
                    if (pk == null) {
                        zipUrl = release.getZipball_url();
                    } else {
                        GitHubRelease.Asset asset = assets.stream()
                                .findFirst()
                                .orElse(null);
                        if (asset == null) {
                            zipUrl = release.getZipball_url();
                        } else {
                            zipUrl = asset.getBrowser_download_url();
                        }
                    }
                }

                URL urlObj = new URL(zipUrl);

                if (!zip.exists()) {
                    if (!zip.createNewFile()) {
                        throw new IOException("Failed to create temporary zip file, unable to update pack.");
                    }
                }

                long result = Files.copy(urlObj.openStream(), zip.toPath(), StandardCopyOption.REPLACE_EXISTING);

                if (result < 1) {
                    return false;
                }

                if (zip.exists()) {
                    File projectFolder = new File(RuntimePylon.getPacksFolder(), pack.getDir().getName());

                    if (!projectFolder.exists()) {
                        mkdir(projectFolder);
                    }

                    unzip(zip, projectFolder);

                    File pki = new File(projectFolder, "pack.yml");
                    YamlConfiguration packYml = YamlConfiguration.loadConfiguration(pki);
                    String id = packYml.getString("id", "");

                    if (!id.equals(packId.getId())) {
                        Debug.log("Successfully updated " + packId + " with pack id changed: " + packId + " -> " + id);
                    } else {
                        Debug.log("Successfully updated  " + packId + "!");
                    }

                    return true;
                }
                return false;
            }
            return true;
        } catch (Exception e) {
            Debug.log("Unable to update " + packId);
            Debug.trace(e);
            return false;
        }
    }

    private static void unzip(File zipFile, File desDirectory) throws IOException {
        if (!desDirectory.exists()) {
            boolean mkdirSuccess = desDirectory.mkdirs();
            if (!mkdirSuccess) {
                throw new IOException("Failed to create temporary folder");
            }
        }

        if (!zipFile.exists()) {
            throw new FileNotFoundException("zip file not found");
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) {
                    String entryName = zipEntry.getName();

                    int firstSlashIndex = entryName.indexOf('/');
                    if (firstSlashIndex != -1) {
                        entryName = entryName.substring(firstSlashIndex + 1);
                    }
                    File outFile = new File(desDirectory, entryName);
                    mkdir(outFile.getParentFile());

                    try (BufferedOutputStream bufferedOutputStream =
                            new BufferedOutputStream(new FileOutputStream(outFile))) {
                        byte[] bytes = new byte[1024];
                        int readLen;
                        long totalBytesRead = 0;
                        while ((readLen = zipInputStream.read(bytes)) != -1) {
                            bufferedOutputStream.write(bytes, 0, readLen);
                            totalBytesRead += readLen;
                        }

                        if (zipEntry.getSize() != -1 && totalBytesRead != zipEntry.getSize()) {
                            throw new IOException("network error");
                        }
                    }
                }
                zipEntry = zipInputStream.getNextEntry();
            }
        }
    }

    private static void mkdir(@Nullable File file) {
        if (file == null || file.exists()) {
            return;
        }
        mkdir(file.getParentFile());
        file.mkdirs();
    }
}
