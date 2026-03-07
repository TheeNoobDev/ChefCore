package net.chefcraft.proxy.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import net.chefcraft.proxy.ChefProxyCore;
import net.chefcraft.proxy.motd.ServerMotdManager;
import net.chefcraft.reflection.base.language.VelocityMessageCompiler;

public class CommonProxyCommands implements SimpleCommand {
	
	public CommonProxyCommands() {
		ProxyServer server = ChefProxyCore.getInstance().getServer();
		server.getCommandManager().register(server.getCommandManager().metaBuilder("proxycore").build(), this);
	}

	@Override
	public void execute(Invocation invocation) {
		String[] args = invocation.arguments();
		
		if (args.length <= 0) {
			VelocityMessageCompiler.sendMessage(ChefProxyCore.getInstance(), invocation.source(), "help");
			return;
		}
		
		if (args[0].equalsIgnoreCase("reload")) {
			
			if (!invocation.source().hasPermission("proxycore.reload")) {
				VelocityMessageCompiler.sendMessage(ChefProxyCore.getInstance(), invocation.source(), "noPermission");
				return;
			}
			
			ChefProxyCore.getConfigJsonFile().reload();
			
			ChefProxyCore.lobbyCommand.unregister();
			ChefProxyCore.lobbyCommand = new LobbyCommand(ChefProxyCore.getConfigJsonFile());
			
			ServerMotdManager.load();
			
			VelocityMessageCompiler.sendMessage(ChefProxyCore.getInstance(), invocation.source(), "reloaded");
			return;
		}
	}

}
