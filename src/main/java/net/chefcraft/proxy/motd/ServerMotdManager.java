package net.chefcraft.proxy.motd;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import com.velocitypowered.api.util.ModInfo;
import net.chefcraft.core.component.ComponentSupport;
import net.chefcraft.core.configuration.CoreJsonFile;
import net.chefcraft.core.util.FileUtils;
import net.chefcraft.core.util.TextUtil.AlignFrom;
import net.chefcraft.proxy.ChefProxyCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;

public class ServerMotdManager {
	
	private static final ProxyServer SERVER = ChefProxyCore.getInstance().getServer();
	private static final byte DEFAULT_SERVER_ICON_PIXEL = 64;
	private static final Random RANDOM = new Random();
	
	private static Listener requestListener = null;
	
	public static void load() {
		try {
			
			JsonObject json = CoreJsonFile.copyResource(ChefProxyCore.getResourceAsStream("motd/motd.json"),
					ChefProxyCore.getPluginDirectory().getAbsolutePath() + File.separator + "motd" + File.separator + "motd.json")
					.getConfig().getAsJsonObject();
			
			File icon = new File(ChefProxyCore.getPluginDirectory().getAbsolutePath() + File.separator + "motd" + File.separator + "server-icon.png");
			if (json.get("enabled").getAsBoolean() && !icon.exists()) {
				FileUtils.copyInputStreamToFile(ChefProxyCore.getResourceAsStream("motd/server-icon.png"), icon);
			}
			
			EventManager manager = ChefProxyCore.getInstance().getServer().getEventManager();
			
			if (requestListener != null) {
				manager.unregisterListener(ChefProxyCore.getInstance(), requestListener);
			}
			
			if (json.get("enabled").getAsBoolean()) {
				requestListener = fromJson(json);
				manager.register(ChefProxyCore.getInstance(), requestListener);
			}
			
		} catch (IOException e) {
			ChefProxyCore.log(Level.SEVERE, "An error occurred loading \"motd.json\" file!", e);
		}
	}
	
	public static Listener fromJson(JsonObject motdData) {
		ServerPing.Builder builder = ServerPing.builder().description(Component.empty());
		JsonObject json = motdData.get("server_icon").getAsJsonObject();
		
		if (json.get("enabled").getAsBoolean()) {
			
			try {
				builder.favicon(Favicon.create(applyImageSettings(ImageIO.read(
						new File(ChefProxyCore.getPluginDirectory(), json.get("file_name").getAsString())))));
			} catch (Exception x) {
				x.printStackTrace();
				ChefProxyCore.log(Level.SEVERE, "An error occurred parsing \"server_icon\" file!");
			}
		}
		
		Consumer<ServerPing.Builder> consumer = null;
		
		json = motdData.get("player_count").getAsJsonObject();
		
		if (json.get("modify").getAsBoolean()) {
			builder.onlinePlayers(json.get("value").getAsInt());
		} else {
			builder.onlinePlayers(SERVER.getPlayerCount());
			
			consumer = addOrCreateForConsumer(consumer, ping -> {
				ping.onlinePlayers(SERVER.getPlayerCount());
			});
		}
		
		json = motdData.get("max_players").getAsJsonObject();
		
		if (json.get("unlimited").getAsBoolean()) {
			builder.maximumPlayers(SERVER.getPlayerCount() + 1);
			
			consumer = addOrCreateForConsumer(consumer, ping -> {
				ping.maximumPlayers(SERVER.getPlayerCount() + 1);
			});
		} else {
			builder.maximumPlayers(json.get("value").getAsInt());
		}
		
		builder.mods(ModInfo.DEFAULT);
		
		//Legacy MOTD - start
		Consumer<ServerPing.Builder> legacyConsumer = consumer;
		ServerPing.Builder legacyBuilder = builder.build().asBuilder();
		//Legacy MOTD - end
		
		final boolean centerText = motdData.get("center_text").getAsBoolean();
		
		//Modern MOTD - start		
		final JsonArray firstLine = motdData.get("first_line").getAsJsonArray();
		consumer = loadLineFromJson(firstLine, builder, consumer, true, centerText);
		
		
		final JsonArray secondLine = motdData.get("second_line").getAsJsonArray();
		consumer = loadLineFromJson(secondLine, builder, consumer, false, centerText);
		//Modern MOTD - end
		
		//Legacy MOTD - start
		final JsonArray firstLineLegacy = motdData.get("legacy_first_line").getAsJsonArray();
		legacyConsumer = loadLineFromJson(firstLineLegacy, legacyBuilder, legacyConsumer, true, centerText);
		
		
		final JsonArray secondLineLegacy = motdData.get("legacy_second_line").getAsJsonArray();
		legacyConsumer = loadLineFromJson(secondLineLegacy, legacyBuilder, legacyConsumer, false, centerText);
		//Legacy MOTD - end
		
		String incompatibleVersionText = motdData.get("incompatible_version_text").getAsString();
		int minimumSupportedProtocol = parseMinimumSupportedVersion(motdData.get("minimum_supported_version").getAsString()).getProtocol();
		
		return new Listener(builder, legacyBuilder, incompatibleVersionText, minimumSupportedProtocol,
				
				nullOrEmptyConsumer(consumer), nullOrEmptyConsumer(legacyConsumer));
	}
	
