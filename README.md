# KArgsParser (beta)
Библиотека на языке Kotlin для парсинга аргументов командной строки.
## Установка
1. Склонировать репозиторий:
```
user@userpc:~$ git clone https://github.com/neakmobi/kargsparser.git
```
2. Перейти в директорию с проектом и запустить сборку:
```
user@userpc:~$ cd kargsparser && ./gradlew jar
```
3. В папке `kargsparser/build/libs` появится файл `kargsparser-{VERSION}.jar`, который подключается к проекту любым удобным способом
## Возможности
* Разбор ключей (пример: `user@userpc:~$ programName -k --key`).
* Разбор связки ключей (пример: `user@userpc:~$ programName -xvf`).
* Разбор опций с параметрами (пример: `user@userpc:~$ programName -s singleParam --multiParams firstParam secondParam`).
* Разбор опций с нефиксированным числом параметров. Парсинг будет производится до следующего ключа или опции, либо до конца всех аргументов командной строки (пример: `user@userpc:~$ programName --unfix param1 param2 param3 -u paramA paramB`).
* Установка приоритетов опциям, т.е. определение порядка поиска опций (ключей) (например, искать в первую очередь ключ `-h` или `--help`)
* Построение подсказки по использованию программы (по ключу `--help`).
* Разбор неименованных аргументов. Всё, что не является опцией, будет разобрано как параметры.
## Использование
#### Инициализация парсера

Парсер может быть проинициализирован следующей конструкцией:

```kotlin
private val clParser = KArgsParser(
    programInfo         = "PROGRAM_INFO",
    helpUsage           = "HELP_USAGE",
    helpPreamble        = "HELP_PREAMBLE",
    helpConclusion      = "HELP_CONCLUSION",
    descriptionIndent   = 42
) { params ->
    // some action with params	// 1
    true 
}
```

Все параметры, за исключением последнего, используются только при построении подсказки по использованию программы:

~~~
user@userpc:~$ test_program --help
PROGRAM_INFO
HELP_USAGE

HELP_PREAMBLE

Options:
  -e --exampleOption0	<42 indent>		- option0 description
  -o --exampleOption1	<42 indent>		- option1 description

HELP_CONCLUSION
~~~

Если нет необходимости строить сообщение-подсказку, можно воспользоваться упрощенной конструкцией:

~~~kotlin
val clParser = KArgsParser { 
    params ->
    // some action with params  // 2
    true
}
~~~

В качестве параметра в (1) и (2) должно приниматься лямбда-выражение со следующей сигнатурой:

~~~kotlin
val applyParams: (Array<String>) -> Boolean
~~~

Эта функция будет использована при разборе параметров программы, т.е. всех значений, которые не являются ключами, опциями или их параметрами. Пример использования:

```kotlin
val clParser = KArgsParser {
    params ->
    println(params[0] + params[1] + "!")
    params[2] == "accept"
}

val argsOk = arrayOf("Hello ", "world", "accept")
val parseResultOk = clParser.parseArgs(args_0)
println( if(parseResultError == ParseResult.OK) "OK" else "ERROR" )
// parseResult == ParseResult.OK

val argsError = arrayOf("Bad", " Params", "error")
val parseResultError = clParser.parseArgs(args_1)
println( if(parseResultError == ParseResult.OK) "OK" else "ERROR" )
// parseResult == ParseResult.INVALID_PARAMS
```

В результате выполнения кода выше будет выведено:

```
Hello world!
OK
BadParams
ERROR
```

При инициализации парсера `applyParams` может быть опущен, в таком случае это поле будет проинициализировано следующим значением:

~~~kotlin
val applyParams = { params -> params.isEmpty() }
~~~

С таким значением парсер не обрабатывает параметры и если они есть (остались после разбора опций) возвращает `ParseResult.INVALID_PARAMS`.

