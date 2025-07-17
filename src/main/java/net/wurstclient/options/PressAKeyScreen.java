/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.options;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public class PressAKeyScreen extends Screen
{
	private PressAKeyCallback prevScreen;
	
	public PressAKeyScreen(PressAKeyCallback prevScreen)
	{
		super(Text.literal(""));
		
		if(!(prevScreen instanceof Screen))
			throw new IllegalArgumentException("prevScreen is not a screen");
		
		this.prevScreen = prevScreen;
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		if(keyCode == GLFW.GLFW_KEY_ESCAPE)
		{
			client.setScreen((Screen)prevScreen);
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
		
		// Don't capture standalone modifier keys - stay on the screen
		if(isModifierKey(keyCode))
			return super.keyPressed(keyCode, scanCode, modifiers);
		
		// Valid key pressed - set it and go back
		prevScreen.setKey(getKeyName(keyCode, scanCode, modifiers));
		client.setScreen((Screen)prevScreen);
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	private String getKeyName(int keyCode, int scanCode, int modifiers)
	{
		// Check for modifier keys
		boolean ctrl = (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;
		boolean alt = (modifiers & GLFW.GLFW_MOD_ALT) != 0;
		boolean shift = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;
		
		String baseKey =
			InputUtil.fromKeyCode(keyCode, scanCode).getTranslationKey();
		
		// Build combination key string (lowercase to match KeybindProcessor)
		StringBuilder keyBuilder = new StringBuilder();
		if(ctrl)
			keyBuilder.append("ctrl+");
		if(alt)
			keyBuilder.append("alt+");
		if(shift)
			keyBuilder.append("shift+");
		keyBuilder.append(baseKey);
		
		return keyBuilder.toString();
	}
	
	private boolean isModifierKey(int keyCode)
	{
		return keyCode == GLFW.GLFW_KEY_LEFT_CONTROL
			|| keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL
			|| keyCode == GLFW.GLFW_KEY_LEFT_ALT
			|| keyCode == GLFW.GLFW_KEY_RIGHT_ALT
			|| keyCode == GLFW.GLFW_KEY_LEFT_SHIFT
			|| keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT
			|| keyCode == GLFW.GLFW_KEY_LEFT_SUPER
			|| keyCode == GLFW.GLFW_KEY_RIGHT_SUPER;
	}
	
	@Override
	public boolean shouldCloseOnEsc()
	{
		return false;
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY,
		float partialTicks)
	{
		renderBackground(context);
		context.drawCenteredTextWithShadow(textRenderer, "Press a key",
			width / 2, height / 4 + 36, 16777215);
		context.drawCenteredTextWithShadow(textRenderer,
			"Hold Ctrl, Alt, or Shift for combinations", width / 2,
			height / 4 + 48, 0xa0a0a0);
		context.drawCenteredTextWithShadow(textRenderer,
			"Examples: Ctrl+O, Alt+Shift+F, etc.", width / 2, height / 4 + 60,
			0xa0a0a0);
		super.render(context, mouseX, mouseY, partialTicks);
	}
}
