package net.minecraftforge.common.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class LazyOptional<T> {
    private final Supplier<? extends T> supplier;
    private T value;
    private boolean resolved;

    private LazyOptional(Supplier<? extends T> supplier) {
        this.supplier = supplier;
    }

    public static <T> LazyOptional<T> of(Supplier<? extends T> supplier) {
        return new LazyOptional<>(Objects.requireNonNull(supplier));
    }

    public static <T> LazyOptional<T> empty() {
        return new LazyOptional<>(() -> null);
    }

    private T value() {
        if (!resolved) {
            value = supplier.get();
            resolved = true;
        }
        return value;
    }

    public T orElse(T fallback) {
        T current = value();
        return current == null ? fallback : current;
    }

    public Optional<T> resolve() {
        return Optional.ofNullable(value());
    }

    public boolean isPresent() {
        return value() != null;
    }

    public void ifPresent(Consumer<? super T> consumer) {
        T current = value();
        if (current != null) consumer.accept(current);
    }

    public <U> LazyOptional<U> map(Function<? super T, ? extends U> mapper) {
        return LazyOptional.of(() -> {
            T current = value();
            return current == null ? null : mapper.apply(current);
        });
    }

    @SuppressWarnings("unchecked")
    public <U> LazyOptional<U> cast() {
        return (LazyOptional<U>) this;
    }

    public void invalidate() {
        value = null;
        resolved = true;
    }
}
