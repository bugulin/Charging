package cz.bornasp.charging.ui.navigation

/**
 * Interface for describing the navigation destinations of the application.
 */
interface NavigationDestination {
    /**
     * Unique name to define the path for a composable.
     */
    val route: String

    /**
     * String resource that contains title to be displayed for the screen.
     */
    val titleRes: Int
}
