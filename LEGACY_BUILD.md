# Rupoop — legacy-сборка для старых планшетов (Android 5.0–7.x, API 21–25)

Эта ветка адаптирована под старые планшеты Android 6.0–7.x (API 23–25).

> Примечание: в текущем maven-окружении все артефакты Jetpack Compose (1.10.x) объявляют minSdk 23, поэтому Android 5.x (API 21–22) assembling-ом не поддержать — минимальный уровень поднят до API 23.

## Что изменено относительно оригинала

| Параметр | Было | Стало |
|---|---|---|
| minSdk | 28 | **23** |
| targetSdk | 37 | **25** |
| compileSdk | 37 | 35 |
| Gradle | 9.8.1 | **8.9** |
| AGP | 9.2.1 | **8.7.2** |
| Kotlin | 2.4.0 | **2.0.21** |
| Compose BOM | 2026.06.01 | **2024.10.01** (последний с minSdk 21) |
| Media3 | 1.10.1 | **1.4.1** (minSdk 21) |
| OkHttp | 5.x | **4.12.0** (minSdk 21) |
| core-ktx / lifecycle / activity-compose / WorkManager / security-crypto | свежие | **1.10.1 / 2.6.2 / 1.8.2 / 2.9.1 / 1.0.0** |

Дополнительно включено **coreLibraryDesugaring** (`desugar_jdk_libs 2.1.2`) — обязательно для java.time / stream API на API < 26.

## Сборка

Требуется **JDK 17** и Android SDK (platform 35, build-tools):

```bash
./gradlew assembleDebug        # или assembleRelease при наличии keystore
```

APK появится в `app/build/outputs/apk/`.

## Что будет работать на Android 5–7

- Весь UI на Jetpack Compose, списки, поиск, Rutube/VK/OK/Lordfilm, HLS-плеер (Media3 1.4.1 поддерживает API 21+), скачивание, синхронизация Gist.

## Известные ограничения на старых прошивках

- **PiP** (картинка в картинке) — только Android 8.0+ (API 26); на 5–7 кнопка PiP неактивна.
- **Фоновый плеер** через MediaSession работает, но без расширенных уведомлений Android 13+.
- Разрешение POST_NOTIFICATIONS — только Android 13+ (в коде уже есть проверки `Build.VERSION.SDK_INT`).
- На Android 10+ запись в общие папки идёт через MediaStore; на 5–7 используется WRITE_EXTERNAL_STORAGE (уже учтено в `RutubeApp.kt`).
