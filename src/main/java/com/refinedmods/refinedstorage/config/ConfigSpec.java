package com.refinedmods.refinedstorage.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ConfigSpec {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Map<String, Value<?>> values;

    private ConfigSpec(Map<String, Value<?>> values) {
        this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    public synchronized void load(Path path) {
        if (Files.isRegularFile(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                JsonElement parsed = JsonParser.parseReader(reader);
                if (parsed.isJsonObject()) {
                    JsonObject root = parsed.getAsJsonObject();
                    values.values().forEach(value -> value.read(root));
                } else {
                    LOGGER.warn("Ignoring non-object config file {}", path);
                }
            } catch (IOException | RuntimeException e) {
                LOGGER.warn("Unable to read config {}, using defaults", path, e);
            }
        }
        save(path);
    }

    private void save(Path path) {
        JsonObject root = new JsonObject();
        values.values().forEach(value -> value.write(root));
        Path parent = path.getParent();
        Path temporary = path.resolveSibling(path.getFileName() + ".tmp");
        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (Writer writer = Files.newBufferedWriter(temporary)) {
                GSON.toJson(root, writer);
            }
            try {
                Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            LOGGER.warn("Unable to write config {}", path, e);
        }
    }

    public static final class Builder {
        private final Deque<String> sections = new ArrayDeque<>();
        private final Map<String, Value<?>> values = new LinkedHashMap<>();

        public Builder push(String name) {
            sections.push(name);
            return this;
        }

        public Builder pop() {
            if (sections.isEmpty()) {
                throw new IllegalStateException("Cannot pop an empty config section stack");
            }
            sections.pop();
            return this;
        }

        public Builder comment(String comment) {
            return this;
        }

        public BooleanValue define(String name, boolean defaultValue) {
            return add(new BooleanValue(path(name), defaultValue));
        }

        public IntValue defineInRange(String name, int defaultValue, int min, int max) {
            if (defaultValue < min || defaultValue > max) {
                throw new IllegalArgumentException("Default value for " + name + " is outside its range");
            }
            return add(new IntValue(path(name), defaultValue, min, max));
        }

        public ConfigSpec build() {
            if (!sections.isEmpty()) {
                throw new IllegalStateException("Unclosed config section " + sections.peek());
            }
            return new ConfigSpec(values);
        }

        private <T extends Value<?>> T add(T value) {
            String key = String.join(".", value.getPath());
            if (values.putIfAbsent(key, value) != null) {
                throw new IllegalArgumentException("Duplicate config value " + key);
            }
            return value;
        }

        private List<String> path(String name) {
            List<String> path = new ArrayList<>(sections.size() + 1);
            sections.descendingIterator().forEachRemaining(path::add);
            path.add(name);
            return List.copyOf(path);
        }
    }

    private abstract static class Value<T> {
        private final List<String> path;
        private final T defaultValue;
        private T value;

        private Value(List<String> path, T defaultValue) {
            this.path = path;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
        }

        protected final T getValue() {
            return value;
        }

        final List<String> getPath() {
            return path;
        }

        private void read(JsonObject root) {
            JsonElement element = find(root);
            if (element == null) {
                value = defaultValue;
                return;
            }
            try {
                T candidate = convert(element);
                value = isValid(candidate) ? candidate : defaultValue;
            } catch (RuntimeException e) {
                value = defaultValue;
            }
        }

        private JsonElement find(JsonObject root) {
            JsonObject current = root;
            for (int i = 0; i < path.size() - 1; ++i) {
                JsonElement child = current.get(path.get(i));
                if (child == null || !child.isJsonObject()) {
                    return null;
                }
                current = child.getAsJsonObject();
            }
            return current.get(path.get(path.size() - 1));
        }

        private void write(JsonObject root) {
            JsonObject current = root;
            for (int i = 0; i < path.size() - 1; ++i) {
                String section = path.get(i);
                JsonElement child = current.get(section);
                if (child == null || !child.isJsonObject()) {
                    JsonObject created = new JsonObject();
                    current.add(section, created);
                    current = created;
                } else {
                    current = child.getAsJsonObject();
                }
            }
            writeValue(current, path.get(path.size() - 1), value);
        }

        protected abstract T convert(JsonElement element);

        protected abstract boolean isValid(T candidate);

        protected abstract void writeValue(JsonObject object, String name, T value);
    }

    public static final class BooleanValue extends Value<Boolean> {
        private BooleanValue(List<String> path, boolean defaultValue) {
            super(path, defaultValue);
        }

        public boolean get() {
            return getValue();
        }

        @Override
        protected Boolean convert(JsonElement element) {
            return element.getAsBoolean();
        }

        @Override
        protected boolean isValid(Boolean candidate) {
            return candidate != null;
        }

        @Override
        protected void writeValue(JsonObject object, String name, Boolean value) {
            object.addProperty(name, value);
        }
    }

    public static final class IntValue extends Value<Integer> {
        private final int min;
        private final int max;

        private IntValue(List<String> path, int defaultValue, int min, int max) {
            super(path, defaultValue);
            this.min = min;
            this.max = max;
        }

        public int get() {
            return getValue();
        }

        @Override
        protected Integer convert(JsonElement element) {
            return element.getAsInt();
        }

        @Override
        protected boolean isValid(Integer candidate) {
            return candidate != null && candidate >= min && candidate <= max;
        }

        @Override
        protected void writeValue(JsonObject object, String name, Integer value) {
            object.addProperty(name, value);
        }
    }
}
