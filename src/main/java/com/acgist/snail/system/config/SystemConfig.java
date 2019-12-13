package com.acgist.snail.system.config;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acgist.snail.utils.NetUtils;

/**
 * <p>系统配置</p>
 * 
 * @author acgist
 * @since 1.0.0
 */
public final class SystemConfig extends PropertiesConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(SystemConfig.class);
	
	private static final SystemConfig INSTANCE = new SystemConfig();
	
	private static final String SYSTEM_CONFIG = "/config/system.properties";

	/**
	 * <p>数据大小比例：{@value}</p>
	 */
	public static final int DATA_SCALE = 1024;
	/**
	 * <p>1KB数据大小：1KB = 1024B</p>
	 */
	public static final int ONE_KB = DATA_SCALE;
	/**
	 * <p>1MB数据大小：1MB = 1024KB = 1024 * 1024B</p>
	 */
	public static final int ONE_MB = DATA_SCALE * ONE_KB;
	/**
	 * <p>最小下载速度：16KB</p>
	 */
	public static final int MIN_BUFFER_KB = 16;
	/**
	 * <p>TCP消息缓冲大小：和Piece交换Slice大小一样</p>
	 */
	public static final int TCP_BUFFER_LENGTH = 16 * ONE_KB;
	/**
	 * <p>UDP消息缓存大小</p>
	 */
	public static final int UDP_BUFFER_LENGTH = 2 * ONE_KB;
	/**
	 * <p>连接超时时间（秒）</p>
	 */
	public static final int CONNECT_TIMEOUT = 5;
	/**
	 * <p>连接超时时间（毫秒）</p>
	 */
	public static final int CONNECT_TIMEOUT_MILLIS = CONNECT_TIMEOUT * 1000;
	/**
	 * <p>接收超时时间（秒）</p>
	 */
	public static final int RECEIVE_TIMEOUT = 5;
	/**
	 * <p>接收超时时间（毫秒）</p>
	 */
	public static final int RECEIVE_TIMEOUT_MILLIS = RECEIVE_TIMEOUT * 1000;
	/**
	 * <p>下载超时时间（秒）</p>
	 */
	public static final int DOWNLOAD_TIMEOUT = 60;
	/**
	 * <p>下载超时时间（毫秒）</p>
	 */
	public static final int DOWNLOAD_TIMEOUT_MILLIS = DOWNLOAD_TIMEOUT * 1000;
	/**
	 * <p>最大的网络包大小</p>
	 * <p>所有创建ByteBuffer和byte[]对象的长度由外部数据设置时需要验证长度：防止恶意攻击导致内存泄露</p>
	 */
	public static final int MAX_NET_BUFFER_LENGTH = 4 * ONE_MB;
	/**
	 * <p>SHA1的HASH值长度：20</p>
	 */
	public static final int SHA1_HASH_LENGTH = 20;
	/**
	 * <p>编码：GBK</p>
	 */
	public static final String CHARSET_GBK = "GBK";
	/**
	 * <p>编码：UTF-8</p>
	 */
	public static final String CHARSET_UTF8 = "UTF-8";
	/**
	 * <p>编码：ASCII</p>
	 */
	public static final String CHARSET_ASCII = "ASCII";
	/**
	 * <p>编码：ISO-8859-1</p>
	 */
	public static final String CHARSET_ISO_8859_1 = "ISO-8859-1";
	/**
	 * <p>系统默认编码（file.encoding）</p>
	 */
	public static final String DEFAULT_CHARSET = CHARSET_UTF8;
	/**
	 * <p>无符号BYTE最大值</p>
	 */
	public static final int UNSIGNED_BYTE_MAX = 2 << 7;
	/**
	 * <p>数字</p>
	 */
	public static final String DIGIT = "0123456789";
	/**
	 * <p>字符（小写）</p>
	 */
	public static final String LETTER = "abcdefghijklmnopqrstuvwxyz";
	/**
	 * <p>字符（大写）</p>
	 */
	public static final String LETTER_UPPER = LETTER.toUpperCase();
	/**
	 * <p>任务列表刷新时间</p>
	 */
	public static final Duration TASK_REFRESH_INTERVAL = Duration.ofSeconds(4);
	/**
	 * <p>用户工作目录</p>
	 * <p>注意：初始化为常量（不能使用类变量：本类初始化时会使用）</p>
	 */
	private static final String USER_DIR = System.getProperty("user.dir");
	
	private SystemConfig() {
		super(SYSTEM_CONFIG);
	}

	static {
		LOGGER.info("初始化系统配置");
		INSTANCE.init();
		INSTANCE.logger();
	}

	public static final SystemConfig getInstance() {
		return INSTANCE;
	}
	
	/**
	 * <p>软件名称</p>
	 */
	private String name;
	/**
	 * <p>软件名称（英文）</p>
	 */
	private String nameEn;
	/**
	 * <p>软件版本</p>
	 */
	private String version;
	/**
	 * <p>FTP匿名用户</p>
	 */
	private String ftpUser;
	/**
	 * <p>FTP匿名密码</p>
	 */
	private String ftpPassword;
	/**
	 * <p>作者</p>
	 */
	private String author;
	/**
	 * <p>官网与源码</p>
	 */
	private String source;
	/**
	 * <p>问题与建议</p>
	 */
	private String support;
	/**
	 * <p>STUN服务器</p>
	 * <dl>
	 * 	<dt>格式</dt>
	 * 	<dd>host</dd>
	 * 	<dd>host:port</dd>
	 * 	<dd>stun:host</dd>
	 * 	<dd>stun:host:port</dd>
	 * </dl>
	 */
	private String stunServer;
	/**
	 * <p>系统服务端口（本地服务：启动检测）</p>
	 */
	private int servicePort;
	/**
	 * <p>BT服务端口（本地端口：Peer、DHT、UTP、STUN）</p>
	 */
	private int torrentPort;
	/**
	 * <p>BT服务端口（外网映射：Peer、DHT、UTP、STUN）</p>
	 */
	private int torrentPortExt = 0;
	/**
	 * <p>WebServer端口</p>
	 */
	private int webServerPort;
	/**
	 * <p>单个任务Peer数量（同时下载）</p>
	 */
	private int peerSize;
	/**
	 * <p>单个任务Tracker数量</p>
	 */
	private int trackerSize;
	/**
	 * <p>任务即将完成时可以重复下载的Piece数量</p>
	 */
	private int pieceRepeatSize;
	/**
	 * <p>DHT执行周期（秒）</p>
	 */
	private int dhtInterval;
	/**
	 * <p>PEX执行周期（秒）</p>
	 */
	private int pexInterval;
	/**
	 * <p>本地发现执行周期（秒）</p>
	 */
	private int lsdInterval;
	/**
	 * <p>Tracker执行周期（秒）</p>
	 */
	private int trackerInterval;
	/**
	 * <p>Peer（连接、接入）优化周期（秒）</p>
	 */
	private int peerOptimizeInterval;
	/**
	 * <p>外网IP地址</p>
	 */
	private String externalIpAddress;
	
	/**
	 * <p>初始化</p>
	 */
	private void init() {
		this.name = getString("acgist.system.name");
		this.nameEn = getString("acgist.system.name.en");
		this.version = getString("acgist.system.version");
		this.ftpUser = getString("acgist.system.ftp.user");
		this.ftpPassword = getString("acgist.system.ftp.password");
		this.author = getString("acgist.system.author");
		this.source = getString("acgist.system.source");
		this.support = getString("acgist.system.support");
		this.stunServer = getString("acgist.system.stun.server");
		this.servicePort = getInteger("acgist.service.port", 16888);
		this.torrentPort = getInteger("acgist.torrent.port", 18888);
		this.webServerPort = getInteger("acgist.web.server.port", 8888);
		this.peerSize = getInteger("acgist.peer.size", 20);
		this.trackerSize = getInteger("acgist.tracker.size", 50);
		this.pieceRepeatSize = getInteger("acgist.piece.repeat.size", 4);
		this.dhtInterval = getInteger("acgist.dht.interval", 120);
		this.pexInterval = getInteger("acgist.pex.interval", 120);
		this.lsdInterval = getInteger("acgist.lsd.interval", 120);
		this.trackerInterval = getInteger("acgist.tracker.interval", 120);
		this.peerOptimizeInterval = getInteger("acgist.peer.optimize.interval", 60);
	}

	/**
	 * <p>日志</p>
	 */
	private void logger() {
		LOGGER.info("软件名称：{}", this.name);
		LOGGER.info("软件名称（英文）：{}", this.nameEn);
		LOGGER.info("软件版本：{}", this.version);
		LOGGER.info("FTP匿名用户：{}", this.ftpUser);
		LOGGER.info("FTP匿名密码：{}", this.ftpPassword);
		LOGGER.info("作者：{}", this.author);
		LOGGER.info("官网与源码：{}", this.source);
		LOGGER.info("问题与建议：{}", this.support);
		LOGGER.info("系统服务端口：{}", this.servicePort);
		LOGGER.info("BT服务端口（Peer、DHT、UTP、STUN）：{}", this.torrentPort);
		LOGGER.info("WebServer端口：{}", this.webServerPort);
		LOGGER.info("单个任务Peer数量（同时下载）：{}", this.peerSize);
		LOGGER.info("单个任务Tracker数量：{}", this.trackerSize);
		LOGGER.info("任务即将完成时可以重复下载的Piece数量：{}", this.pieceRepeatSize);
		LOGGER.info("DHT执行周期（秒）：{}", this.dhtInterval);
		LOGGER.info("PEX执行周期（秒）：{}", this.pexInterval);
		LOGGER.info("本地发现执行周期（秒）：{}", this.lsdInterval);
		LOGGER.info("Tracker执行周期（秒）：{}", this.trackerInterval);
		LOGGER.info("Peer（连接、接入）优化周期（秒）：{}", this.peerOptimizeInterval);
		LOGGER.info("用户工作目录：{}", SystemConfig.USER_DIR);
	}
	
	/**
	 * <p>软件名称</p>
	 */
	public static final String getName() {
		return INSTANCE.name;
	}

	/**
	 * <p>软件名称（英文）</p>
	 */
	public static final String getNameEn() {
		return INSTANCE.nameEn;
	}

	/**
	 * <p>软件版本</p>
	 */
	public static final String getVersion() {
		return INSTANCE.version;
	}

	/**
	 * <p>FTP匿名用户</p>
	 */
	public static final String getFtpUser() {
		return INSTANCE.ftpUser;
	}

	/**
	 * <p>FTP匿名密码</p>
	 */
	public static final String getFtpPassword() {
		return INSTANCE.ftpPassword;
	}
	
	/**
	 * <p>作者</p>
	 */
	public static final String getAuthor() {
		return INSTANCE.author;
	}

	/**
	 * <p>官网与源码</p>
	 */
	public static final String getSource() {
		return INSTANCE.source;
	}

	/**
	 * <p>问题与建议</p>
	 */
	public static final String getSupport() {
		return INSTANCE.support;
	}

	/**
	 * <p>STUN服务器</p>
	 */
	public static final String getStunServer() {
		return INSTANCE.stunServer;
	}
	
	/**
	 * <p>系统服务端口</p>
	 */
	public static final int getServicePort() {
		return INSTANCE.servicePort;
	}

	/**
	 * <p>BT服务端口（本机：Peer、DHT、UTP、STUN）</p>
	 */
	public static final int getTorrentPort() {
		return INSTANCE.torrentPort;
	}
	
	/**
	 * <p>设置BT服务端口（外网：Peer、DHT、UTP、STUN）</p>
	 * <p>内网端口和外网端口可能不一致</p>
	 */
	public static final void setTorrentPortExt(int torrentPortExt) {
		LOGGER.info("服务端口（外网：Peer、DHT、UTP、STUN）：{}", torrentPortExt);
		INSTANCE.torrentPortExt = torrentPortExt;
	}
	
	/**
	 * <p>BT服务端口（外网：Peer、DHT、UTP、STUN）</p>
	 * <p>如果不存在返回{@linkplain #getTorrentPort() 内网端口}</p>
	 */
	public static final int getTorrentPortExt() {
		if(INSTANCE.torrentPortExt == 0) {
			return getTorrentPort();
		}
		return INSTANCE.torrentPortExt;
	}
	
	/**
	 * <p>BT服务端口（外网：Peer、DHT、UTP、STUN）：short</p>
	 */
	public static final short getTorrentPortExtShort() {
		return NetUtils.encodePort(getTorrentPortExt());
	}
	
	/**
	 * <p>WebServer端口</p>
	 * 
	 * @return WebServer端口
	 */
	public static final int getWebServerPort() {
		return INSTANCE.webServerPort;
	}
	
	/**
	 * <p>单个任务Peer数量（同时下载）</p>
	 */
	public static final int getPeerSize() {
		return INSTANCE.peerSize;
	}
	
	/**
	 * <p>单个任务Tracker数量</p>
	 */
	public static final int getTrackerSize() {
		return INSTANCE.trackerSize;
	}

	/**
	 * <p>任务即将完成时可以重复下载的Piece数量</p>
	 */
	public static final int getPieceRepeatSize() {
		return INSTANCE.pieceRepeatSize;
	}

	/**
	 * <p>DHT执行周期（秒）</p>
	 */
	public static final int getDhtInterval() {
		return INSTANCE.dhtInterval;
	}
	
	/**
	 * <p>PEX执行周期（秒）</p>
	 */
	public static final int getPexInterval() {
		return INSTANCE.pexInterval;
	}
	
	/**
	 * <p>本地发现执行周期（秒）</p>
	 */
	public static final int getLsdInterval() {
		return INSTANCE.lsdInterval;
	}
	
	/**
	 * <p>Tracker执行周期（秒）</p>
	 */
	public static final int getTrackerInterval() {
		return INSTANCE.trackerInterval;
	}
	
	/**
	 * <p>Peer（连接、接入）优化周期（秒）</p>
	 */
	public static final int getPeerOptimizeInterval() {
		return INSTANCE.peerOptimizeInterval;
	}

	/**
	 * <p>用户工作目录</p>
	 */
	public static final String userDir() {
		return SystemConfig.USER_DIR;
	}
	
	/**
	 * <p>用户工作目录中的文件路径</p>
	 * 
	 * @param path 文件相对路径：以“/”开头
	 */
	public static final String userDir(String path) {
		return SystemConfig.USER_DIR + path;
	}
	
	/**
	 * <p>获取软件信息：软件名称（英文） 软件版本</p>
	 */
	public static final String getNameEnAndVersion() {
		return INSTANCE.nameEn + " " + INSTANCE.version;
	}

	/**
	 * <p>设置外网IP地址</p>
	 */
	public static final void setExternalIpAddress(String externalIpAddress) {
		LOGGER.info("设置外网IP地址：{}", externalIpAddress);
		INSTANCE.externalIpAddress = externalIpAddress;
	}
	
	/**
	 * <p>获取外网IP地址</p>
	 */
	public static final String getExternalIpAddress() {
		return INSTANCE.externalIpAddress;
	}
	
}
