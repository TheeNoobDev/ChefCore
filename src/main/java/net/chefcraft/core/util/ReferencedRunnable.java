package net.chefcraft.core.util;

/**
 *  Run and get results
 */
public interface ReferencedRunnable<T> extends Runnable {

	/**
	 * @return a result
	 */
	public T get();
}
