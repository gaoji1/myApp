package org.red5.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
//import org.slf4j.Logger;
import org.red5.server.api.stream.IBroadcastStream;

import bean.live.Live;
import bean.playback.PlayBack;

/**
 * Sample application that uses the client manager.
 * 
 * @author The Red5 Project (red5@osflash.org)
 */
public class Application extends MultiThreadedApplicationAdapter {

	// private static Logger log = Red5LoggerFactory.getLogger(Application.class);
	private String contextPath = new String();
	private static SessionFactory sessionfactory;
	// ��̬����飬��ʼ��Hibernate�����ݿ�����ӣ�����Hibernate����
	static {
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.cfg.xml");
		sessionfactory = configuration.buildSessionFactory();
	}

	/** {@inheritDoc} */
	@Override
	public boolean connect(IConnection conn, IScope scope, Object[] params) {
		// log.info("appConnect");
		System.out.println("���ӳɹ�...");
		this.contextPath = scope.getContextPath();

		return true;
	}

	/** {@inheritDoc} */
	@Override
	public void disconnect(IConnection conn, IScope scope) {
		// log.info("disconnect");
		System.out.println("���ӶϿ�");

		super.disconnect(conn, scope);
	}

	@Override
	public void streamPublishStart(IBroadcastStream stream) {
		// TODO Auto-generated method stub
		System.out.println("��ʼ�������洢,�洢���ļ���Ϊ���ڼ�������");
		// �ȼ�鵱ǰ���Ƿ�����ע���û�����
		System.out.println("��鵱ǰ���Ƿ���Դ����֪�û�");
		Session session = sessionfactory.openSession();
		Transaction ts = session.beginTransaction();
		Criteria c = session.createCriteria(Live.class);
		c.add(Restrictions.eq("streamName", stream.getPublishedName()));
		List<Live> live = (List<Live>)c.list();
		ts.commit();
		if (live.size() == 0) {
			System.out.println("��ǰ��(" + stream.getPublishedName() + ")���û�û��ע�ᣬֱ���䲻����ʾ");
		} else {
			String uName = live.get(0).getuName();
			System.out.println("��ǰ��(" + stream.getPublishedName() + ")���û�Ϊ:" + uName);
			System.out.println("�ı�ֱ����״̬Ϊ�ڲ�");
			Live changeLive = live.get(0);
			changeLive.setIsOpen(true);
//			�޸��ύ
			ts = session.beginTransaction();
			session.save(changeLive);
			ts.commit();
			
			//����Ϊ�洢�ط��ļ�
			// ����string builder ׼��ƴ���ļ�����
			StringBuilder fileName = new StringBuilder();
			// ��ʽ������
			SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd");
			// �����ļ���yy-MM-dd-streamName
			fileName.append(dateFormate.format(new Date())).append("-").append(stream.getPublishedName());
			// ������,ͬһ�첻ͬʱ���ֱ�������Ӵ洢�ڵ�����ļ���
			try {
				stream.saveAs(fileName.toString(), true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// ����Ƶ��Ϣд�����ݿ�
			System.out.println("��Ƶ�������");
			ts = session.beginTransaction();
			PlayBack playback = new PlayBack();
			playback.setuName(changeLive.getuName());
			playback.setLiveTime(new Date());
			playback.setFileName(fileName.toString());
			session.save(playback);
			ts.commit();
		}
		session.close();
		super.streamPublishStart(stream);

	}

	@Override
	public void streamBroadcastClose(IBroadcastStream stream) {
		// TODO Auto-generated method stub
		System.out.println("�㲥�ر�,ɾ��ֱ����Ϣ");
		System.out.println("�ҵ�����ֱ�������û����ı䵱ǰ����״̬");
//		Session session = sessionfactory.openSession();
//		Transaction ts = session.beginTransaction();
//		Criteria c = session.createCriteria(Live.class);
//		List<Live> live;
//		c.add(Restrictions.eq("streamName", stream.getPublishedName()));
		
		super.streamBroadcastClose(stream);

	}

	@Override
	public void streamBroadcastStart(IBroadcastStream stream) {
		// TODO Auto-generated method stub
		super.streamBroadcastStart(stream);
	}

}
