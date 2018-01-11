package org.red5.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
//import org.slf4j.Logger;
import org.red5.server.api.stream.IBroadcastStream;

import medialive.DAO.impl.liveDAOImpl;
import medialive.DAO.impl.playbackDAOImpl;
import medialive.domain.playback;

/**
 * Sample application that uses the client manager.
 * 
 * @author The Red5 Project (red5@osflash.org)
 */
public class Application extends MultiThreadedApplicationAdapter {
	
	//private static Logger log = Red5LoggerFactory.getLogger(Application.class);
	private String contextPath = new String();
	/** {@inheritDoc} */
    @Override
	public boolean connect(IConnection conn, IScope scope, Object[] params) {
    	//log.info("appConnect");
    	System.out.println("连接成功...");
    	this.contextPath = scope.getContextPath();
    	
		return true;
	}

	/** {@inheritDoc} */
    @Override
	public void disconnect(IConnection conn, IScope scope) {
    	//log.info("disconnect");
    	System.out.println("连接断开");

		super.disconnect(conn, scope);
	}

	@Override
	public void streamPublishStart(IBroadcastStream stream) {
		// TODO Auto-generated method stub
		System.out.println("开始发布并存储,存储的文件名为日期加流名称");
//		建立string builder 准备拼接文件名称
		StringBuilder fileName = new StringBuilder();
//		格式化日期
		SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd");
//		产生文件名yy-MM-dd-streamName
		fileName.append(dateFormate.format(new Date())).append("-").append(stream.getPublishedName());
//		保存流,同一天不同时间的直播流叠加存储于当天的文件中
		try {
			stream.saveAs(fileName.toString(), true);
			} catch (Exception e) {
			e.printStackTrace();
			}
//		将视频信息写入数据库
		System.out.println("视频数据入库");
		playbackDAOImpl playbackDAO = new playbackDAOImpl();
		playback pbDemo = new playback();
		pbDemo.setStreamName(stream.getPublishedName());
		pbDemo.setLiveDate(new Date());
		pbDemo.setFileName(fileName.toString());
		pbDemo.setRed5URL(contextPath);
		playbackDAO.save(pbDemo);
		super.streamPublishStart(stream);
		
	}

	@Override
	public void streamBroadcastClose(IBroadcastStream stream) {
		// TODO Auto-generated method stub
		System.out.println("广播关闭,删除直播信息");
		liveDAOImpl liveDAO = new liveDAOImpl();
		liveDAO.deleteBystream(stream.getPublishedName());
		super.streamBroadcastClose(stream);
		
	}

	@Override
	public void streamBroadcastStart(IBroadcastStream stream) {
		// TODO Auto-generated method stub
		super.streamBroadcastStart(stream);
	}
	
    
    
    

}
