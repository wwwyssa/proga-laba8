1. Создайте файл dbconfig.properties, в котором будет следующее содержание:
```
db.url=
db.user=
db.password=
```
2. Скачать JavaFX [по ссылке](https://download2.gluonhq.com/openjfx/17.0.15/openjfx-17.0.15_windows-x64_bin-sdk.zip)
3. Распаковать JavaFX в директорию lib
3. Для запуска клиента использовать
```java --module-path ".\lib\javafx-sdk-17.0.15\lib" --add-modules javafx.controls,javafx.fxml -jar .\lab-client\target\lab-client-1.jar```

> Вручную указываем путь до javafx из-за проблем с загрузкой JavaFX через Maven (на другой сборщик переходить лень).
