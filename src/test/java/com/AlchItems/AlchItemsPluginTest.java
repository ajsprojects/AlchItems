package com.AlchItems;

import alchitems.AlchItemsPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class AlchItemsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(AlchItemsPlugin.class);
		RuneLite.main(args);
	}
}