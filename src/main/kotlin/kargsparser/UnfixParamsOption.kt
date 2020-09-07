package kargsparser

/**
 *
 * Опция с нефиксированным числом параметров. Добавление параметров опции производится до следующей опции.
 *
 */
class UnfixParamsOption(
        shortName       : String,
        fullName        : String = "",
        description     : String = "",
        priority        : Int = Int.MIN_VALUE,
        required        : Boolean = false,
        var usageHelp   : String = "",
        val action      : (Array<String>) -> Boolean
) : AOption(
        shortName,
        fullName,
        description,
        priority,
        required
) {
    override fun apply(args: MutableList<String>): OptionParseResult {
        val iterator = args.iterator()
        while (iterator.hasNext()) {
            if (checkName(iterator.next())) {
                iterator.remove()
                val params = mutableListOf<String>()
                var currArg: String
                do {
                    currArg = iterator.next()
                    if (currArg[0] == '-')
                        break
                    params.add(currArg)
                    iterator.remove()
                } while (iterator.hasNext())
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
     */
    fun errMsg(msg: String): Boolean {
        println("Error with $fullName!\n$msg")
        println(usageHelp)
        return false
    }

}
