package nz.co.lolnet.bungeeproxy.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.common.io.ByteStreams;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import nz.co.lolnet.bungeeproxy.BungeeProxy;

public class Config {
	
	private Configuration configuration;
	
	public void loadConfig() {
		if (!BungeeProxy.getInstance().getDataFolder().exists()) {
			BungeeProxy.getInstance().getDataFolder().mkdir();
		}
		
		configuration = loadFile("config.yml", configuration);
	}
	
	public Configuration loadFile(String name, Configuration config) {
		try {
			File file = new File(BungeeProxy.getInstance().getDataFolder(), name);
			
			if (!file.exists()) {
				file.createNewFile();
				InputStream inputStream = BungeeProxy.getInstance().getResourceAsStream(name);
				OutputStream outputStream = new FileOutputStream(file);
				ByteStreams.copy(inputStream, outputStream);
				BungeeProxy.getInstance().getLogger().info("Successfully created " + name);
			}
			
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException | NullPointerException | SecurityException ex) {
			BungeeProxy.getInstance().getLogger().severe("Exception loading " + name);
			ex.printStackTrace();
		}
		return null;
	}
	
	public void saveFile(String name, Configuration config) {
		try {
			File file = new File(BungeeProxy.getInstance().getDataFolder(), name);
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
		} catch (IOException | NullPointerException | SecurityException ex) {
			BungeeProxy.getInstance().getLogger().severe("Exception saving " + name);
			ex.printStackTrace();
		}
	}
	
	public Configuration getConfiguration() {
		return this.configuration;
	}
}