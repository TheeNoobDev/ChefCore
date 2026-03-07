package net.chefcraft.proxy.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.chefcraft.core.configuration.CoreJsonFile;
import net.chefcraft.proxy.ChefProxyCore;
import net.chefcraft.proxy.player.CoreProxyPlayer;
import net.chefcraft.reflection.base.language.VelocityMessageCompiler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LobbyCommand implements SimpleCommand {
	
	private CoreJsonFile jsonFile;
	private List<String> lobbies = new ArrayList<>();
	private CommandMeta meta = null;
	
	public LobbyCommand(@NotNull CoreJsonFile jsonFile) {
		this.jsonFile = jsonFile;
		JsonObject obj = this.jsonFile.getConfig().getAsJsonObject().get("lobby").getAsJsonObject();
		for (JsonElement elem : obj.getAsJsonArray("servers")) {
			this.lobbies.add(elem.getAsString());
		}
		
		ProxyServer server = ChefProxyCore.getInstance().getServer();
		
		JsonArray array = obj.getAsJsonArray("commands");
		
		if (array.size() > 0) {
			CommandMeta.Builder builder = server.getCommandManager().metaBuilder(array.get(0).getAsString());
			String[] subCommands = null;
			
			if (array.size() > 1) {
				subCommands = new String[array.size() - 1];
				
				for (int i = 0; i < subCommands.length; i++) {
					subCommands[i] = array.get(i + 1).getAsString();
				}
				builder.aliases(subCommands);
			}
			this.meta = builder.build();
			
			server.getCommandManager().register(this.meta, this);
		}
	}
	
	public void unregister() {
		if (this.meta == null) return;
		ChefProxyCore.getInstance().getServer().getCommandManager().unregister(this.meta);
	}
	
	public boolean isLobbyServer(String name) {
		for (int i = 0; i < lobbies.size(); i++) {
			if (lobbies.get(i).equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void execute(Invocation invocation) {
		CommandSource sender = invocation.source();
		if (sender instanceof Player player) {
			CoreProxyPlayer proxyPlayer = ChefProxyCore.getProxyPlayerByPlayer(player);
			
			Optional<ServerConnection> server = player.getCurrentServer();
			if (server.isPresent() && this.isLobbyServer(server.get().getServerInfo().getName())) {
				proxyPlayer.sendMessage("lobbyCommand.alreadyConnected");
				return;
			}
			
			if (this.lobbies.size() > 0) {
				Optional<RegisteredServer> find = ChefProxyCore.getInstance().getServer().getServer(this.lobbies.get(0));
				if (find.isPresent()) {
					player.createConnectionRequest(find.get()).fireAndForget();
				} else {
					proxyPlayer.sendMessage("lobbyCommand.error");
				}
			}
			
			return;
		}
		
		VelocityMessageCompiler.sendMessage(ChefProxyCore.getInstance(), sender, "onlyPlayers");
	}

}
