package net.chefcraft.reflection.base.utils;

/**
 *  Run and get results
 */
public interface ReferencedRunnable<T> extends Runnable {

    /**
     * @return a result
     */

    public T get();
}