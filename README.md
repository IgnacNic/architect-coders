# ArchitectCoders App

This app was started as the project attached to the learning course of Architect Coders. The development follows the cycles described in a custom Jira project created solely for this app: [Architect Coders Project](https://inicolaslop.atlassian.net/jira/software/projects/ACP/boards/1)


## Branching

The branching strategy for the project is as follows:

1. All branches must start with a prefix from the following: `feature` or `practicas` depending on the Jira issue type they are linked to.
2. All branches must then have their respective ticket number from the `ACP` project e.g.: `feature/ACP-11/...`. For the branches under `practicas`, they must have only the numeral version of the release made for review, such as `practicas/practica_1`.
3. Lastly, branches under `feature` must end with a short explanatory name, for example: `feature/ACP-18/location-updates-flows`.

## Architecture
When deciding the project's architecture I opted for a mixed approach of Clean Architecture. That is between a modularisation in features, which each module would contain every layer of said feature and a modularisation in layers, in which each module would contain only the code relevant of its layer. I opted to take the layer modularisation and restrict it to one module for UI, one for the App and another for both Data and Domain. Also, having the UI broken into feature-related submodules where they can depend on one or more Data & Domain submodules.

The reason for going this way is that in the future, adding new features to the app would start just by adding a new UI submodule and only picking up the necessary Data part of the Data & Domain modules, or implementing new ones if necessary.

The project is divided in four modules: `app`, `feature`, `business-logic` and `common`. The three last are divided into submodules, depending on their specific function, aiming to keep as much cohesion as possible in their content. The modules' content and structure are:

- `app` Module: Project's main module where the app is run. This module contains the entry point of the app, as well as the navigation management and the Dependency Injection.
- `feature` Module: This module contains the different submodules that contain the Presentation layer. These submodules are independent from each other and may depend on various `business-logic` submodules' domain layer.
- `business-logic` Module: Both **Data** and **Domain** layer. Each submodule here is also independent from each other and are broken into a package for each layer.
- `common` Module: Under this module are the common tools that both `feature` and `business-logic` modules may use. They include project's entities, ui resources, networking utils, et cetera.

<img width="442" height="622" alt="architecture" src="https://github.com/user-attachments/assets/f73f04c1-eb69-4924-a068-cc1789b2d3a4" />

Furthermore I've decided to go for an MVI for the presenters' pattern, each one of the defining their `Actions`, `UI State` and `One-Shot` actions (`Side-Effects`). The View Models as presenters will communicate with the Views by updating the `UI State`, and the view will communicate any user or non-user interaction by invoking the View Model's action reducer function with the relevant `Action` as parameter. For performing a UI action that is outside of the View's control (such as navigation, interacting with the OS, ...) the View Model will emit a `One-Shot` action that will be consumed by the View holder and reduced accordingly.
