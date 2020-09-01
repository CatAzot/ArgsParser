# KArgsParser (beta)
Библиотека на языке Kotlin для парсинга аргументов командной строки.
## Установка
1. Склонировать репозиторий:
    >git clone https://github.com/neakmobi/kargsparser.git
2. Перейти в директорию с проектом и запустить сборку:
    >cd kargsparser && ./gradlew jar
3. В папке kargsparser/build/libs появится файл kargsparser-0.1.jar, который подключается к проекту любым удобным способом
## Использование
Пример использования можно посмотреть в проекте [TexturePacker](https://github.com/neakmobi/texturepacker) либо в [файле](https://github.com/neakmobi/kargsparser/blob/master/src/test/kotlin/argsparsertest/KArgsParserTest.kt) с тестами
## Возможности
* Разбор ключей (пример: programName -k --key)
* Разбор опций с параметрами (пример: programName -s singleParam --multiParams firstParam secondParam)
* Разбор опций с нефиксированным числом параметров. Парсинг будет производится до следующего ключа или опции, либо до конца всех аргументов (пример: programName --unfix param1 param2 param3 -u paramA paramB)
* Установка приоритетов опциям, т.е. определение порядка поиска опций (ключей) (например, искать в первую очередь ключ -h или --help)
* Построение подсказки по использованию программы (по ключу --help)
* Разбор неименованных аргументов. Всё, что не является опцией, будет разобрано как параметры
## Документация
Её можно посмотреть в папке javadoc. На данный момент она не полная, будет дополняться.