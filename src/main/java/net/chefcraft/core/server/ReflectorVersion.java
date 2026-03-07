package net.chefcraft.core.server;

import net.chefcraft.core.collect.ImmutableList;
import net.chefcraft.core.registry.CoreRegistry;
import net.chefcraft.core.util.ObjectKey;
import net.chefcraft.reflection.AbstractReflections;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

public class ReflectorVersion implements ObjectKey {
	
	public static final ReflectorVersion LATEST  = new ReflectorVersion("latest", ServerVersion.UNKNOWN);
	public static final ReflectorVersion v1_8_R3  = new ReflectorVersion("v1_8_R3", ServerVersion.v1_8_8);
	public static final ReflectorVersion v1_9_R1  = new ReflectorVersion("v1_9_R1", ServerVersion.v1_9_1, ServerVersion.v1_9_2);
	public static final ReflectorVersion v1_9_R2  = new ReflectorVersion("v1_9_R2", ServerVersion.v1_9_3, ServerVersion.v1_9_4);
	public static final ReflectorVersion v1_10_R1 = new ReflectorVersion("v1_10_R1", ServerVersion.v1_10_1, ServerVersion.v1_10_2);
	public static final ReflectorVersion v1_11_R1 = new ReflectorVersion("v1_11_R1", ServerVersion.v1_11_1, ServerVersion.v1_11_2);
	public static final ReflectorVersion v1_12_R1 = new ReflectorVersion("v1_12_R1", ServerVersion.v1_12_1, ServerVersion.v1_12_2);
	public static final ReflectorVersion v1_13_R1 = new ReflectorVersion("v1_13_R1", ServerVersion.v1_13_1);
	public static final ReflectorVersion v1_13_R2 = new ReflectorVersion("v1_13_R2", ServerVersion.v1_13_2);
	public static final ReflectorVersion v1_14_R1 = new ReflectorVersion("v1_14_R1", ServerVersion.v1_14_1, ServerVersion.v1_14_2);
	public static final ReflectorVersion v1_14_R2 = new ReflectorVersion("v1_14_R2", ServerVersion.v1_14_3, ServerVersion.v1_14_4);
	public static final ReflectorVersion v1_15_R1 = new ReflectorVersion("v1_15_R1", ServerVersion.v1_15_1, ServerVersion.v1_15_2);
	public static final ReflectorVersion v1_16_R1 = new ReflectorVersion("v1_16_R1", ServerVersion.v1_16_1, ServerVersion.v1_16_2);
	public static final ReflectorVersion v1_16_R2 = new ReflectorVersion("v1_16_R2", ServerVersion.v1_16_3);
	public static final ReflectorVersion v1_16_R3 = new ReflectorVersion("v1_16_R3", ServerVersion.v1_16_4, ServerVersion.v1_16_5);
	public static final ReflectorVersion v1_17_R1 = new ReflectorVersion("v1_17_R1", ServerVersion.v1_17_1);
	public static final ReflectorVersion v1_18_R1 = new ReflectorVersion("v1_18_R1", ServerVersion.v1_18_1);
	public static final ReflectorVersion v1_18_R2 = new ReflectorVersion("v1_18_R2", ServerVersion.v1_18_2);
	public static final ReflectorVersion v1_19_R1 = new ReflectorVersion("v1_19_R1", ServerVersion.v1_19_1, ServerVersion.v1_19_2);
	public static final ReflectorVersion v1_19_R2 = new ReflectorVersion("v1_19_R2", ServerVersion.v1_19_3);
	public static final ReflectorVersion v1_19_R3 = new ReflectorVersion("v1_19_R3", ServerVersion.v1_19_4);
	public static final ReflectorVersion v1_20_R1 = new ReflectorVersion("v1_20_R1", ServerVersion.v1_20_1);
	public static final ReflectorVersion v1_20_R2 = new ReflectorVersion("v1_20_R2", ServerVersion.v1_20_2);
	public static final ReflectorVersion v1_20_R3 = new ReflectorVersion("v1_20_R3", ServerVersion.v1_20_3, ServerVersion.v1_20_4);
	public static final ReflectorVersion v1_20_R4 = new ReflectorVersion("v1_20_R4", ServerVersion.v1_20_5, ServerVersion.v1_20_6);
	public static final ReflectorVersion v1_21_R1 = new ReflectorVersion("v1_21_R1", ServerVersion.v1_21_1);
	public static final ReflectorVersion v1_21_R2 = new ReflectorVersion("v1_21_R2", ServerVersion.v1_21_2, ServerVersion.v1_21_3);
	public static final ReflectorVersion v1_21_R3 = new ReflectorVersion("v1_21_R3", ServerVersion.v1_21_4);
	public static final ReflectorVersion v1_21_R4 = new ReflectorVersion("v1_21_R4", ServerVersion.v1_21_5);
	public static final ReflectorVersion v1_21_R5 = new ReflectorVersion("v1_21_R5", ServerVersion.v1_21_6);
	public static final ReflectorVersion v1_21_R6 = new ReflectorVersion("v1_21_R6", ServerVersion.v1_21_7, ServerVersion.v1_21_8);
	
	private final List<ServerVersion> serverVersion;
	private final String reflectorVersion;
	private final String reflectorPackageName;
	
	private ReflectorVersion(@NotNull String reflectorVersion, @NotNull ServerVersion ...serverVersions) {
		this.serverVersion = ImmutableList.copyOf(Objects.requireNonNull(serverVersions, "server versions cannot be null!"));
		this.reflectorVersion = Objects.requireNonNull(reflectorVersion, "reflector version cannot be null!");
		this.reflectorPackageName = "net.chefcraft.reflector." + reflectorVersion;
	}
	
	@NotNull
	@Unmodifiable
	public List<ServerVersion> getSupportedVersions() {
		return this.serverVersion;
	}
	
	@Override
    @NotNull
	public String getKey() {
		return this.reflectorVersion;
	}
	
	@NotNull
	public String getReflectorVersion() {
		return this.reflectorVersion;
	}
	
	@NotNull
	public String getReflectorPackageName() {
		return this.reflectorPackageName;
	}
	
	@NotNull
	public String getReflectorClassPath() {
		return this.reflectorPackageName + ".MainReflector";
	}
	
	@Nullable
	public AbstractReflections tryDetectAndLoad() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException {
		Class<?> clazz = Class.forName(this.getReflectorClassPath());
		return (AbstractReflections) clazz.getDeclaredConstructors()[0].newInstance();
	}
	
	@Override
	public String toString() {
		return this.reflectorVersion;
	}
	
	private static final CoreRegistry<ReflectorVersion> REGISTRY = new CoreRegistry<>(ReflectorVersion.class);
	private static final ReflectorVersion CURRENT = getByServerVersion(ServerVersion.current());
	
	@NotNull
	public static ReflectorVersion current() {
		return CURRENT;
	}
	
	@NotNull
	public static ReflectorVersion getByServerVersion(@NotNull ServerVersion version) {
		for (ReflectorVersion e : REGISTRY) {
			if (e.serverVersion.contains(version)) {
				return e;
			}
		}
		return ReflectorVersion.LATEST;
	}
}
