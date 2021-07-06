# Contributions

Given that we're coding on this project while [livestreaming](http://bit.ly/cwi-twitch) (you can catch up [on YouTube](http://bit/ly/cwi-yt),
if you want), we gladly accept contributions via PRs for minor bug fixes and graphical assets, but not for features and larger fixes.
Instead, we'd love to have you on the livestream to help us out with implementing features and fixing major bugs.

If you're camera shy, but still want to contribute, please open an issue or [get in touch](https://twitter.com/codewiththeita) directly.
We'll figure out a way to accept your contributions while still maintaining our mission, which is to walk our audience through the
whole process of writing an app.

ℹ️ **Note:** if you're having issues building the app, please look at the [FAQs](#frequently-asked-questions) below.

# How to contribute

Prerequisites:

- Familiarity with [pull requests](https://help.github.com/articles/using-pull-requests) and [issues](https://guides.github.com/features/issues/).
- Knowledge of [Markdown](https://help.github.com/articles/markdown-basics/) for editing `.md` documents.

In particular, this community seeks the following types of contributions:

- **Ideas**: participate in an issue thread or start your own to have your voice heard.
- **Bug fixes**: bugs are inevitable, fixes are always welcome.
- **Features**: see the [Contributions](#contributions) section above
- **Design**: we sorely need this. Please [get in touch](https://twitter.com/codewiththeita).

# Conduct

We are committed to providing a friendly, safe and welcoming environment for
all, regardless of gender, sexual orientation, disability, ethnicity, religion,
or similar personal characteristic.

Please avoid using overtly sexual nicknames or other nicknames that
might detract from a friendly, safe and welcoming environment for all.

Please be kind and courteous. There's no need to be mean or rude.
Respect that people have differences of opinion and that every design or
implementation choice carries a trade-off and numerous costs. There is seldom
a right answer, merely an optimal answer given a set of values and
circumstances. Try to assume that our community members are nice people
and remember, [miscommunication happens](https://hiddenbrain.org/podcast/why-conversations-go-wrong/).

Please keep unstructured critique to a minimum. If you have solid ideas you
want to experiment with, make a fork and see how it works.

We will exclude you from interaction if you insult, demean or harass anyone.
That is not welcome behaviour. We interpret the term "harassment" as
including the definition in the [Code of Conduct](CODE_OF_CONDUCT.md);
if you have any lack of clarity about what might be included in that concept,
please read the definition. In particular, we don't tolerate behavior that
excludes people in socially marginalized groups.

Private harassment is also unacceptable. No matter who you are, if you feel
you have been or are being harassed or made uncomfortable by a community
member, please contact us [on Twitter](https://twitter.com/codewiththeita)
immediately.
Whether you're a regular contributor or a newcomer, we care about
making this community a safe place for you and we've got your back.

Likewise any spamming, trolling, flaming, baiting or other attention-stealing
behaviour is not welcome.

# Communication

For generic communications, please reach out [on Twitter](https://twitter.com/codewiththeita)
or in the comments during our regular [livestreams](http://bit.ly/cwi-twitch).

GitHub issues are the primary way for communicating about specific proposed
changes to this project.

In both contexts, please follow the conduct guidelines above. Language issues
are often contentious and we'd like to keep discussion brief, civil and focused
on what we're actually doing, not wandering off into too much imaginary stuff.

# Frequently Asked Questions

## I can't build the app because it is missing a `google-services.json` file
That file is required for Crashlytics, which the app uses to report issues to the authors. If you want to build and run the app, you need to obtain your own copy from Firebase:

 1. Create a project on [Firebase console](https://console.firebase.google.com/) using the package name `dev.sebastiano.bundel`. Refer to [this guide](https://firebase.google.com/docs/android/setup) for further details.
 2. Follow the instructions, download the `google-services.json` file and put it in the `app` folder.
 3. Enable Crashlytics on the Firebase console.

### Using dummy data
The [`dummy-google-services.json`](https://github.com/rock3r/bundel/blob/main/build-config/dummy-data/dummy-google-services.json) is enough if you just need to be able to build the app, but don't need to actually run it. That's what the CI uses in order to run the tests and static analysis checks.

If you don't need the app to be fully functional but just want a way to make it build for analysis:
 1. Copy the [`build-config/dummy-data/dummy-google-services.json`](https://github.com/rock3r/bundel/blob/main/build-config/dummy-data/dummy-google-services.json) file into the [`app/`](https://github.com/rock3r/bundel/tree/main/app) folder
 2. Rename the `dummy-google-services.json` file to `google-services.json`
 3. Run static analysis or any checks you want, but remember, the resulting apk is **not ok** to distribute to users. You need to generate a proper `google-services.json` from the Firebase Console as described above, to create an apk that works for end users.

## What testing tool do you use to generate notifications?

From the [first stream episode on YouTube](https://www.youtube.com/watch?v=H6jEZY0gxg8&t=1357s), here is the link to the [notification generator](https://github.com/googlecreativelab/digital-wellbeing-experiments-toolkit/tree/master/notifications/notification-generator). Build this project and run it on your device; it generates notifications at reggular intervals.

# Inspiration
This guide is inspired to [Juno Suárez' contribution guidelines](https://github.com/junosuarez/CONTRIBUTING.md/blob/master/CONTRIBUTING.md),
licensed under CC-0 license.
