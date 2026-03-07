package net.chefcraft.core.party;

public interface PartyListener<T> {

	public abstract void onInviteAccepted(T whoAcceptted);
		
	public abstract void onPlayerLeft(T whoLeft, PartyLeaveCause cause);

	public abstract void onOwnerChanged(T newOwner);
		
	public abstract void onDisbanded();
	
	//for proxy
	public abstract void onLeaderSwitchServer(Object server);
}
