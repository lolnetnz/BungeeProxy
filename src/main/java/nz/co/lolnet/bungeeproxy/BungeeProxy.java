package nz.co.lolnet.bungeeproxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.netty.PipelineUtils;
import nz.co.lolnet.bungeeproxy.config.Config;
import nz.co.lolnet.bungeeproxy.handlers.ChannelHandler;
import nz.co.lolnet.bungeeproxy.util.Reference;

public class BungeeProxy extends Plugin {
	
	private static BungeeProxy instance;
	private Config config;
	
	@Override
	public void onEnable() {
		instance = this;
		config = new Config();
		getConfig().loadConfig();
		
		try {
			Field serverChildField = PipelineUtils.class.getDeclaredField("SERVER_CHILD");
			serverChildField.setAccessible(true);
			
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(serverChildField, Modifier.PUBLIC & Modifier.STATIC);
			
			ChannelInitializer<Channel> bungeeChannelInitializer = PipelineUtils.SERVER_CHILD;
			Method initChannelMethod = ChannelInitializer.class.getDeclaredMethod("initChannel", Channel.class);
			initChannelMethod.setAccessible(true);
			
			serverChildField.set(null, new ChannelHandler(bungeeChannelInitializer, initChannelMethod));
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
	
	public static BungeeProxy getInstance() {
		return instance;
	}
	
	public Config getConfig() {
		return config;
	}
}