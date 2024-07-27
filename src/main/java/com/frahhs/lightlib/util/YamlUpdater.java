package com.frahhs.lightlib.util;

import com.frahhs.lightlib.LightPlugin;
import com.google.gson.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YamlUpdater {
    private final String GITHUB_CONTENTS_URL;
    private final String GITHUB_URL_TEMPLATE;
    private final String LOCAL_PATH_TEMPLATE;

    public YamlUpdater(LightPlugin plugin) {
        GITHUB_CONTENTS_URL = plugin.getOptions().getGithubContentsUrl();
        GITHUB_URL_TEMPLATE = plugin.getOptions().getGithubUrlTemplate();
        LOCAL_PATH_TEMPLATE = plugin.getDataFolder() + File.separator + "lang" + File.separator;
    }

    public void update() {
        try {
            List<String> languages = fetchAvailableLanguages();

            if (languages == null) {
                throw new RuntimeException("Unable to retrieve languages files.");
            }

            for (String lang : languages) {
                String githubUrl = GITHUB_URL_TEMPLATE + lang;
                String localPath = LOCAL_PATH_TEMPLATE + lang;

                Map<String, Object> githubYaml = downloadYaml(githubUrl);

                File localFile = new File(localPath);
                if (!localFile.exists()) {
                    saveYaml(localPath, githubYaml);
                } else {
                    Map<String, Object> localYaml = loadYaml(localPath);
                    updateYaml(localYaml, githubYaml);
                    saveYaml(localPath, localYaml);
                }
            }
        } catch (IOException | RuntimeException e) {
            LightPlugin.getLightLogger().warning("Error while trying to update language files, be sure to have the latest version of the plugin installed.", e);
        }
    }

    private List<String> fetchAvailableLanguages() throws IOException {
        List<String> languages = new ArrayList<>();
        String response = httpGet(GITHUB_CONTENTS_URL);

        JsonArray jsonArray;
        if (response != null) {
            Gson gson = new Gson();
            try {
                jsonArray = gson.fromJson(response, JsonArray.class);
            } catch (JsonSyntaxException e) {
                return null;
            }

            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                String filename = jsonObject.get("name").getAsString();
                if (filename.endsWith(".yml")) {
                    languages.add(filename);
                }
            }
        }
        return languages;
    }

    private String httpGet(String url) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try (InputStream inputStream = entity.getContent();
                         InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                         BufferedReader reader = new BufferedReader(isr)) {
                        return reader.lines().collect(Collectors.joining("\n"));
                    }
                }
            }
        }
        return null;
    }

    private Map<String, Object> downloadYaml(String url) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try (InputStream inputStream = entity.getContent();
                         InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                         BufferedReader reader = new BufferedReader(isr)) {
                        Yaml yaml = new Yaml();
                        return yaml.load(reader);
                    }
                }
            }
        }
        return null;
    }

    private Map<String, Object> loadYaml(String path) throws IOException {
        try (InputStream inputStream = Files.newInputStream(Paths.get(path));
             InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {
            Yaml yaml = new Yaml();
            return yaml.load(reader);
        }
    }

    private void saveYaml(String path, Map<String, Object> data) throws IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8)) {
            yaml.dump(data, writer);
        }
    }

    private void updateYaml(Map<String, Object> localYaml, Map<String, Object> githubYaml) {
        for (Map.Entry<String, Object> entry : githubYaml.entrySet()) {
            if (!localYaml.containsKey(entry.getKey())) {
                localYaml.put(entry.getKey(), entry.getValue());
            } else if (entry.getValue() instanceof Map) {
                updateYaml((Map<String, Object>) localYaml.get(entry.getKey()), (Map<String, Object>) entry.getValue());
            }
        }
    }
}