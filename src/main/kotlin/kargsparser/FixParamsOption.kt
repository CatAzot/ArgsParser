package kargsparser

/**
 *
 * Опция с фиксированным числом параметров
 *
 * @param cntParams     Количество параметров.
 * @param action        Действие, выполняемое при применении параметров
 * @property usageHelp  Подсказка по использованию опции
 *
 */
class FixParamsOption(
        shortName       : String,
        fullName        : String = "",
        description     : String = "",
        priority        : Int = Int.MIN_VALUE,
        required        : Boolean = false,
        var usageHelp   : String = "",
        private val cntParams   : Int,
        val action              : (Array<String>) -> Boolean
) : AOption(
        shortName,
        fullName,
        description,
        priority,
        required
) {

    /**
     * Позволяет создать опцию с одним параметром и использовать обработчик
     * с сигнатурой (str) -> bool вместо (str[]) -> bool (без массива на входе)
     */
    constructor(
            shortName       : String,
            fullName        : String = "",
            description     : String = "",
            priority        : Int = Int.MIN_VALUE,
            required        : Boolean = false,
            usageHelp       : String = "",
            action          : (String) -> Boolean
    ) : this(
            shortName,
            fullName,
            description,
            priority,
            required,
            usageHelp,
            1,
            { t: Array<String> -> action(t[0]) }
    )

    override fun apply(args: MutableList<String>): OptionParseResult {
        val iterator = args.iterator()
        while (iterator.hasNext()) {
            if (checkName(iterator.next())) {
                iterator.remove()
                val params = mutableListOf<String>()
                for (idx in 0 until cntParams) {
                    if(iterator.hasNext()) {
                        params.add(iterator.next())
                        iterator.remove()
                    } else return OptionParseResult.MISSING_PARAMS
                }
                status = if (!action(params.toTypedArray())) OptionParseResult.ERROR else OptionParseResult.OK
                return status
            }
        }
        return status
    }

    /**
     *
     * Вывод сообщения об ошибке и возврат false (для проверки параметров)
     *
     * @param msg Сообщение, выводимое после "Error with $fullName!"
     *
     */
    fun errMsg(msg: String): Boolean {
        println("Error with $fullName!\n$msg")
        println(usageHelp)
        return false
    }

}