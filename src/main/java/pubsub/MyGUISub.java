package pubsub;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.BasicConfigurator;

import data.Person;
import helper.XMLConvert;

public class MyGUISub {

	JTextField tf;
	JTextArea ta;
	String noiDung = "";

	public static void main(String[] args) {
		MyGUISub gui = new MyGUISub();

	}

	public void guiNe() throws Exception {
//		
		// thiết lập môi trường cho JMS logging
		BasicConfigurator.configure();
//thiết lập môi trường cho JJNDI
		Properties settings = new Properties();
		settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
//tạo context
		Context ctx = new InitialContext(settings);
//lookup JMS connection factory
		Object obj = ctx.lookup("TopicConnectionFactory");
		ConnectionFactory factory = (ConnectionFactory) obj;
//tạo connection
		Connection con = factory.createConnection("admin", "admin");
//nối đến MOM
		con.start();
//tạo session
		Session session = con.createSession(/* transaction */false, /* ACK */Session.AUTO_ACKNOWLEDGE);
		Destination destination = (Destination) ctx.lookup("dynamicTopics/thanthidet");
//		Destination destination = session.createQueue("dynamicTopics/hoangminhtri");
//tạo producer
		MessageProducer producer = session.createProducer(destination);
//Tạo 1 message
		Message msg = session.createTextMessage("");
//gửi
		producer.send(msg);
//shutdown connection
		session.close();
		con.close();
		System.out.println("Finished...");
	}

	public MyGUISub() {
		JFrame frame = new JFrame("Sub");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);

		// Creating the MenuBar and adding components
		JMenuBar mb = new JMenuBar();
		JMenu m1 = new JMenu("FILE");
		JMenu m2 = new JMenu("Help");
		mb.add(m1);
		mb.add(m2);
		JMenuItem m11 = new JMenuItem("Open");
		JMenuItem m22 = new JMenuItem("Save as");
		m1.add(m11);
		m1.add(m22);

		// Creating the panel at bottom and adding components
		JPanel panel = new JPanel(); // the panel is not visible in output
		JLabel label = new JLabel("Enter Text");
		tf = new JTextField(10); // accepts upto 10 characters
		JButton send = new JButton("Send");
		JButton reset = new JButton("Reset");
		panel.add(label); // Components Added using Flow Layout
		panel.add(tf);
		panel.add(send);
		panel.add(reset);

		// Text Area at the Center
		ta = new JTextArea();

		// Adding Components to the frame.
//		frame.getContentPane().add(BorderLayout.SOUTH, panel);
		frame.getContentPane().add(BorderLayout.NORTH, mb);
		frame.getContentPane().add(BorderLayout.CENTER, ta);
		frame.setVisible(true);

		try {
			guiNe();
			nhan();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String ms = tf.getText().toString();
				try {
					guiNe();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
//				ta.setText(ms);

			}
		});
	}

	public void nhan() throws Exception {
//		
		// thiết lập môi trường cho JMS
		BasicConfigurator.configure();
		// thiết lập môi trường cho JJNDI
		Properties settings = new Properties();
		settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
		// tạo context
		Context ctx = new InitialContext(settings);
		// lookup JMS connection factory
		Object obj = ctx.lookup("TopicConnectionFactory");
		ConnectionFactory factory = (ConnectionFactory) obj;
		// tạo connection
		Connection con = factory.createConnection("admin", "admin");
		// nối đến MOM
		con.start();
		// tạo session
		Session session = con.createSession(/* transaction */false, /* ACK */Session.AUTO_ACKNOWLEDGE);
		// tạo consumer
		Destination destination = (Destination) ctx.lookup("dynamicTopics/thanthidet");
		MessageConsumer receiver = session.createConsumer(destination);
		// receiver.receive();//blocked method
		// Cho receiver lắng nghe trên queue, chừng có message thì notify
		receiver.setMessageListener(new MessageListener() {
			@Override
			// có message đến queue, phương thức này được thực thi
			public void onMessage(Message msg) {// msg là message nhận được
				try {
					if (msg instanceof TextMessage) {
						TextMessage tm = (TextMessage) msg;
						String txt = tm.getText();
						ta.setText(txt);
						msg.acknowledge();// gửi tín hiệu ack
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}