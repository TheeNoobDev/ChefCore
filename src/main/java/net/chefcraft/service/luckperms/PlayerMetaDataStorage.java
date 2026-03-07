package net.chefcraft.service.luckperms;
import net.chefcraft.core.language.MessageHolder;
import net.chefcraft.core.language.TranslationSource;
import net.chefcraft.core.party.PartyPlatformProvider;
import net.chefcraft.core.util.Placeholder;
import net.chefcraft.world.util.PapiHook;

/**
 * @since 1.2.2
 */
public class PlayerMetaDataStorage {

	private final TranslationSource translationSource;
	private String groupName = "";
	private String prefix = "";
	private String suffixFirst = "";
	private String suffixSecond = "";
	private String prefixColor = "";
	private String suffixColor = "";
	private String usernameColor = "";
	private String messageColor = "";
	private int tabHeight = 99;
	
	public PlayerMetaDataStorage(TranslationSource translationSource) {
		this.translationSource = translationSource;
	}
	
	public String replaceAllFormats(String text) {
		return PapiHook.translatePlaceholders(translationSource, text.replace("{PREFIX}", this.prefix)
				.replace("{SUFFIX_FIRST}", this.suffixFirst)
				.replace("{SUFFIX_SECOND}", this.suffixSecond)
				.replace("{PREFIX_COLOR}", this.prefixColor)
				.replace("{SUFFIX_COLOR}", this.suffixColor)
				.replace("{USERNAME_COLOR}", this.usernameColor)
				.replace("{MESSAGE_COLOR}", this.messageColor)
				.replace("{PLAYER_NAME}", PartyPlatformProvider.GET_USERNAME.apply(this.translationSource)));
	}
	
	public MessageHolder replaceAllFormatsAndGetAsMessageHolder(String text) {
		String newText = PapiHook.translatePlaceholders(this.translationSource, text.replace("{PREFIX}", this.prefix)
				.replace("{SUFFIX_FIRST}", this.suffixFirst)
				.replace("{SUFFIX_SECOND}", this.suffixSecond)
				.replace("{PREFIX_COLOR}", this.prefixColor)
				.replace("{SUFFIX_COLOR}", this.suffixColor)
				.replace("{USERNAME_COLOR}", this.usernameColor)
				.replace("{MESSAGE_COLOR}", this.messageColor)
				.replace("{PLAYER_NAME}", PartyPlatformProvider.GET_USERNAME.apply(this.translationSource)));
		
		return MessageHolder.text("", newText, this.translationSource.getRandomColor());
	}
	
	public Placeholder addToPlaceholder(Placeholder holder) {
		holder.add("{PREFIX}", this.prefix);
		holder.add("{SUFFIX_FIRST}", this.suffixFirst);
		holder.add("{SUFFIX_SECOND}", this.suffixSecond);
		holder.add("{PREFIX_COLOR}", this.prefixColor);
		holder.add("{SUFFIX_COLOR}", this.suffixColor);
		holder.add("{USERNAME_COLOR}", this.usernameColor);
		holder.add("{MESSAGE_COLOR}", this.messageColor);
		holder.add("{PLAYER_NAME}", PartyPlatformProvider.GET_USERNAME.apply(this.translationSource));
		return holder;
	}
	
	public TranslationSource getCorePlayer() {
		return translationSource;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffixFirst() {
		return this.suffixFirst;
	}

	public void setSuffixFirst(String suffixFirst) {
		this.suffixFirst = suffixFirst;
	}
	
	public String getSuffixSecond() {
		return this.suffixSecond;
	}

	public void setSuffixSecond(String suffixSecond) {
		this.suffixSecond = suffixSecond;
	}

	public String getPrefixColor() {
		return prefixColor;
	}

	public void setPrefixColor(String prefixColor) {
		this.prefixColor = prefixColor;
	}

	public String getSuffixColor() {
		return suffixColor;
	}

	public void setSuffixColor(String suffixColor) {
		this.suffixColor = suffixColor;
	}

	public String getUsernameColor() {
		return usernameColor;
	}

	public void setUsernameColor(String usernameColor) {
		this.usernameColor = usernameColor;
	}

	public String getMessageColor() {
		return messageColor;
	}

	public void setMessageColor(String messageColor) {
		this.messageColor = messageColor;
	}

	public int getTabHeight() {
		return tabHeight;
	}

	public void setTabHeight(int tabHeight) {
		this.tabHeight = tabHeight;
	}
}
