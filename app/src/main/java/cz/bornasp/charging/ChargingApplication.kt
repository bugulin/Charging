package cz.bornasp.charging

import android.app.Application
import cz.bornasp.charging.data.AppContainer
import cz.bornasp.charging.data.AppDataContainer

class ChargingApplication : Application() {
    /**
     * [AppContainer] instance used by the rest of classes to obtain dependencies.
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
