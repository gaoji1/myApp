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
    	System.out.println("���ӳɹ�...");
    	this.contextPath = scope.getContextPath();
    	
		return true;
	}

	/** {@inheritDoc} */
    @Override
	public void disconnect(IConnection conn, IScope scope) {
    	//log.info("disconnect");
    	System.out.println("���ӶϿ�");

		super.disconnect(conn, scope);
	}

	@Override
	public void streamPublishStart(IBroadcastStream stream) {
		// TODO Auto-generated method stub
		System.out.println("��ʼ�������洢,�洢���ļ���Ϊ���ڼ�������");
//		����string builder ׼��ƴ���ļ�����
		StringBuilder fileName = new StringBuilder();
//		��ʽ������
		SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd");
//		�����ļ���yy-MM-dd-streamName
		fileName.append(dateFormate.format(new Date())).append("-").append(stream.getPublishedName());
//		������,ͬһ�첻ͬʱ���ֱ�������Ӵ洢�ڵ�����ļ���
		try {
			stream.saveAs(fileName.toString(), true);
			} catch (Exception e) {
			e.printStackTrace();
			}
//		����Ƶ��Ϣд�����ݿ�
		System.out.println("��Ƶ�������");
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
		System.out.println("�㲥�ر�,ɾ��ֱ����Ϣ");
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