	private static Consumer<ServerPing.Builder> loadLineFromJson(JsonArray array, ServerPing.Builder builder, Consumer<ServerPing.Builder> consumer, boolean firstLine, boolean centerText) {
		if (!array.isEmpty()) {
			
			if (firstLine) {
				
				builder.description(centerText ? MiniMessage.miniMessage().deserialize(ComponentSupport.support().alignToCenter(AlignFrom.LEFT_SIDE, array.get(0).getAsString(), 255)) :
					MiniMessage.miniMessage().deserialize(array.get(0).getAsString()));
				
			} else {
				builder.description(builder.getDescriptionComponent().get().appendNewline().append(centerText ? 
						MiniMessage.miniMessage().deserialize(ComponentSupport.support().alignToCenter(AlignFrom.LEFT_SIDE, array.get(0).getAsString(), 255)) : MiniMessage.miniMessage().deserialize(array.get(0).getAsString())));
			}
			
			if (array.size() > 1) {
				
				final List<Component> motds = Lists.newArrayList();
				
				for (JsonElement element : array) {
					motds.add(centerText ? MiniMessage.miniMessage().deserialize(ComponentSupport.support().alignToCenter(AlignFrom.LEFT_SIDE, element.getAsString(), 255)) : MiniMessage.miniMessage().deserialize(element.getAsString()));
				}
				
				if (firstLine) {
					return addOrCreateForConsumer(consumer, ping -> {
						ping.description(motds.get(RANDOM.nextInt(motds.size())));
					});
				} else {
					return addOrCreateForConsumer(consumer, ping -> {
						ping.description(ping.getDescriptionComponent().get().appendNewline().append(motds.get(RANDOM.nextInt(motds.size()))));
					});
				}
			}
		}
		return consumer;
	}
	
	@NotNull
	private static <T> Consumer<T> addOrCreateForConsumer(@Nullable Consumer<T> a, @NotNull Consumer<T> b) {
		Objects.requireNonNull(b, "second action cannot be null!");
		return a == null ? b : a.andThen(b);
	}
	
	@NotNull
	private static <T> Consumer<T> nullOrEmptyConsumer(@Nullable Consumer<T> action) {
		return action != null ? action : t -> {};
	}
	
	public static ProtocolVersion parseMinimumSupportedVersion(@NotNull String gameVersion) {
		for (ProtocolVersion v : ProtocolVersion.values()) {
			if (v.getVersionsSupportedBy().contains(gameVersion)) {
				return v;
			}
		}
		
		return ProtocolVersion.MINIMUM_VERSION;
	}
	
	public static BufferedImage applyImageSettings(BufferedImage originalImage) {
		BufferedImage resizedImage = new BufferedImage(DEFAULT_SERVER_ICON_PIXEL, DEFAULT_SERVER_ICON_PIXEL, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = resizedImage.createGraphics();
		graphics2D.drawImage(originalImage, 0, 0, DEFAULT_SERVER_ICON_PIXEL, DEFAULT_SERVER_ICON_PIXEL, null);
		graphics2D.dispose();
		return resizedImage;
	}
	
	public static class Listener {
		
		private final ServerPing.Builder builder;
		private final ServerPing.Builder legacyBuilder;
		private final int minimumSupportedProtocol;
		private final Consumer<ServerPing.Builder> updateMotdConsumer;
		private final Consumer<ServerPing.Builder> legacyUpdateMotdConsumer;
		private final ServerPing.Version minimumVersion;
		private final Predicate<Integer> protocolVersionFun = protocol -> ProtocolVersion.MINECRAFT_1_15_2.getProtocol() < protocol;
		
		public Listener(@NotNull ServerPing.Builder builder, @NotNull ServerPing.Builder legacyBuilder, @NotNull String incompatibleVersionText, int minimumSupportedProtocol, 
				@NotNull Consumer<ServerPing.Builder> updateMotdConsumer, @NotNull Consumer<ServerPing.Builder> legacyUpdateMotdConsumer) {
			
			this.updateMotdConsumer = updateMotdConsumer;
			this.minimumSupportedProtocol = minimumSupportedProtocol;
			this.builder = builder;
			this.legacyBuilder = legacyBuilder;
			this.legacyUpdateMotdConsumer = legacyUpdateMotdConsumer;
			this.minimumVersion = new ServerPing.Version(minimumSupportedProtocol, incompatibleVersionText);
		}

		@Subscribe
		public EventTask onProxyPingEvent(ProxyPingEvent event) {
			return new EventTask() {

				@Override
				public void execute(Continuation continuation) {
					int connectionProtocol = event.getConnection().getProtocolVersion().getProtocol();
					
					ServerPing.Version version = connectionProtocol >= minimumSupportedProtocol ? new ServerPing.Version(connectionProtocol, "") : minimumVersion;
					
					if (protocolVersionFun.test(connectionProtocol)) {
						updateMotdConsumer.accept(builder);
						event.setPing(builder.build().asBuilder().version(version).build());
					} else {
						legacyUpdateMotdConsumer.accept(legacyBuilder);
						event.setPing(legacyBuilder.build().asBuilder().version(version).build());
					}
					
					continuation.resume();
				}
				
				@Override
				public boolean requiresAsync() {
					return true;
				}
			};
		}
	}
}
