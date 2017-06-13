package nz.co.lolnet.bungeeproxy.handlers;

import java.lang.reflect.Method;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import nz.co.lolnet.bungeeproxy.BungeeProxy;
import nz.co.lolnet.bungeeproxy.util.Reference;

public class ChannelHandler extends ChannelInitializer<Channel> {
	
	private final Object bungeeChannelInitializer;
	private final Method initChannelMethod;
	
	public ChannelHandler(Object bungeeChannelInitializer, Method initChannelMethod) {
		this.bungeeChannelInitializer = bungeeChannelInitializer;
		this.initChannelMethod = initChannelMethod;
	}
	
	@Override
	protected void initChannel(Channel channel) {
		try {
			if (getInitChannelMethod() == null || getBungeeChannelInitializer() == null) {
				throw new NullPointerException();
			}
			
			getInitChannelMethod().invoke(getBungeeChannelInitializer(), channel);
			channel.pipeline().addAfter("timeout", Reference.DECODER_NAME, new HAProxyMessageDecoder());
			channel.pipeline().addAfter(Reference.DECODER_NAME, Reference.HANDLER_NAME, new ProxyHandler());
		} catch (Exception ex) {
			BungeeProxy.getInstance().getLogger().severe("Encountered an error processing 'initChannel' in '" + getClass().getSimpleName() + "' - " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	private Object getBungeeChannelInitializer() {
		return bungeeChannelInitializer;
	}
	
	private Method getInitChannelMethod() {
		return initChannelMethod;
	}
}