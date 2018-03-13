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
	// 静态代码块，初始化Hibernate与数据库的连接，加载Hibernate配置
	static {
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.cfg.xml");
		sessionfactory = configuration.buildSessionFactory();
	}

	/** {@inheritDoc} */
	@Override
	public boolean connect(IConnection conn, IScope scope, Object[] params) {
		// log.info("appConnect");
		System.out.println("连接成功...");
		this.contextPath = scope.getContextPath();

		return true;
	}

	/** {@inheritDoc} */
	@Override
	public void disconnect(IConnection conn, IScope scope) {
		// log.info("disconnect");
		System.out.println("连接断开");

		super.disconnect(conn, scope);
	}

	@Override
	public void streamPublishStart(IBroadcastStream stream) {
		// TODO Auto-generated method stub
		System.out.println("开始发布并存储,存储的文件名为日期加流名称");
		// 先检查当前流是否是已注册用户的流
		System.out.println("检查当前流是否来源于已知用户");
		Session session = sessionfactory.openSession();
		Transaction ts = session.beginTransaction();
		Criteria c = session.createCriteria(Live.class);
		c.add(Restrictions.eq("streamName", stream.getPublishedName()));
		List<Live> live = (List<Live>)c.list();
		ts.commit();
		if (live.size() == 0) {
			System.out.println("当前流(" + stream.getPublishedName() + ")的用户没有注册，直播间不予显示");
		} else {
			String uName = live.get(0).getuName();
			System.out.println("当前流(" + stream.getPublishedName() + ")的用户为:" + uName);
			System.out.println("改变直播间状态为在播");
			Live changeLive = live.get(0);
			changeLive.setIsOpen(true);
//			修改提交
			ts = session.beginTransaction();
			session.save(changeLive);
			ts.commit();
			
			//下面为存储回放文件
			// 建立string builder 准备拼接文件名称
			StringBuilder fileName = new StringBuilder();
			// 格式化日期
			SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd");
			// 产生文件名yy-MM-dd-streamName
			fileName.append(dateFormate.format(new Date())).append("-").append(stream.getPublishedName());
			// 保存流,同一天不同时间的直播流叠加存储于当天的文件中
			try {
				stream.saveAs(fileName.toString(), true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 将视频信息写入数据库
			System.out.println("视频数据入库");
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
		System.out.println("广播关闭,删除直播信息");
		System.out.println("找到发布直播流的用户并改变当前在线状态");
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
