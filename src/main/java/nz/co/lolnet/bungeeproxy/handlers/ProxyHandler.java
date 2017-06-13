package nz.co.lolnet.bungeeproxy.handlers;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;

import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import nz.co.lolnet.bungeeproxy.BungeeProxy;

public class ProxyHandler extends ChannelInboundHandlerAdapter {
	
	private Field remoteAddress;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		try {
			remoteAddress = AbstractChannel.class.getDeclaredField("remoteAddress");
			getRemoteAddress().setAccessible(true);
		} catch (Exception ex) {
			BungeeProxy.getInstance().getLogger().severe("Encountered an error processing 'channelActive' in '" + getClass().getSimpleName() + "' - " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		try {
			if (msg instanceof HAProxyMessage && getRemoteAddress() != null) {
				HAProxyMessage proxyMessage = (HAProxyMessage) msg;
				getRemoteAddress().set(ctx.channel(), new InetSocketAddress(proxyMessage.sourceAddress(), proxyMessage.sourcePort()));
				return;
			}
			
			super.channelRead(ctx, msg);
		} catch (Exception ex) {
			BungeeProxy.getInstance().getLogger().severe("Encountered an error processing 'channelRead' in '" + getClass().getSimpleName() + "' - " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
		if (ctx.channel().isActive()) {
			ctx.channel().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		}
		BungeeProxy.getInstance().getLogger().severe("Exception caught in '" + getClass().getSimpleName() + "' - " + throwable.getMessage());
	}
	
	private Field getRemoteAddress() {
		return remoteAddress;
	}
}