После инициализации парсера можно добавить встроенную опцию с подсказкой (вызов программы с ключом `-h` и `--help`) , которая выведет сообщение с помощью и выставит результат парсинга `ParseResult.HELP_REQUEST`. Для добавления такой опции перед разбором опций необходимо присвоить полю `manualHelpOption` значение `false`:

~~~kotlin
val clParser = KArgsParser()
clParser.manualHelpOption = false // option -h OR --help added
~~~

#### Добавление опций парсеру

Для добавления опции парсеру используется следующая функция:

~~~kotlin
val clParser = KArgsParser()
val option: AOption = /* concrete option */
clParser.addOption(option: AOption)
~~~

Объект опции в процессе обработки других опций можно менять, тем самым корректируя поведение парсера на этапе выполнения программы.

Всего можно определить 3 основных типа опций:

 	1. Ключ
 	2. Опция с фиксированным числом параметров
 	3. Опция с нефиксированным числом параметров

##### 1. Добавление ключа

Ключ представляет собой опцию без параметров. Примером ключа может служить опция, присутствующая в практически любой программе:

```
ls -h
cd --help
```

Для определения ключа и добавления его парсеру используется следующая конструкция:

```kotlin
val clParser = KArgsParser()
val helpKey = Key(
    shortName 	= "-h",
    fullName 	= "--help",
    description = "Show help message",
    priority 	= Integer.MIN_VALUE,
    required 	= false,
    action      = { println(clParser.buildHelp()); false }
)
clParser.addOption(helpKey)
```

В результате добавления этой опции к парсеру, если в аргументах командной строки будут встречены значения `-h` или `--help`, то на экран будет выведена подсказка по использованию программы. Лямбда `action` возвращает `false` чтобы прервать дальнейший разбор аргументов.

Обратите внимание, приоритет указан минимальный, т.е. данная опция будет разобрана первой, за исключением случаев наличия других опций с этим же приоритетом.

##### 2. Добавление опции с фиксированным числом параметров

Примерами таких опций могут служить следующие вызовы:

~~~
user@userpc:~$ unknownProg -p 1 --values v0 v1 v2
~~~

В этом вызове 2 опции: `-p1` с одним параметром `"1"` и `--values` с параметрами: `"v0", "v1", "v2"`.

Определить опцию можно двумя способами

  * Опция с одним параметром:

     ```kotlin
     val clParser = KArgsParser()
     val singleParam = FixParamsOption(
         shortName = "-p",
         fullName = "--params",
         description = "Params option"
         priority = Integer.MAX_VALUE,
         required = false,
         usageHelp = "Usage: unknownProg --params value"
         action = { str -> println("Params value: ${str}" ); true }
     )
     clParser.addOption(singleParam)
     ```

     ​	Обратите внимание, в качестве приоритета указано значение `Integer.MAX_VALUE`, т.е. опция будет разобрана одной из последних. 

     ​	В результате применения этой опции к следующему вызову будет выведено сообщение:

     ~~~
     user@userpc:~$ unknownProg --params hello
     Params value: hello
     ~~~

  * Опция с несколькими параметрами:

     ~~~kotlin
     val clParser = KArgsParser()
     clParser.addOption(FixParamsOption(
           shortName = "-m",
           fullName = "--multi_params",
           description = "Multi params option",
           priority = 0,
           required = true,
           usageHelp = "Usage: unknownProg -m 1 2",
           cntParams = 2,
           action = { params ->
               val a = params[0].toInt()
               val b = params[1].toInt()
               println("Sum: ${a + b}")
               true
           })
     )
~~~
     
Обратите внимание, поставлен флаг `required = true`. Если в вызове программы не будет содержаться эта опция, парсер вернет ошибку `ParseResult.MISSING_REQUIRED_OPTION`.
     
Объект опции был создан сразу во время добавления опции, однако стоит отметить что это лишает определенной гибкости в настройке парсера. Поле `required` открыто в `AOption`, таким образом разбор одной опции может менять флаг `required` другой, а открытое поле `action` в классах `Key`, `FixParamsOption` и `UnfixParamsOption` позволяет менять поведение разбора опций во время парсинга параметров (и в целом выполнения программы).
     
