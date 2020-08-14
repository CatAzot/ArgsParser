package argsparser

import java.util.*

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
        val action      : (Array<String>) -> Boolean
) : AOption(
        shortName,
        fullName,
        description,
        priority,
        required
) {
    override fun apply(args: MutableList<String>): ParseResult {
        val iterator = args.iterator()
        while (iterator.hasNext()) {
            if (checkName(iterator.next())) {

                iterator.remove()
                val params = LinkedList<String>()
                var currArg: String

                do {
                    currArg = iterator.next()
                    if (currArg[0] == '-')
                        break
                    params.add(currArg)
                    iterator.remove()
                } while (iterator.hasNext())

                if (!action(params.toTypedArray())) return ParseResult.INVALID_OPTION

                applied = true
                return ParseResult.OK
            }
        }
        return if (required) ParseResult.MISSING_REQUIRED_OPTIONS else ParseResult.OK
    }

}
