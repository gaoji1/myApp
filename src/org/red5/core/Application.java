package org.red5.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
//import org.slf4j.Logger;
import org.red5.server.api.stream.IBroadcastStream;

import medialive.DAO.impl.playbackDAOImpl;
import medialive.domain.playback;

/**
 * Sample application that uses the client manager.
 * 
 * @author The Red5 Project (red5@osflash.org)
 */
public class Application extends MultiThreadedApplicationAdapter {
	
	//private static Logger log = Red5LoggerFactory.getLogger(Application.class);

	/** {@inheritDoc} */
    @Override
	public boolean connect(IConnection conn, IScope scope, Object[] params) {
    	//log.info("appConnect");
    	System.out.println("连接成功...");
    	System.out.println(scope.getContextPath());
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
		StringBuilder fileName = new StringBuilder();
		SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd");
		fileName.append(dateFormate.format(new Date())).append("-").append(stream.getPublishedName());
		try {
			stream.saveAs(fileName.toString(), true);
			} catch (Exception e) {
			e.printStackTrace();
			}
		System.out.println("视频数据入库");
		playbackDAOImpl playbackDAO = new playbackDAOImpl();
		playback pbDemo = new playback();
		pbDemo.setStreamName(stream.getPublishedName());
		pbDemo.setLiveDate(new Date());
		pbDemo.setFileName(fileName.toString());
		playbackDAO.save(pbDemo);
		super.streamPublishStart(stream);
		
	}

	@Override
	public void streamBroadcastClose(IBroadcastStream stream) {
		// TODO Auto-generated method stub
		System.out.println("广播关闭");
		super.streamBroadcastClose(stream);
		
	}

	@Override
	public void streamBroadcastStart(IBroadcastStream stream) {
		// TODO Auto-generated method stub
		super.streamBroadcastStart(stream);
	}
	
    
    
    

}
