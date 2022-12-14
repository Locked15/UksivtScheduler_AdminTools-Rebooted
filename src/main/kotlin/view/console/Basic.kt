package view.console

import controller.view.console.BasicController
import model.view.Command
import model.view.CommandInfo
import java.util.*


/**
 * Basic view element.
 *
 * Contains logic of the user-system interactions.
 * Works via console.
 */
class Basic(private val user: String) {

    /* region Properties */

    /**
     * Contains a controller object with realizations of the interaction functions.
     *
     * When user performs any accessible action, view sends a message to the controller,
     *      and the controller executes corresponding actions (commands).
     * Then, controller may return something (like the success of execution).
     *
     * View Action -> Controller Command -> View Result.
     */
    private val controller = BasicController()

    /**
     * [Dictionary] with key-value pairs.
     * Second values contains [Pair] objects.
     * [First of them][Pair.first] contains description of the function.
     * [Second one][Pair.second] contains [Runnable] objects, that contains execution logic for this action.
     *
     * Please, get the values of this dictionary with ignoring register.
     * Use [Map.get] or [String.lowercase] to prevent register dependant.
     *
     * If you want to create new action, you must remember:
     * KEYS CAN'T REPEAT.
     * Templates can create anything else.
     */
    private val availableActions = mapOf(
            // Valuable Commands:
            "schedule" to Pair(scheduleDescription,
                               Command("Schedule") {
                                   controller.parseSchedule(it)
                               }),
            "changes" to Pair(changesDescription,
                              Command("Changes") {
                                  controller.parseChanges(it)
                              }
            ),
            // Functional Commands:
            "help" to Pair(helpDescription,
                           Command("Help") {
                               controller.showHelp()
                           }),
            "parse" to Pair(parseDescription,
                            Command("Parse") {
                                controller.initializeBasicParsingProcessByArguments(it)
                            }),
            "write" to Pair(writeDescription,
                            Command("Write") {
                                controller.writeLastResult()
                            }),
            "show" to Pair(showDescription,
                           Command("Show") {
                               controller.showLastResult()
                           }),
            "exit" to Pair(exitDescription,
                           Command("Exit") {
                               controller.exit()
                           }
            )
    )
    /* endregion */

    /* region Functions */

