## Проблема
Flyway seed містив Unsplash URL які повертали 404.
Фронтенд не бачив реальних зображень і мокав їх вручну,
що призводило до помилок з ресайзом.

## Рішення
- Додано локальні зображення в `src/main/resources/bootstrap/image/`
- `SeedImageImporter` завантажує їх при старті через `ApplicationRunner`
- Зображення проходять через існуючий `ImageProcessing` та `StorageService`
- Активується тільки на профілі `local` при `seed.images.enabled=true`
- Виправлено `LocalStorageService.upload()` — прибрано залежність від `HttpServletRequest`

## Що не чіпав
- Логіка `ProductImageService` не змінювалась
- Flyway міграції не змінювались