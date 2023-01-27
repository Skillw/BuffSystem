//@EffectType(boss_bar)
//文件注解顶头写

//这个Effect的作用是： 给玩家显示BossBar

Player = find("org.bukkit.entity.Player");
BarStyle = org.bukkit.boss.BarStyle;
BarColor = org.bukkit.boss.BarColor;
BarFlag = org.bukkit.boss.BarFlag;
Coerce = static("Coerce");

function realize(entity, data, map) {
    if (!(entity instanceof Player)) return;
    const handled = data.handle(map, []);
    const title = handled.getOrDefault("title", "你妹填title");
    // PINK,
    // BLUE,
    // RED,
    // GREEN,
    // YELLOW,
    // PURPLE,
    // WHITE
    const color = BarColor.valueOf(handled.getOrDefault("color", "BLUE"));
    //  SOLID,
    // SEGMENTED_6,
    // SEGMENTED_10,
    // SEGMENTED_12,
    // SEGMENTED_20
    const style = BarStyle.valueOf(handled.getOrDefault("style", "SOLID"));
    // progress∈[0,1]
    const progress = handled.getOrDefault("progress", "0.75");
    const isVisible = Coerce.toBoolean(handled.getOrDefault("visible", "true"));
    const flags = handled.getOrDefault("flags", listOf("PLAY_BOSS_MUSIC"));
    const key = data.uniqueId + "bossbar-effect";
    var bossBar = Data.get(key);
    if (bossBar == null) {
        bossBar = PlayerUtils.sendBossBar(entity, title, color, style, progress);
    }
    bossBar.setTitle(title);
    bossBar.color = color;
    bossBar.style = style;
    bossBar.progress = progress;
    bossBar.isVisible = isVisible;

    var allFlags = BarFlag.values;
    for (index in allFlags) {
        let flag = allFlags.get(index);
        if (flags.contains(flag.name)) {
            bossBar.addFlag(flag);
        } else {
            bossBar.removeFlag(flag);
        }
    }
    Data.put(key, bossBar);
}

function unrealize(entity, data, map) {
    const bossBar = Data.get(data.uniqueId + "bossbar-effect");
    if (bossBar == null) return;
    bossBar.removeAll();
}
