package com.acgist.snail.system.context;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acgist.snail.gui.GuiHandler;
import com.acgist.snail.net.torrent.dht.bootstrap.NodeManager;
import com.acgist.snail.net.torrent.peer.bootstrap.PeerManager;
import com.acgist.snail.net.torrent.tracker.bootstrap.TrackerClient;
import com.acgist.snail.net.torrent.tracker.bootstrap.TrackerManager;
import com.acgist.snail.pojo.session.NodeSession;
import com.acgist.snail.pojo.session.PeerSession;
import com.acgist.snail.utils.FileUtils;

/**
 * <p>系统控制台</p>
 * <p>记录系统状态</p>
 * 
 * @author acgist
 * @since 1.0.0
 */
public class SystemConsole {

	private static final Logger LOGGER = LoggerFactory.getLogger(SystemConsole.class);
	
	private static final SystemConsole INSTANCE = new SystemConsole();

	private static final String NEW_LINE = "\r\n";
	
	private StringBuilder builder = new StringBuilder(NEW_LINE);
	
	private SystemConsole() {
	}

	public static final SystemConsole getInstance() {
		return INSTANCE;
	}
	
	/**
	 * 输出系统状态
	 */
	public synchronized void console() {
		system();
		node();
		tracker();
		peer();
		LOGGER.info("统计信息：{}", this.builder.toString());
		GuiHandler.getInstance().alert("统计信息", this.builder.toString());
		this.builder.setLength(0);
		this.builder = new StringBuilder(NEW_LINE);
	}
	
	/**
	 * 系统状态
	 */
	private void system() {
		var statistics = SystemStatistics.getInstance().getSystemStatistics();
		this.builder.append("累计上传：").append(FileUtils.formatSize(statistics.uploadSize())).append(NEW_LINE);
		this.builder.append("累计下载：").append(FileUtils.formatSize(statistics.downloadSize())).append(NEW_LINE);
	}
	
	/**
	 * DHT节点
	 */
	private void node() {
		final List<NodeSession> nodes = NodeManager.getInstance().nodes();
		final Map<NodeSession.Status, Long> group = nodes.stream()
			.collect(Collectors.groupingBy(NodeSession::getStatus, Collectors.counting()));
		this.builder.append("Node数量：").append(nodes.size()).append(NEW_LINE);
		this.builder.append("Node数量（未使用）：").append(group.getOrDefault(NodeSession.Status.unuse, 0L)).append(NEW_LINE);
		this.builder.append("Node数量（使用中）：").append(group.getOrDefault(NodeSession.Status.verify, 0L)).append(NEW_LINE);
		this.builder.append("Node数量（有效）：").append(group.getOrDefault(NodeSession.Status.available, 0L)).append(NEW_LINE);
	}
	
	/**
	 * Tracker
	 */
	private void tracker() {
		final List<TrackerClient> clients = TrackerManager.getInstance().clients();
		final Map<Boolean, Long> group = clients.stream()
			.collect(Collectors.groupingBy(TrackerClient::available, Collectors.counting()));
		this.builder.append("Tracker数量：").append(clients.size()).append(NEW_LINE);
		this.builder.append("Tracker数量（可用）：").append(group.getOrDefault(Boolean.TRUE, 0L)).append(NEW_LINE);
		this.builder.append("Tracker数量（不可用）：").append(group.getOrDefault(Boolean.FALSE, 0L)).append(NEW_LINE);
	}
	
	/**
	 * Peer
	 */
	private void peer() {
		final Map<String, List<PeerSession>> peers = PeerManager.getInstance().peers();
		final var dht = new AtomicInteger(0);
		final var pex = new AtomicInteger(0);
		final var utp = new AtomicInteger(0);
		final var lsd = new AtomicInteger(0);
		final var tracker = new AtomicInteger(0);
		final var connect = new AtomicInteger(0);
		final var upload = new AtomicInteger(0);
		final var download = new AtomicInteger(0);
		final var available = new AtomicInteger(0);
		peers.entrySet().stream()
			.filter(entry -> entry.getValue() != null)
			.forEach(entry -> {
				this.builder.append("Peer InfoHashHex：").append(entry.getKey()).append(NEW_LINE);
				final var list = entry.getValue();
				list.forEach(peer -> {
					if(peer.dht()) {
						dht.incrementAndGet();
					}
					if(peer.pex()) {
						pex.incrementAndGet();
					}
					if(peer.lsd()) {
						lsd.incrementAndGet();
					}
					if(peer.utp()) {
						utp.incrementAndGet();
					}
					if(peer.tracker()) {
						tracker.incrementAndGet();
					}
					if(peer.connect()) {
						connect.incrementAndGet();
					}
					if(peer.uploading()) {
						upload.incrementAndGet();
						this.builder.append(peer.host()).append("：").append("上传大小-").append(FileUtils.formatSize(peer.statistics().uploadSize())).append(NEW_LINE);
					}
					if(peer.downloading()) {
						download.incrementAndGet();
						this.builder.append(peer.host()).append("：").append("下载大小-").append(FileUtils.formatSize(peer.statistics().downloadSize())).append(NEW_LINE);
					}
					if(peer.available()) {
						available.incrementAndGet();
					}
				});
			this.builder
				.append("Peer：")
				.append("Peer数量-").append(list.size()).append("、")
				.append("uTP数量-").append(utp.getAndSet(0)).append("、")
				.append("Peer数量（可用）-").append(available.getAndSet(0)).append(NEW_LINE)
				.append("来源：")
				.append("DHT-").append(dht.getAndSet(0)).append("、")
				.append("PEX-").append(pex.getAndSet(0)).append("、")
				.append("LSD-").append(lsd.getAndSet(0)).append("、")
				.append("Tracker-").append(tracker.getAndSet(0)).append("、")
				.append("Connect-").append(connect.getAndSet(0)).append(NEW_LINE)
				.append("状态：")
				.append("上传中-").append(upload.getAndSet(0)).append("、")
				.append("下载中-").append(download.getAndSet(0)).append(NEW_LINE);
		});
	}

}
