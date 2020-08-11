package org.argsparser

/**
 *
 * Опция командной строки.
 *
 * @property shortName   Короткое имя опции (Пример: -h).
 * @property fullName    Полное имя опции (Пример: --help).
 * @property description Описание опции. Используется при формировании подсказки.
 * @property priority    Приоритет опции. Чем выше это значение, тем ранее будет обработана опция.
 * @property required    Флаг, указывающий, является ли опция обязательной. Флаг является изменяемым и
 *                       открытым, на случай если он может стать обязательным во время применения других
 *                       опций.
 * @property help        Подсказка по опции.
 *
 */
abstract class AOption(
        protected val shortName   : String,
        protected val fullName    : String,
        protected val description : String,
        val priority              : Int,
        var required              : Boolean,
        var help                  : String = "") {

    init {
        if(help == "")
            help = buildHelp()
    }

    /**
     *
     * Принятие опции.
     *
     * @param args Список аргументов командной строки. После принятия опции, аргументы
     *             удаляются из списка.
     *
     * @return     Результат парсинга опции.
     *
     */
    abstract fun apply(args: MutableList<String>): ParseResult

    /**
     *
     * Построение подсказки по опции.
     *
     * @return Строка с подсказкой.
     *
     */
    private fun buildHelp(): String = "\t$shortName, $fullName\n\t\t\t$description"

    /**
     *
     * Проверка имени опции.
     *
     * @param name Проверка имени.
     *
     */
    internal fun checkName(name: String): Boolean = (name == shortName || name == fullName)

}