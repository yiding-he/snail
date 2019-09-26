package com.acgist.snail.net.torrent.dht.bootstrap.response;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.acgist.snail.net.torrent.TorrentManager;
import com.acgist.snail.net.torrent.dht.bootstrap.NodeManager;
import com.acgist.snail.net.torrent.dht.bootstrap.Request;
import com.acgist.snail.net.torrent.dht.bootstrap.Response;
import com.acgist.snail.net.torrent.peer.bootstrap.PeerManager;
import com.acgist.snail.pojo.session.NodeSession;
import com.acgist.snail.pojo.session.PeerSession;
import com.acgist.snail.pojo.session.TorrentSession;
import com.acgist.snail.system.config.DhtConfig;
import com.acgist.snail.system.config.PeerConfig;
import com.acgist.snail.utils.NetUtils;
import com.acgist.snail.utils.StringUtils;

/**
 * <p>返回Peer和Node</p>
 * <p>
 * 返回8个最近的Node。<br>
 * 如果有Peer也需要返回，返回的Peer最大数量100个。
 * </p>
 * 
 * @author acgist
 * @since 1.0.0
 */
public class GetPeersResponse extends Response {

	private GetPeersResponse(byte[] t) {
		super(t);
		this.put(DhtConfig.KEY_TOKEN, NodeManager.getInstance().token());
	}
	
	private GetPeersResponse(Response response) {
		super(response.getT(), response.getY(), response.getR(), response.getE());
	}

	public static final GetPeersResponse newInstance(Response response) {
		return new GetPeersResponse(response);
	}

	public static final GetPeersResponse newInstance(Request request) {
		return new GetPeersResponse(request.getT());
	}
	
	public byte[] getToken() {
		return getBytes(DhtConfig.KEY_TOKEN);
	}
	
	/**
	 * 获取响应Node，同时加入到Node列表。
	 */
	public List<NodeSession> getNodes() {
		final byte[] bytes = this.getBytes(DhtConfig.KEY_NODES);
		if(bytes == null) {
			return List.of();
		}
		final ByteBuffer buffer = ByteBuffer.wrap(bytes);
		final List<NodeSession> list = new ArrayList<>();
		while(true) {
			final var session = readNode(buffer);
			if(session == null) {
				break;
			}
			list.add(session);
		}
		NodeManager.getInstance().sortNodes();
		return list;
	}
	
	/**
	 * 获取响应Peer，同时添加到列表。
	 */
	public List<PeerSession> getPeers(Request request) {
		return this.getValues(request);
	}
	
	/**
	 * 获取响应Peer，同时添加到列表。
	 */
	public List<PeerSession> getValues(Request request) {
		final byte[] infoHash = request.getBytes(DhtConfig.KEY_INFO_HASH);
		final String infoHashHex = StringUtils.hex(infoHash);
		final TorrentSession torrentSession = TorrentManager.getInstance().torrentSession(infoHashHex);
		if(torrentSession == null) {
			return List.of();
		}
		final List<?> values = this.getList(DhtConfig.KEY_VALUES);
		if(values == null) {
			return List.of();
		}
		byte[] bytes;
		PeerSession session;
		final ByteBuffer buffer = ByteBuffer.allocate(6);
		final List<PeerSession> list = new ArrayList<>();
		for (Object object : values) {
			bytes = (byte[]) object;
			buffer.put(bytes);
			buffer.flip();
			session = PeerManager.getInstance().newPeerSession(
				infoHashHex,
				torrentSession.statistics(),
				NetUtils.decodeIntToIp(buffer.getInt()),
				NetUtils.decodePort(buffer.getShort()),
				PeerConfig.SOURCE_DHT);
			buffer.flip();
			list.add(session);
		}
		return list;
	}
	
	/**
	 * 是否含有Node
	 */
	public boolean haveNodes() {
		return get(DhtConfig.KEY_NODES) != null;
	}
	
	/**
	 * 是否含有Peer
	 */
	public boolean havePeers() {
		return haveValues();
	}
	
	/**
	 * 是否含有Values
	 */
	public boolean haveValues() {
		return get(DhtConfig.KEY_VALUES) != null;
	}
	
}
