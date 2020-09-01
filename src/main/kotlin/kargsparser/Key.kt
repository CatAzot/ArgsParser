package kargsparser

/**
 *
 * Ключ (опция без параметров)
 *
 * @param action Действие, выполняемое при применении опции
 *
 */
class Key(
        shortName       : String,
        fullName        : String = "",
        description     : String = "",
        priority        : Int = Int.MIN_VALUE,
        required        : Boolean = false,
        val action      : () -> Boolean
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
                if (!action())
                    return ParseResult.INVALID_OPTION
                return ParseResult.OK
            }
        }
        return if (required) ParseResult.MISSING_REQUIRED_OPTIONS else ParseResult.OK
    }

}