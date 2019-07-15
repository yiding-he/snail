package com.acgist.snail.net.torrent.utp;

import com.acgist.snail.net.UdpClient;
import com.acgist.snail.net.torrent.peer.bootstrap.PeerSubMessageHandler;
import com.acgist.snail.net.torrent.server.TorrentServer;
import com.acgist.snail.pojo.session.PeerSession;

/**
 * UTP客户端
 * 
 * @author acgist
 * @since 1.1.0
 */
public class UtpClient extends UdpClient<UtpMessageHandler> {

	private final PeerSession peerSession;
	private final PeerSubMessageHandler peerSubMessageHandler;
	
	private UtpClient(PeerSession peerSession, PeerSubMessageHandler peerSubMessageHandler) {
		super("UTP Client", new UtpMessageHandler(peerSubMessageHandler, peerSession.peerSocketAddress()), peerSession.peerSocketAddress());
		this.peerSession = peerSession;
		this.peerSubMessageHandler = peerSubMessageHandler;
	}
	
	public static final UtpClient newInstance(PeerSession peerSession, PeerSubMessageHandler peerSubMessageHandler) {
		return new UtpClient(peerSession, peerSubMessageHandler);
	}
	
	@Override
	public boolean open() {
		return open(TorrentServer.getInstance().channel());
	}

	/**
	 * 握手
	 */
	public boolean connect() {
		return this.handler.connect();
	}

	public PeerSession peerSession() {
		return this.peerSession;
	}
	
	public PeerSubMessageHandler peerSubMessageHandler() {
		return this.peerSubMessageHandler;
	}
	
}