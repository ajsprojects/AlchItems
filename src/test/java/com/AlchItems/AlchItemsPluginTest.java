package com.AlchItems;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class AlchItemsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(net.runelite.client.plugins.alchitems.AlchItemsPlugin.class);
		RuneLite.main(args);
	}
}