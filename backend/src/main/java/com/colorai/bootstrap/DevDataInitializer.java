package com.colorai.bootstrap;

import com.colorai.domain.SavedPalette;
import com.colorai.domain.User;
import com.colorai.repository.PaletteRepository;
import com.colorai.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 仅 dev 环境生效：初始化一个演示账号与若干示例调色板，便于线上 Demo 直接体验。
 * 演示账号：demo / demo123（生产部署请删除或改用强密码）。
 */
@Component
@Profile("dev")
public class DevDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PaletteRepository paletteRepository;
    private final PasswordEncoder passwordEncoder;

    public DevDataInitializer(UserRepository userRepository, PaletteRepository paletteRepository,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.paletteRepository = paletteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        User demo = new User("demo", "demo@colorai.dev", passwordEncoder.encode("demo123"));
        demo.setDisplayName("演示用户");
        demo = userRepository.save(demo);

        List<SavedPalette> samples = Arrays.asList(
                sample(demo, "晨雾蓝调", "#A8DADC,#457B9D,#1D3557,#F1FAEE,#E63946", "web", "清爽科技风网页主色"),
                sample(demo, "暖橘生活", "#FFB703,#FB8500,#023047,#8ECAE6,#219EBC", "brand", "活力品牌识别"),
                sample(demo, "莫兰迪高级灰", "#B0A8B9,#A7BBC4,#D4C5B9,#8E9AAF,#CBC0D3", "interior", "轻奢室内空间"),
                sample(demo, "森系自然", "#386641,#6A994E,#A7C957,#F2E8CF,#BC4749", "nature", "自然主题插画"),
                sample(demo, "复古胶片", "#E07A5F,#3D405B,#81B29A,#F2CC8F,#F4F1DE", "poster", "复古海报配色")
        );
        paletteRepository.saveAll(samples);
    }

    private SavedPalette sample(User user, String name, String colors, String scenario, String desc) {
        SavedPalette p = new SavedPalette();
        p.setUser(user);
        p.setName(name);
        p.setColors(colors);
        p.setScenario(scenario);
        p.setDescription(desc);
        p.setTags(Arrays.asList(scenario, "示例"));
        return p;
    }
}
