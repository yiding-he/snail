package com.acgist.snail.net.torrent.lsd;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acgist.snail.net.UdpClient;
import com.acgist.snail.net.torrent.peer.bootstrap.PeerService;
import com.acgist.snail.pojo.wrapper.HeaderWrapper;
import com.acgist.snail.system.config.SystemConfig;
import com.acgist.snail.system.exception.NetException;
import com.acgist.snail.utils.ArrayUtils;
import com.acgist.snail.utils.NetUtils;
import com.acgist.snail.utils.StringUtils;

/**
 * <p>本地发现客户端</p>
 * 
 * @author acgist
 * @since 1.1.0
 */
public final class LocalServiceDiscoveryClient extends UdpClient<LocalServiceDiscoveryMessageHandler> {

	private static final Logger LOGGER = LoggerFactory.getLogger(LocalServiceDiscoveryClient.class);
	
	/**
	 * <p>BT-SEARCH协议</p>
	 */
	private static final String PROTOCOL = "BT-SEARCH * HTTP/1.1";
	
	private LocalServiceDiscoveryClient(InetSocketAddress socketAddress) {
		super("LSD Client", new LocalServiceDiscoveryMessageHandler(), socketAddress);
	}

	/**
	 * <p>创建本地发现客户端</p>
	 * 
	 * @param socketAddress 地址
	 */
	public static final LocalServiceDiscoveryClient newInstance() {
		return new LocalServiceDiscoveryClient(NetUtils.buildSocketAddress(LocalServiceDiscoveryServer.LSD_HOST, LocalServiceDiscoveryServer.LSD_PORT));
	}

	@Override
	public boolean open() {
		return this.open(LocalServiceDiscoveryServer.getInstance().channel());
	}
	
	/**
	 * <p>发送本地发现消息</p>
	 * 
	 * @param infoHashs InfoHash数组
	 */
	public void localSearch(String ... infoHashs) {
		if(ArrayUtils.isEmpty(infoHashs)) {
			return;
		}
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("发送本地发现消息（InfoHash）：{}", String.join(",", infoHashs));
		}
		try {
			this.send(this.buildLocalSearch(infoHashs));
		} catch (NetException e) {
			LOGGER.error("发送本地发现消息异常", e);
		}
	}
	
	/**
	 * <p>创建本地发现消息</p>
	 * 
	 * @param infoHashs InfoHash数组
	 * 
	 * @return 本地发现消息
	 */
	private String buildLocalSearch(String ... infoHashs) {
		final String peerId = StringUtils.hex(PeerService.getInstance().peerId());
		final HeaderWrapper builder = HeaderWrapper.newBuilder(PROTOCOL);
		builder
			.header(LocalServiceDiscoveryMessageHandler.HEADER_HOST, LocalServiceDiscoveryServer.LSD_HOST + ":" + LocalServiceDiscoveryServer.LSD_PORT)
			.header(LocalServiceDiscoveryMessageHandler.HEADER_PORT, String.valueOf(SystemConfig.getTorrentPort()))
			.header(LocalServiceDiscoveryMessageHandler.HEADER_COOKIE, peerId);
		for (String infoHash : infoHashs) {
			builder.header(LocalServiceDiscoveryMessageHandler.HEADER_INFOHASH, infoHash);
		}
		return builder.build();
	}
	
}