    /**
     * [Greets][greetUser] a [user] and begins new session of the work.
     *
     * Call this function to begin work session with AdminTools.
     * This is the entry point (after constructor, of course).
     */
    fun beginSession() {
        greetUser(user, Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
        do {
            print("So, what you want to do now?\nEnter command code: ")
            val action = readlnOrNull()

            performUserInput(action ?: "help")
        } while (!action.isNullOrBlank())
    }

    /**
     * Performs any user input.
     * It parses inputted text and seeks corresponding command.
     *
     * If the corresponding command is found, it [confirms execution][confirmCommandExecution] and
     *    then executes the command.
     * Although, it [returns to input of the command name][beginSession].
     */
    private fun performUserInput(input: String) {
        if (input.isNotBlank()) {
            val commandInfo = parseInputtedText(this, input.trim())
            if (commandInfo.action != null) {
                if (confirmCommandExecution(commandInfo.action.first)) {
                    executeCommand(commandInfo.args, commandInfo.action.second)
                }
            }
            else {
                println("Inputted command isn't supported, please enter 'help' to get list of supported ones.\n")
            }
        }
    }

    /**
     * Confirms [Command] [execution][executeCommand].
     * It shows command description and asks user to input [confirmation][String].
     *
     * If a user confirms execution, it [returns][Boolean] 'true', another 'false'.
     */
    private fun confirmCommandExecution(desc: String): Boolean {
        print("Selected command: $desc. \nAre you sure (Y/N)? ")
        val confirmation = readlnOrNull()

        return !confirmation.isNullOrBlank() && confirmation.equals("y", true)
    }

    /**
     * Executes [command] with given [arguments][args].
     * Also, it marks command output in the terminal (console) output.
     */
    private fun executeCommand(args: List<String>, command: Command) {
        println("\n\t\tCommand ('${command.name}') Output:")
        command.setUpArgs(args).execute()

        println("\t\tExecution complete.\n")
    }
    /* endregion */

    /* region Companion */

    companion object {

        /* region Properties */

        /**
         * Description of the 'Help' command.
         *
         * This is a functional one.
         */
        private val helpDescription: String

        /**
         * Description of the 'Schedule' command.
         *
         * This is a valuable one.
         */
        private val scheduleDescription: String

        /**
         * Description of the 'Changes' command.
         *
         * This is a valuable one.
         */
        private val changesDescription: String

        /**
         * Description of the 'Parse' command.
         */
        private val parseDescription: String

        /**
         * Description of the 'Write' command.
         */
        private val writeDescription: String

        /**
         * Description of the 'Show' command.
         */
        private val showDescription: String

        /**
         * Description of the 'Exit' command.
         */
        private val exitDescription: String
        /* endregion */

        /* region Initializers */

        /**
         * Initializes all static properties by current system language.
         * Some languages aren't supported yet.
         */
        init {
            when (Locale.getDefault()) {
                Locale.ENGLISH -> {
                    scheduleDescription = "Begins schedule-reading process (requires prepared file)"
                    changesDescription = "Begins changes-reading process (requires downloaded document)"

                    helpDescription = "Show context help for this application"
                    parseDescription = "Begins basic parsing process (may be useful for debugging process)"
                    writeDescription = "Writes last gotten result value to file"
                    showDescription = "Show last gotten result in the console (terminal)"
                    exitDescription = "Exits from program"
                }
                Locale.CHINESE -> {
                    scheduleDescription = "??????????????????????????????????????????????????????"
                    changesDescription = "???????????????????????????????????????????????????"

                    helpDescription = "???????????????????????????????????????"
                    parseDescription = "?????????????????????????????????????????????????????????"
                    writeDescription = "???????????????????????????????????????"
                    showDescription = "??????????????????????????????????????????????????????"
                    exitDescription = "????????????"
                }

                else -> {
                    scheduleDescription = "???????????? ?????????????? ???????????????????? ?????????? ???????????????????? (?????????????? ???????????????? ??????????)"
                    changesDescription = "???????????? ?????????????? ???????????? ?????????? (?????????????????? ?????????????????????? ????????????????)"

                    helpDescription = "???????????????? ?????????????????????? ?????????????? ?????? ????????????????????"
                    parseDescription = "???????????? ?????????????? ?????????????? ?????????? ????????-???????? (?????????? ???????? ?????????????? ?????? ????????????????????????)"
                    writeDescription = "???????????????? ?????????????????? ???????????????????? ?????????????????? ?? ????????"
                    showDescription = "???????????????????? ?????????????????? ???????????????????? ?????????????????? ?? ?????????????? (??????????????????)"
                    exitDescription = "?????????? ???? ??????????????????"
                }
            }
        }
        /* endregion */

        /* region Functions */

        /**
         * Greets [user].
         * Greeting message depends by [current hour][time].
         */
        private fun greetUser(user: String, time: Int) {
            if (time != 23) {
                if (time <= 6) println("\nGood night, $user!")
                else if (time <= 9) println("\nGood morning, $user!")
                else if (time <= 16) println("\nGood afternoon, $user!")
                else println("\nGood evening, $user!")
            }
            else {
                println("\nNight's become. Civilians lies to sleep and mafia wakes up." +
                                "Beware, $user...")
            }
        }

        /**
         * Parses user [inputted a text][input] and tries to transform it into [object][CommandInfo].
         * Requires [instance][console] of the main class to get [target action][Command] from [list][availableActions].
         */
        private fun parseInputtedText(console: Basic, input: String): CommandInfo {
            val splatted = input.split(" ")
            val command = splatted[0]

            // We take all arguments, ignoring the first element (that contains the command itself).
            return CommandInfo(args = splatted.drop(1), action = console.availableActions[command.lowercase()])
        }
        /* endregion */
    }
    /* endregion */
}
