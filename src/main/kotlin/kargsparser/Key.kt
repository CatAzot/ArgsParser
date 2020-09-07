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

    override fun apply(args: MutableList<String>): OptionParseResult {
        val iterator = args.iterator()
        while (iterator.hasNext()) {
            if (checkName(iterator.next())) {
                iterator.remove()
                status = if(action()) OptionParseResult.OK else OptionParseResult.ERROR
                return status
            }
        }
        return status
    }

}