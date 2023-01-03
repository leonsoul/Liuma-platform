package com.autotest.LiuMa.common.utils;
import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Properties;

/**
 * @author bene
 * QQ 邮箱发送
 */
public class EmailUtils {
    /**
     * Receiver 收件人地址，title邮件主题，content邮件正文
     */
    private static final String SENDER = "leon.jack@qq.com";
    private static final String AUTH_CODE = "hgqaskhkxihsbhae";
    public static void sendMail(String receiver, String title, String content){
        //1、创建连接对象，连接到邮箱服务器
        try {
            Properties prop = new Properties();
            // 开启debug调试，以便在控制台查看
            prop.setProperty("mail.debug", "true");
            // 设置邮件服务器主机名
            prop.setProperty("mail.host", "smtp.qq.com");
            // 发送服务器需要身份验证
            prop.setProperty("mail.smtp.auth", "true");
            // 发送邮件协议名称
            prop.setProperty("mail.transport.protocol", "smtp");

            // 开启SSL加密，否则会失败
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            prop.put("mail.smtp.ssl.enable", "true");
            prop.put("mail.smtp.ssl.socketFactory", sf);

            // 创建session
            Session session = Session.getInstance(prop,new Authenticator(){
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER,AUTH_CODE);
                }
            });
            // 创建邮件
            //2、创建邮件对象
            Message message = new MimeMessage(session);
            //2.1、设置发件人
            message.setFrom(new InternetAddress(SENDER));
            //2.2、设置收件人
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
            //2.3设置邮件的主题
            message.setSubject(title);
            //2.4、设置邮件正文
            message.setContent(content, "text/html;charset=UTF-8");
            Transport.send(message);
        } catch (MessagingException | GeneralSecurityException e) {
            System.out.println("ErrCode : " + e.getMessage());
            e.printStackTrace();
        }
    }
}