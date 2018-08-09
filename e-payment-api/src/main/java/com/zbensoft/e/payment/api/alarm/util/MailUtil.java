package com.zbensoft.e.payment.api.alarm.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

public class MailUtil {

	public static final String ALARM_FILE_NAME = "email-alarm.properties";
	public static final String EPAY_FILE_NAME = "email-epay.properties";
	public static final String NEW_LINE = "<br/>";

	/**
	 * 获取共通消息
	 * 
	 * @param fileName
	 *            配置文件名称
	 * @return
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 * @throws AddressException
	 */
	public static Message getMessage(String fileName) throws AddressException, UnsupportedEncodingException, MessagingException {
		Properties properties = getProperty(fileName);
		Session session = getSession(properties, getAuthenticator(properties));
		// 设置消息体
		Message message = new MimeMessage(session);
		String from = properties.getProperty("mail.username");
		String fromrename = properties.getProperty("mail.username.rename");

		message.setFrom(new InternetAddress("\"" + MimeUtility.encodeText(fromrename) + "\" <" + from + ">"));
		return message;

		// message.setFrom(new InternetAddress("\"" + MimeUtility.encodeText(ADMIN_NAME) + "\" <" + ADMIN_USERNAME + ">"));
		// message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toList.toString().substring(1, toList.toString().length() - 1)));
		// message.setSubject(subject);
	}

	/**
	 * 得到session
	 * 
	 * @param props
	 *            初始化系统属性
	 * @param auth
	 *            邮件服务器用户认证
	 * @return
	 */
	private static Session getSession(Properties props, Authenticator auth) {
		boolean SESSION_DEBUG = false;
		String SESSION_DEBUGSTR = props.getProperty("mail.session.debug");
		if (SESSION_DEBUGSTR != null && SESSION_DEBUGSTR.equalsIgnoreCase("true")) {
			SESSION_DEBUG = true;
		}

		Session session = Session.getInstance(props, auth);
		session.setDebug(SESSION_DEBUG);
		return session;
	}

	/**
	 * 初始化系统属性
	 * 
	 * @return
	 */
	private static Properties getProperty(String fileName) {
		Properties properties = new Properties();
		try {
			properties.load(MailUtil.class.getClassLoader().getResourceAsStream(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	/**
	 * 邮件服务器用户认证
	 * 
	 * @param username
	 *            系统验证用户名
	 * @param password
	 *            系统验证密码
	 * @return
	 */
	private static Authenticator getAuthenticator(final Properties properties) {
		return new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				String username = properties.getProperty("mail.username");
				String password = properties.getProperty("mail.password");
				return new PasswordAuthentication(username, password);
			}
		};
	}

	public static Address[] parse(List<String> list) throws AddressException {
		return InternetAddress.parse(list.toString().substring(1, list.toString().length() - 1));
	}

	public static void sendEmail(String fileName, String subject, List<String> to, List<String> cc, List<String> bcc, String content) throws MessagingException, UnsupportedEncodingException {

		Message message = getMessage(fileName);
		message.setRecipients(Message.RecipientType.TO, parse(to));
		if (cc != null && cc.size() > 0) {
			message.setRecipients(Message.RecipientType.CC, parse(cc));
		}
		if (bcc != null && bcc.size() > 0) {
			message.setRecipients(Message.RecipientType.BCC, parse(bcc));
		}
		message.setSubject(subject);
		message.setContent(doContent(content), "text/html;charset=utf-8");
		Transport.send(message);
	}

	private static String doContent(String content) {
		// content.replaceAll("\n", NEW_LINE);
		String end = "\n";
		StringBuffer sb = new StringBuffer();
		sb.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">").append(end);
		sb.append("<html>").append(end);
		sb.append("<body>").append(end);

		sb.append("<table width=\"100%\" style=\" border-collapse:collapse;\" border=\"0\">").append(end);

		sb.append("<tr>").append(end);
		sb.append("<td>&nbsp;</td>").append(end);
		sb.append("</tr>").append(end);

		sb.append("<tr>").append(end);
		sb.append("<td>").append(content).append("</td>").append(end);
		sb.append("</tr>").append(end);

		sb.append("<tr>").append(end);
		sb.append("<td>&nbsp;</td>").append(end);
		sb.append("</tr>").append(end);
		sb.append("<tr>").append(end);
		sb.append("<td>----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</td>").append(end);
		sb.append("</tr>").append(end);

		sb.append("<tr>").append(end);
		sb.append("<td>Nota: El presente correo es enviado automáticamente por nuestro sistema, por favor, no responda, ni reenvíe mensajes a esta cuenta.</td>").append(end);
		sb.append("</tr>").append(end);


		sb.append("<tr>").append(end);
		sb.append("<td>----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</td>").append(end);
		sb.append("</tr>").append(end);

		sb.append("<tr>").append(end);
		sb.append("<td>Note: This email is sent automatically by our system, please do not respond, or forward messages to this account.</td>").append(end);
		sb.append("</tr>").append(end);
		
		
		sb.append("</table>").append(end);

		sb.append("</body>").append(end);
		sb.append("</html>").append(end);
		return sb.toString();
	}

	public static void sendEmail(String alarmFileName, String subject, String recvPersonMail, String ccPersonMail, String bccPersonMail, String content) throws UnsupportedEncodingException, MessagingException {
		List<String> toList = getEmailList(recvPersonMail);
		List<String> ccList = getEmailList(ccPersonMail);
		List<String> bccList = getEmailList(bccPersonMail);
		sendEmail(alarmFileName, subject, toList, ccList, bccList, content);
	}

	private static List<String> getEmailList(String personMail) {
		List<String> list = new ArrayList<String>();
		if (personMail != null && personMail.length() > 0) {
			String[] mails = personMail.split(",");
			if (mails != null && mails.length > 0) {
				for (String mail : mails) {
					list.add(mail);
				}
			}
		}
		return list;
	}

	public static void main(String[] args) throws AddressException, UnsupportedEncodingException, MessagingException {
		StringBuffer sb = new StringBuffer();
		sb.append(AlarmUtil.getIPInfo());
		sb.append("Start Server now,The Server Path is ").append(System.getProperty("user.dir")).append(MailUtil.NEW_LINE).append(MailUtil.NEW_LINE);
		sb.append("IF It is a unknow start.Please check the server!").append(MailUtil.NEW_LINE).append(MailUtil.NEW_LINE);
		
		List<String> toList = new ArrayList<String>();
		toList.add("hongwangcolin@sina.com");
  	//sendEmail(MailUtil.ALARM_FILE_NAME, "Alarm-cantv-epay", toList, null, null, sb.toString());
   	sendEmail(MailUtil.EPAY_FILE_NAME, "cantv-epay", toList, null, null, "password is change to 111111");
	}

}
