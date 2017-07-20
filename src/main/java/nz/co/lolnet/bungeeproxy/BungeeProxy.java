package nz.co.lolnet.bungeeproxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import nz.co.lolnet.bungeeproxy.config.Config;
import nz.co.lolnet.bungeeproxy.handlers.ChannelHandler;
import nz.co.lolnet.bungeeproxy.util.Reference;

public class BungeeProxy extends Plugin {
	
	private static BungeeProxy instance;
	private Config config;
	private Field remoteAddress;
	
	@Override
	public void onEnable() {
		instance = this;
		config = new Config();
		getConfig().loadConfig();
		
		try {
			Class<?> pipelineUtilsClass = Class.forName("net.md_5.bungee.netty.PipelineUtils");
			
			Field serverChildField = pipelineUtilsClass.getField("SERVER_CHILD");
			serverChildField.setAccessible(true);
			
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(serverChildField, serverChildField.getModifiers() & ~Modifier.FINAL);
			
			Object bungeeChannelInitializer = serverChildField.get(null);
			Method initChannelMethod = ChannelInitializer.class.getDeclaredMethod("initChannel", Channel.class);
			initChannelMethod.setAccessible(true);
			
			serverChildField.set(null, new ChannelHandler(bungeeChannelInitializer, initChannelMethod));
			
			remoteAddress = AbstractChannel.class.getDeclaredField("remoteAddress");
			getRemoteAddress().setAccessible(true);
			getLogger().info(Reference.PLUGIN_NAME + " Enabled.");
		} catch (Exception ex) {
			BungeeProxy.getInstance().getLogger().severe("Encountered an error processing 'onEnable' in '" + getClass().getSimpleName() + "' - " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	@Override
	public void onDisable() {
		getLogger().info(Reference.PLUGIN_NAME + " Disabled!");
	}
	
	public void debugMessage(String message) {
		if (getConfiguration() != null && getConfiguration().getBoolean("BungeeProxy.Debug")) {
			getLogger().info(message);
		}
	}
	
	public static BungeeProxy getInstance() {
		return instance;
	}
	
	public Config getConfig() {
		return config;
	}
	
	public Configuration getConfiguration() {
		if (getConfig() != null) {
			return getConfig().getConfiguration();
		}
		
		return null;
	}
	
	public Field getRemoteAddress() {
		return remoteAddress;
	}
}