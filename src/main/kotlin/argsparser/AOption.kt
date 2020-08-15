package argsparser

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
 * @property applied     Флаг, указывающий была ли обработана и принята опция.
 *
 */
abstract class AOption(
        val shortName     : String,
        val fullName      : String,
        val description   : String,
        val priority      : Int,
        var required      : Boolean) {

    var applied = false
    private var customHelp = false

    var help: String = buildHelp()
        set(value) {
            if(value != "") {
                customHelp = true
                field = value
            } else field = buildHelp()
        }

    internal var descriptionIndent: Int = 30
        set(value) {
            if(value == field) return
            field = value
            if(!customHelp) help = buildHelp()
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
    private fun buildHelp(): String = "  $shortName, ${fullName.padEnd(descriptionIndent)} - $description"

    /**
     *
     * Проверка имени опции.
     *
     * @param name Проверка имени.
     *
     */
    internal fun checkName(name: String): Boolean = (name == shortName || name == fullName)

}