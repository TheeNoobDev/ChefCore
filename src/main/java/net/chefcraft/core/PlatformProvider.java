package net.chefcraft.core;

import net.chefcraft.core.language.MessageCompiler;
import net.chefcraft.core.party.PartyPlatformProivderBukkit;
import net.chefcraft.core.party.PartyPlatformProivderVelocity;
import net.chefcraft.core.party.PartyPlatformProvider;
import net.chefcraft.proxy.ChefProxyCore;
import net.chefcraft.reflection.base.language.BukkitMessageCompiler;
import net.chefcraft.reflection.base.language.PaperMessageCompiler;
import net.chefcraft.reflection.base.language.VelocityMessageCompiler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PlatformProvider {
	
	private static final boolean LUCKPERMS = PlatformProvider.hasClass("net.luckperms.api.LuckPermsProvider");
	private static final boolean BUKKIT = hasClass("org.bukkit.plugin.java.JavaPlugin");
	private static final boolean VELOCITY = hasClass("com.velocitypowered.api.plugin.PluginContainer");
	private static final boolean KYORI = hasClass("net.kyori.adventure.Adventure");
	private static final boolean PAPER = hasClass("io.papermc.paper.ServerBuildInfo") || hasClass("com.destroystokyo.paper.Title") || hasClass("org.github.paperspigot.Title");
	private static final MessageCompiler MESSAGE_COMPILER = VELOCITY ? new VelocityMessageCompiler() : PAPER ? new PaperMessageCompiler() : new BukkitMessageCompiler();
	private static final PluginInstance PLUGIN = VELOCITY ? ChefProxyCore.getInstance() : ChefCore.getInstance();
	private static final PartyPlatformProvider PARTY = VELOCITY ? new PartyPlatformProivderVelocity() : new PartyPlatformProivderBukkit();
			
	public static boolean hasClass(@NotNull String name) {
		try {
			Class.forName(Objects.requireNonNull(name));
			return true;
		} catch (ClassNotFoundException x) {
			return false;
		}
	}
	
	public static PartyPlatformProvider party() {
		return PARTY;
	}

	public static boolean usingBukkit() {
		return BUKKIT;
	}
	
	public static boolean usingVelocity() {
		return VELOCITY;
	}
	
	public static boolean hasKyoriAdventure() {
		return KYORI;
	}
	
	public static boolean hasPaper() {
		return PAPER;
	}
	
	public static boolean paperHasSupportingKyori() {
		return PAPER && KYORI;
	}
	
	public static boolean hasLuckPerms() {
		return LUCKPERMS;
	}
	
	@NotNull
	public static MessageCompiler messageCompiler() {
		return MESSAGE_COMPILER;
	}
	
	@NotNull
	public static PluginInstance getPluginInstance() {
		return PLUGIN;
	}
}
