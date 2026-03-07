package net.chefcraft.world.scoreboard.custom;

public interface CoreTeamManager {
	
	CoreTeamHolder getTeam(String name);

	CoreTeamHolder findJoinedTeam(String entry);

	boolean hasTeam(String name);

	boolean moveEntryToOtherTeam(String teamName, String entry);
	
	boolean leaveTeam(String entry);

	void registerTeam(CoreTeamHolder coreTeam);

	void unregisterTeam(String teamName);

	void updateTeam(String teamName);

	void removeTeams();

	void updateTeams();
	
	void joinTeam(String teamName, String entry);
}
