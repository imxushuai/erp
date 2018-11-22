package cn.itcast.erp.util;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 发送邮件工具
 * Author xushuai
 * Description
 */
public class MailUtil {
    private JavaMailSender sender;//Spring封装的发送邮件类
    private String from;//发件人


    /**
     * 发送邮件方法
     *
     * @param to      收件人
     * @param subject 主题
     * @param text    邮件内容
     */
    public void sendMail(String to, String subject, String text) throws MessagingException {
        // 创建邮件
        MimeMessage mimeMessage = sender.createMimeMessage();
        // 邮件包装工具
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        // 发件人
        helper.setFrom(from);
        // 设置收件人
        helper.setTo(to);
        // 设置邮件主题
        helper.setSubject(subject);
        // 设置邮件内容
        helper.setText(text);

        //发送邮件
        sender.send(mimeMessage);
    }

    public void setSender(JavaMailSender sender) {
        this.sender = sender;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}

