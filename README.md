✨ [블로그 바로가기](https://doteloper.tistory.com/120) ✨ 

### 개발환경

- SpringBoot 3.1.4
- kotlin / java 17

## build.gradle.kts 설정

사용할 `springframework mail` 설정 추가

```kotlin
implementation("org.springframework.boot:spring-boot-starter-mail")
```

## application.yml 설정

메일 전송에 필요한 `smtp` 설정

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: YOUR_GMAIL_EMAIL
    password: YOUR_GMAIL_APP_PASSWORD
    properties:
      mail:
        smtp:
          auth: true
          timeout: 5000
          starttls:
            enable: true
```

- `host` `port`: 메일 전송에 사용할 SMTP 서버 호스트 및 포트
    - gmail을 사용한다면 위 설정 그대로 사용
- `password`: gmail 2차 비밀번호 설정에 있는 **앱 비밀번호** 입력
- `properties`: 사용하는 smtp 서버에 대한 세부 설정
    - gmail의 경우 [Gmail SMTP settings](https://support.google.com/mail/answer/7104828?hl=en&ref_topic=7280141&sjid=9320346727492655609-AP) 를 참고

# MailSender 구현

### Mail Server Properties

- `MailSender Interface`: 메일 전송의 기본적인 구조를 가지고 있는 최상위 인터페이스
- `JavaMailSender Interface`: `MailSender` 의 하위 인터페이스. MIME 메세지를 지원하며, `MimeMessageHelper` 클래스와 함께 `MimeMessage`를 생성.
- `JavaMailSenderImpl class`: `JavaMailSender` 인터페이스의 구현체. MimeMessage와 SimpleMailMessage를 지원
- `SimpleMailMessage` 클래스: from, to, cc, subject, text를 포함한 simple mail message를 만드는데 사용
- `MimeMessagePreparator` 인터페이스: MIME 메세지를 준비하는 콜백 인터페이스 제공
- `MimeMessageHelper` 클래스: MIME 메세지 생성을 도와주는 클래스. 이미지, 일반적인 메일 첨부 파일 및 HTML 레이아웃의 텍스트 콘텐츠를 지원

### MailSender

```kotlin
@Component
class MailSender(
    private val mailSender: JavaMailSender  // Autowired
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
```

- `SimpleMailMessage()` : 이미지 및 첨부파일, HTML Layout이 필요하지 않으므로, 간단히 구현할 수 있는 SimpleMailMessage 사용
- `MimeMessageHelper` : `createMimeMessage`를 이용하여 MimeMessage를 생성하고, Helper를 통해 실제 내부 컨텐츠를 주입
- `from`: 필수적이진 않지만, from이 존재하지 않는경우 대부분의 SMTP 서버들이 Message를 Reject할 수 있음
