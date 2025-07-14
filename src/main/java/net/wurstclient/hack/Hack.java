/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hack;

import java.util.Objects;

import net.wurstclient.Category;
import net.wurstclient.Feature;
import net.wurstclient.hacks.ClickGuiHack;
import net.wurstclient.hacks.NavigatorHack;
import net.wurstclient.hacks.TooManyHaxHack;
import net.wurstclient.settings.TextFieldSetting;

public abstract class Hack extends Feature
{
	private final String name;
	private final String description;
	private Category category;
	
	private boolean enabled;
	private final boolean stateSaved =
		!getClass().isAnnotationPresent(DontSaveState.class);
	
	// HUD name alias setting
	private final TextFieldSetting hudNameAlias = new TextFieldSetting(
		"Custom HUD name",
		"Custom name to display in the HUD instead of the default hack name.\n\n"
			+ "Leave empty to use the default name.",
		"");
	
	public Hack(String name)
	{
		this.name = Objects.requireNonNull(name);
		description = "description.wurst.hack." + name.toLowerCase();
		addPossibleKeybind(name, "Toggle " + name);
		addSetting(hudNameAlias);
	}
	
	@Override
	public final String getName()
	{
		return name;
	}
	
	public String getRenderName()
	{
		String alias = hudNameAlias.getValue().trim();
		String baseName = alias.isEmpty() ? name : alias;
		String statusInfo = getStatusInfo();
		
		if(statusInfo == null || statusInfo.isEmpty())
			return baseName;
		
		return baseName + " " + statusInfo;
	}
	
	/**
	 * Override this method to add status information to the HUD display name.
	 * The status info will be appended to the base name (custom or default).
	 *
	 * @return status information to append, or null/empty for no status
	 */
	protected String getStatusInfo()
	{
		return null;
	}
	
	@Override
	public final String getDescription()
	{
		return WURST.translate(description);
	}
	
	public final String getDescriptionKey()
	{
		return description;
	}
	
	@Override
	public final Category getCategory()
	{
		return category;
	}
	
	protected final void setCategory(Category category)
	{
		this.category = category;
	}
	
	@Override
	public final boolean isEnabled()
	{
		return enabled;
	}
	
	public final void setEnabled(boolean enabled)
	{
		if(this.enabled == enabled)
			return;
		
		TooManyHaxHack tooManyHax = WURST.getHax().tooManyHaxHack;
		if(enabled && tooManyHax.isEnabled() && tooManyHax.isBlocked(this))
			return;
		
		this.enabled = enabled;
		
		if(!(this instanceof NavigatorHack || this instanceof ClickGuiHack))
			WURST.getHud().getHackList().updateState(this);
		
		if(enabled)
			onEnable();
		else
			onDisable();
		
		if(stateSaved)
			WURST.getHax().saveEnabledHax();
	}
	
	@Override
	public final String getPrimaryAction()
	{
		return enabled ? "Disable" : "Enable";
	}
	
	@Override
	public final void doPrimaryAction()
	{
		setEnabled(!enabled);
	}
	
	public final boolean isStateSaved()
	{
		return stateSaved;
	}
	
	protected void onEnable()
	{
		
	}
	
	protected void onDisable()
	{
		
	}
}