В результате опции выше к следующему вызову будет выведено сообщение:
    
    ~~~
    user@userpc:~$ unknownProg -m 35 7
    Sum: 42
    ~~~

##### 3. Опция с нефиксированным числом параметров:

Опция с нефиксированным числом параметров:

~~~kotlin
val clParser = KArgsParser()
val unfixParamsOption = UnfixParamsOption(
    shortName = "-u",
    fullName = "--unfix",
    description = "",
    priority = 0,
    required = false,
    usageHelp = "",
    action = { params ->
        if(params.isEmpty()) return false	// if list of params is empty
        val strBuilder = StringBuilder()
        strBuilder.append("Params: ")
        for (param in params)
        	strBuilder.append(param)
        strBuilder.append("\n")
        println(strBuilder.toString())
        true
    }
)
clParser.addOption(unfixParamsOption)
~~~

Обратите внимание на проверку списка параметров: если он пуст, то это прервет парсинг, что позволит обработать ошибку ввода и предупредить об этом пользователя.

С такой конфигурацией парсера будет получен следующий вывод:

~~~
user@userpc:~$ unknownProg --unfix p1 p2 p3 -k -h
Params: p1 p2 p3

user@userpc:~$ unknownProg --unfix Hello World
Params: Hello World
~~~

Обратите внимание на вызов: разбор опции с нефиксированным числом параметров проводится либо до следующего ключа/опции (первый вызов), либо до конца всех аргументов командной строки (второй вызов).

#### Общие особенности опций

* `shortName` и `fullName` могут быть произвольными, не обязательно начинаться с префиксов '-' и '--'

* Поля `description` и `usageHelp` не определяют поведения парсера и используются только при формировании help-сообщения

* Одно из имен может быть опущено (установлено значение пустой строки). В этом случае у опции будет лишь одно имя.

* Для опции может быть установлена кастомная строка подсказки 

  ~~~kotlin
  option.help = "-h, --help  \t\t  Show help message"
  ~~~

  Для возврата автоматической подсказки:

  ~~~kotlin
  option.help = ""
  ~~~

### Парсинг аргументов командной строки

Небольшая участок кода, демонстрирующий алгоритм использования парсера:

~~~kotlin
fun main(args: Array<String>) {
    val clParser = KArgsParser(/* parser params */)
    clParser.addOption(/* some option */)
    // ...
    when(clParser.parseArgs(args)) {
        OK              -> break;
        EMPTY_ARGS      -> println("Error! Args is empty!") return;
        INVALID_OPTION  -> clParser.badOption.errMsg("Some option must be...")
        ...
    }
}
~~~

Обратите внимание на поле `badOption` в `KArgsParser`. Если во время разбора опций произошла ошибка, в этом поле хранится опция, на которой прервался парсинг.

Результатом парсинга может быть одно из следующих значений:

* `ParseResult.OK` - разбор опций произошел без ошибок
* `ParseResult.EMPTY_ARGS` - список аргументов пуст, когда ожидалось обратное
* `ParseResult.HELP_REQUESTED` - запрошена подсказка
* `ParseResult.MISSING_REQUIRED_OPTIONS` - пропущены обязательные опции
* `ParseResult.INVALID_OPTION` - некорректная опция
* `ParseResult.INVALID_PARAMS` - некорректный параметр
* `ParseResult.INVALID_OPTION_PARAMS` - некорректный параметр опции
* `ParseResult.UNKNOWN_ERROR` - на всякий случай :)

Дополнительные примеры использования можно посмотреть в проекте [TexturePacker](https://github.com/neakmobi/texturepacker) либо в [файле](https://github.com/neakmobi/kargsparser/blob/master/src/test/kotlin/argsparsertest/KArgsParserTest.kt) с тестами.

## Документация

Её можно посмотреть в папке javadoc. На данный момент она не полная, будет дополняться.