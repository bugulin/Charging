<div align="center">
  <img src="assets/ic_launcher.svg" alt="app icon">
  <h1>Charging</h1>
</div>

An Android application for monitoring your battery status and charging habits.
Written in Kotlin, built with Jetpack Compose. The application allows you to
see how you've been charging your device and to set up a charge alarm.

The
[charge alarm notification icon](app/src/main/res/drawable-anydpi/notification_charge_alarm.xml)
comes from Material icons and is available under the
[Apache License Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt).

## Features

- uses modern toolkit: Jetpack Compose, Material Design 3 Components
- has a monochromatic icon
- provides edge-to-edge experience
- supports per-app language preferences
- checked by Detekt and lint

## Screenshots

<table>
  <tbody>
    <tr>
      <td><img src="assets/screenshots/discharging.png" alt="Home screen when discharging" /></td>
      <td><img src="assets/screenshots/charging.png" alt="Home screen when charging" /></td>
      <td><img src="assets/screenshots/history.png" alt="Screen with charging history" /></td>
    </tr>
    <tr>
      <td><img src="assets/screenshots/statistics.png" alt="Charging statistics" /></td>
      <td><img src="assets/screenshots/charge_alarm.png" alt="Charge alarm" /></td>
    </tr>
</table>

## Known issues

- The application uses many experimental APIs of Compose Material 3.
- Jetpack Compose renders a level smaller UI (e.g. Headline Medium in Large top
  app bar looks like Headline Small).
- The charging history screen hasn't been tested out with larger number of
  items. Probably use of some pager will be appropriate.

<sup>This project was created as a credit program for the
[Mobile Devices Programming](https://d3s.mff.cuni.cz/teaching/nprg056/)
course at MFF UK.</sup>
