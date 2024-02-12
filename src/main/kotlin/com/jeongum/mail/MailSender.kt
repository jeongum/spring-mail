package com.jeongum.mail

import org.springframework.core.io.FileSystemResource
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import java.io.File

@Component
class MailSender(
    private val mailSender: JavaMailSender
) {

    fun sendSimpleMail(to: String, subject: String, content: String) {
        val message = SimpleMailMessage().apply {
            from = "YOUR_GMAIL_MAIL@gmail.com"
            setTo(to)
            setSubject(subject)
            text = content
        }

        mailSender.send(message)
    }

    fun sendMailWithAttachment(to: String, subject: String, content: String, pathToAttachment: String) {
        val message = mailSender.createMimeMessage()

        MimeMessageHelper(message).apply {
            setFrom("YOUR_GMAIL_MAIL@gmail.com")
            setTo(to)
            setSubject(subject)
            setText(content)
            addAttachment("file", FileSystemResource(File(pathToAttachment)))
        }

        mailSender.send(message)
    }
}
