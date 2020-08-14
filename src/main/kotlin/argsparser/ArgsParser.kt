package argsparser

import java.util.*

/**
 *
 * Реализация парсера аргументов командной строки
 *
 * @property programName        Имя программы. Используется при формировании подсказки по ключу --help
 * @property programVersion     Версия программы. Используется при формировании подсказки по ключу --help
 * @property helpPreamble       Преамбула подсказки по ключу --help
 * @property applyParams        Функция, обрабатывающая параметры командной строки
 * @property helpConclusion     Заключение подсказки по ключу --help
 * @property helpUsage          Подсказка по использованию опций. Если пользователь не предоставляет свою подсказку,
 *                              формируется автоматически
 * @property helpOptions        Подсказка для опций. Если не задана, подсказка по опциям формируется автоматически
 * @property customHelpOption   Флаг, указывающий на необходимость создания кастомной опции помощи
 *
 */
class ArgsParser(
        private val programName         : String,
        private val programVersion      : String,
        private val helpPreamble        : String,
        private val helpConclusion      : String,
        private val applyParams         : (Array<String>) -> Boolean = { false },
        private var helpUsage           : String = "",
        private var helpOptions         : String = "",
        private var customHelpOption    : Boolean = false
) {

    /**
     *
     * @property options             Опции в аргументах командной строки
     * @property requiredControl     Количество опций с флагом "Обязательная"
     * @property helpRequested       Флаг, указывающий что была запрошена помощь
     * @property badOption           Опция, на которой произошла ошибка парсинга
     *
     */
    private val options = LinkedList<AOption>()
    private var requiredControl = 0
    private var helpRequested = false
    var badOption: AOption? = null

    /**
     *
     * Добавление опции к списку ожидаемых опций
     *
     * @param option Добавляемая опция.
     *
     */
    fun addOption(option: AOption) {
        if(option.required) requiredControl++
        options.add(option)
    }

    /**
     *
     * Парсинг аргументов командной строки.
     *
     * @param args Массив аргументов команды запуска
     *
     */
    fun parseArgs(args: Array<String>): ParseResult {

        if(args.isEmpty())
            return if(requiredControl == 0) ParseResult.OK else ParseResult.EMPTY_ARGS

        val argsList = args.toMutableList()

        splitChainKeys(argsList)

        if(!customHelpOption) {
            options.add(Key(
                    "-h",
                    "--help",
                    "Show this message",
                    Int.MIN_VALUE,
                    false
            ) { println(buildHelp()); helpRequested = true; true })
        }

        options.sortBy { it.priority }

        val applyResult = applyOptions(argsList)
        if(applyResult != ParseResult.OK)
            return applyResult

        if(helpRequested) return ParseResult.HELP_REQUESTED

        if(!applyParams(argsList.toTypedArray()))
            return ParseResult.INVALID_PARAMS

        return ParseResult.OK

    }

    /**
     *
     * Формирование подсказки (по ключу --help или -h)
     *
     * @return Сформированная подсказка
     *
     */
    fun buildHelp(): String {
        return  "\n" + programName  + "\n"   +
                programVersion      + "\n\n" +
                helpPreamble        + "\n\n" +
                helpUsage           + "\n\n" +
                buildHelpOptions()  + "\n"   +
                helpConclusion      + "\n"
    }

    private fun applyOptions(argsList: MutableList<String>): ParseResult {

        var parseResult: ParseResult
        for(option in options) {
            parseResult = option.apply(argsList)
            if(parseResult != ParseResult.OK) {
                badOption = option
                return parseResult
            }
            if(option.required) requiredControl--
        }

        if(requiredControl != 0)
            return ParseResult.MISSING_REQUIRED_OPTIONS

        return ParseResult.OK
    }

    /**
     *
     * Формирование подсказки для опций
     *
     * @return Подсказка по использованию опций
     *
     */
    private fun buildHelpOptions(): String {
        val retStr = StringBuilder()
        retStr.append("Options:\n")
        for(option in options) {
            retStr.append(option.help)
            retStr.append("\n")
        }
        return retStr.toString()
    }

    /**
     *
     * Разбитие связки ключей на отдельные ключи (если связки есть)
     *
     */
    private fun splitChainKeys(argsList: MutableList<String>) {
        val chainKeysRegex = "^[-][^-]..*".toRegex()
        val chainKeys = argsList.find { it.matches(chainKeysRegex) } ?: return
        argsList.remove(chainKeys)
        for(idx in 1 until chainKeys.length)
            argsList.add("-${chainKeys[idx]}")
    }

}