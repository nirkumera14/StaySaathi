package com.pgfinder.platform.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class IndiaCityCatalogService {

    private static final String CATALOG_FILE = "india-statutory-towns.csv";
    private static final int MAX_LIMIT = 100;

    private final List<IndiaCityCatalogEntry> entries;
    private final Map<String, List<IndiaCityCatalogEntry>> entriesByCity;

    public IndiaCityCatalogService() {
        List<IndiaCityCatalogEntry> loadedEntries = loadFromCsv();
        this.entries = Collections.unmodifiableList(loadedEntries);
        this.entriesByCity = buildByCityIndex(loadedEntries);
    }

    public List<IndiaCityCatalogEntry> search(String query, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), MAX_LIMIT);
        String safeQuery = normalize(query);

        if (safeQuery.isBlank()) {
            return entries.stream()
                .limit(safeLimit)
                .toList();
        }

        return entries.stream()
            .filter(entry -> entry.normalizedCity().contains(safeQuery) || entry.normalizedLabel().contains(safeQuery))
            .sorted(Comparator
                .comparing((IndiaCityCatalogEntry entry) -> !entry.normalizedCity().startsWith(safeQuery))
                .thenComparing(entry -> !entry.normalizedLabel().startsWith(safeQuery))
                .thenComparing(IndiaCityCatalogEntry::city)
                .thenComparing(IndiaCityCatalogEntry::state))
            .limit(safeLimit)
            .toList();
    }

    public List<IndiaCityCatalogEntry> findByCity(String city) {
        String key = normalize(city);
        if (key.isBlank()) {
            return List.of();
        }
        return entriesByCity.getOrDefault(key, List.of());
    }

    public IndiaCityCatalogEntry findFirstByCity(String city) {
        List<IndiaCityCatalogEntry> matches = findByCity(city);
        if (matches.isEmpty()) {
            return null;
        }
        return matches.get(0);
    }

    private List<IndiaCityCatalogEntry> loadFromCsv() {
        ClassPathResource resource = new ClassPathResource(CATALOG_FILE);
        if (!resource.exists()) {
            throw new IllegalStateException("Missing classpath resource: " + CATALOG_FILE);
        }

        Map<String, IndiaCityCatalogEntry> deduped = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
        )) {
            String line;
            boolean headerSkipped = false;

            while ((line = reader.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = parseCsvLine(line);
                if (parts.length < 5) {
                    continue;
                }

                String code = parts[0].trim();
                String city = parts[1].trim();
                String state = parts[2].trim();
                String latRaw = parts[3].trim();
                String lngRaw = parts[4].trim();

                if (code.isEmpty() || city.isEmpty() || state.isEmpty() || latRaw.isEmpty() || lngRaw.isEmpty()) {
                    continue;
                }

                double latitude;
                double longitude;
                try {
                    latitude = Double.parseDouble(latRaw);
                    longitude = Double.parseDouble(lngRaw);
                } catch (NumberFormatException ignored) {
                    continue;
                }

                String normalizedCity = normalize(city);
                String normalizedState = normalize(state);
                if (normalizedCity.isBlank() || normalizedState.isBlank()) {
                    continue;
                }

                IndiaCityCatalogEntry entry = new IndiaCityCatalogEntry(
                    code,
                    city,
                    state,
                    latitude,
                    longitude,
                    normalizedCity,
                    normalizedCity + ", " + normalizedState
                );

                String dedupeKey = normalizedCity + "|" + normalizedState;
                deduped.putIfAbsent(dedupeKey, entry);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load " + CATALOG_FILE, ex);
        }

        return deduped.values().stream()
            .sorted(Comparator.comparing(IndiaCityCatalogEntry::state).thenComparing(IndiaCityCatalogEntry::city))
            .toList();
    }

    private Map<String, List<IndiaCityCatalogEntry>> buildByCityIndex(List<IndiaCityCatalogEntry> loadedEntries) {
        Map<String, List<IndiaCityCatalogEntry>> byCity = new LinkedHashMap<>();
        for (IndiaCityCatalogEntry entry : loadedEntries) {
            byCity.computeIfAbsent(entry.normalizedCity(), key -> new ArrayList<>()).add(entry);
        }
        for (Map.Entry<String, List<IndiaCityCatalogEntry>> indexEntry : byCity.entrySet()) {
            indexEntry.setValue(List.copyOf(indexEntry.getValue()));
        }
        return Map.copyOf(byCity);
    }

    private String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
                continue;
            }

            if (c == ',' && !inQuotes) {
                values.add(current.toString());
                current.setLength(0);
                continue;
            }
            current.append(c);
        }
        values.add(current.toString());
        return values.toArray(String[]::new);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ENGLISH);
    }
}
