package org.argsparser

/**
 *
 * Результат парсинга
 *
 * @property OK                         Парсинг прошел без ошибок
 * @property EMPTY_ARGS                 Аргументы отсутствуют
 * @property HELP_REQUESTED             Запрос подсказки
 * @property MISSING_REQUIRED_OPTIONS   Отсутствуют требуемые опции
 * @property INVALID_OPTION             Некорректная опция
 * @property INVALID_PARAMS             Некорректные параметры программы
 *
 */

enum class ParseResult {
    OK,
    EMPTY_ARGS,
    HELP_REQUESTED,
    MISSING_REQUIRED_OPTIONS,
    INVALID_OPTION,
    INVALID_PARAMS
}
