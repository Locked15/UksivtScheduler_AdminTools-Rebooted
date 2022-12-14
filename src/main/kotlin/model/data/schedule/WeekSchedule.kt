package model.data.schedule

import com.fasterxml.jackson.databind.ObjectMapper


/**
 * Class, that represents whole week schedule.
 *
 * It indented to identify group schedule, so it contains [property][groupName] with group name.
 */
class WeekSchedule(var groupName: String?, var daySchedules: MutableList<DaySchedule>) {
	
	/* region Constructors */

	/**
	 * Creates a new instance of [WeekSchedule].
	 * New instance with given [group name][groupName].
	 * Schedules initializes with [empty list][listOf].
	 */
	constructor(groupName: String?): this(groupName, mutableListOf())
	/* endregion */

	/* region Functions */

	/**
	 * Returns string representation of the [object][WeekSchedule].
	 */
	override fun toString(): String {
		val serializer = ObjectMapper()
		return serializer.writerWithDefaultPrettyPrinter().writeValueAsString(this)
	}
	/* endregion */
}
