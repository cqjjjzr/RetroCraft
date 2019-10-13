package index.alchemy.util;

import java.util.Arrays;
import java.util.function.*;

import index.project.version.annotation.Omega;

@Omega
public interface FunctionHelper {
    
    @SafeVarargs
    static <T> void always(Consumer<T> consumer, T... ts) {
        Arrays.stream(ts).forEach(consumer);
    }
    
    @SafeVarargs
    static <A, B> void alwaysA(BiConsumer<A, B> consumer, A a, B... bs) {
        Arrays.stream(bs).forEach(b -> consumer.accept(a, b));
    }
    
    @SafeVarargs
    static <A, B> void alwaysB(BiConsumer<A, B> consumer, B b, A... as) {
        Arrays.stream(as).forEach(a -> consumer.accept(a, b));
    }
    
    static <A, B> BiConsumer<B, A> exchange(BiConsumer<A, B> consumer) {
        return (b, a) -> consumer.accept(a, b);
    }
    
    static Runnable link(Runnable... runnables) {
        return () -> {
            for (Runnable runnable : runnables)
                runnable.run();
        };
    }
    
    @SafeVarargs
    static <T> Consumer<T> link(Consumer<T>... consumers) {
        return t -> {
            for (Consumer<T> consumer : consumers)
                consumer.accept(t);
        };
    }
    
    static <A> Runnable link(Supplier<A> supplier, Consumer<A> consumer) {
        return () -> consumer.accept(supplier.get());
    }
    
    static <A, B> Consumer<A> link(Function<A, B> function, Consumer<B> consumer) {
        return a -> consumer.accept(function.apply(a));
    }
    
    static <A, B, C> Function<A, C> map(Function<A, B> functionA, Function<B, C> functionB) {
        return a -> functionB.apply(functionA.apply(a));
    }
    
    static <A, B, C> BiConsumer<A, C> link(Function<A, B> function, BiConsumer<B, C> consumer) {
        return (a, c) -> consumer.accept(function.apply(a), c);
    }
    
    static <A, B> Consumer<A> linkA(Supplier<B> supplier, BiConsumer<A, B> consumer) {
        return a -> consumer.accept(a, supplier.get());
    }
    
    static <A, B> Consumer<B> linkB(Supplier<A> supplier, BiConsumer<A, B> consumer) {
        return b -> consumer.accept(supplier.get(), b);
    }
    
    static <A, B, C> Function<A, C> linkA(Supplier<B> supplier, BiFunction<A, B, C> consumer) {
        return a -> consumer.apply(a, supplier.get());
    }
    
    static <A, B, C> Function<B, C> linkB(Supplier<A> supplier, BiFunction<A, B, C> consumer) {
        return b -> consumer.apply(supplier.get(), b);
    }
    
    static <A, B> Supplier<B> map(Supplier<A> supplier, Function<A, B> function) {
        return () -> function.apply(supplier.get());
    }
    
    static <T> BinaryOperator<T> first() {
        return (a, b) -> a;
    }
    
    static <T> BinaryOperator<T> end() {
        return (a, b) -> b;
    }
    
    static <T> T rethrow(Throwable throwable) {
        throw new RuntimeException(throwable);
    }
    
    static void rethrowVoid(Throwable throwable) {
        throw new RuntimeException(throwable);
    }
    
    @FunctionalInterface
    interface ExRunnable {
        
        void run() throws Throwable;
        
    }
    
    static Runnable onThrowableRunnable(ExRunnable runnable, Consumer<Throwable> handle) {
        return () -> { try { runnable.run(); } catch (Throwable t) { handle.accept(t); } };
    }
    
    @FunctionalInterface
    interface ExSupplier<T> {
        
        T get() throws Throwable;
        
    }
    
    static <T> Supplier<T> onThrowableSupplier(ExSupplier<T> supplier, Consumer<Throwable> handle) {
        return () -> {
            try { return supplier.get(); } catch (Throwable t) {
                handle.accept(t);
                return null;
            }
        };
    }
    
    static <T> Supplier<T> onThrowableSupplier(ExSupplier<T> supplier, Function<Throwable, T> handle) {
        return () -> { try { return supplier.get(); } catch (Throwable t) { return handle.apply(t); } };
    }
    
    @FunctionalInterface
    interface ExConsumer<T> {
        
        void accept(T t) throws Throwable;
        
    }
    
    static <T> Consumer<T> onThrowableConsumer(ExConsumer<T> consumer, Consumer<Throwable> handle) {
        return o -> { try { consumer.accept(o); } catch (Throwable t) { handle.accept(t); } };
    }
    
    @FunctionalInterface
    interface ExFunction<A, B> {
        
        B apply(A a) throws Throwable;
        
    }
    
    static <A, B> Function<A, B> onThrowableFunction(ExFunction<A, B> function, Function<Throwable, B> handle) {
        return o -> { try { return function.apply(o); } catch (Throwable t) { return handle.apply(t); } };
    }
    
    @FunctionalInterface
    interface ExBiFunction<A, B, C> {
        
        C apply(A a, B b) throws Throwable;
        
    }
    
    static <A, B, C> BiFunction<A, B, C> onThrowableBiFunction(ExBiFunction<A, B, C> function, Function<Throwable, C> handle) {
        return (a, b) -> { try { return function.apply(a, b); } catch (Throwable t) { return handle.apply(t); } };
    }
    
}
