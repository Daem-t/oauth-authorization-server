package com.daem.oauth.interfaces.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.Random;

@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    private static final int WIDTH = 120, HEIGHT = 40;
    private static final int CODE_LENGTH = 4;
    private static final String CHAR_POOL = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final Random random = new Random();

    private final IpLimitService ipLimitService;
    private final Cache captchaCache;

    public CaptchaController(IpLimitService ipLimitService, CacheManager cacheManager) {
        this.ipLimitService = ipLimitService;
        this.captchaCache = Objects.requireNonNull(cacheManager.getCache(CacheConfig.CAPTCHA_CACHE));
    }

    @GetMapping
    public ResponseEntity<?> generateCaptcha(HttpServletRequest request) throws IOException {
        String ip = request.getRemoteAddr();
        if (!ipLimitService.isAllowed(ip)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("message", "请求过于频繁，请稍后再试"));
        }

        String code = generateRandomCode();
        String id = UUID.randomUUID().toString();
        captchaCache.put(id, code);

        BufferedImage image = generateCaptchaImage(code);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());

        return ResponseEntity.ok(Map.of("captchaId", id, "image", "data:image/png;base64," + base64));
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }

    private BufferedImage generateCaptchaImage(String code) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 填充背景
        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制干扰线
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 20; i++) {
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 绘制验证码
        g.setFont(new Font("Arial", Font.BOLD, 32));
        for (int i = 0; i < code.length(); i++) {
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            
            // 随机旋转角度
            AffineTransform old = g.getTransform();
            double theta = (random.nextBoolean() ? 1 : -1) * random.nextDouble() * 0.5; // 旋转角度
            g.rotate(theta, 20 * i + 15, HEIGHT / 2.0 + 10);
            g.drawString(String.valueOf(code.charAt(i)), 20 * i + 10, HEIGHT - 10);
            g.setTransform(old);
        }

        g.dispose();
        return image;
    }

    private Color getRandColor(int fc, int bc) {
        if (fc > 255) fc = 255;
        if (bc > 255) bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}

 