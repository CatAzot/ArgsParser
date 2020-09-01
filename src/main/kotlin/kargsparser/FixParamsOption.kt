package kargsparser

import java.util.*

/**
 *
 * Опция с фиксированным числом параметров
 *
 * @param cntParams Количество параметров.
 * @param action    Действие, выполняемое при примении параметров
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
     *
     * Позволяет создать опцию с одним параметром и использовать обработчик без массива
     *
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

    override fun apply(args: MutableList<String>): ParseResult {
        val iterator = args.iterator()
        while (iterator.hasNext()) {
            if (checkName(iterator.next())) {
                iterator.remove()
                val params = LinkedList<String>()
                for (idx in 0 until cntParams) {
                    if(iterator.hasNext()) {
                        params.add(iterator.next())
                        iterator.remove()
                    } else return ParseResult.INVALID_OPTION_PARAMS
                }
                if (!action(params.toTypedArray()))
                    return ParseResult.INVALID_OPTION
                applied = true
                return ParseResult.OK
            }
        }
        return if (required) ParseResult.MISSING_REQUIRED_OPTIONS else ParseResult.OK
    }

    /**
     *
     * Вывод сообщения об ошибке и возврат false (для проверки параметров)
     *
     */
    fun errMsg(msg: String): Boolean {
        println("Error with $fullName!\n$msg")
        println(usageHelp)
        return false
    }

}