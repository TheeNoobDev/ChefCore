package net.chefcraft.core.server;

import com.google.common.collect.ImmutableList;
import net.chefcraft.core.PlatformProvider;
import net.minecraft.SharedConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public enum ServerVersion {
	
	UNKNOWN		(-1, "Unknown"),
	v1_8_8		(47, "1.8.8"),
	v1_9		(107, "1.9"),
	v1_9_1		(108, "1.9.1"),
	v1_9_2		(109, "1.9.2"),
	v1_9_3		(110, "1.9.3"),
	v1_9_4		(110, "1.9.4"),
	v1_10		(210, "1.10"),
	v1_10_1		(210, "1.10.1"),
	v1_10_2		(210, "1.10.2"),
	v1_11		(315, "1.11"),
	v1_11_1		(316, "1.11.1"),
	v1_11_2		(316, "1.11.2"),
	v1_12		(335, "1.12"),
	v1_12_1		(338, "1.12.1"),
	v1_12_2		(340, "1.12.2"),
	v1_13		(393, "1.13"),
	v1_13_1		(401, "1.13.1"),
	v1_13_2		(404, "1.13.2"),
	v1_14		(477, "1.14"),
	v1_14_1		(480, "1.14.1"),
	v1_14_2		(485, "1.14.2"),
	v1_14_3		(490, "1.14.3"),
	v1_14_4		(498, "1.14.4"),
	v1_15		(573, "1.15"),
	v1_15_1		(575, "1.15.1"),
	v1_15_2		(578, "1.15.2"),
	v1_16		(735, "1.16"),
	v1_16_1		(736, "1.16.1"),
	v1_16_2		(751, "1.16.2"),
	v1_16_3		(753, "1.16.3"),
	v1_16_4		(754, "1.16.4"),
	v1_16_5		(754, "1.16.5"),
	v1_17		(755, "1.17"),
	v1_17_1		(756, "1.17.1"),
	v1_18		(757, "1.18"),
	v1_18_1		(757, "1.18.1"),
	v1_18_2		(758, "1.18.2"),
	v1_19		(759, "1.19"),
	v1_19_1		(760, "1.19.1"),
	v1_19_2 	(760, "1.19.2"),
	v1_19_3 	(761, "1.19.3"),
	v1_19_4 	(762, "1.19.4"),
	v1_20 		(763, "1.20"),
	v1_20_1 	(763, "1.20.1"),
	v1_20_2 	(764, "1.20.2"),
	v1_20_3 	(765, "1.20.3"),
	v1_20_4 	(765, "1.20.4"),
	v1_20_5 	(766, "1.20.5"),
	v1_20_6 	(766, "1.20.6"),
	v1_21 		(767, "1.21"),
	v1_21_1 	(767, "1.21.1"),
	v1_21_2 	(768, "1.21.2"),
	v1_21_3 	(768, "1.21.3"),
	v1_21_4 	(769, "1.21.4"),
	v1_21_5 	(770, "1.21.5"),
	v1_21_6 	(771, "1.21.6"),
	v1_21_7 	(772, "1.21.7"),
	v1_21_8 	(772, "1.21.8"),
    v1_21_9 	(773, "1.21.9"),
    v1_21_10 	(773, "1.21.10");
	
	private final int protocol;
	private final String name;
	
	private ServerVersion(int protocol, @NotNull String name) {
		this.protocol = protocol;
		this.name = Objects.requireNonNull(name, "version name cannot be null!");
	}

	public int getProtocol() {
		return protocol;
	}

	public String getName() {
		return name;
	}
	
	public boolean isHigherThan(@NotNull ServerVersion version) {
		return protocol > Objects.requireNonNull(version, "server version cannot be null!").protocol;
	}
	
	public boolean isLowerThan(@NotNull ServerVersion version) {
		return protocol < Objects.requireNonNull(version, "server version cannot be null!").protocol;
	}
	
	private static final List<ServerVersion> VERSION_LIST = ImmutableList.copyOf(ServerVersion.values());
	private static final ServerVersion CURRENT;
	
	static {
		ServerVersion ver = ServerVersion.UNKNOWN;
		
		try {
			ver = getByProtocol(Class.forName("net.minecraft.SharedConstants").getField("RELEASE_NETWORK_PROTOCOL_VERSION").getInt(null));
		} catch (Exception x) {
			String lore = PlatformProvider.usingVelocity() ? "Velocity" : org.bukkit.Bukkit.getServer().getVersion();
			try {
				ver = getByName(lore.substring(lore.indexOf("(MC: ") + 5, lore.length() - 1));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		CURRENT = ver;
	}
	
	private static final boolean LEGACY = CURRENT.isLowerThan(ServerVersion.v1_13);
	
	@NotNull
	public static ServerVersion getByName(@NotNull String name) {
		Objects.requireNonNull(name, "version name cannot be null!");
		
		for (int i = VERSION_LIST.size() - 1; i >= 0; i--) {
			ServerVersion version = VERSION_LIST.get(i);
			
			if (version.name.equalsIgnoreCase(name)) {
				return version;
			}
		}
		
		return UNKNOWN;
	}
	
	@NotNull
	public static ServerVersion getByProtocol(int protocol) {
		
		for (int i = VERSION_LIST.size() - 1; i >= 0; i--) {
			ServerVersion version = VERSION_LIST.get(i);
			
			if (version.protocol == protocol) {
				return version;
			}
		}
		
		return UNKNOWN;
	}
	
	@NotNull
	public static ServerVersion current() {
		return CURRENT;
	}
	
	public static boolean isLegacy() {
		return LEGACY;
	}
	
	@NotNull
	@Unmodifiable
	public static List<ServerVersion> list() {
		return VERSION_LIST;
	}
	
	@NotNull
	@Unmodifiable
	public static List<ServerVersion> filter(@NotNull Predicate<ServerVersion> filter) {
		Objects.requireNonNull(filter, "filter cannot be null!");
		ImmutableList.Builder<ServerVersion> builder =  ImmutableList.builder();
		
		for (int i = VERSION_LIST.size() - 1; i >= 0; i--) {
			ServerVersion ver = VERSION_LIST.get(i);
			if (filter.test(ver)) {
				builder.add(ver);
			}
		}
		
		return builder.build();
	}
	
	public static void forEach(@NotNull Consumer<ServerVersion> action) {
		Objects.requireNonNull(action, "action cannot be null!");
		
		for (int i = VERSION_LIST.size() - 1; i >= 0; i--) {
			action.accept(VERSION_LIST.get(i));
		}
	}
	
	public static void forEach(@NotNull Consumer<ServerVersion> action, @NotNull Predicate<ServerVersion> filter) {
		Objects.requireNonNull(action, "action cannot be null!");
		Objects.requireNonNull(filter, "filter cannot be null!");
		
		for (int i = VERSION_LIST.size() - 1; i >= 0; i--) {
			ServerVersion ver = VERSION_LIST.get(i);
			if (filter.test(ver)) {
				action.accept(ver);
			}
		}
	}
}
